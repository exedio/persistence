package com.exedio.cope.lib;

public abstract class EnumValue
{

	private boolean initialized = false;
	private Class enumerationClass;
	private String code;
	private int number;
	private Integer numberObject;
	
	final boolean isInitialized()
	{
		return initialized;
	}

	final void initialize(final Class enumerationClass, final String code, final int number)
	{
		if(initialized)
			throw new RuntimeException("enumeration attribute "+this.enumerationClass+"#"+this.code+" has been already initialized");
		if(enumerationClass==null)
			throw new NullPointerException();
		if(!EnumValue.class.isAssignableFrom(enumerationClass))
			throw new RuntimeException();
		if(code==null)
			throw new NullPointerException();
			
		this.enumerationClass = enumerationClass;
		this.code = code.intern();
		this.number = number;
		this.numberObject = new Integer(number);
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
	
	public final int getNumber()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return number;
	}
	
	public final Integer getNumberObject()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return numberObject;
	}
	
	public final String toString()
	{
		if(!initialized)
			throw new RuntimeException("enumeration attribute is not yet initialized");

		return code + '(' + number + ')';
	}
	
}
