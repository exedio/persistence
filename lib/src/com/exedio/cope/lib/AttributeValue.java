package com.exedio.cope.lib;

public final class AttributeValue
{
	public final Attribute attribute;
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
	
	public AttributeValue(final IntegerAttribute attribute, final Integer value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public AttributeValue(final DoubleAttribute attribute, final Double value)
	{
		this.attribute = attribute;
		this.value = value;
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
	

