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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Attribute;
import com.exedio.cope.AttributeValue;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class Qualifier extends Pattern
{
	private final ItemAttribute parent;
	private final FunctionAttribute[] keys;
	private final List keyList;
	private final UniqueConstraint qualifyUnique;
	private List attributes;

	public Qualifier(final UniqueConstraint qualifyUnique)
	{
		if(qualifyUnique==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class intialization order.");
		
		final List attributes = qualifyUnique.getUniqueAttributes();
		if(attributes.size()<2)
			throw new RuntimeException(attributes.toString());

		this.parent = (ItemAttribute)attributes.get(0);
		this.keys = new FunctionAttribute[attributes.size()-1];
		for(int i = 0; i<this.keys.length; i++)
			this.keys[i] = (FunctionAttribute)attributes.get(i+1);
		this.keyList = Collections.unmodifiableList(Arrays.asList(this.keys));
		this.qualifyUnique = qualifyUnique;
	}
	
	// TODO implicit external source: new Qualifier(QualifiedStringQualifier.key))
	// TODO internal source: new Qualifier(stringAttribute(OPTIONAL))
	// TODO use registerPattern on sources

	public final ItemAttribute getParent()
	{
		return parent;
	}

	/**
	 * @return a list of {@link FunctionAttribute}s.
	 */
	public final List getKeys()
	{
		return keyList;
	}

	public final UniqueConstraint getQualifyUnique()
	{
		return qualifyUnique;
	}
	
	// second initialization phase ---------------------------------------------------

	public void initialize()
	{
		if(this.attributes!=null)
			throw new RuntimeException();

		final Type type = qualifyUnique.getType();
		final ArrayList attributesModifiyable = new ArrayList(type.getAttributes().size());
		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute!=parent && !keyList.contains(attribute))
				attributesModifiyable.add(attribute);
		}
		this.attributes = Collections.unmodifiableList(attributesModifiyable);
	}

	public final List getAttributes()
	{
		if(this.attributes==null)
			throw new RuntimeException();

		return attributes;
	}

	public final Item getQualifier(final Object[] values)
	{
		return qualifyUnique.searchUnique(values);
	}
	
	public final Object get(final Object[] values, final FunctionAttribute attribute)
	{
		final Item item = qualifyUnique.searchUnique(values);
		if(item!=null)
			return attribute.getObject(item);
		else
			return null;
	}
	
	public Item getForSet(final Object[] values)
	{
		Item item = qualifyUnique.searchUnique(values);
		if(item==null)
		{
			final AttributeValue[] initialAttributeValues = new AttributeValue[values.length];
			int j = 0;
			for(Iterator i = qualifyUnique.getUniqueAttributes().iterator(); i.hasNext(); j++)
			{
				final FunctionAttribute uniqueAttribute = (FunctionAttribute)i.next();
				initialAttributeValues[j] = new AttributeValue(uniqueAttribute, values[j]);
			}
			item = qualifyUnique.getType().newItem(initialAttributeValues);
		}
		return item;
	}

}
