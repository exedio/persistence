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
	
	private final FunctionAttribute<?>[] uniqueAttributes;
	private final List<FunctionAttribute<?>> uniqueAttributeList;
	private String databaseID;

	private UniqueConstraint(final FunctionAttribute<?>[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		for(int i = 0; i<uniqueAttributes.length; i++)
			uniqueAttributes[i].registerUniqueConstraint(this);
	}
	
	/**
	 * Is not public, because one should use {@link Item#UNIQUE} etc.
	 */
	UniqueConstraint(final FunctionAttribute uniqueAttribute)
	{
		this(new FunctionAttribute[]{uniqueAttribute});
	}
	
	public UniqueConstraint(final FunctionAttribute uniqueAttribute1, final FunctionAttribute uniqueAttribute2)
	{
		this(new FunctionAttribute[]{uniqueAttribute1, uniqueAttribute2});
	}
	
	public UniqueConstraint(final FunctionAttribute uniqueAttribute1, final FunctionAttribute uniqueAttribute2, final FunctionAttribute uniqueAttribute3)
	{
		this(new FunctionAttribute[]{uniqueAttribute1, uniqueAttribute2, uniqueAttribute3});
	}
	
	public UniqueConstraint(final FunctionAttribute uniqueAttribute1, final FunctionAttribute uniqueAttribute2, final FunctionAttribute uniqueAttribute3, final FunctionAttribute uniqueAttribute4)
	{
		this(new FunctionAttribute[]{uniqueAttribute1, uniqueAttribute2, uniqueAttribute3, uniqueAttribute4});
	}
	
	public final List<FunctionAttribute<?>> getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	
	static final String IMPLICIT_UNIQUE_SUFFIX = "ImplicitUnique";
	
	final void materialize(final Database database)
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
		for(int i = 0; i<uniqueAttributes.length; i++)
		{
			if(i>0)
				bf.append(',');
			final FunctionAttribute<?> uniqueAttribute = uniqueAttributes[i];
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
		buf.append(uniqueAttributes[0].toString());
		for(int i = 1; i<uniqueAttributes.length; i++)
		{
			buf.append(',');
			buf.append(uniqueAttributes[i].toString());
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
		final List<FunctionAttribute<?>> attributes = getUniqueAttributes();
		if(attributes.size()!=values.length)
			throw new RuntimeException("-"+attributes.size()+'-'+values.length);

		final Iterator<FunctionAttribute<?>> attributeIterator = attributes.iterator();
		final Condition[] conditions = new Condition[attributes.size()];
		for(int j = 0; attributeIterator.hasNext(); j++)
			conditions[j] = Cope.equalAndCast(attributeIterator.next(), values[j]);

		return getType().searchSingleton(new CompositeCondition(CompositeCondition.Operator.AND, conditions));
	}
	
}
