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

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class DType extends Item
{
	private static final long serialVersionUID = 1l;
	
	public static final StringField parentTypeId = new StringField().toFinal();
	public static final StringField dtypeSystemName = new StringField().toFinal();
	public static final StringField code = new StringField().toFinal();
	public static final UniqueConstraint uniqueConstraint = new UniqueConstraint(parentTypeId, dtypeSystemName, code);
	
	
	public DAttribute addAttribute(final String name, final DAttribute.ValueType valueType)
	{
		final List<DAttribute> attributes = getAttributes(); // TODO make more efficient
		final int position = attributes.isEmpty() ? 0 : (attributes.get(attributes.size()-1).getPosition()+1);
		final List<DAttribute> attributesPerValuetype = getAttributes(valueType); // TODO make more efficient
		final int positionPerValuetype = attributesPerValuetype.isEmpty() ? 0 : (attributesPerValuetype.get(attributesPerValuetype.size()-1).getPositionPerValueType()+1);
		getDtypeSystem().assertCapacity(valueType, positionPerValuetype);
		//System.out.println("----------------"+getCode()+'-'+name+'-'+position);
		return new DAttribute(this, position, name, valueType, positionPerValuetype);
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
		return DAttribute.TYPE.search(DAttribute.parent.equal(this), DAttribute.position, true);
	}
	
	public DAttribute getAttribute(final String code)
	{
		return DAttribute.TYPE.searchSingleton(DAttribute.parent.equal(this).and(DAttribute.code.equal(code)));
	}
	
	private List<DAttribute> getAttributes(final DAttribute.ValueType valueType)
	{
		return DAttribute.TYPE.search(DAttribute.parent.equal(this).and(DAttribute.valueType.equal(valueType)), DAttribute.positionPerValueType, true);
	}
	
	
	
	DType(final DTypeSystem dtypeSystem, final String code)
	{
		super(new SetValue[]{
				DType.parentTypeId.map(dtypeSystem.getType().getID()),
				DType.dtypeSystemName.map(dtypeSystem.getName()),
				DType.code.map(code),
		});
	}
	
	private DType(final SetValue[] setValues)
	{
		super(setValues);
	}
	
	private DType(final ReactivationConstructorDummy d, final int pk)
	{
		super(d, pk);
	}
	
	private String getParentTypeId()
	{
		return parentTypeId.get(this);
	}
	
	public Type getParentType()
	{
		return TYPE.getModel().findTypeByID(getParentTypeId());
	}
	
	private String getDtypeSystemName()
	{
		return dtypeSystemName.get(this);
	}
	
	public DTypeSystem getDtypeSystem()
	{
		return (DTypeSystem)getParentType().getDeclaredFeature(getDtypeSystemName());
	}
	
	public String getCode()
	{
		return code.get(this);
	}
	
	public static final Type<DType> TYPE = newType(DType.class);
}
