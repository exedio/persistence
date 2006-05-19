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

package com.exedio.cope.dtype;

import java.util.List;

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
	private final StringAttribute[] strings;
	private final IntegerAttribute[] integers;
	private final FunctionAttribute<?>[] attributes;

	public DTypeSystem()
	{
		strings = new StringAttribute[5];
		integers = new IntegerAttribute[8];
		attributes = new FunctionAttribute[strings.length+integers.length];

		registerSource(type = new ItemAttribute<DType>(Item.OPTIONAL, DType.class));
		int n = 0;
		for(int i = 0; i<strings.length; i++)
			registerSource(attributes[n++] = strings[i] = new StringAttribute(Item.OPTIONAL));
		for(int i = 0; i<integers.length; i++)
			registerSource(attributes[n++] = integers[i] = new IntegerAttribute(Item.OPTIONAL));
	}

	public void initialize()
	{
		final String name = getName();
		
		initialize(type, name + "Type");
		for(int i = 0; i<strings.length; i++)
			initialize(strings[i], name + "String" + (i+1/*TODO: make this '1' customizable*/));
		for(int i = 0; i<integers.length; i++)
			initialize(integers[i], name + "Int" + (i+1/*TODO: make this '1' customizable*/));
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
	
	public DType getType(final Item item)
	{
		return this.type.get(item);
	}
	
	public void setType(final Item item, final DType type)
	{
		if(type!=null && !this.equals(type.getDtypeSystem()))
			throw new RuntimeException("dynamic type system mismatch: new type has system " + type.getDtypeSystem() + ", but mut be " + toString());
		
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
	
	public Object get(final DAttribute attribute, final Item item)
	{
		assertType(attribute, item);
		final int pos = attribute.getPositionPerValueType();
		switch(attribute.getValueType())
		{
			case STRING:  return strings [pos].get(item);
			case INTEGER: return integers[pos].get(item);
			default:
				throw new RuntimeException(attribute.getValueType().toString());
		}
	}
	
	public void set(final DAttribute attribute, final Item item, final Object value)
	{
		assertType(attribute, item);
		final int pos = attribute.getPositionPerValueType();
		switch(attribute.getValueType())
		{
			case STRING:  strings [pos].set(item, (String) value); break;
			case INTEGER: integers[pos].set(item, (Integer)value); break;
			default:
				throw new RuntimeException(attribute.getValueType().toString());
		}
	}
	
}
