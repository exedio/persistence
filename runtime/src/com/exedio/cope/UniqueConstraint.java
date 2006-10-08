/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
	
	private final FunctionField<?>[] attributes;
	private final List<FunctionField<?>> attributeList;
	private String databaseID;

	private UniqueConstraint(final FunctionField<?>[] attributes)
	{
		this.attributes = attributes;
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
		for(final FunctionField attribute : attributes)
			attribute.registerUniqueConstraint(this);
	}
	
	/**
	 * Is not public, because one should use {@link Item#UNIQUE} etc.
	 */
	UniqueConstraint(final FunctionField attribute)
	{
		this(new FunctionField[]{attribute});
	}
	
	public UniqueConstraint(final FunctionField attribute1, final FunctionField attribute2)
	{
		this(new FunctionField[]{attribute1, attribute2});
	}
	
	public UniqueConstraint(final FunctionField attribute1, final FunctionField attribute2, final FunctionField attribute3)
	{
		this(new FunctionField[]{attribute1, attribute2, attribute3});
	}
	
	public UniqueConstraint(final FunctionField attribute1, final FunctionField attribute2, final FunctionField attribute3, final FunctionField attribute4)
	{
		this(new FunctionField[]{attribute1, attribute2, attribute3, attribute4});
	}
	
	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public final List<FunctionField<?>> getUniqueAttributes()
	{
		return getFields();
	}
	
	public final List<FunctionField<?>> getFields()
	{
		return attributeList;
	}
	
	static final String IMPLICIT_UNIQUE_SUFFIX = "ImplicitUnique";
	
	final void connect(final Database database)
	{
		if(this.databaseID!=null)
			throw new RuntimeException();

		final String featureName = getName();
		final String databaseName =
			featureName.endsWith(IMPLICIT_UNIQUE_SUFFIX)
			? featureName.substring(0, featureName.length()-IMPLICIT_UNIQUE_SUFFIX.length())
			: featureName;
		this.databaseID = database.makeName(getType().id + '_' + databaseName + "_Unq").intern();
		database.addUniqueConstraint(databaseID, this);
	}

	final void dematerialize()
	{
		if(this.databaseID==null)
			throw new RuntimeException();

		this.databaseID = null;
	}

	private final String getDatabaseID()
	{
		if(databaseID==null)
			throw new RuntimeException();
			
		return databaseID;
	}
	
	final void makeSchema(final Table dsmfTable)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append('(');
		for(int i = 0; i<attributes.length; i++)
		{
			if(i>0)
				bf.append(',');
			final FunctionField<?> uniqueAttribute = attributes[i];
			bf.append(uniqueAttribute.getColumn().protectedID);
		}
		bf.append(')');

		new com.exedio.dsmf.UniqueConstraint(dsmfTable, getDatabaseID(), bf.toString());
	}
	
	@Override
	final String toStringNonInitialized()
	{
		final StringBuffer buf = new StringBuffer();
		
		buf.append("unique(");
		buf.append(attributes[0].toString());
		for(int i = 1; i<attributes.length; i++)
		{
			buf.append(',');
			buf.append(attributes[i].toString());
		}
		buf.append(')');
		
		return buf.toString();
	}
	
	/**
	 * Finds an item by its unique attributes.
	 * @return null if there is no matching item.
	 */
	public final Item searchUnique(final Object[] values)
	{
		// TODO: search nativly for unique constraints
		final List<FunctionField<?>> attributes = getFields();
		if(attributes.size()!=values.length)
			throw new RuntimeException("-"+attributes.size()+'-'+values.length);

		final Iterator<FunctionField<?>> attributeIterator = attributes.iterator();
		final Condition[] conditions = new Condition[attributes.size()];
		for(int j = 0; attributeIterator.hasNext(); j++)
			conditions[j] = Cope.equalAndCast(attributeIterator.next(), values[j]);

		return getType().searchSingleton(new CompositeCondition(CompositeCondition.Operator.AND, conditions));
	}
	
}
