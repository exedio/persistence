package com.exedio.cope.lib.function;

import com.exedio.cope.lib.ComputedIntegerFunction;
import com.exedio.cope.lib.IntegerFunction;

public final class SumFunction
	extends ComputedIntegerFunction
	implements IntegerFunction
{

	public SumFunction(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		this(new IntegerFunction[]{addend1, addend2});
	}

	public SumFunction(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		this(new IntegerFunction[]{addend1, addend2, addend3});
	}

	private SumFunction(final IntegerFunction[] addends)
	{
		super(addends, makePlusses(addends.length), "sum");
	}
	
	private static final String[] makePlusses(final int length)
	{
		final String[] result = new String[length+1];

		result[0] = "(";
		for(int i=length-1; i>0; i--)
			result[i] = "+";
		result[length] = ")";

		return result;
	}

	public final Object mapJava(final Object[] sourceValues)
	{
		int result = 0;
		for(int i=0; i<sourceValues.length; i++)
		{
			if(sourceValues[i]==null)
				return null;
			result += ((Integer)sourceValues[i]).intValue();
		}
		return new Integer(result);
	}

}
