package com.exedio.copernica;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.StringAttribute;
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
			final Attribute attribute = (Attribute)j.next();
			if(!attribute.isReadOnly())
			{
				final String name = attribute.getName();
				final String value;
				if(attribute instanceof StringAttribute)
				{
					final StringAttribute stringAttribute = (StringAttribute)attribute;

					final String requestValue = Cop.getParameter(parameters, name);
					if(requestValue!=null)
						value = requestValue;
					else
					{
						final String itemValue = (String)item.getAttribute(stringAttribute);
						value = (itemValue==null) ? "" : itemValue;
					}
					toSave = true;
				}
				else
					continue;

				fields.put(attribute, new Field(name, value));
			}
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
		
		final String getName()
		{
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
			item.setAttribute(attribute, field.value);
		}
	}
	
}
