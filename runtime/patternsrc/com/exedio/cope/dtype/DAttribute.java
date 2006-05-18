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

import com.exedio.cope.EnumAttribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class DAttribute extends Item
{
	public static final ItemAttribute<DType> parent = newItemAttribute(FINAL, DType.class);
	public static final IntegerAttribute position = new IntegerAttribute(FINAL);
	public static final UniqueConstraint uniqueConstraint = new UniqueConstraint(parent, position);

	public static final StringAttribute name = new StringAttribute(FINAL);

	public static enum ValueType
	{
		STRING,
		INTEGER;
	}
	public static final EnumAttribute<ValueType> valueType = newEnumAttribute(FINAL, ValueType.class);
	
	

	
	DAttribute(final DType parent, final int position, final String name, final ValueType valueType)
	{
		super(new SetValue[]{
				DAttribute.parent.map(parent),
				DAttribute.position.map(position),
				DAttribute.name.map(name),
				DAttribute.valueType.map(valueType),
		});
	}
	
	private DAttribute(final SetValue[] initialAttributes)
	{
		super(initialAttributes);
	}
	
	private DAttribute(final ReactivationConstructorDummy d, final int pk)
	{
		super(d, pk);
	}
	
	int getPosition()
	{
		return position.getMandatory(this);
	}
	
	public static final Type<DAttribute> TYPE = newType(DAttribute.class);
}
