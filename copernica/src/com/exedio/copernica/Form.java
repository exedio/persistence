package com.exedio.copernica;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;

public class Form
{
	final Item item;
	final Type type;
	final Map parameters;
	boolean toSave = false;

	private final HashMap fields = new HashMap();
	
	Form(final Item item, final Map parameters)
	{
		this.item = item;
		this.type = item.getType();
		this.parameters = parameters;

		for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
		{
			final Attribute anyAttribute = (Attribute)j.next();
			final Field field;
			if(anyAttribute instanceof ObjectAttribute)
			{
				final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
				final String name = attribute.getName();

				if(attribute instanceof StringAttribute)
				{
					final String value;
	
					final String requestValue = Cop.getParameter(parameters, name);
					if(requestValue!=null)
						value = requestValue;
					else
					{
						final String itemValue = (String)item.getAttribute(attribute);
						value = (itemValue==null) ? "" : itemValue;
					}
					if(!attribute.isReadOnly())
						field = new Field(name, value);
					else
						field = new Field(value);
				}
				else if(attribute instanceof IntegerAttribute)
				{
					final String value;
	
					final String requestValue = Cop.getParameter(parameters, name);
					if(requestValue!=null)
						value = requestValue;
					else
					{
						final Integer itemValue = (Integer)item.getAttribute(attribute);
						value = (itemValue==null) ? "" : String.valueOf(itemValue);
					}
					if(!attribute.isReadOnly())
						field = new Field(name, value);
					else
						field = new Field(value);
				}
				else
					continue;
			}
			else
				continue;

			if(!field.isReadOnly())
				toSave = true;
			fields.put(anyAttribute, field);
		}
	}
	
	class Field
	{
		private final String name;
		private final String value;
		
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
	}
	
	Field getField(Attribute attribute)
	{
		return (Field)fields.get(attribute);
	}
	
	void save()
		throws ConstraintViolationException
	{
		for(Iterator i = fields.keySet().iterator(); i.hasNext(); )
		{
			final ObjectAttribute attribute = (ObjectAttribute)i.next();
			final Field field = (Field)fields.get(attribute);
			if(!field.isReadOnly())
			{
				final Object value;
				if(attribute instanceof StringAttribute)
				{
					value = field.value;
				}
				else if(attribute instanceof IntegerAttribute)
				{
					final String valueString = field.value;
					if(valueString.length()>0)
					{
						try
						{
							value = new Integer(Integer.parseInt(valueString));
						}
						catch(NumberFormatException e)
						{
							throw new SystemException(e);
						}
					}
					else
						value = null;
				}
				else
					throw new RuntimeException();
				
				item.setAttribute(attribute, value);
			}
		}
	}
	
}
