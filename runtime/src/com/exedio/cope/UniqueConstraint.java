/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Intern.intern;

import com.exedio.cope.util.Cast;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class UniqueConstraint extends Feature implements Copyable
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<?>[] fields;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final List<FunctionField<?>> fieldList;
	private String databaseID;

	private UniqueConstraint(final FunctionField<?>[] fields)
	{
		this.fields = fields;
		this.fieldList = Collections.unmodifiableList(Arrays.asList(fields));
		for(final FunctionField<?> f : fields)
			f.registerUniqueConstraint(this);
	}

	/**
	 * Is not public, because one should use {@link FunctionField#unique()} etc.
	 */
	UniqueConstraint(final FunctionField<?> field)
	{
		this(new FunctionField<?>[]{field});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2)
	{
		this(new FunctionField<?>[]{field1, field2});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3)
	{
		this(new FunctionField<?>[]{field1, field2, field3});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5});
	}

	public UniqueConstraint(final FunctionField<?> field1, final FunctionField<?> field2, final FunctionField<?> field3, final FunctionField<?> field4, final FunctionField<?> field5, final FunctionField<?> field6)
	{
		this(new FunctionField<?>[]{field1, field2, field3, field4, field5, field6});
	}

	@Override
	public UniqueConstraint copy(final CopyMapper mapper)
	{
		return new UniqueConstraint(mapper.get(fields));
	}

	public List<FunctionField<?>> getFields()
	{
		return fieldList;
	}

	static final String IMPLICIT_UNIQUE_SUFFIX = "ImplicitUnique";

	void connect(final Table table)
	{
		if(this.databaseID!=null)
			throw new RuntimeException();

		final String schemaName =
			(fields.length==1)
			? fields[0].getSchemaName()
			: getSchemaName();
		this.databaseID = intern(table.makeGlobalID(schemaName + "_Unq"));

		table.database.executor.addUniqueConstraint(databaseID, this);
	}

	void disconnect()
	{
		if(this.databaseID==null)
			throw new RuntimeException();

		this.databaseID = null;
	}

	private String getDatabaseID()
	{
		if(databaseID==null)
			throw new RuntimeException();

		return databaseID;
	}

	void makeSchema(final com.exedio.dsmf.Table dsmfTable)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('(');
		for(int i = 0; i<fields.length; i++)
		{
			if(i>0)
				bf.append(',');
			final FunctionField<?> f = fields[i];
			bf.append(f.getColumn().quotedID);
		}
		bf.append(')');

		new com.exedio.dsmf.UniqueConstraint(dsmfTable, getDatabaseID(), bf.toString());
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append("unique(");
		fields[0].toString(bf, defaultType);
		for(int i = 1; i<fields.length; i++)
		{
			bf.append(',');
			fields[i].toString(bf, defaultType);
		}
		bf.append(')');
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	public Item search(final Object... values)
	{
		// TODO: search natively for unique constraints
		final List<FunctionField<?>> fields = getFields();
		if(fields.size()!=values.length)
			throw new RuntimeException(String.valueOf(fields.size())+'-'+values.length);

		for(int i = 0; i<values.length; i++)
			if(values[i]==null)
				throw new NullPointerException("cannot search uniquely for null on " + getID() + " for " + fields.get(i).getID());

		final Iterator<FunctionField<?>> fieldIter = fields.iterator();
		final Condition[] conditions = new Condition[fields.size()];
		for(int j = 0; fieldIter.hasNext(); j++)
			conditions[j] = Cope.equalAndCast(fieldIter.next(), values[j]);

		return getType().searchSingleton(new CompositeCondition(CompositeCondition.Operator.AND, conditions));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	public <P extends Item> P search(final Class<P> typeClass, final Object... values)
	{
		return Cast.verboseCast(typeClass, search(values));
	}

	void check(final Item item, final Map<? extends Field<?>, ?> fieldValues)
	{
		field:
		for(final FunctionField<?> testField : fields)
		{
			if(fieldValues.containsKey(testField))
			{
				final Object[] values = new Object[fields.length];
				int i = 0;

				for(final FunctionField<?> f : fields)
				{
					final Object value = fieldValues.containsKey(f) ? fieldValues.get(f) : (item!=null ? f.get(item) : null);
					if(value==null)
						break field;
					values[i++] = value;
				}

				final Item collision = search(values);
				if(collision!=null && (item==null || !item.equals(collision)))
					throw new UniqueViolationException(this, item, null);

				break field;
			}
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<FunctionField<?>> getUniqueAttributes()
	{
		return getFields();
	}

	/**
	 * @deprecated Use {@link #search(Object[])} instead
	 */
	@Deprecated
	public Item searchUnique(final Object... values)
	{
		return search(values);
	}

	/**
	 * @deprecated Use {@link #search(Class,Object[])} instead
	 */
	@Deprecated
	public <P extends Item> P searchUnique(final Class<P> typeClass, final Object... values)
	{
		return search(typeClass, values);
	}
}
