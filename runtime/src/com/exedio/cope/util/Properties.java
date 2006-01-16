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
		public final String key;
		
		Field(final String key)
		{
			this.key = key;
			fields.add(this);
		}
		
		public abstract Object getValue();
	}
	
	public final class BooleanField extends Field
	{
		final boolean defaultValue;
		public final boolean value;
		
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
		
		public Object getValue()
		{
			return Boolean.valueOf(value);
		}
	}

	public final class IntField extends Field
	{
		final int defaultValue;
		public final int value;
		
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
		
		public Object getValue()
		{
			//return Integer.valueOf(value); recommended for JDK 1.5 and later
			return new Integer(value);
		}
	}

	public void ensureEquality(final Properties other)
	{
		final Iterator j = other.fields.iterator();
		for(Iterator i = fields.iterator(); i.hasNext()&&j.hasNext(); )
		{
			final Field thisField = (Field)i.next();
			final Field otherField = (Field)j.next();
			
			if(!thisField.key.equals(otherField.key))
				throw new RuntimeException("inconsistent fields");
			
			final Object thisValue = thisField.getValue();
			final Object otherValue = otherField.getValue();
			
			if((thisValue!=null && !thisValue.equals(otherValue)) ||
				(thisValue==null && otherValue!=null))
				throw new RuntimeException(
						"inconsistent initialization for " + thisField.key +
						" between " + source + " and " + other.source +
						(false/*TODO hideValues*/ ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
		}
		
	}
	
}
