/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.ItemField.DeletePolicy;

public abstract class Composite implements Serializable
{
	static final class Type<X>
	{
		final Constructor<X> constructor;
		final LinkedHashMap<String, FunctionField> templates = new LinkedHashMap<String, FunctionField>();
		final HashMap<FunctionField, Integer> templatePositions = new HashMap<FunctionField, Integer>();
		final List<FunctionField> templateList;
		final int componentSize;
		
		Type(final Class<X> valueClass)
		{
			//System.out.println("---------------new Composite.Type(" + vc + ')');
			try
			{
				constructor = valueClass.getDeclaredConstructor(SetValue[].class);
			}
			catch(NoSuchMethodException e)
			{
				throw new IllegalArgumentException(
						valueClass.getName() + " does not have a constructor " +
						valueClass.getSimpleName() + '(' + SetValue.class.getName() + "[])", e);
			}
			constructor.setAccessible(true);
			
			try
			{
				int position = 0;
				for(final java.lang.reflect.Field field : valueClass.getDeclaredFields())
				{
					if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
						continue;
					if(!Feature.class.isAssignableFrom(field.getType()))
						continue;
					
					field.setAccessible(true);
					final Feature feature = (Feature)field.get(null);
					if(feature==null)
						throw new NullPointerException(valueClass.getName() + '#' + field.getName());
					if(!(feature instanceof FunctionField))
						throw new IllegalArgumentException(valueClass.getName() + '#' + field.getName() + " must be an instance of " + FunctionField.class);
					final FunctionField template = (FunctionField)feature;
					if(template.isFinal())
						throw new IllegalArgumentException("final fields not supported: " + valueClass.getName() + '#' + field.getName());
					templates.put(field.getName(), template);
					templatePositions.put(template, position++);
				}
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(valueClass.getName(), e);
			}
			this.templateList = Collections.unmodifiableList(new ArrayList<FunctionField>(templates.values()));
			this.componentSize = templates.size();
		}
	}
	
	private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;
	
	private static final HashMap<Class, Type> types = new HashMap<Class, Type>();

	@SuppressWarnings("unchecked")
	static final <E> Type<E> getType(final Class valueClass)
	{
		assert valueClass!=null;
		
		synchronized(types)
		{
			Type<E> result = types.get(valueClass);
			if(result==null)
			{
				result = new Type(valueClass);
				types.put(valueClass, result);
			}
			return result;
		}
	}
	
	private static final long serialVersionUID = 1l;
	
	private final Object[] values;

	protected Composite(final SetValue... setValues)
	{
		for(final SetValue<?> v : setValues)
			check(v);
		final Type<?> type = type();
		values = new Object[type.componentSize];
		for(final SetValue v : setValues)
		{
			final Integer position = type.templatePositions.get(v.settable);
			if(position==null)
				throw new IllegalArgumentException("not a member");
			
			values[position.intValue()] = v.value;
		}
	}

	private static final <E> void check(final SetValue<E> setValue)
	{
		((Field<E>)setValue.settable).check(setValue.value);
	}
	
	@SuppressWarnings("unchecked")
	public final <X> X get(final FunctionField<X> member)
	{
		return (X)values[position(member)];
	}
	
	public final int getMandatory(final IntegerField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");
		
		return (Integer)values[position(member)];
	}
	
	public final long getMandatory(final LongField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");
		
		return (Long)values[position(member)];
	}
	
	public final double getMandatory(final DoubleField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");
		
		return (Double)values[position(member)];
	}
	
	public final boolean getMandatory(final BooleanField member)
	{
		if(!member.isMandatory())
			throw new IllegalArgumentException("member is not mandatory");
		
		return (Boolean)values[position(member)];
	}
	
	public final <X> void set(final FunctionField<X> member, final X value)
	{
		member.check(value);
		values[position(member)] = value;
	}
	
	
	private transient Type<?> typeIfSet = null;
	
	private final Type<?> type()
	{
		Type<?> typeIfSet = this.typeIfSet;
		if(typeIfSet!=null)
			return typeIfSet;
		typeIfSet = getType(getClass());
		this.typeIfSet = typeIfSet;
		return typeIfSet;
	}

	private final int position(final FunctionField<?> member)
	{
		final Type<?> type = type();
		final Integer result = type.templatePositions.get(member);
		if(result==null)
			throw new IllegalArgumentException("not a member");
		return result.intValue();
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(this==other)
			return true;
		
		return
			other!=null &&
			getClass().equals(other.getClass()) &&
			Arrays.equals(values, ((Composite)other).values);
	}
	
	@Override
	public final int hashCode()
	{
		return getClass().hashCode() ^ Arrays.hashCode(values);
	}
	
	// convenience for subclasses --------------------------------------------------
	
	public static final <E extends Enum<E>> EnumField<E> newEnumField(final Class<E> valueClass)
	{
		return Item.newEnumField(valueClass);
	}
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass)
	{
		return Item.newItemField(valueClass);
	}
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		return Item.newItemField(valueClass, policy);
	}
}
