/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Dumper
{
	private final HashMap<Type, AtomicInteger> pks = new HashMap<Type, AtomicInteger>();

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
			final SetValue... setValues)
	throws IOException
	{
		final LinkedHashMap<Field, Object> fieldValues = type.executeCreate(setValues);
		final Row row = new Row();
		for(final Map.Entry<Field, Object> e : fieldValues.entrySet())
		{
			final FunctionField field = (FunctionField)e.getKey();
			set(row, field, e.getValue());
		}
		final int pk = nextPk(type);
		final E result = type.activate(pk);
		final HashMap<BlobColumn, byte[]> blobs = result.toBlobs(fieldValues, null);
		insert(type.getModel().connect().dialect, blobs, type, pk, row, type, out);
		return result;
	}

	private int nextPk(final Type type)
	{
		Type pkType = type;
		while(pkType.getSupertype()!=null)
			pkType = pkType.getSupertype();

		AtomicInteger pkSource = pks.get(pkType);
		if(pkSource==null)
		{
			pkSource = new AtomicInteger();
			pks.put(pkType, pkSource);
		}
		return pkSource.getAndIncrement();
	}

	@SuppressWarnings("unchecked")
	private static void set(final Row row, final FunctionField field, final Object value)
	{
		field.set(row, value);
	}

	private static void insert(
			final Dialect dialect,
			final Map<BlobColumn, byte[]> blobs,
			final Type type,
			final int pk,
			final Row row,
			final Type tableType,
			final Appendable out)
	throws IOException
	{
		final Type supertype = tableType.supertype;
		if(supertype!=null)
			insert(dialect, blobs, type, pk, row, supertype, out);

		final Table table = tableType.getTable();

		final List<Column> columns = table.getColumns();

		final Statement bf = new Statement(dialect);
		final StringColumn typeColumn = table.typeColumn;

		bf.append("insert into ").
			append(table.quotedID).
			append("(").
			append(table.primaryKey.quotedID);

		if(typeColumn!=null)
		{
			bf.append(',').
				append(typeColumn.quotedID);
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
				appendParameter(type.id);
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
