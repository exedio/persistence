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
import com.exedio.cope.Item;
import com.exedio.cope.Type;

public final class DType
{
	private final DTypeSystem system;
	final Item backingItem;
	
	public DAttribute addAttribute(final String name, final DAttribute.ValueType valueType)
	{
		final List<DAttribute> attributes = getAttributes(); // TODO make more efficient
		final int position = attributes.isEmpty() ? 0 : (attributes.get(attributes.size()-1).getPosition()+1);
		final List<DAttribute> attributesPerValuetype = getAttributes(valueType); // TODO make more efficient
		final int positionPerValuetype = attributesPerValuetype.isEmpty() ? 0 : (attributesPerValuetype.get(attributesPerValuetype.size()-1).getPositionPerValueType()+1);
		getDtypeSystem().assertCapacity(valueType, positionPerValuetype);
		//System.out.println("----------------"+getCode()+'-'+name+'-'+position);
		return new DAttribute(system,
				system.attributeType.newItem(
						Cope.mapAndCast(system.attributeParent, backingItem),
						system.attributePosition.map(position),
						system.attributeCode.map(name),
						system.attributeValueType.map(valueType),
						system.attributePositionPerValueType.map(positionPerValuetype)));
	}
	
	public DAttribute addStringAttribute(final String name)
	{
		return addAttribute(name, DAttribute.ValueType.STRING);
	}
	
	public DAttribute addBooleanAttribute(final String name)
	{
		return addAttribute(name, DAttribute.ValueType.BOOLEAN);
	}
	
	public DAttribute addIntegerAttribute(final String name)
	{
		return addAttribute(name, DAttribute.ValueType.INTEGER);
	}
	
	public DAttribute addDoubleAttribute(final String name)
	{
		return addAttribute(name, DAttribute.ValueType.DOUBLE);
	}
	
	public DAttribute addEnumAttribute(final String name)
	{
		return addAttribute(name, DAttribute.ValueType.ENUM);
	}
	
	public List<DAttribute> getAttributes()
	{
		final List<? extends Item> backingItems = system.attributeType.search(Cope.equalAndCast(system.attributeParent, backingItem), system.attributePosition, true);
		final ArrayList<DAttribute> result = new ArrayList<DAttribute>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new DAttribute(system, backingItem));
		return Collections.unmodifiableList(result);
	}
	
	public DAttribute getAttribute(final String code)
	{
		return toDAttribute(system.attributeType.searchSingleton(Cope.equalAndCast(system.attributeParent, backingItem).and(system.attributeCode.equal(code))));
	}
	
	private List<DAttribute> getAttributes(final DAttribute.ValueType valueType)
	{
		final List<? extends Item> backingItems = system.attributeType.search(Cope.equalAndCast(system.attributeParent, backingItem).and(system.attributeValueType.equal(valueType)), system.attributePositionPerValueType, true);
		final ArrayList<DAttribute> result = new ArrayList<DAttribute>(backingItems.size());
		for(final Item backingItem : backingItems)
			result.add(new DAttribute(system, backingItem));
		return Collections.unmodifiableList(result);
	}
	
	
	
	DType(final DTypeSystem system, final Item backingItem)
	{
		this.system = system;
		this.backingItem = backingItem;
		assert system!=null;
		assert backingItem!=null;
	}
	
	public Type getParentType()
	{
		return system.getType();
	}
	
	public DTypeSystem getDtypeSystem()
	{
		return system;
	}
	
	public String getCode()
	{
		return system.typeCode.get(backingItem);
	}
	
	public final Item getBackingItem()
	{
		return backingItem;
	}
	
	private DAttribute toDAttribute(final Item backingItem)
	{
		return backingItem!=null ? new DAttribute(system, backingItem) : null;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		return other instanceof DType && backingItem.equals(((DType)other).backingItem);
	}
	
	@Override
	public int hashCode()
	{
		return backingItem.hashCode() ^ 6853522;
	}
	
	@Override
	public String toString()
	{
		return backingItem.toString();
	}
}
