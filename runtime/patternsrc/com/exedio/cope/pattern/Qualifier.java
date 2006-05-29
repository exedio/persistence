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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.Attribute;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class Qualifier extends Pattern
{
	private final ItemAttribute<Item> parent;
	private final FunctionAttribute[] keys;
	private final List<FunctionAttribute> keyList;
	private final UniqueConstraint qualifyUnique; // TODO SOON rename to uniqueConstraint
	private List<Attribute> attributes;

	public Qualifier(final UniqueConstraint qualifyUnique)
	{
		if(qualifyUnique==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class initialization order.");
		
		final List<FunctionAttribute<?>> attributes = qualifyUnique.getUniqueAttributes();
		if(attributes.size()<2)
			throw new RuntimeException(attributes.toString());

		this.parent = castItemAttribute(attributes.get(0));
		this.keys = new FunctionAttribute[attributes.size()-1];
		for(int i = 0; i<this.keys.length; i++)
			this.keys[i] = attributes.get(i+1);
		this.keyList = Collections.unmodifiableList(Arrays.asList(this.keys));
		this.qualifyUnique = qualifyUnique;

		for(final FunctionAttribute attribute : attributes)
			registerSource(attribute);
	}

	@SuppressWarnings("unchecked") // OK: UniqueConstraint looses type information
	private static final ItemAttribute<Item> castItemAttribute(final Attribute a)
	{
		return (ItemAttribute<Item>)a;
	}
	
	// TODO SOON implicit external source: new Qualifier(QualifiedStringQualifier.key))
	// TODO SOON internal source: new Qualifier(stringAttribute(OPTIONAL))
	// TODO SOON use registerPattern on sources

	public ItemAttribute<Item> getParent()
	{
		return parent;
	}

	public List<FunctionAttribute> getKeys()
	{
		return keyList;
	}

	public UniqueConstraint getQualifyUnique() // TODO SOON rename to getUniqueConstraint
	{
		return qualifyUnique;
	}
	
	// second initialization phase ---------------------------------------------------

	public void initialize()
	{
		if(this.attributes!=null)
			throw new RuntimeException();

		final Type<?> type = getType();
		final Type<?> uniqueConstraintType = qualifyUnique.getType();
		if(!type.equals(uniqueConstraintType))
			throw new RuntimeException("unique contraint for qualifier must be declared on the same type as the qualifier itself, expected " + type.getID() + ", but got " + uniqueConstraintType.getID());
		// TODO SOON do this in all patterns and make a method for this
	}

	public List<Attribute> getAttributes()
	{
		if(this.attributes==null)
		{
			final List<Attribute> typeAttributes = getType().getAttributes();
			final ArrayList<Attribute> attributesModifiyable = new ArrayList<Attribute>(typeAttributes.size());
			for(final Attribute attribute : typeAttributes)
			{
				if(attribute!=parent && !keyList.contains(attribute))
					attributesModifiyable.add(attribute);
			}
			this.attributes = Collections.unmodifiableList(attributesModifiyable);
		}

		return attributes;
	}

	public Item getQualifier(final Object[] values)
	{
		return qualifyUnique.searchUnique(values);
	}
	
	public Object get(final Object[] values, final FunctionAttribute attribute)
	{
		final Item item = qualifyUnique.searchUnique(values);
		if(item!=null)
			return attribute.get(item);
		else
			return null;
	}
	
	public Item getForSet(final Object[] keys)
	{
		Item item = qualifyUnique.searchUnique(keys);
		if(item==null)
		{
			final SetValue[] keySetValues = new SetValue[keys.length];
			int j = 0;
			for(final FunctionAttribute uniqueAttribute : qualifyUnique.getUniqueAttributes())
				keySetValues[j] = new SetValue(uniqueAttribute, keys[j++]);
			
			item = qualifyUnique.getType().newItem(keySetValues);
		}
		return item;
	}

	public Item set(final Object[] keys, final SetValue[] values)
	{
		Item item = qualifyUnique.searchUnique(keys);
		
		if(item==null)
		{
			final SetValue[] keyValues = new SetValue[values.length + keys.length];
			System.arraycopy(values, 0, keyValues, 0, values.length);
			
			int j = 0;
			for(final FunctionAttribute uniqueAttribute : qualifyUnique.getUniqueAttributes())
				keyValues[j + values.length] = new SetValue(uniqueAttribute, keys[j++]);
			
			item = qualifyUnique.getType().newItem(keyValues);
		}
		else
		{
			item.set(values);
		}
		
		return item;
	}
	
	private static final HashMap<Type<?>, List<Qualifier>> qualifiers = new HashMap<Type<?>, List<Qualifier>>();
	
	/**
	 * Returns all qualifiers where <tt>type</tt> is
	 * the parent type {@link #getParent()()}.{@link ItemAttribute#getValueType() getValueType()}.
	 * 
	 * @see Relation#getRelations(Type)
	 */
	public static final List<Qualifier> getQualifiers(final Type<?> type)
	{
		synchronized(qualifiers)
		{
			{
				final List<Qualifier> cachedResult = qualifiers.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}
			
			final ArrayList<Qualifier> resultModifiable = new ArrayList<Qualifier>();
			
			for(final ItemAttribute<?> ia : type.getReferences())
				for(final Pattern pattern : ia.getPatterns())
				{
					if(pattern instanceof Qualifier)
					{
						final Qualifier qualifier = (Qualifier)pattern;
						if(ia==qualifier.getParent())
							resultModifiable.add(qualifier);
					}
				}
			resultModifiable.trimToSize();
			
			final List<Qualifier> result =
				!resultModifiable.isEmpty()
				? Collections.unmodifiableList(resultModifiable)
				: Collections.<Qualifier>emptyList();
			qualifiers.put(type, result);
			return result;
		}
	}

}
