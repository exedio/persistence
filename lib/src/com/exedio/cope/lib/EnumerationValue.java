
package com.exedio.cope.lib;

public class EnumerationValue
{
	public final int number;
	public final Integer numberObject;
	public final String code;
	
	public EnumerationValue(final int number, final String code)
	{
		this.number = number;
		this.numberObject = new Integer(number);
		this.code = code;
	}
	
	public final String toString()
	{
		return code + '(' + number + ')';
	}
	
}
