
package com.exedio.cope.lib.mapping;

import com.exedio.cope.lib.AttributeMapping;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.StringFunction;

public final class UppercaseMapping extends AttributeMapping implements StringFunction
// TODO rename class to UppercaseFunction and move to package function
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
