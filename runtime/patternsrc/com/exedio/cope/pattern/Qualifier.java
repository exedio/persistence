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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.Field;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class Qualifier extends Pattern
{
	private final ItemField<Item> parent;
	private final FunctionField<?>[] keys;
	private final List<FunctionField<?>> keyList;
	private final UniqueConstraint uniqueConstraint;

	public Qualifier(final UniqueConstraint uniqueConstraint)
	{
		if(uniqueConstraint==null)
			throw new RuntimeException(
				"argument of qualifier constructor is null, " +
				"may happen due to bad class initialization order.");
		
		final List<FunctionField<?>> uniqueFields = uniqueConstraint.getFields();
		if(uniqueFields.size()<2)
			throw new RuntimeException(uniqueFields.toString());

		this.parent = castItemField(uniqueFields.get(0));
		this.keys = new FunctionField<?>[uniqueFields.size()-1];
		for(int i = 0; i<this.keys.length; i++)
			this.keys[i] = uniqueFields.get(i+1);
		this.keyList = Collections.unmodifiableList(Arrays.asList(this.keys));
		this.uniqueConstraint = uniqueConstraint;

		for(final FunctionField field : uniqueFields)
			registerSource(field);
	}

	@SuppressWarnings("unchecked") // OK: UniqueConstraint looses type information
	private static final ItemField<Item> castItemField(final Field f)
	{
		return (ItemField<Item>)f;
	}
	
	// TODO implicit external source: new Qualifier(QualifiedStringQualifier.key))
	// TODO internal source: new Qualifier(new StringField(OPTIONAL))

	public ItemField<Item> getParent()
	{
		return parent;
	}

	public List<FunctionField<?>> getKeys()
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

	private List<Field> fields;

	public List<Field> getFields()
	{
		if(this.fields==null)
		{
			final List<Feature> features = getFeatures();
			final ArrayList<Field> result = new ArrayList<Field>(features.size());
			for(final Feature f : features)
			{
				if(f instanceof Field)
					result.add((Field)f);
			}
			result.trimToSize();
			this.fields = Collections.unmodifiableList(result);
		}
		return fields;
	}

	/**
	 * @deprecated renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<Field> getAttributes()
	{
		return getFields();
	}
	
	public Item getQualifier(final Object... keys)
	{
		return uniqueConstraint.searchUnique(keys);
	}
	
	public <X> X get(final FunctionField<X> field, final Object... keys)
	{
		final Item item = uniqueConstraint.searchUnique(keys);
		if(item!=null)
			return field.get(item);
		else
			return null;
	}
	
	public Item getForSet(final Object... keys)
	{
		Item item = uniqueConstraint.searchUnique(keys);
		if(item==null)
		{
			final SetValue[] keySetValues = new SetValue[keys.length];
			int j = 0;
			for(final FunctionField<?> field : uniqueConstraint.getFields())
				keySetValues[j] = Cope.mapAndCast(field, keys[j++]);
			
			item = uniqueConstraint.getType().newItem(keySetValues);
		}
		return item;
	}
	
	public <X> void set(final FunctionField<X> field, final X value, final Object... keys)
	{
		final Item item = getForSet(keys);
		field.set(item, value);
	}

	public Item set(final Object[] keys, final SetValue[] values)
	{
		Item item = uniqueConstraint.searchUnique(keys);
		
		if(item==null)
		{
			final SetValue[] keyValues = new SetValue[values.length + keys.length];
			System.arraycopy(values, 0, keyValues, 0, values.length);
			
			int j = 0;
			for(final FunctionField<?> field : uniqueConstraint.getFields())
				keyValues[j + values.length] = Cope.mapAndCast(field, keys[j++]);
			
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
	private static final HashMap<Type<?>, List<Qualifier>> cacheForGetDeclaredQualifiers = new HashMap<Type<?>, List<Qualifier>>();
	
	/**
	 * Returns all qualifiers where <tt>type</tt> or any of it's super types is
	 * the parent type {@link #getParent()}.{@link ItemField#getValueType() getValueType()}.
	 *
	 * @see #getDeclaredQualifiers(Type)
	 * @see Relation#getRelations(Type)
	 * @see VectorRelation#getRelations(Type)
	 */
	public static final List<Qualifier> getQualifiers(final Type<?> type)
	{
		return getQualifiers(false, cacheForGetQualifiers, type);
	}

	/**
	 * Returns all qualifiers where <tt>type</tt> is
	 * the parent type {@link #getParent()}.{@link ItemField#getValueType() getValueType()}.
	 *
	 * @see #getQualifiers(Type)
	 * @see Relation#getDeclaredRelations(Type)
	 * @see VectorRelation#getDeclaredRelations(Type)
	 */
	public static final List<Qualifier> getDeclaredQualifiers(final Type<?> type)
	{
		return getQualifiers(true, cacheForGetDeclaredQualifiers, type);
	}
	
	private static final List<Qualifier> getQualifiers(final boolean declared, final HashMap<Type<?>, List<Qualifier>> cache, final Type<?> type)
	{
		synchronized(cache)
		{
			{
				final List<Qualifier> cachedResult = cache.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}
			
			final ArrayList<Qualifier> resultModifiable = new ArrayList<Qualifier>();
			
			for(final ItemField<?> ia : declared ? type.getDeclaredReferences() : type.getReferences())
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
			cache.put(type, result);
			return result;
		}
	}

}
