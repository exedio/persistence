package com.exedio.copernica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Form
{
	private final Map parameters;
	boolean toSave = false;
	private final HashMap fieldMap = new HashMap();
	private final ArrayList fieldList = new ArrayList();
	
	Form(final Map parameters)
	{
		this.parameters = parameters;
	}
	
	class Field
	{
		public final Object key;
		public final String name;
		public final String value;
		public String error;
		
		Field(final Object key, final String name, final String value)
		{
			this.key = key;
			this.name = name;
			this.value = value;
			fieldMap.put(key, this);
			fieldList.add(this);
		}
		
		Field(final Object key, final String value)
		{
			this.key = key;
			this.name = null;
			this.value = value;
			fieldMap.put(key, this);
			fieldList.add(this);
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
	
	final List getFields()
	{
		return Collections.unmodifiableList(fieldList);
	}
	
}
