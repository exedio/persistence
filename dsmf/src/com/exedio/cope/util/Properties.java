/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Properties
{
	final Source source;
	final String sourceDescription;
	final ArrayList<Field> fields = new ArrayList<Field>();
	private final Source context;
	
	public Properties(final java.util.Properties source, final String sourceDescription)
	{
		this(source, sourceDescription, null);
	}
	
	public Properties(final java.util.Properties source, final String sourceDescription, final Source context)
	{
		this(getSource(source, sourceDescription), context);
	}
	
	public Properties(final Source source, final Source context)
	{
		this.source = source;
		this.sourceDescription = source.getDescription();
		this.context = context;
		
		// TODO check, that no other property key do occur
	}
	
	public final String getSource()
	{
		return sourceDescription;
	}

	public final List<Field> getFields()
	{
		return Collections.unmodifiableList(fields);
	}

	/**
	 * @throws IllegalStateException if there is no context for these properties.
	 */
	public final Source getContext()
	{
		if(context==null)
			throw new IllegalStateException("no context available");
		
		return context;
	}

	/**
	 * @throws IllegalArgumentException if the context does not contain a value for <tt>key</tt>.
	 * @throws IllegalStateException if there is no context for these properties.
	 * @deprecated Use {@link #getContext()} instead.
	 */
	@Deprecated
	public final String getContext(final String key)
	{
		if(key==null)
			throw new NullPointerException("key must not be null");
		if(context==null)
			throw new IllegalStateException("no context available");
		
		final String result = context.get(key);
		if(result==null)
			throw new IllegalArgumentException("no value available for key >" + key + "< in context " + context.getDescription());
		
		return result;
	}
	
	final String resolve(final String key)
	{
		final String raw = source.get(key);
		if(raw==null || context==null)
			return raw;

		final StringBuilder bf = new StringBuilder();
		int previous = 0;
		for(int dollar = raw.indexOf("${"); dollar>=0; dollar = raw.indexOf("${", previous))
		{
			final int contextKeyBegin = dollar+2;
			final int contextKeyEnd = raw.indexOf('}', contextKeyBegin);
			if(contextKeyEnd<0)
				throw new IllegalArgumentException("missing '}' in " + raw);
			if(contextKeyBegin==contextKeyEnd)
				throw new IllegalArgumentException("${} not allowed in " + raw);
			final String contextKey = raw.substring(contextKeyBegin, contextKeyEnd);
			final String replaced = context.get(contextKey);
			if(replaced==null)
				throw new IllegalArgumentException("key '" + contextKey + "\' not defined by context " + context.getDescription());
			bf.append(raw.substring(previous, dollar)).
				append(replaced);
			previous = contextKeyEnd + 1;
		}
		bf.append(raw.substring(previous));
		return bf.toString();
	}
	
	public interface Source
	{
		String get(String key);

		/**
		 * Returns all keys, for which {@link #get(String)}
		 * does not return null.
		 * This operation is optional -
		 * if this context does not support this operation,
		 * it returns null.
		 * The result is always unmodifiable.
		 */
		Collection<String> keySet();
		
		String getDescription();
	}
	
	/**
	 * @deprecated Use {@link #getSystemPropertySource()} instead
	 */
	@Deprecated
	public static final Source getSystemPropertyContext()
	{
		return getSystemPropertySource();
	}

	public static final Source getSystemPropertySource()
	{
		return new Source(){
			public String get(final String key)
			{
				return System.getProperty(key);
			}
			
			public Collection<String> keySet()
			{
				return null;
			}

			public String getDescription()
			{
				return "java.lang.System.getProperty";
			}

			@Override
			public String toString()
			{
				return "java.lang.System.getProperty";
			}
		};
	}
	
	/**
	 * @deprecated Use {@link #getSource(java.util.Properties,String)} instead
	 */
	@Deprecated
	public static final Source getContext(final java.util.Properties properties, final String description)
	{
		return getSource(properties, description);
	}

	public static final Source getSource(final java.util.Properties properties, final String description)
	{
		return new Source(){
			public String get(final String key)
			{
				return properties.getProperty(key);
			}
			
			public Collection<String> keySet()
			{
				final ArrayList<String> result = new ArrayList<String>();
				for(final Enumeration<?> names = properties.propertyNames(); names.hasMoreElements(); )
					result.add((String)names.nextElement());
				return Collections.unmodifiableList(result);
			}

			public String getDescription()
			{
				return description;
			}

			@Override
			public String toString()
			{
				return description;
			}
		};
	}
	
	public abstract class Field
	{
		final String key;
		private final boolean specified;
		
		Field(final String key)
		{
			this.key = key;
			this.specified = source.get(key)!=null;

			if(key==null)
				throw new NullPointerException("key must not be null.");
			if(key.length()==0)
				throw new RuntimeException("key must not be empty.");
			
			fields.add(this);
		}
		
		public final String getKey()
		{
			return key;
		}
		
		public abstract Object getDefaultValue();
		public abstract Object getValue();

		public final boolean isSpecified()
		{
			return specified;
		}
		
		public boolean hasHiddenValue()
		{
			return false;
		}
	}
	
	public final class BooleanField extends Field
	{
		private final boolean defaultValue;
		private final boolean value;
		
		public BooleanField(final String key, final boolean defaultValue)
		{
			super(key);
			this.defaultValue = defaultValue;
			
			final String s = resolve(key);
			if(s==null)
				this.value = defaultValue;
			else
			{
				if(s.equals("true"))
					this.value = true;
				else if(s.equals("false"))
					this.value = false;
				else
					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " has invalid value, expected >true< or >false<, but got >" + s + "<.");
			}
		}
		
		@Override
		public Object getDefaultValue()
		{
			return Boolean.valueOf(defaultValue);
		}
		
		@Override
		public Object getValue()
		{
			return Boolean.valueOf(value);
		}
		
		public boolean getBooleanValue()
		{
			return value;
		}
	}

	public final class IntField extends Field
	{
		private final int defaultValue;
		private final int value;
		
		public IntField(final String key, final int defaultValue, final int minimumValue)
		{
			super(key);
			this.defaultValue = defaultValue;
			
			if(defaultValue<minimumValue)
				throw new RuntimeException(key+defaultValue+','+minimumValue);
			
			final String s = resolve(key);
			if(s==null)
				value = defaultValue;
			else
			{
				try
				{
					value = Integer.parseInt(s);
				}
				catch(NumberFormatException e)
				{
					throw new IllegalArgumentException(
							"property " + key + " in " + sourceDescription + " has invalid value, " +
							"expected an integer greater or equal " + minimumValue + ", but got >" + s + "<.", e);
				}

				if(value<minimumValue)
					throw new IllegalArgumentException(
							"property " + key + " in " + sourceDescription + " has invalid value, " +
							"expected an integer greater or equal " + minimumValue + ", but got " + value + '.');
			}
		}
		
		@Override
		public Object getDefaultValue()
		{
			return Integer.valueOf(defaultValue);
		}
		
		@Override
		public Object getValue()
		{
			return Integer.valueOf(value);
		}
		
		public int getIntValue()
		{
			return value;
		}
	}

	public final class StringField extends Field
	{
		private final String defaultValue;
		private final boolean hideValue;
		private final String value;
		
		/**
		 * Creates a mandatory string field.
		 */
		public StringField(final String key)
		{
			this(key, null, false);
		}
		
		public StringField(final String key, final String defaultValue)
		{
			this(key, defaultValue, false);

			if(defaultValue==null)
				throw new NullPointerException("defaultValue must not be null.");
		}
		
		/**
		 * Creates a mandatory string field.
		 */
		public StringField(final String key, final boolean hideValue)
		{
			this(key, null, hideValue);
		}
		
		private StringField(final String key, final String defaultValue, final boolean hideValue)
		{
			super(key);
			this.defaultValue = defaultValue;
			this.hideValue = hideValue;
			
			assert !(defaultValue!=null && hideValue);
			
			final String s = resolve(key);
			if(s==null)
			{
				if(defaultValue==null)
					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " not set and no default value specified.");
				else
					this.value = defaultValue;
			}
			else
				this.value = s;
		}
		
		@Override
		public Object getDefaultValue()
		{
			return defaultValue;
		}
		
		@Override
		public Object getValue()
		{
			return value;
		}
		
		public String getStringValue()
		{
			return value;
		}
		
		@Override
		public boolean hasHiddenValue()
		{
			return hideValue;
		}
	}

	public final class FileField extends Field
	{
		private final File value;
		
		public FileField(final String key)
		{
			super(key);

			final String valueString = resolve(key);
			this.value = (valueString==null) ? null : new File(valueString);
		}
		
		@Override
		public Object getDefaultValue()
		{
			return null;
		}
		
		@Override
		public Object getValue()
		{
			return value;
		}
		
		public File getFileValue()
		{
			return value;
		}
		
		@Override
		public boolean hasHiddenValue()
		{
			return false;
		}
	}

	public final class MapField extends Field
	{
		private final java.util.Properties value;
		
		public MapField(final String key)
		{
			super(key);

			value = new java.util.Properties();
			
			final Collection<String> keySet = source.keySet();
			if(keySet==null)
				return;
			
			final String prefix = key + '.';
			final int prefixLength = prefix.length();
			for(Iterator i = keySet.iterator(); i.hasNext(); )
			{
				final String currentKey = (String)i.next();
				if(currentKey.startsWith(prefix))
					value.put(currentKey.substring(prefixLength), resolve(currentKey));
			}
		}
		
		@Override
		public Object getDefaultValue()
		{
			return null;
		}
		
		@Override
		public Object getValue()
		{
			return value;
		}
		
		public java.util.Properties getMapValue()
		{
			return value;
		}
		
		public String getValue(final String key)
		{
			return value.getProperty(key);
		}
	}

	public final void ensureValidity(final String... prefixes)
	{
		final Collection<String> keySet = source.keySet();
		if(keySet==null)
			return;
		
		final HashSet<String> allowedValues = new HashSet<String>();
		final ArrayList<String> allowedPrefixes = new ArrayList<String>();
		
		for(final Field field : fields)
		{
			if(field instanceof MapField)
				allowedPrefixes.add(field.key+'.');
			else
				allowedValues.add(field.key);
		}
		
		if(prefixes!=null)
			allowedPrefixes.addAll(Arrays.asList(prefixes));
		
		for(final Object keyObject : source.keySet())
		{
			final String key = (String)keyObject;
			if(!allowedValues.contains(key))
			{
				boolean error = true;
				for(String allowedPrefix : allowedPrefixes)
				{
					if(key.startsWith(allowedPrefix))
					{
						error = false;
						break;
					}
				}
				if(error)
				{
					// maintain order of fields lost in allowedValues
					final ArrayList<String> allowedValueList = new ArrayList<String>();
					for(final Field field : fields)
						if(!(field instanceof MapField))
							allowedValueList.add(field.key);
					
					throw new IllegalArgumentException("property " + key + " in " + sourceDescription + " is not allowed, but only one of " + allowedValueList + " or one starting with " + allowedPrefixes + '.');
				}
			}
		}
	}
	
	public final void ensureEquality(final Properties other)
	{
		final Iterator<Field> j = other.fields.iterator();
		for(Iterator<Field> i = fields.iterator(); i.hasNext()&&j.hasNext(); )
		{
			final Field thisField = i.next();
			final Field otherField = j.next();
			final boolean thisHideValue = thisField.hasHiddenValue();
			final boolean otherHideValue = otherField.hasHiddenValue();
			
			if(!thisField.key.equals(otherField.key))
				throw new RuntimeException("inconsistent fields");
			if(thisHideValue!=otherHideValue)
				throw new RuntimeException("inconsistent fields with hide value");
			
			final Object thisValue = thisField.getValue();
			final Object otherValue = otherField.getValue();
			
			if((thisValue!=null && !thisValue.equals(otherValue)) ||
				(thisValue==null && otherValue!=null))
				throw new IllegalArgumentException(
						"inconsistent initialization for " + thisField.key +
						" between " + sourceDescription + " and " + other.sourceDescription +
						(thisHideValue ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
		}
	}
	
	public static final java.util.Properties loadProperties(final File file)
	{
		final java.util.Properties result = new java.util.Properties();
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(file);
			result.load(stream);
			return result;
		}
		catch(IOException e)
		{
			throw new RuntimeException("property file "+file.getAbsolutePath()+" not found.", e);
		}
		finally
		{
			if(stream!=null)
			{
				try
				{
					stream.close();
				}
				catch(IOException e) {/*IGNORE*/}
			}
		}
	}
}
