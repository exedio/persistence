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

import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringAttribute;

public final class DTypeSystem extends Pattern
{
	private final StringAttribute[] strings;
	private final IntegerAttribute[] integers;

	public DTypeSystem()
	{
		strings = new StringAttribute[5];
		integers = new IntegerAttribute[8];
		
		for(int i = 0; i<strings.length; i++)
			registerSource(strings[i] = new StringAttribute(Item.OPTIONAL));
		for(int i = 0; i<integers.length; i++)
			registerSource(integers[i] = new IntegerAttribute(Item.OPTIONAL));
	}

	public void initialize()
	{
		final String name = getName();
		
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
	
	public Object get(final DAttribute attribute, final Item item)
	{
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
