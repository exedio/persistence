
package com.exedio.cope.lib.mapping;

import com.exedio.cope.lib.AttributeMapping;
import com.exedio.cope.lib.StringAttribute;

public final class UppercaseMapping extends AttributeMapping
{

	public UppercaseMapping(final StringAttribute sourceAttribute)
	{
		super(sourceAttribute, "UPPER(", ")", "upper");
	}

	public final Object mapJava(final Object sourceValue)
	{
		return sourceValue==null ? null : ((String)sourceValue).toUpperCase();
	}
	
}
