
package com.exedio.cope.lib.function;

import com.exedio.cope.lib.ComputedFunction;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.StringFunction;

public final class UppercaseFunction extends ComputedFunction implements StringFunction
{

	public UppercaseFunction(final StringAttribute sourceAttribute)
	{
		super(sourceAttribute, "UPPER(", ")", "upper");
	}

	public final Object mapJava(final Object sourceValue)
	{
		return sourceValue==null ? null : ((String)sourceValue).toUpperCase();
	}
	
}
