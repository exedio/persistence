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
import java.util.Collections;
import java.util.List;

import com.exedio.cope.Cope;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;

public final class DAttribute
{
	private final DTypeSystem system;
	final Item backingItem;
	
	public static enum ValueType
	{
		STRING (String.class,     "String"),
		BOOLEAN(Boolean.class,    "Bool"),
		INTEGER(Integer.class,    "Int"),
		DOUBLE (Double.class,     "Double"),
		ENUM   (DEnumValue.class, "Enum");
		
		final Class valueClass;
		final String postfix;
		
		ValueType(final Class valueClass, final String postfix)
		{
			this.valueClass = valueClass;
			this.postfix = postfix;
		}
		
		public final Class getValueClass()
		{
			return valueClass;
		}
	}

	public Object get(final Item item)
	{
		return getParent().getDtypeSystem().get(item, this);
	}
	
	public void set(final Item item, final Object value)
	{
		getParent().getDtypeSystem().set(item, this, value);
	}
	
	private void assertEnum()
	{
		final ValueType vt = getValueType();
		if(vt!=ValueType.ENUM)
			throw new IllegalArgumentException("operation allowed for getValueType()==ENUM attributes only, but was " + vt);
	}
	
	public List<DEnumValue> getEnumValues()
	{
		assertEnum();
		final List<? extends Item> backingItems = system.enumValueType.search(Cope.equalAndCast(system.enumValueParent, backingItem), system.enumValuePosition, true);
		final ArrayList<DEnumValue> result = new ArrayList<DEnumValue>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new DEnumValue(system, backingItem));
		return Collections.unmodifiableList(result);
	}
	
	public DEnumValue getEnumValue(final String code)
	{
		assertEnum();
		return toDEnumValue(system.enumValueType.searchSingleton(Cope.equalAndCast(system.enumValueParent, backingItem).and(system.enumValueCode.equal(code))));
	}
	
	public DEnumValue addEnumValue(final String code)
	{
		assertEnum();
		final List<DEnumValue> values = getEnumValues(); // TODO make more efficient
		final int position = values.isEmpty() ? 0 : (values.get(values.size()-1).getPosition()+1);
		return new DEnumValue(system,
				system.enumValueType.newItem(
						Cope.mapAndCast(system.enumValueParent, backingItem),
						system.enumValuePosition.map(position),
						system.enumValueCode.map(code)));
	}
	
	

	
	DAttribute(final DTypeSystem system, final Item backingItem)
	{
		this.system = system;
		this.backingItem = backingItem;
		assert system!=null;
		assert backingItem!=null;
	}
	
	public DType getParent()
	{
		return new DType(system, system.attributeParent.get(backingItem));
	}
	
	public int getPosition()
	{
		return system.attributePosition.getMandatory(backingItem);
	}
	
	public ValueType getValueType()
	{
		return system.attributeValueType.get(backingItem);
	}
	
	int getPositionPerValueType()
	{
		return system.attributePositionPerValueType.getMandatory(backingItem);
	}
	
	public String getCode()
	{
		return system.attributeCode.get(backingItem);
	}
	
	public FunctionField<?> getField()
	{
		return getParent().getDtypeSystem().getField(this);
	}
	
	private DEnumValue toDEnumValue(final Item backingItem)
	{
		return backingItem!=null ? new DEnumValue(system, backingItem) : null;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		return other instanceof DAttribute && backingItem.equals(((DAttribute)other).backingItem);
	}
	
	@Override
	public int hashCode()
	{
		return backingItem.hashCode() ^ 63352268;
	}
	
	@Override
	public String toString()
	{
		return backingItem.toString();
	}
}
