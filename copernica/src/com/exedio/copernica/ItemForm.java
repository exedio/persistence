package com.exedio.copernica;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Search;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Type;

public class ItemForm extends Form
{
	static final String VALUE_NULL = "null";
	static final String VALUE_ON = "on";
	static final String VALUE_OFF = "off";
	
	static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	final Item item;
	final Type type;
	
	ItemForm(final Item item, final Map parameters, final boolean save)
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
					|| attribute instanceof LongAttribute
					|| attribute instanceof DateAttribute
					|| attribute instanceof ItemAttribute
					|| attribute instanceof BooleanAttribute
					|| attribute instanceof EnumerationAttribute)
				{
					final String value;
	
					if(save)
						value = Cop.getParameter(parameters, name);
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
						else if(attribute instanceof LongAttribute)
						{
							value = (itemValue==null) ? "" : String.valueOf((Long)itemValue);
						}
						else if(attribute instanceof DateAttribute)
						{
							 if(itemValue==null)
								value =  "";
							else
							{
								final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
								value = df.format((Date)itemValue);
							}
						}
						else if(attribute instanceof ItemAttribute)
						{
							value = (itemValue==null) ? "" : ((Item)itemValue).getID();
						}
						else if(attribute instanceof BooleanAttribute)
						{
							value = (itemValue==null) ? VALUE_NULL : ((Boolean)itemValue).booleanValue() ? VALUE_ON : VALUE_OFF;
						}
						else if(attribute instanceof EnumerationAttribute)
						{
							value = (itemValue==null) ? VALUE_NULL : ((EnumerationValue)itemValue).getCode();
						}
						else
							throw new RuntimeException();
					}
					if(!attribute.isReadOnly())
						field = new Field(attribute, name, value);
					else
						field = new Field(attribute, value);
				}
				else
				{
					final Object itemValue = item.getAttribute(attribute);
					field = new Field(attribute, itemValue==null ? "leer" : itemValue.toString());
				}
			}
			else
				continue;

			if(!field.isReadOnly())
				toSave = true;
		}
	}
	
	void save()
	{
		for(Iterator i = getFields().iterator(); i.hasNext(); )
		{
			final Field field = (Field)i.next();
			final ObjectAttribute attribute = (ObjectAttribute)field.key;
			if(!field.isReadOnly())
			{
				try
				{
					final Object value;
					final String valueString = field.value;
					if(attribute instanceof StringAttribute)
					{
						value = field.value;
					}
					else if(attribute instanceof IntegerAttribute)
					{
						if(valueString.length()>0)
							value = new Integer(Integer.parseInt(valueString));
						else
							value = null;
					}
					else if(attribute instanceof LongAttribute)
					{
						if(valueString.length()>0)
							value = new Long(Long.parseLong(valueString));
						else
							value = null;
					}
					else if(attribute instanceof DateAttribute)
					{
						if(valueString.length()>0)
						{
							final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
							value = df.parse(valueString);
						}
						else
							value = null;
					}
					else if(attribute instanceof ItemAttribute)
					{
						if(valueString.length()>0)
							value = Search.findByID(valueString);
						else
							value = null;
					}
					else if(attribute instanceof BooleanAttribute)
					{
						if(valueString==null)
							value = Boolean.FALSE;
						else if(VALUE_NULL.equals(valueString))
							value = null;
						else if(VALUE_ON.equals(valueString))
							value = Boolean.TRUE;
						else if(VALUE_OFF.equals(valueString))
							value = Boolean.FALSE;
						else
							throw new RuntimeException(valueString);
					}
					else if(attribute instanceof EnumerationAttribute)
					{
						if(VALUE_NULL.equals(valueString))
							value = null;
						else
						{
							final EnumerationAttribute enumAttribute = (EnumerationAttribute)attribute;
							value = enumAttribute.getValue(valueString);
							if(value==null)
								throw new NullPointerException(field.name);
						}
					}
					else
						throw new RuntimeException();
				
					item.setAttribute(attribute, value);
				}
				catch(NumberFormatException e)
				{
					field.error = "bad number: "+e.getMessage();
				}
				catch(ParseException e)
				{
					field.error = "bad date: "+e.getMessage();
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
