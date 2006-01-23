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

package com.exedio.cope.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Properties
{
	private final java.util.Properties properties;
	private final String source;
	private final ArrayList fields = new ArrayList();
	
	public Properties(final java.util.Properties properties, final String source)
	{
		this.properties = properties;
		this.source = source;
		
		// TODO check, that no other property key do occur
	}
	
	public final String getSource()
	{
		return source;
	}

	public final List getFields()
	{
		return Collections.unmodifiableList(fields);
	}
	
	public abstract class Field
	{
		private final String key;
		private final boolean specified;
		
		Field(final String key)
		{
			this.key = key;
			this.specified = properties.containsKey(key);

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
			
			final String s = properties.getProperty(key);
			if(s==null)
				this.value = defaultValue;
			else
			{
				if(s.equals("true"))
					this.value = true;
				else if(s.equals("false"))
					this.value = false;
				else
					throw new RuntimeException("property "+key+" in "+source+" has invalid value, expected >true< or >false<, but got >"+s+"<.");
			}
		}
		
		public Object getDefaultValue()
		{
			return Boolean.valueOf(defaultValue);
		}
		
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
			
			final String s = properties.getProperty(key);
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
					// TODO wrong text: must read "greater or equal"
					throw new RuntimeException(
							"property " + key + " in " + source + " has invalid value, " +
							"expected an integer greater " + minimumValue + ", but got >" + s + "<.", e);
				}

				// TODO wrong text: must read "greater or equal"
				if(value<minimumValue)
					throw new RuntimeException(
							"property " + key + " in " + source + " has invalid value, " +
							"expected an integer greater " + minimumValue + ", but got " + value + '.');
			}
		}
		
		public Object getDefaultValue()
		{
			//return Integer.valueOf(defaultValue); recommended for JDK 1.5 and later
			return new Integer(defaultValue);
		}
		
		public Object getValue()
		{
			//return Integer.valueOf(value); recommended for JDK 1.5 and later
			return new Integer(value);
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
			
			final String s = properties.getProperty(key);
			if(s==null)
			{
				if(defaultValue==null)
					throw new RuntimeException("property " + key + " in " + source + " not set."); // TODO mention default values
				else
					this.value = defaultValue;
			}
			else
				this.value = s;
		}
		
		public Object getDefaultValue()
		{
			return defaultValue;
		}
		
		public Object getValue()
		{
			return value;
		}
		
		public String getStringValue()
		{
			return value;
		}
		
		public boolean hasHiddenValue()
		{
			return hideValue;
		}
	}

	public final class MapField extends Field
	{
		private final java.util.Properties value;
		
		public MapField(final String key)
		{
			super(key);
			
			final String prefix = key + '.';
			final int prefixLength = prefix.length();

			value = new java.util.Properties();
			for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
			{
				final String currentKey = (String)i.next();
				if(currentKey.startsWith(prefix))
					value.put(currentKey.substring(prefixLength), properties.getProperty(currentKey));
			}
		}
		
		public Object getDefaultValue()
		{
			return null;
		}
		
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

	public void ensureEquality(final Properties other)
	{
		final Iterator j = other.fields.iterator();
		for(Iterator i = fields.iterator(); i.hasNext()&&j.hasNext(); )
		{
			final Field thisField = (Field)i.next();
			final Field otherField = (Field)j.next();
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
				throw new RuntimeException(
						"inconsistent initialization for " + thisField.key +
						" between " + source + " and " + other.source +
						(thisHideValue ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
		}
		
	}
	
}
