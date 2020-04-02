/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public final class Dumper
{
	private final HashMap<Type<?>, AtomicLong> pks = new HashMap<>();

	/**
	 * @deprecated This is weird functionality that will be removed sooner or later.
	 */
	@Deprecated
	public Dumper()
	{
		// just to make it deprecated
	}

	public void prepare(
			final Appendable out,
			final Model model)
	throws IOException
	{
		model.connect().dialect.prepareDumperConnection(out);
	}

	public void unprepare(
			final Appendable out,
			final Model model)
	throws IOException
	{
		model.connect().dialect.unprepareDumperConnection(out);
	}

	/**
	 * @throws IOException from calling out.{@link Appendable#append(CharSequence) append}.
	 */
	public <E extends Item> E newItem(
			final Appendable out,
			final Type<E> type,
			final SetValue<?>... setValues)
	throws IOException
	{
		final FieldValues fieldValues = type.executeCreate(setValues);
		final Row row = new Row(type);
		for(final Map.Entry<Field<?>, Object> e : fieldValues.dirtySet())
		{
			final Field<?> field = e.getKey();

			if(field instanceof DataField && ((DataField)field).getVaultInfo()!=null)
				throw new RuntimeException("Dumper does not support DataField Vault: " + field);

			if(field instanceof FunctionField<?>)
				set(row, (FunctionField<?>)field, e.getValue());
		}
		final long pk = nextPk(type);
		final E result = type.activate(pk);
		final IdentityHashMap<BlobColumn, byte[]> blobs = fieldValues.toBlobs();
		final Connect connect = type.getModel().connect();
		insert(connect.dialect, connect.marshallers, blobs, type, pk, row, type, out);
		return result;
	}

	private long nextPk(final Type<?> type)
	{
		Type<?> pkType = type;
		while(pkType.getSupertype()!=null)
			pkType = pkType.getSupertype();

		return pks.computeIfAbsent(pkType, k -> new AtomicLong()).getAndIncrement();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void set(final Row row, final FunctionField field, final Object value)
	{
		field.set(row, value);
	}

	private static void insert(
			final Dialect dialect,
			final Marshallers marshallers,
			final IdentityHashMap<BlobColumn, byte[]> blobs,
			final Type<?> type,
			final long pk,
			final Row row,
			final Type<?> tableType,
			final Appendable out)
	throws IOException
	{
		final Type<?> supertype = tableType.supertype;
		if(supertype!=null)
			insert(dialect, marshallers, blobs, type, pk, row, supertype, out);

		final Table table = tableType.getTable();

		final List<Column> columns = table.getColumns();

		final Statement bf = new Statement(dialect, marshallers);
		final StringColumn typeColumn = table.typeColumn;
		final IntegerColumn updateCounter = table.updateCounter;

		bf.append("insert into ").
			append(table.quotedID).
			append("(").
			append(table.primaryKey.quotedID);

		if(typeColumn!=null)
		{
			bf.append(',').
				append(typeColumn.quotedID);
		}

		if(updateCounter!=null)
		{
			bf.append(',').
				append(updateCounter.quotedID);
		}

		for(final Column column : columns)
		{
			if(!(column instanceof BlobColumn) || blobs.containsKey(column))
			{
				bf.append(',').
					append(column.quotedID);
			}
		}

		bf.append(")values(").
			appendParameter(pk);

		if(typeColumn!=null)
		{
			bf.append(',').
				appendParameter(type.schemaId);
		}

		if(updateCounter!=null)
		{
			bf.append(',').
				appendParameter(0);
		}

		for(final Column column : columns)
		{
			if(column instanceof BlobColumn)
			{
				if(blobs.containsKey(column))
				{
					bf.append(',').
						appendParameterBlob(blobs.get(column));
				}
			}
			else
			{
				bf.append(',').
					appendParameter(column, row.get(column));
			}
		}
		bf.append(");");

		out.append(bf.getText());
	}
}
