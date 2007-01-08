/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegrityViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.CustomAttributeException;
import com.exedio.cope.pattern.Media;
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
	final Type<? extends Item> type;
	/*TODO final*/ boolean hasFiles;
	boolean toSave = false;
	final CopernicaSection currentSection;
	final List<com.exedio.cope.Field> displayedAttributes;
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
		final List<com.exedio.cope.Field> hiddenAttributes;
		final Collection sections = provider.getSections(type);
		final ArrayList<Field> visibleFields = new ArrayList<Field>();

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

			displayedAttributes = new ArrayList<com.exedio.cope.Field>(provider.getMainAttributes(type));
			hiddenAttributes = new ArrayList<com.exedio.cope.Field>();
			for(Iterator i = sections.iterator(); i.hasNext(); )
			{
				final CopernicaSection section = (CopernicaSection)i.next();
				new Section(section.getCopernicaID(), section.getCopernicaName(cop.language));
				final Collection<? extends com.exedio.cope.Field> sectionAttributes = section.getCopernicaAttributes();
				if(section.equals(currentSection))
					displayedAttributes.addAll(sectionAttributes);
				else
					hiddenAttributes.addAll(sectionAttributes);
			}
		}
		else
		{
			currentSection = null;
			displayedAttributes = type.getFields();
			hiddenAttributes = Collections.<com.exedio.cope.Field>emptyList();
		}
		final ArrayList<com.exedio.cope.Field> attributes = new ArrayList<com.exedio.cope.Field>(displayedAttributes.size()+hiddenAttributes.size());
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
		
		for(final com.exedio.cope.Field anyAttribute : attributes)
		{
			if(!anyAttribute.isFinal())
			{
				if(anyAttribute instanceof FunctionField)
				{
					final Field field = createField((FunctionField)anyAttribute, post, cop, model);
					toSave = true;
					if(displayedAttributes.contains(anyAttribute))
						visibleFields.add(field);
				}
				else if(anyAttribute instanceof DataField)
				{
					toSave = true;
					hasFilesTemp = true;
				}
			}
		}
		this.hasFiles = hasFilesTemp;

		if(save)
			save();
	}
	
	private final Field createField(
			final FunctionField attribute,
			final boolean post, final ItemCop cop, final Model model)
	{
		return createField(attribute, this.item, attribute.getName(), post, cop, model);
	}
	
	private final Field createField(
			final FunctionField attribute, final Item item, final String name,
			final boolean post, final ItemCop cop, final Model model)
	{
		if(attribute.isFinal())
			throw new RuntimeException(attribute.toString());
		
		if(attribute instanceof com.exedio.cope.EnumField)
		{
			final com.exedio.cope.EnumField<? extends Enum> enumAttribute = (com.exedio.cope.EnumField<? extends Enum>)attribute;
			if(post)
				return new EnumField(enumAttribute, cop);
			else
				return new EnumField(enumAttribute, enumAttribute.get(item), cop);
		}
		else if(attribute instanceof BooleanField)
		{
			final BooleanField boolAttribute = (BooleanField)attribute;
			if(attribute.isMandatory())
			{
				if(post)
					return new CheckboxField(this, attribute, name);
				else
					return new CheckboxField(this, attribute, name, boolAttribute.get(item).booleanValue());
			}
			else
			{
				if(post)
					return new BooleanEnumField(boolAttribute, cop);
				else
					return new BooleanEnumField(boolAttribute, boolAttribute.get(item), cop);
			}
		}
		else if(attribute instanceof com.exedio.cope.IntegerField)
		{
			if(post)
				return new IntegerField(this, attribute, name);
			else
				return new IntegerField(this, attribute, name, ((com.exedio.cope.IntegerField)attribute).get(item));
		}
		else if(attribute instanceof com.exedio.cope.LongField)
		{
			if(post)
				return new LongField(this, attribute, name);
			else
				return new LongField(this, attribute, name, ((com.exedio.cope.LongField)attribute).get(item));
		}
		else if(attribute instanceof com.exedio.cope.DoubleField)
		{
			if(post)
				return new DoubleField(this, attribute, name);
			else
				return new DoubleField(this, attribute, name, ((com.exedio.cope.DoubleField)attribute).get(item));
		}
		else if(attribute instanceof com.exedio.cope.DateField)
		{
			if(post)
				return new DateField(this, attribute, name);
			else
				return new DateField(this, attribute, name, ((com.exedio.cope.DateField)attribute).get(item));
		}
		else if(attribute instanceof com.exedio.cope.StringField)
		{
			if(post)
				return new StringField(this, attribute, name);
			else
				return new StringField(this, attribute, name, ((com.exedio.cope.StringField)attribute).get(item));
		}
		else if(attribute instanceof com.exedio.cope.ItemField)
		{
			if(post)
				return new ItemField(attribute, name, model, cop);
			else
				return new ItemField(attribute, name, ((com.exedio.cope.ItemField<? extends Item>)attribute).get(item), model, cop);
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
		public ItemField(final Object key, final String name, final Item value, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name, (value==null) ? "" : value.getCopeID());

			this.model = model;
			this.cop = cop;
			this.content = value;
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		public ItemField(final Object key, final String name, final Model model, final ItemCop cop)
		{
			super(ItemForm.this, key, name);
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

		@Override
		public void writeIt(final PrintStream out)
		{
			super.writeIt(out);
			ItemCop_Jspm.write(out, this);
		}
		
		@Override
		public Object getContent()
		{
			return content;
		}
		
	}
	
	final class EnumField extends RadioField
	{
		private static final String VALUE_NULL = "null";

		final com.exedio.cope.EnumField<? extends Enum> attribute;
		final Enum content;

		/**
		 * Constructs a form field with an initial value.
		 */
		EnumField(final com.exedio.cope.EnumField<? extends Enum> attribute, final Enum value, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), (value==null) ? VALUE_NULL : value.name());
			
			this.attribute = attribute;
			this.content = value;
			addOptions(cop);
		}
	
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		EnumField(final com.exedio.cope.EnumField<? extends Enum> attribute, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName());
			
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
			for(final Enum currentValue : attribute.getValues())
			{
				final String currentCode = currentValue.name();
				final String currentName = cop.getDisplayName(currentValue);
				addOption(currentCode, currentName);
			}
		}
	
		@Override
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
		BooleanEnumField(final BooleanField attribute, final Boolean value, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName(), value==null ? VALUE_NULL : value.booleanValue() ? VALUE_ON : VALUE_OFF);
			
			this.content = value;
			addOptions(cop);
		}
		
		/**
		 * Constructs a form field with a value obtained from the submitted form.
		 */
		BooleanEnumField(final BooleanField attribute, final ItemCop cop)
		{
			super(ItemForm.this, attribute, attribute.getName());
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
		
		@Override
		public Object getContent()
		{
			return content;
		}
	}
	

	private void save()
	{
		final ArrayList<SetValue> setValues = new ArrayList<SetValue>();
		
		for(Iterator i = getFields().iterator(); i.hasNext(); )
		{
			final Field field = (Field)i.next();
			if(field.key instanceof DataField)
			{
				final DataField attribute = (DataField)field.key;
				final Media media = Media.get(attribute);
				final FileItem fileItem = getParameterFile(attribute.getName());
				
				if(fileItem!=null)
				{
					String contentType = fileItem.getContentType();
					if(contentType!=null)
					{
						// fix for MSIE behaviour
						if("image/pjpeg".equals(contentType))
							contentType = "image/jpeg";
						
						try
						{
							final InputStream data = fileItem.getInputStream();
							media.set(item, data, contentType);
						}
						catch(IOException e)
						{
							throw new RuntimeException(e);
						}
					}
				}
			}
			if(field.error==null)
			{
				final FunctionField<?> attribute = (FunctionField)field.key;
				setValues.add(Cope.mapAndCast(attribute, field.getContent()));
			}
		}
		try
		{
			item.set(setValues.toArray(new SetValue[setValues.size()]));
		}
		catch(MandatoryViolationException e)
		{
			final Field field = getFieldByKey(e.getFeature());
			field.error = "error.notnull:"+e.getFeature().toString();
		}
		catch(FinalViolationException e)
		{
			throw new RuntimeException(e);
		}
		catch(UniqueViolationException e)
		{
			final Field field = getFieldByKey(e.getFeature().getFields().iterator().next());
			field.error = e.getClass().getName();
		}
		catch(LengthViolationException e)
		{
			final Field field = getFieldByKey(e.getFeature());
			field.error = e.getClass().getName();
		}
		catch(CustomAttributeException e)
		{
			final Field field = getFieldByKey(e.getFeature());
			field.error = e.getClass().getName();
		}
	}
	
}
