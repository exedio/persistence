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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class DEnumValue extends Item
{
	private static final long serialVersionUID = 1l;

	public static final ItemField<DAttribute> parent = newItemField(DAttribute.class, CASCADE).toFinal();

	public static final IntegerField position = new IntegerField().toFinal();
	public static final UniqueConstraint uniquePosition = new UniqueConstraint(parent, position);
	
	public static final StringField code = new StringField().toFinal();
	public static final UniqueConstraint uniqueCode = new UniqueConstraint(parent, code);
	
	DEnumValue(final DAttribute parent, final int position, final String code)
	{
		super(new SetValue[]{
				DEnumValue.parent.map(parent),
				DEnumValue.position.map(position),
				DEnumValue.code.map(code),
		});
	}
	
	@SuppressWarnings("unused") // OK: called by reflection
	private DEnumValue(final SetValue[] initialAttributes)
	{
		super(initialAttributes);
	}
	
	@SuppressWarnings("unused") // OK: called by reflection
	private DEnumValue(final ReactivationConstructorDummy d, final int pk)
	{
		super(d, pk);
	}
	
	public DAttribute getParent()
	{
		return parent.get(this);
	}
	
	public int getPosition()
	{
		return position.getMandatory(this);
	}
	
	public String getCode()
	{
		return code.get(this);
	}
	
	public static final Type<DEnumValue> TYPE = newType(DEnumValue.class);
}
