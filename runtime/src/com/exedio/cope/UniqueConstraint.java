/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.dsmf.Table;

public final class UniqueConstraint extends Feature
{
	private final FunctionField<?>[] fields;
	private final List<FunctionField<?>> fieldList;
	private String databaseID;

	private UniqueConstraint(final FunctionField<?>[] fields)
	{
		this.fields = fields;
		this.fieldList = Collections.unmodifiableList(Arrays.asList(fields));
		for(final FunctionField f : fields)
			f.registerUniqueConstraint(this);
	}
	
	/**
	 * Is not public, because one should use {@link Item#UNIQUE} etc.
	 */
	UniqueConstraint(final FunctionField field)
	{
		this(new FunctionField[]{field});
	}
	
	public UniqueConstraint(final FunctionField field1, final FunctionField field2)
	{
		this(new FunctionField[]{field1, field2});
	}
	
	public UniqueConstraint(final FunctionField field1, final FunctionField field2, final FunctionField field3)
	{
		this(new FunctionField[]{field1, field2, field3});
	}
	
	public UniqueConstraint(final FunctionField field1, final FunctionField field2, final FunctionField field3, final FunctionField field4)
	{
		this(new FunctionField[]{field1, field2, field3, field4});
	}
	
	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<FunctionField<?>> getUniqueAttributes()
	{
		return getFields();
	}
	
	public List<FunctionField<?>> getFields()
	{
		return fieldList;
	}
	
	static final String IMPLICIT_UNIQUE_SUFFIX = "ImplicitUnique";
	
	void connect(final Database database)
	{
		if(this.databaseID!=null)
			throw new RuntimeException();

		final String featureName = getName();
		final String databaseName =
			featureName.endsWith(IMPLICIT_UNIQUE_SUFFIX)
			? featureName.substring(0, featureName.length()-IMPLICIT_UNIQUE_SUFFIX.length())
			: featureName;
		this.databaseID = database.intern(database.makeName(getType().id + '_' + databaseName + "_Unq"));
		database.addUniqueConstraint(databaseID, this);
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
	
	void makeSchema(final Table dsmfTable)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append('(');
		for(int i = 0; i<fields.length; i++)
		{
			if(i>0)
				bf.append(',');
			final FunctionField<?> f = fields[i];
			bf.append(f.getColumn().protectedID);
		}
		bf.append(')');

		new com.exedio.dsmf.UniqueConstraint(dsmfTable, getDatabaseID(), bf.toString());
	}
	
	@Override
	void toStringNonInitialized(final StringBuffer bf, final Type defaultType)
	{
		bf.append("unique(");
		bf.append(fields[0].toString());
		for(int i = 1; i<fields.length; i++)
		{
			bf.append(',');
			bf.append(fields[i].toString());
		}
		bf.append(')');
	}
	
	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 */
	public Item searchUnique(final Object... values)
	{
		// TODO: search nativly for unique constraints
		final List<FunctionField<?>> fields = getFields();
		if(fields.size()!=values.length)
			throw new RuntimeException("-"+fields.size()+'-'+values.length);

		final Iterator<FunctionField<?>> fieldIter = fields.iterator();
		final Condition[] conditions = new Condition[fields.size()];
		for(int j = 0; fieldIter.hasNext(); j++)
			conditions[j] = Cope.equalAndCast(fieldIter.next(), values[j]);

		return getType().searchSingleton(new CompositeCondition(CompositeCondition.Operator.AND, conditions));
	}
}
