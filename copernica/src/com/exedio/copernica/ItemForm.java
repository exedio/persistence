package com.exedio.copernica;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.ConstraintViolationException;
import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.EnumAttribute;
import com.exedio.cope.lib.EnumValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.MediaAttribute;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.pattern.Qualifier;
import com.exedio.cope.lib.search.EqualCondition;

final class ItemForm extends Form
{
	static final String VALUE_NULL = "null";
	static final String VALUE_ON = "on";
	static final String VALUE_OFF = "off";
	static final String SAVE_BUTTON = "SAVE";
	static final String CHECK_BUTTON = "CHECK";
	
	static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	final Item item;
	final Type type;
	final boolean hasFiles;
	
	ItemForm(final Item item, final HttpServletRequest request)
	{
		super(request);
		
		final boolean save = getParameter(SAVE_BUTTON)!=null;
		final boolean post = save || getParameter(CHECK_BUTTON)!=null;
		this.item = item;
		this.type = item.getType();
		
		boolean hasFilesTemp = false;

		for(Iterator j = type.getAttributes().iterator(); j.hasNext(); )
		{
			final Attribute anyAttribute = (Attribute)j.next();
			final Field field;
			if(anyAttribute instanceof ObjectAttribute)
			{
				final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
				final String name = attribute.getName();
				final String value;

				if(post)
					value = getParameter(name);
				else
				{
					final Object itemValue = item.getAttribute(attribute);
					value = valueToString(attribute, itemValue);
				}
				if(!attribute.isReadOnly())
					field = new Field(attribute, name, value);
				else
					field = new Field(attribute, value);
			}
			else if(anyAttribute instanceof MediaAttribute)
			{
				final MediaAttribute attribute = (MediaAttribute)anyAttribute;
				field = new Field(attribute, "");
				if(!attribute.isReadOnly())
				{
					toSave = true;
					hasFilesTemp = true;
				}
			}
			else
				continue;

			if(!field.isReadOnly())
				toSave = true;
		}
		this.hasFiles = hasFilesTemp;

		for(Iterator j = type.getQualifiers().iterator(); j.hasNext(); )
		{
			final Qualifier qualifier = (Qualifier)j.next();
			final Collection values = qualifier.getQualifyUnique().getType().search(new EqualCondition(qualifier.getParent(), item));
			for(Iterator k = qualifier.getAttributes().iterator(); k.hasNext(); )
			{
				final Attribute anyAttribute = (Attribute)k.next();
				for(Iterator l = values.iterator(); l.hasNext(); )
				{
					final Item value = (Item)l.next(); 
					if(anyAttribute instanceof ObjectAttribute)
					{
						final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
						final Object qualifiedValue = value.getAttribute(attribute);
						if(qualifiedValue!=null)
							new Field(attribute, valueToString(attribute, qualifiedValue));
					}
				}
			}
		}
		
		if(save)
		{
			save();
		}
	}
	
	private void save()
	{
		for(Iterator i = getFields().iterator(); i.hasNext(); )
		{
			final Field field = (Field)i.next();
			if(field.key instanceof MediaAttribute)
			{
				final MediaAttribute attribute = (MediaAttribute)field.key;
				final FileItem fileItem = getParameterFile(attribute.getName());
				if(fileItem!=null)
				{
					final String contentType = fileItem.getContentType();
					if(contentType!=null)
					{
						final int pos = contentType.indexOf('/');
						if(pos<=0)
							throw new RuntimeException("invalid content type "+contentType);
						final String mimeMajor = contentType.substring(0, pos);
						String mimeMinor = contentType.substring(pos+1);
						
						// fix for MSIE behaviour
						if("image".equals(mimeMajor) && "pjpeg".equals(mimeMinor))
							mimeMinor = "jpeg";
						
						try
						{
							final InputStream data = fileItem.getInputStream();
							item.setMediaData(attribute, data, mimeMajor, mimeMinor);
						}
						catch(IOException e)
						{
							throw new NestingRuntimeException(e);
						}
						catch(NotNullViolationException e)
						{
							throw new NestingRuntimeException(e);
						}
					}
				}
			}
			if(!field.isReadOnly())
			{
				final ObjectAttribute attribute = (ObjectAttribute)field.key;
				try
				{
					final Object value;
					final String valueString = field.value;
					value = stringToValue(attribute, valueString);
					item.setAttribute(attribute, value);
				}
				catch(MalformedFieldException e)
				{
					field.error = e.getMessage();
				}
				catch(NotNullViolationException e)
				{
					field.error = "error.notnull:"+e.getNotNullAttribute().toString();
				}
				catch(ConstraintViolationException e)
				{
					field.error = e.getClass().getName();
				}
			}
		}
	}
	
	final static String valueToString(final ObjectAttribute attribute, final Object itemValue)
	{
		final String value;
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
		else if(attribute instanceof DoubleAttribute)
		{
			value = (itemValue==null) ? "" : String.valueOf((Double)itemValue);
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
		else if(attribute instanceof EnumAttribute)
		{
			value = (itemValue==null) ? VALUE_NULL : ((EnumValue)itemValue).getCode();
		}
		else
			throw new RuntimeException();
			
		return value;
	}
	
	final static Object stringToValue(final ObjectAttribute attribute, final String valueString)
				throws MalformedFieldException
	{
		try
		{
			final Object value;
			if(attribute instanceof StringAttribute)
			{
				value = valueString;
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
			else if(attribute instanceof DoubleAttribute)
			{
				if(valueString.length()>0)
					value = new Double(Double.parseDouble(valueString));
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
					value = attribute.getType().getModel().findByID(valueString);
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
			else if(attribute instanceof EnumAttribute)
			{
				if(VALUE_NULL.equals(valueString))
					value = null;
				else
				{
					final EnumAttribute enumAttribute = (EnumAttribute)attribute;
					value = enumAttribute.getValue(valueString);
					if(value==null)
						throw new NullPointerException(valueString);
				}
			}
			else
				throw new RuntimeException();
	
			return value;
		}
		catch(NumberFormatException e)
		{
			throw new MalformedFieldException("bad number: "+e.getMessage());
		}
		catch(ParseException e)
		{
			throw new MalformedFieldException("bad date: "+e.getMessage());
		}
		catch(NoSuchIDException e)
		{
			throw new MalformedFieldException(e.getMessage());
		}
	}
}
