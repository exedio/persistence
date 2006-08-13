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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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

public final class AttributeMap extends Pattern
{
	private final Class<?> attributesDefinition;
	private ItemAttribute<?> parent = null;
	private final FunctionAttribute<?> key;
	private UniqueConstraint uniqueConstraint = null;
	private Type<?> relationType = null;
	private List<Feature> features;
	private List<Attribute> attributes;

	public AttributeMap(final FunctionAttribute<?> key, final Class<?> attributesDefinition)
	{
		this.attributesDefinition = attributesDefinition;
		this.key = key;
		if(attributesDefinition==null)
			throw new NullPointerException("attributesDefinition must not be null");
		if(key==null)
			throw new NullPointerException("key must not be null");
		if(key.getImplicitUniqueConstraint()!=null)
			throw new NullPointerException("key must not be unique");
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		parent = Item.newItemAttribute(Attribute.Option.FINAL, type.getJavaClass(), ItemAttribute.DeletePolicy.CASCADE);
		uniqueConstraint = new UniqueConstraint(parent, key);
		final LinkedHashMap<String, Feature> relationTypeFeatures = new LinkedHashMap<String, Feature>();
		relationTypeFeatures.put("parent", parent);
		relationTypeFeatures.put("key", key);
		relationTypeFeatures.put("uniqueConstraint", uniqueConstraint);

		// TODO SOON duplicates Type#getFeatureMap
		final Field[] fields = attributesDefinition.getDeclaredFields();
		final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
		final ArrayList<Feature> features = new ArrayList<Feature>(fields.length);
		final ArrayList<Attribute> attributes = new ArrayList<Attribute>(fields.length);
		try
		{
			for(final Field field : fields)
			{
				if((field.getModifiers()&expectedModifier)==expectedModifier)
				{
					final Class fieldType = field.getType();
					if(Feature.class.isAssignableFrom(fieldType))
					{
						field.setAccessible(true);
						final Feature feature = (Feature)field.get(null);
						if(feature==null)
							throw new RuntimeException(field.getName());
						relationTypeFeatures.put(field.getName(), feature);
						features.add(feature);
						if(feature instanceof Attribute)
							attributes.add((Attribute)feature);
					}
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		
		this.relationType = newType(relationTypeFeatures);

		features.trimToSize();
		attributes.trimToSize();
		this.features = Collections.unmodifiableList(features);
		this.attributes = Collections.unmodifiableList(attributes);
	}
	
	public ItemAttribute<?> getParent()
	{
		assert parent!=null;
		return parent;
	}
	
	public FunctionAttribute<?> getKey()
	{
		return key;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		assert uniqueConstraint!=null;
		return uniqueConstraint;
	}
	
	public Type<?> getRelationType()
	{
		assert relationType!=null;
		return relationType;
	}
	
	public List<Feature> getFeatures()
	{
		return features;
	}

	public List<Attribute> getAttributes()
	{
		return attributes;
	}

	public <X> X get(final FunctionAttribute<X> attribute, final Object... keys)
	{
		final Item item = uniqueConstraint.searchUnique(keys);
		if(item!=null)
			return attribute.get(item);
		else
			return null;
	}
	
	private Item getForSet(final Object... keys)
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
	
	public <X> void set(final FunctionAttribute<X> attribute, final X value, final Object... keys)
	{
		final Item item = getForSet(keys);
		attribute.set(item, value);
	}

}
