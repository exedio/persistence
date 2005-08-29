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

import com.exedio.cope.Attribute;
import com.exedio.cope.AttributeValue;
import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.EnumValue;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.Model;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.Qualifier;
import com.exedio.cope.search.EqualCondition;
import com.exedio.cops.CheckboxField;
import com.exedio.cops.DateField;
import com.exedio.cops.DoubleField;
import com.exedio.cops.Field;
import com.exedio.cops.Form;
import com.exedio.cops.IntegerField;
import com.exedio.cops.LongField;
import com.exedio.cops.RadioField;
import com.exedio.cops.StringField;
import com.exedio.cops.TextField;

final class ItemForm extends Form
{
	static final String SAVE_BUTTON = "SAVE";
	static final String CHECK_BUTTON = "CHECK";
	static final String DELETE_BUTTON = "DELETE";
	static final String SECTION = "section";
	
	final Item item;
	final Type type;
	/*TODO final*/ boolean hasFiles;
	boolean toSave = false;
	final CopernicaSection currentSection;
	final ArrayList visibleFields;
	boolean deleted = false;
	String deletedName = null;
	String deletedError = null;
	
	ItemForm(final ItemCop cop, final HttpServletRequest request)
	{
		super(request);
		
		this.item = cop.item;
		this.type = item.getCopeType();
		final CopernicaProvider provider = cop.provider;
		final Model model = provider.getModel();
		final List displayedAttributes;
		final List hiddenAttributes;
		final Collection sections = provider.getSections(type);
		this.visibleFields = new ArrayList();

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

		final boolean delete = getParameter(DELETE_BUTTON)!=null;
		if(delete)
		{
			deletedName = cop.provider.getDisplayName(cop, cop.language, item);
			try
			{
				item.deleteCopeItem();
				deleted = true;
				return;
			}
			catch(IntegrityViolationException e)
			{
				deletedError = deletedName + " could not be deleted.";
			}
		}

		final boolean save = getParameter(SAVE_BUTTON)!=null;
		final boolean post = save || sectionButton || getParameter(CHECK_BUTTON)!=null;
		boolean hasFilesTemp = false;
		
		for(Iterator j = attributes.iterator(); j.hasNext(); )
		{
			final Attribute anyAttribute = (Attribute)j.next();
			final Field field;
			if(anyAttribute instanceof ObjectAttribute)
			{
				field = createField((ObjectAttribute)anyAttribute, post, cop, model);
			}
			else if(anyAttribute instanceof DataAttribute)
			{
				final DataAttribute attribute = (DataAttribute)anyAttribute;
				field = new StringField(this, attribute, null, true, "");
				if(!attribute.isReadOnly())
				{
					toSave = true;
					hasFilesTemp = true;
				}
			}
			else
				continue;
			
			if(displayedAttributes.contains(anyAttribute))
				visibleFields.add(field);

			if(!field.isReadOnly())
				toSave = true;
		}
		this.hasFiles = hasFilesTemp;

		for(Iterator j = type.getFeatures().iterator(); j.hasNext(); )
		{
			final Feature feature = (Feature)j.next();
			if(feature instanceof Qualifier)
			{
				final Qualifier qualifier = (Qualifier)feature;
				final Collection values = qualifier.getQualifyUnique().getType().search(new EqualCondition(null, qualifier.getParent(), item));
				for(Iterator k = qualifier.getAttributes().iterator(); k.hasNext(); )
				{
					final Attribute anyAttribute = (Attribute)k.next();
					for(Iterator l = values.iterator(); l.hasNext(); )
					{
						final Item value = (Item)l.next();
						if(anyAttribute instanceof ObjectAttribute)
						{
							final ObjectAttribute attribute = (ObjectAttribute)anyAttribute;
							final Object qualifiedValue = value.get(attribute);
							if(qualifiedValue!=null)
								createField(attribute, value, value.getCopeID()+'.'+attribute.getName(), true, false, cop, model);
						}
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
			final boolean post, final ItemCop cop, final Model model)
	{
		return createField(attribute, this.item, attribute.getName(), attribute.isReadOnly(), post, cop, model);
	}
	
	private final Field createField(
			final ObjectAttribute attribute, final Item item, final String name, final boolean readOnly,
			final boolean post, final ItemCop cop, final Model model)
	{
		if(attribute instanceof EnumAttribute)
		{
			if(post)
				return new EnumField((EnumAttribute)attribute, cop);
			else
				return new EnumField((EnumAttribute)attribute, (EnumValue)item.get(attribute), cop);
		}
		else if(attribute instanceof BooleanAttribute)
		{
			if(attribute.isMandatory())
			{
				if(post)
					return new CheckboxField(this, attribute, name, readOnly);
				else
					return new CheckboxField(this, attribute, name, readOnly, ((Boolean)item.get(attribute)).booleanValue());
			}
			else
			{
				if(post)
					return new BooleanEnumField((BooleanAttribute)attribute, cop);
				else
					return new BooleanEnumField((BooleanAttribute)attribute, (Boolean)item.get(attribute), cop);
			}
		}
		else if(attribute instanceof IntegerAttribute)
		{
			if(post)
				return new IntegerField(this, attribute, name, readOnly);
			else
				return new IntegerField(this, attribute, name, readOnly, (Integer)item.get(attribute));
		}
		else if(attribute instanceof LongAttribute)
		{
			if(post)
				return new LongField(this, attribute, name, readOnly);
			else
				return new LongField(this, attribute, name, readOnly, (Long)item.get(attribute));
		}
		else if(attribute instanceof DoubleAttribute)
		{
			if(post)
				return new DoubleField(this, attribute, name, readOnly);
			else
				return new DoubleField(this, attribute, name, readOnly, (Double)item.get(attribute));
		}
		else if(attribute instanceof DateAttribute)
		{
			if(post)
				return new DateField(this, attribute, name, readOnly);
			else
				return new DateField(this, attribute, name, readOnly, (Date)item.get(attribute));
		}
		else if(attribute instanceof StringAttribute)
		{
			if(post)
				return new StringField(this, attribute, name, readOnly);
			else
				return new StringField(this, attribute, name, readOnly, (String)item.get(attribute));
		}
		else if(attribute instanceof ItemAttribute)
		{
			if(post)
				return new ItemField(attribute, name, readOnly, model, cop);
			else
				return new ItemField(attribute, name, readOnly, (Item)item.get(attribute), model, cop);
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
		 * Constructs a form field with an initial value.
		 */
		public ItemField(final Object key, final String name, final boolean readOnly, final Item value, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, readOnly, (value==null) ? "" : value.getCopeID());

			this.model = model;
			this.cop = cop;
			this.content = value;
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		public ItemField(final Object key, final String name, final boolean readOnly, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, readOnly);
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

		public void writeIt(final PrintStream out) throws IOException
		{
			super.writeIt(out);
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
		 * Constructs a form field with an initial value.
		 */
		EnumField(final EnumAttribute attribute, final EnumValue value, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), (value==null) ? VALUE_NULL : value.getCode());
			
			this.attribute = attribute;
			this.content = value;
			addOptions(cop);
		}
	
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		EnumField(final EnumAttribute attribute, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly());
			
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
			if(!attribute.isMandatory())
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
		 * Constructs a form field with an initial value.
		 */
		BooleanEnumField(final BooleanAttribute attribute, final Boolean value, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly(), value==null ? VALUE_NULL : value.booleanValue() ? VALUE_ON : VALUE_OFF);
			
			this.content = value;
			addOptions(cop);
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		BooleanEnumField(final BooleanAttribute attribute, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), attribute.isReadOnly());
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
	

	private void save()
	{
		final ArrayList attributeValues = new ArrayList();
		
		for(Iterator i = getFields().iterator(); i.hasNext(); )
		{
			final Field field = (Field)i.next();
			if(field.key instanceof DataAttribute)
			{
				final DataAttribute attribute = (DataAttribute)field.key;
				final Media entity = Media.get(attribute);
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
							entity.set(item, data, mimeMajor, mimeMinor);
						}
						catch(IOException e)
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
					final ObjectAttribute attribute = (ObjectAttribute)field.key;
					attributeValues.add(new AttributeValue(attribute, field.getContent()));
				}
			}
		}
		try
		{
			item.set((AttributeValue[])attributeValues.toArray(new AttributeValue[attributeValues.size()]));
		}
		catch(MandatoryViolationException e)
		{
			final Field field = getField(e.getNotNullAttribute().getName());
			field.error = "error.notnull:"+e.getNotNullAttribute().toString();
		}
		catch(ReadOnlyViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(UniqueViolationException e)
		{
			final Field field = getField(((ObjectAttribute)e.getConstraint().getUniqueAttributes().iterator().next()).getName());
			field.error = e.getClass().getName();
		}
		catch(LengthViolationException e)
		{
			final Field field = getField(e.getStringAttribute().getName());
			field.error = e.getClass().getName();
		}
	}
	
}
