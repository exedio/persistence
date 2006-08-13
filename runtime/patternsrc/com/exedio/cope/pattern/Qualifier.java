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
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
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
	private final FunctionAttribute<?>[] keys;
	private final List<FunctionAttribute<?>> keyList;
	private final UniqueConstraint uniqueConstraint;

	public Qualifier(final UniqueConstraint uniqueConstraint)
	{
		if(uniqueConstraint==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class initialization order.");
		
		final List<FunctionAttribute<?>> uniqueAttributes = uniqueConstraint.getUniqueAttributes();
		if(uniqueAttributes.size()<2)
			throw new RuntimeException(uniqueAttributes.toString());

		this.parent = castItemAttribute(uniqueAttributes.get(0));
		this.keys = new FunctionAttribute<?>[uniqueAttributes.size()-1];
		for(int i = 0; i<this.keys.length; i++)
			this.keys[i] = uniqueAttributes.get(i+1);
		this.keyList = Collections.unmodifiableList(Arrays.asList(this.keys));
		this.uniqueConstraint = uniqueConstraint;

		for(final FunctionAttribute uniqueAttribute : uniqueAttributes)
			registerSource(uniqueAttribute);
	}

	@SuppressWarnings("unchecked") // OK: UniqueConstraint looses type information
	private static final ItemAttribute<Item> castItemAttribute(final Attribute a)
	{
		return (ItemAttribute<Item>)a;
	}
	
	// TODO implicit external source: new Qualifier(QualifiedStringQualifier.key))
	// TODO internal source: new Qualifier(stringAttribute(OPTIONAL))

	public ItemAttribute<Item> getParent()
	{
		return parent;
	}

	public List<FunctionAttribute<?>> getKeys()
	{
		return keyList;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return uniqueConstraint;
	}
	
	// second initialization phase ---------------------------------------------------

	private List<Feature> features;
	
	public List<Feature> getFeatures()
	{
		if(this.features==null)
		{
			final Type<?> type = getType();
			final Type.This<?> typeThis = type.getThis();
			final List<Feature> typeFeatures = type.getFeatures();
			final ArrayList<Feature> result = new ArrayList<Feature>(typeFeatures.size());
			for(final Feature f : typeFeatures)
			{
				if(f!=typeThis && f!=this && f!=parent && !keyList.contains(f) && f!=uniqueConstraint)
					result.add(f);
			}
			result.trimToSize();
			this.features = Collections.unmodifiableList(result);
		}
		return features;
	}

	private List<Attribute> attributes;

	public List<Attribute> getAttributes()
	{
		if(this.attributes==null)
		{
			final List<Feature> features = getFeatures();
			final ArrayList<Attribute> result = new ArrayList<Attribute>(features.size());
			for(final Feature f : features)
			{
				if(f instanceof Attribute)
					result.add((Attribute)f);
			}
			result.trimToSize();
			this.attributes = Collections.unmodifiableList(result);
		}
		return attributes;
	}

	public Item getQualifier(final Object[] keys)
	{
		return uniqueConstraint.searchUnique(keys);
	}
	
	public <X> X get(final FunctionAttribute<X> attribute, final Object[] keys)
	{
		final Item item = uniqueConstraint.searchUnique(keys);
		if(item!=null)
			return attribute.get(item);
		else
			return null;
	}
	
	public Item getForSet(final Object[] keys)
	{
		Item item = uniqueConstraint.searchUnique(keys);
		if(item==null)
		{
			final SetValue[] keySetValues = new SetValue[keys.length];
			int j = 0;
			for(final FunctionAttribute<?> uniqueAttribute : uniqueConstraint.getUniqueAttributes())
				keySetValues[j] = Cope.mapAndCast(uniqueAttribute, keys[j++]);
			
			item = uniqueConstraint.getType().newItem(keySetValues);
		}
		return item;
	}
	
	public <X> void set(final FunctionAttribute<X> attribute, final X value, final Object[] keys)
	{
		final Item item = getForSet(keys);
		attribute.set(item, value);
	}

	public Item set(final Object[] keys, final SetValue[] values)
	{
		Item item = uniqueConstraint.searchUnique(keys);
		
		if(item==null)
		{
			final SetValue[] keyValues = new SetValue[values.length + keys.length];
			System.arraycopy(values, 0, keyValues, 0, values.length);
			
			int j = 0;
			for(final FunctionAttribute<?> uniqueAttribute : uniqueConstraint.getUniqueAttributes())
				keyValues[j + values.length] = Cope.mapAndCast(uniqueAttribute, keys[j++]);
			
			item = uniqueConstraint.getType().newItem(keyValues);
		}
		else
		{
			item.set(values);
		}
		
		return item;
	}
	
	// static convenience methods ---------------------------------

	private static final HashMap<Type<?>, List<Qualifier>> cacheForGetQualifiers = new HashMap<Type<?>, List<Qualifier>>();
	
	/**
	 * Returns all qualifiers where <tt>type</tt> is
	 * the parent type {@link #getParent()}.{@link ItemAttribute#getValueType() getValueType()}.
	 *
	 * @see Relation#getRelations(Type)
	 * @see VectorRelation#getRelations(Type)
	 */
	public static final List<Qualifier> getQualifiers(final Type<?> type)
	{
		synchronized(cacheForGetQualifiers)
		{
			{
				final List<Qualifier> cachedResult = cacheForGetQualifiers.get(type);
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
						if(ia==qualifier.parent)
							resultModifiable.add(qualifier);
					}
				}
			resultModifiable.trimToSize();
			
			final List<Qualifier> result =
				!resultModifiable.isEmpty()
				? Collections.unmodifiableList(resultModifiable)
				: Collections.<Qualifier>emptyList();
			cacheForGetQualifiers.put(type, result);
			return result;
		}
	}

}
