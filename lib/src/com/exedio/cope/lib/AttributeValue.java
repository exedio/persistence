/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
package com.exedio.cope.lib;

public final class AttributeValue
{
	public final ObjectAttribute attribute;
	public final Object value;
	
	public AttributeValue(final ObjectAttribute attribute, final Object value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final StringAttribute attribute, final String value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final BooleanAttribute attribute, final Boolean value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final BooleanAttribute attribute, final boolean value)
	{
		this.attribute = attribute;
		this.value = value ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public AttributeValue(final IntegerAttribute attribute, final Integer value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final IntegerAttribute attribute, final int value)
	{
		this.attribute = attribute;
		this.value = new Integer(value);
	}
	
	public AttributeValue(final LongAttribute attribute, final Long value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final LongAttribute attribute, final long value)
	{
		this.attribute = attribute;
		this.value = new Long(value);
	}
	
	public AttributeValue(final DoubleAttribute attribute, final Double value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final DoubleAttribute attribute, final double value)
	{
		this.attribute = attribute;
		this.value = new Double(value);
	}
	
	public AttributeValue(final ItemAttribute attribute, final Item value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final EnumAttribute attribute, final EnumValue value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
}
	

