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

import com.exedio.cope.Item;

public final class DEnumValue
{
	private final DTypeSystem system;
	final Item backingItem;
	
	DEnumValue(final DTypeSystem system, final Item backingItem)
	{
		this.system = system;
		this.backingItem = backingItem;
		assert system!=null;
		assert backingItem!=null;
	}
	
	public DAttribute getParent()
	{
		return new DAttribute(system, system.enumValueParent.get(backingItem));
	}
	
	public int getPosition()
	{
		return system.enumValuePosition.getMandatory(backingItem);
	}
	
	public String getCode()
	{
		return system.enumValueCode.get(backingItem);
	}
	
	public final Item getBackingItem()
	{
		return backingItem;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		return other instanceof DEnumValue && backingItem.equals(((DEnumValue)other).backingItem);
	}
	
	@Override
	public int hashCode()
	{
		return backingItem.hashCode() ^ 765744;
	}
	
	@Override
	public String toString()
	{
		return backingItem.toString();
	}
}
