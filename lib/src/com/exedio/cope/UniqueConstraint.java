/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.Condition;
import com.exedio.cope.search.EqualCondition;
import com.exedio.dsmf.Table;

public final class UniqueConstraint extends Feature
{
	
	private final ObjectAttribute[] uniqueAttributes;
	private final List uniqueAttributeList;
	private String databaseID;

	private UniqueConstraint(final ObjectAttribute[] uniqueAttributes)
	{
		this.uniqueAttributes = uniqueAttributes;
		this.uniqueAttributeList = Collections.unmodifiableList(Arrays.asList(uniqueAttributes));
		for(int i = 0; i<uniqueAttributes.length; i++)
			if(uniqueAttributes[i]==null)
				throw new RuntimeException(String.valueOf(i));
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute)
	{
		this(new ObjectAttribute[]{uniqueAttribute});
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute, ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2)
	{
		this(new ObjectAttribute[]{uniqueAttribute1, uniqueAttribute2});
	}
	
	/**
	 * @see Item#uniqueConstraint(ObjectAttribute, ObjectAttribute, ObjectAttribute)
	 */
	UniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2, final ObjectAttribute uniqueAttribute3)
	{
		this(new ObjectAttribute[]{uniqueAttribute1, uniqueAttribute2, uniqueAttribute3});
	}
	
	void initialize(final Type type, final String name)
	{
		super.initialize(type, name);
		type.registerInitialization(this);
	}

	/**
	 * @return a list of {@link ObjectAttribute}s.
	 */
	public final List getUniqueAttributes()
	{
		return uniqueAttributeList;
	}
	
	final void materialize(final Database database)
	{
		if(this.databaseID!=null)
			throw new RuntimeException();

		this.databaseID = database.trimName(getType().getID()+"_"+getName()+"_Unq").intern();
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
			final Attribute uniqueAttribute = uniqueAttributes[i];
			bf.append(uniqueAttribute.getMainColumn().protectedID);
		}
		bf.append(')');

		new com.exedio.dsmf.UniqueConstraint(dsmfTable, getDatabaseID(), bf.toString());
	}
	

	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		//buf.append(super.toString());
		buf.append("unique(");
		buf.append(uniqueAttributes[0].getName());
		for(int i = 1; i<uniqueAttributes.length; i++)
		{
			buf.append(',');
			buf.append(uniqueAttributes[i].getName());
		}
		buf.append(')');
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
	public final Item searchUnique(final Object[] values)
	{
		// TODO: search nativly for unique constraints
		final List attributes = getUniqueAttributes();
		if(attributes.size()!=values.length)
			throw new RuntimeException();

		final Iterator attributeIterator = attributes.iterator();
		final Condition[] conditions = new Condition[attributes.size()];
		for(int j = 0; attributeIterator.hasNext(); j++)
			conditions[j] = new EqualCondition((ObjectAttribute)attributeIterator.next(), values[j]);

		return getType().searchUnique(new AndCondition(conditions));
	}
	
}
