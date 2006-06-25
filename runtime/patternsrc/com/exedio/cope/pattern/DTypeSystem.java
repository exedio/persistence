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

import java.util.List;

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.Cope;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringAttribute;

public final class DTypeSystem extends Pattern
{
	private final ItemAttribute<DType> type;
	private final FunctionAttribute<?>[] attributes;

	private final StringAttribute[]  strings;
	private final BooleanAttribute[] booleans;
	private final IntegerAttribute[] integers;
	private final DoubleAttribute[]  doubles;
	private final ItemAttribute<DEnumValue>[]  enums;
	
	public DTypeSystem(
			final int stringCapacity,
			final int booleanCapacity,
			final int integerCapacity,
			final int doubleCapacity,
			final int enumCapacity)
	{
		strings  = new StringAttribute[stringCapacity];
		booleans = new BooleanAttribute[booleanCapacity];
		integers = new IntegerAttribute[integerCapacity];
		doubles  = new DoubleAttribute [doubleCapacity];
		enums    = castEnumArray(new ItemAttribute[enumCapacity]);
		attributes = new FunctionAttribute[strings.length + booleans.length + integers.length + doubles.length + enums.length];

		registerSource(type = new ItemAttribute<DType>(Item.OPTIONAL, DType.class));
		int n = 0;
		for(int i = 0; i<strings.length; i++)
			registerSource(attributes[n++] = strings [i] = new StringAttribute(Item.OPTIONAL));
		for(int i = 0; i<booleans.length; i++)
			registerSource(attributes[n++] = booleans[i] = new BooleanAttribute(Item.OPTIONAL));
		for(int i = 0; i<integers.length; i++)
			registerSource(attributes[n++] = integers[i] = new IntegerAttribute(Item.OPTIONAL));
		for(int i = 0; i<doubles.length; i++)
			registerSource(attributes[n++] = doubles [i] = new DoubleAttribute(Item.OPTIONAL));
		for(int i = 0; i<enums.length; i++)
			registerSource(attributes[n++] = enums   [i] = new ItemAttribute<DEnumValue>(Item.OPTIONAL, DEnumValue.class));
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private static final ItemAttribute<DEnumValue>[] castEnumArray(final ItemAttribute[] o)
	{
		return (ItemAttribute<DEnumValue>[])o;
	}

	private FunctionAttribute<?>[] array(final DAttribute.ValueType valueType)
	{
		switch(valueType)
		{
			case STRING:  return strings;
			case BOOLEAN: return booleans;
			case INTEGER: return integers;
			case DOUBLE:  return doubles;
			case ENUM:    return enums;
			default:
				throw new RuntimeException(valueType.toString());
		}
	}
	
	void assertCapacity(final DAttribute.ValueType valueType, final int positionPerValuetype)
	{
		final int capacity = array(valueType).length;
		if(capacity<=positionPerValuetype)
			throw new RuntimeException("capacity for " + valueType + " exceeded, " + capacity + " available, but tried to allocate " + (positionPerValuetype+1));
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		initialize(type, name + "Type");

		for(final DAttribute.ValueType valueType : DAttribute.ValueType.values())
		{
			final FunctionAttribute<?>[] array = array(valueType);
			final String postfix = valueType.postfix;
			for(int i = 0; i<array.length; i++)
				initialize(array[i], name + postfix + (i+1/*TODO: make this '1' customizable*/));
		}
	}
	
	public DType createType(final String code)
	{
		return new DType(this, code);
	}
	
	public List<DType> getTypes()
	{
		return DType.TYPE.search(
				DType.parentTypeId.equal(getType().getID()).and(
				DType.dtypeSystemName.equal(getName())));
	}
	
	public DType getType(final String code)
	{
		return DType.TYPE.searchSingleton(
				DType.parentTypeId.equal(getType().getID()).and(
				DType.dtypeSystemName.equal(getName())).and(
				DType.code.equal(code)));
	}
	
	public DType getType(final Item item)
	{
		return this.type.get(item);
	}
	
	public void setType(final Item item, final DType type)
	{
		if(type!=null && !this.equals(type.getDtypeSystem()))
			throw new RuntimeException("dynamic type system mismatch: new type has system " + type.getDtypeSystem() + ", but must be " + toString());
		
		final SetValue[] values = new SetValue[1+attributes.length];
		values[0] = this.type.map(type);
		for(int i = 0; i<attributes.length; i++)
			values[1+i] = attributes[i].map(null);
		item.set(values);
	}
	
	private void assertType(final DAttribute attribute, final Item item)
	{
		final DType attributeType = attribute.getParent();
		final DType itemType = type.get(item);
		if(!attributeType.equals(itemType))
			throw new RuntimeException("dynamic type mismatch: attribute has type " + attributeType.getCode() + ", but item has " + (itemType!=null ? itemType.getCode() : "none"));
	}
	
	private FunctionAttribute<?> getAttribute(final DAttribute attribute)
	{
		final DAttribute.ValueType valueType = attribute.getValueType();
		final int pos = attribute.getPositionPerValueType();

		final FunctionAttribute[] array = array(valueType);
		
		// make a more verbose exception instead
		// of the ArrayIndexOutOfBoundException
		// thrown by the last line.
		final int capacity = array.length;
		if(capacity<=pos)
			throw new RuntimeException("accessing " + attribute + " exceeded capacity for " + valueType + ", " + capacity + " available, but tried to access " + (pos+1));

		return array[pos];
	}
	
	public Object get(final DAttribute attribute, final Item item)
	{
		assertType(attribute, item);
		return getAttribute(attribute).get(item);
	}
	
	public void set(final DAttribute attribute, final Item item, final Object value)
	{
		assertType(attribute, item);
		
		if(value!=null &&
			value instanceof DEnumValue &&
			attribute.getValueType()==DAttribute.ValueType.ENUM)
		{
			final DEnumValue enumValue = (DEnumValue)value;
			final DAttribute enumValueParent = enumValue.getParent();
			if(!enumValueParent.equals(attribute))
				throw new RuntimeException("dynamic type system mismatch: enum value " + enumValue + " has type " + enumValueParent + ", but must be " + attribute);
		}
		
		Cope.setAndCast(getAttribute(attribute), item, value);
	}
	
}
