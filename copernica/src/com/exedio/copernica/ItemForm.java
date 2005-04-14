package com.exedio.copernica;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NoSuchIDException;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.pattern.Qualifier;
import com.exedio.cope.lib.search.EqualCondition;
import com.exedio.cops.CheckboxField;
import com.exedio.cops.DateField;
import com.exedio.cops.DoubleField;
import com.exedio.cops.Field;
import com.exedio.cops.Form;
import com.exedio.cops.IntegerField;
import com.exedio.cops.LongField;
import com.exedio.cops.RadioField;
import com.exedio.cops.TextField;

final class ItemForm extends Form
{
	static final String VALUE_NULL = "null";
	static final String VALUE_ON = CheckboxField.VALUE_ON;
	static final String VALUE_OFF = "off";
	static final String SAVE_BUTTON = "SAVE";
	static final String CHECK_BUTTON = "CHECK";
	static final String SECTION = "section";
	
	static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	final Item item;
	final Type type;
	/*final TODO*/ boolean hasFiles;
	boolean toSave = false;
	final CopernicaSection currentSection;
	
	ItemForm(final ItemCop cop, final HttpServletRequest request)
	{
		super(request);
		
		this.item = cop.item;
		this.type = item.getType();
		final CopernicaProvider provider = cop.provider;
		final List displayedAttributes;
		final List hiddenAttributes;
		final Collection sections = provider.getSections(type);
		
		boolean sectionButton = false;
		if(sections!=null)
		{
			{
				CopernicaSection buttonSection = null;
				CopernicaSection previousSection = null;
				CopernicaSection firstSection = null;
				final String previousSectionParam = getParameter(SECTION);
				
				for(Iterator i = sections.iterator(); i.hasNext(); )
				{
					final CopernicaSection section = (CopernicaSection)i.next();
					if(firstSection==null)
						firstSection = section;
					
					final String id = section.getCopernicaID();
					if(getParameter(id)!=null)
					{
						buttonSection = section;
						sectionButton = true;
						break;
					}

					if(id.equals(previousSectionParam))
						previousSection = section;
				}
				if(buttonSection!=null)
					currentSection = buttonSection;
				else if(previousSection!=null)
					currentSection = previousSection;
				else
					currentSection = firstSection;
			}

			displayedAttributes = new ArrayList(provider.getMainAttributes(type));
			hiddenAttributes = new ArrayList();
			for(Iterator i = sections.iterator(); i.hasNext(); )
			{
				final CopernicaSection section = (CopernicaSection)i.next();
				new Section(section.getCopernicaID(), section.getCopernicaName(cop.language));
				final Collection sectionAttributes = section.getCopernicaAttributes();
				if(section.equals(currentSection))
					displayedAttributes.addAll(sectionAttributes);
				else
					hiddenAttributes.addAll(sectionAttributes);
			}
		}
		else
		{
			currentSection = null;
			displayedAttributes = type.getAttributes();
			hiddenAttributes = Collections.EMPTY_LIST;
		}
		final ArrayList attributes = new ArrayList(displayedAttributes.size()+hiddenAttributes.size());
		attributes.addAll(displayedAttributes);
		attributes.addAll(hiddenAttributes);

		final boolean save = getParameter(SAVE_BUTTON)!=null;
		final boolean post = save || sectionButton || getParameter(CHECK_BUTTON)!=null;
		boolean hasFilesTemp = false;

		for(Iterator j = attributes.iterator(); j.hasNext(); )
		{
			final Attribute anyAttribute = (Attribute)j.next();
			final Field field;
			if(anyAttribute instanceof ObjectAttribute)
			{
				final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
				final String name = attribute.getName();
				final String value;

				if(post)
				{
					final String postValue = getParameter(name);
					if(postValue==null)
					{
						if(attribute instanceof BooleanAttribute && attribute.isNotNull())
							value = VALUE_OFF;
						else
						{
							if(attribute.isReadOnly())
								value = valueToString(attribute, item.getAttribute(attribute));
							else
								throw new NullPointerException(name);
						}
					}
					else
						value = postValue;
				}
				else
					value = valueToString(attribute, item.getAttribute(attribute));
				
				final boolean hidden = hiddenAttributes.contains(attribute);
				
				if(attribute instanceof EnumAttribute)
				{
					field = new EnumField((EnumAttribute)attribute, value, hidden, cop);
				}
				else if(attribute instanceof BooleanAttribute)
				{
					if(attribute.isNotNull())
						field = new CheckboxField(this, attribute, name, attribute.isReadOnly(), value, hidden);
					else
						field = new BooleanEnumField((BooleanAttribute)attribute, value, hidden, cop);
				}
				else if(attribute instanceof IntegerAttribute)
				{
					field = new IntegerField(this, attribute, name, attribute.isReadOnly(), value, hidden);
				}
				else if(attribute instanceof LongAttribute)
				{
					if(post)
						field = new LongField(this, attribute, name, attribute.isReadOnly(), hidden);
					else
						field = new LongField(this, attribute, name, attribute.isReadOnly(), (Long)item.getAttribute(attribute), hidden);
				}
				else if(attribute instanceof DoubleAttribute)
				{
					field = new DoubleField(this, attribute, name, attribute.isReadOnly(), value, hidden);
				}
				else if(attribute instanceof DateAttribute)
				{
					field = new DateField(this, attribute, name, attribute.isReadOnly(), value, hidden);
				}
				else if(attribute instanceof StringAttribute)
				{
					field = new TextField(this, attribute, name, attribute.isReadOnly(), value, hidden);
				}
				else if(attribute instanceof ItemAttribute)
				{
					field = new ItemField(attribute, name, attribute.isReadOnly(), value, hidden, provider.getModel(), cop);
				}
				else
				{
					field = new Field(this, attribute, name, attribute.isReadOnly(), value, hidden);
				}
			}
			else if(anyAttribute instanceof MediaAttribute)
			{
				final MediaAttribute attribute = (MediaAttribute)anyAttribute;
				field = new Field(this, attribute, null, true, "", hiddenAttributes.contains(attribute));
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
							new Field(this, attribute, null, true, valueToString(attribute, qualifiedValue), false);
					}
				}
			}
		}
		
		if(save)
		{
			save();
		}
	}
	
	public class ItemField extends TextField
	{
		final Model model;
		final ItemCop cop;
		final Item content;
		
		public ItemField(final Object key, final String name, final boolean readOnly, final String value, final boolean hidden, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, readOnly, value, hidden);
			this.model = model;
			this.cop = cop;

			if(value.length()>0)
			{
				Item parsed = null;
				try
				{
					parsed = model.findByID(value);
				}
				catch(NoSuchIDException e)
				{
					error = e.getMessage();
				}
				content = error==null ? parsed : null;
			}
			else
				content = null;
		}
		
		public Item getItem()
		{
			return content;
		}
		
		public void write(final PrintStream out) throws IOException
		{
			super.write(out);
			ItemCop_Jspm.write(out, this);
		}
		
		public Object getContent()
		{
			return content;
		}
		
	}
	
	final class EnumField extends RadioField
	{
		final EnumAttribute attribute;

		EnumField(final EnumAttribute attribute, final String value, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), value, hidden);
			
			this.attribute = attribute;
			
			if(!attribute.isNotNull())
			{
				addOption(VALUE_NULL, cop.getDisplayNameNull());
			}
			for(Iterator k = attribute.getValues().iterator(); k.hasNext(); )
			{
				final EnumValue currentValue = (EnumValue)k.next();
				final String currentCode = currentValue.getCode();
				final String currentName = cop.getDisplayName(currentValue);
				addOption(currentCode, currentName);
			}
		}
	
		public Object getContent()
		{
			final String value = this.value;
			if(VALUE_NULL.equals(value))
				return null;
			
			final EnumValue result = attribute.getValue(value);
			if(result==null)
				throw new RuntimeException(value);
			
			return result;
		}

	}
	
	final class BooleanEnumField extends RadioField
	{

		BooleanEnumField(final BooleanAttribute attribute, final String value, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), value, hidden);
			
			addOption(VALUE_NULL, cop.getDisplayNameNull());
			addOption(VALUE_ON, cop.getDisplayNameOn());
			addOption(VALUE_OFF, cop.getDisplayNameOff());
		}
		
		public Object getContent()
		{
			final String value = this.value;
			
			if(VALUE_NULL.equals(value))
				return null;
			else if(VALUE_ON.equals(value))
				return Boolean.TRUE;
			else if(VALUE_OFF.equals(value))
				return Boolean.FALSE;
			else
				throw new RuntimeException(value);
		}
	}
	
	private void save()
	{
		for(Iterator i = getVisibleFields().iterator(); i.hasNext(); )
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
				if(field.error==null)
				{
					try
					{
						final ObjectAttribute attribute = (ObjectAttribute)field.key;
						item.setAttribute(attribute, field.getContent());
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
	
}
