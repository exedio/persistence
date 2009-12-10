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
import java.util.Arrays;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.ItemField.DeletePolicy;

public abstract class Composite implements Serializable
{
	private static final long serialVersionUID = 1l;
	
	private final Object[] values;

	protected Composite(final SetValue... setValues)
	{
		final CompositeType<?> type = type();
		values = new Object[type.componentSize];
		for(final SetValue v : setValues)
		{
			final Integer position = type.templatePositions.get(v.settable);
			if(position==null)
				throw new IllegalArgumentException("not a member");
			
			values[position.intValue()] = v.value;
		}
		int i = 0;
		for(final FunctionField ff : type.templateList)
			check(ff, values[i++]);
	}

	@SuppressWarnings("unchecked")
	private static final <E> void check(final FunctionField field, final Object value)
	{
		field.check(value);
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
	
	
	private transient CompositeType<?> typeIfSet = null;
	
	private final CompositeType<?> type()
	{
		CompositeType<?> typeIfSet = this.typeIfSet;
		if(typeIfSet!=null)
			return typeIfSet;
		typeIfSet = CompositeType.get(getClass());
		this.typeIfSet = typeIfSet;
		return typeIfSet;
	}

	private final int position(final FunctionField<?> member)
	{
		final CompositeType<?> type = type();
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
