package com.exedio.copernica;

import java.util.Iterator;
import java.util.Map;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Type;

public class ItemForm extends Form
{
	final Item item;
	final Type type;
	
	ItemForm(final Item item, final Map parameters)
	{
		super(parameters);
		this.item = item;
		this.type = item.getType();

		for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
		{
			final Attribute anyAttribute = (Attribute)j.next();
			final Field field;
			if(anyAttribute instanceof ObjectAttribute)
			{
				final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
				final String name = attribute.getName();

				if(attribute instanceof StringAttribute
					|| attribute instanceof IntegerAttribute
					|| attribute instanceof ItemAttribute)
				{
					final String value;
	
					final String requestValue = Cop.getParameter(parameters, name);
					if(requestValue!=null)
						value = requestValue;
					else
					{
						final Object itemValue = item.getAttribute(attribute);
						if(attribute instanceof StringAttribute)
						{
							value = (itemValue==null) ? "" : (String)itemValue;
						}
						else if(attribute instanceof IntegerAttribute)
						{
							value = (itemValue==null) ? "" : String.valueOf((Integer)itemValue);
						}
						else if(attribute instanceof ItemAttribute)
						{
							value = (itemValue==null) ? "" : ((Item)itemValue).getID();
						}
						else
							throw new RuntimeException();
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
	
	void save()
	{
		for(Iterator i = fields.keySet().iterator(); i.hasNext(); )
		{
			final ObjectAttribute attribute = (ObjectAttribute)i.next();
			final Field field = (Field)fields.get(attribute);
			if(!field.isReadOnly())
			{
				try
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
							value = new Integer(Integer.parseInt(valueString));
						else
							value = null;
					}
					else if(attribute instanceof ItemAttribute)
					{
						final String valueString = field.value;
						if(valueString.length()>0)
							value = Search.findByID(valueString);
						else
							value = null;
					}
					else
						throw new RuntimeException();
				
					item.setAttribute(attribute, value);
				}
				catch(NumberFormatException e)
				{
					field.error = "bad number: "+e.getMessage();
				}
				catch(ConstraintViolationException e)
				{
					field.error = e.getClass().getName();
				}
				catch(NoSuchIDException e)
				{
					field.error = e.getMessage();
				}
			}
		}
	}
	
}
