package com.exedio.cope.lib;

public final class AttributeValue
{
	public final ObjectAttribute attribute;
	public final Object value;
	
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
	
	public AttributeValue(final EnumerationAttribute attribute, final EnumerationValue value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
}
	

