package com.exedio.copernica;

import java.util.HashMap;
import java.util.Map;

import com.exedio.cope.lib.Attribute;

public abstract class Form
{
	private final Map parameters;
	boolean toSave = false;
	protected final HashMap fields = new HashMap();
	
	Form(final Map parameters)
	{
		this.parameters = parameters;
	}
	
	class Field
	{
		public final String name;
		public final String value;
		public String error;
		
		Field(final String name, final String value)
		{
			this.name = name;
			this.value = value;
		}
		
		Field(final String value)
		{
			this.name = null;
			this.value = value;
		}
		
		final boolean isReadOnly()
		{
			return name==null;
		}
		
		final String getName()
		{
			if(name==null)
				throw new RuntimeException();
			return name;
		}
		
		final String getValue()
		{
			return value;
		}
		
		final String getError()
		{
			return error;
		}
	}
	
	Field getField(Attribute attribute)
	{
		return (Field)fields.get(attribute);
	}
	
}
