package com.exedio.copernica;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
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
	static final String SAVE_BUTTON = "SAVE";
	static final String CHECK_BUTTON = "CHECK";
	static final String SECTION = "section";
	
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
		final Model model = provider.getModel();
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
				field = createField((ObjectAttribute)anyAttribute, post, cop, hiddenAttributes.contains(anyAttribute), model);
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
							createField(attribute, value, value.getID()+'.'+attribute.getName(), true, false, cop, false, model);
					}
				}
			}
		}
		
		if(save)
		{
			save();
		}
	}
	
	private final Field createField(
			final ObjectAttribute attribute,
			final boolean post, final ItemCop cop, final boolean hidden, final Model model)
	{
		return createField(attribute, this.item, attribute.getName(), attribute.isReadOnly(), post, cop, hidden, model);
	}
	
	private final Field createField(
			final ObjectAttribute attribute, final Item item, final String name, final boolean readOnly,
			final boolean post, final ItemCop cop, final boolean hidden, final Model model)
	{
		if(attribute instanceof EnumAttribute)
		{
			if(post)
				return new EnumField((EnumAttribute)attribute, hidden, cop);
			else
				return new EnumField((EnumAttribute)attribute, (EnumValue)item.getAttribute(attribute), hidden, cop);
		}
		else if(attribute instanceof BooleanAttribute)
		{
			if(attribute.isNotNull())
			{
				if(post)
					return new CheckboxField(this, attribute, name, readOnly, hidden);
				else
					return new CheckboxField(this, attribute, name, readOnly, ((Boolean)item.getAttribute(attribute)).booleanValue(), hidden);
			}
			else
			{
				if(post)
					return new BooleanEnumField((BooleanAttribute)attribute, hidden, cop);
				else
					return new BooleanEnumField((BooleanAttribute)attribute, (Boolean)item.getAttribute(attribute), hidden, cop);
			}
		}
		else if(attribute instanceof IntegerAttribute)
		{
			if(post)
				return new IntegerField(this, attribute, name, readOnly, hidden);
			else
				return new IntegerField(this, attribute, name, readOnly, (Integer)item.getAttribute(attribute), hidden);
		}
		else if(attribute instanceof LongAttribute)
		{
			if(post)
				return new LongField(this, attribute, name, readOnly, hidden);
			else
				return new LongField(this, attribute, name, readOnly, (Long)item.getAttribute(attribute), hidden);
		}
		else if(attribute instanceof DoubleAttribute)
		{
			if(post)
				return new DoubleField(this, attribute, name, readOnly, hidden);
			else
				return new DoubleField(this, attribute, name, readOnly, (Double)item.getAttribute(attribute), hidden);
		}
		else if(attribute instanceof DateAttribute)
		{
			if(post)
				return new DateField(this, attribute, name, readOnly, hidden);
			else
				return new DateField(this, attribute, name, readOnly, (Date)item.getAttribute(attribute), hidden);
		}
		else if(attribute instanceof StringAttribute)
		{
			if(post)
				return new StringField((StringAttribute)attribute, name, readOnly, hidden);
			else
				return new StringField((StringAttribute)attribute, name, readOnly, (String)item.getAttribute(attribute), hidden);
		}
		else if(attribute instanceof ItemAttribute)
		{
			if(post)
				return new ItemField(attribute, name, readOnly, hidden, model, cop);
			else
				return new ItemField(attribute, name, readOnly, (Item)item.getAttribute(attribute), hidden, model, cop);
		}
		else
		{
			throw new RuntimeException(attribute.getClass().toString());
		}
	}
	
	public class ItemField extends TextField
	{
		final Model model;
		final ItemCop cop;
		final Item content;
		
		/**
		 * Constructs a form field with an inital value.
		 */
		public ItemField(final Object key, final String name, final boolean readOnly, final Item value, final boolean hidden, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, readOnly, (value==null) ? "" : value.getID(), hidden);

			this.model = model;
			this.cop = cop;
			this.content = value;
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		public ItemField(final Object key, final String name, final boolean readOnly, final boolean hidden, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, readOnly, hidden);
			this.model = model;
			this.cop = cop;

			final String value = this.value;
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

		// TODO remove method
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
		private static final String VALUE_NULL = "null";

		final EnumAttribute attribute;
		final EnumValue content;

		/**
		 * Constructs a form field with an inital value.
		 */
		EnumField(final EnumAttribute attribute, final EnumValue value, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), (value==null) ? VALUE_NULL : value.getCode(), hidden);
			
			this.attribute = attribute;
			this.content = value;
			addOptions(cop);
		}
	
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		EnumField(final EnumAttribute attribute, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), hidden);
			
			this.attribute = attribute;
			addOptions(cop);

			final String value = this.value;
			if(VALUE_NULL.equals(value))
				content = null;
			else
			{
				content = attribute.getValue(value);
				if(content==null)
					throw new RuntimeException(value);
			}
		}
		
		private void addOptions(final ItemCop cop)
		{
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
			return content;
		}

	}
	
	final class BooleanEnumField extends RadioField
	{
		private static final String VALUE_NULL = "null";
		private static final String VALUE_ON = "on";
		private static final String VALUE_OFF = "off";
		
		final Boolean content;

		/**
		 * Constructs a form field with an inital value.
		 */
		BooleanEnumField(final BooleanAttribute attribute, final Boolean value, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), value==null ? VALUE_NULL : value.booleanValue() ? VALUE_ON : VALUE_OFF, hidden);
			
			this.content = value;
			addOptions(cop);
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		BooleanEnumField(final BooleanAttribute attribute, final boolean hidden, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), hidden);
			addOptions(cop);

			final String value = this.value;
			if(VALUE_NULL.equals(value))
				content = null;
			else if(VALUE_ON.equals(value))
				content = Boolean.TRUE;
			else if(VALUE_OFF.equals(value))
				content = Boolean.FALSE;
			else
				throw new RuntimeException(value);
		}
		
		private final void addOptions(final ItemCop cop)
		{
			addOption(VALUE_NULL, cop.getDisplayNameNull());
			addOption(VALUE_ON, cop.getDisplayNameOn());
			addOption(VALUE_OFF, cop.getDisplayNameOff());
		}
		
		public Object getContent()
		{
			return content;
		}
	}
	
	// TODO: put StringField in cops framework
	final class StringField extends TextField
	{
		final String content;

		/**
		 * Constructs a form field with an inital value.
		 */
		StringField(final StringAttribute attribute, final String name, final boolean readOnly, final String value, final boolean hidden)
		{
			super(ItemForm.this, attribute, name, readOnly, (value==null) ? "" : value, hidden);
			
			this.content = value;
		}
	
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		StringField(final StringAttribute attribute, final String name, final boolean readOnly, final boolean hidden)
		{
			super(ItemForm.this, attribute, name, readOnly, hidden);
			
			final String value = this.value;
			content = value; // TODO: convert empty string to null
		}
		
		public Object getContent()
		{
			return content;
		}

	}
	
	private void save()
	{
		for(Iterator i = getAllFields().iterator(); i.hasNext(); )
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
	
}
