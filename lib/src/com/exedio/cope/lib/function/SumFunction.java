package com.exedio.cope.lib.function;

import com.exedio.cope.lib.ComputedFunction;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.IntegerFunction;

public class SumFunction extends ComputedFunction implements IntegerFunction
{

	public SumFunction(final IntegerAttribute addend1, final IntegerAttribute addend2)
	{
		this(new IntegerAttribute[]{addend1, addend2});
	}

	private SumFunction(final IntegerAttribute[] addends)
	{
		super(addends, addends[0], makePlusses(addends.length), "sum");
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
