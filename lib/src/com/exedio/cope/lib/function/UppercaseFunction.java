
package com.exedio.cope.lib.function;

import com.exedio.cope.lib.ComputedStringFunction;
import com.exedio.cope.lib.StringFunction;

public final class UppercaseFunction
	extends ComputedStringFunction
	implements StringFunction
{
	private static final String[] sql = new String[]{"UPPER(", ")"};

	public UppercaseFunction(final StringFunction source)
	{
		super(new StringFunction[]{source}, sql, "upper");
	}

	public final Object mapJava(final Object[] sourceValues)
	{
		final Object sourceValue = sourceValues[0];
		return sourceValue==null ? null : ((String)sourceValue).toUpperCase();
	}
	
}
