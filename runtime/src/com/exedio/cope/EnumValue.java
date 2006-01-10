/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

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
