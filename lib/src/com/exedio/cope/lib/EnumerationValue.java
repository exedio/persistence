
package com.exedio.cope.lib;

public class EnumerationValue
{
	public final int number;
	public final Integer numberObject;
	
	public EnumerationValue(final int number)
	{
		this.number = number;
		this.numberObject = new Integer(number);
	}
	
	private boolean initialized = false;
	private Class enumerationClass;
	private String code;
	
	final boolean isInitialized()
	{
		return initialized;
	}

	final void initialize(final Class enumerationClass, final String code)
	{
		if(initialized)
			throw new RuntimeException("enumeration attribute "+this.enumerationClass+"#"+this.code+" has been already initialized");
		if(enumerationClass==null)
			throw new NullPointerException();
		if(!EnumerationValue.class.isAssignableFrom(enumerationClass))
			throw new RuntimeException();
		if(code==null)
			throw new NullPointerException();
			
		this.enumerationClass = enumerationClass;
		this.code = code;
		initialized = true;
	}
	
	public final Class getEnumerationClass()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return enumerationClass;
	}
	
	public final String getCode()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return code;
	}
	
	public final String toString()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return code + '(' + number + ')';
	}
	
}
