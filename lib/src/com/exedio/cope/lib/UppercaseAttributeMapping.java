
package com.exedio.cope.lib;

public final class UppercaseAttributeMapping extends AttributeMapping
{

	public UppercaseAttributeMapping(final StringAttribute sourceAttribute)
	{
		super(sourceAttribute, "UPPER(", ")", "upper");
	}

	public final Object mapJava(final Object sourceValue)
	{
		return ((String)sourceValue).toUpperCase();
	}
	
}
