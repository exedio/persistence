package com.exedio.copernica;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;


public class TransientCopernicaProvider implements CopernicaProvider
{
	public Collection getDisplayLanguages()
	{
		return Collections.EMPTY_LIST;
	}
	
	public Collection getRootCategories()
	{
		return Collections.EMPTY_LIST;
	}
	
	public Collection getMainAttributes(final Type type)
	{
		return Collections.EMPTY_LIST;
	}

	public Collection getSections(final Type type)
	{
		return Collections.EMPTY_LIST;
	}
	
	public static final String breakupName(final String name)
	{
		final StringBuffer result = new StringBuffer(name.length());
		boolean wordStart = true;
		for(int i=0; i<name.length(); i++)
		{
			final char c = name.charAt(i);

			if(Character.isUpperCase(c))
			{
				if(!wordStart)
					result.append("&nbsp;");
				wordStart = true;
			}
			else
				wordStart = false;

			if(i==0)
				result.append(Character.toUpperCase(c));
			else
				result.append(c);
		}
		return result.toString();
	}
	
	public String getDisplayName(final com.exedio.copernica.Language displayLanguage, final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return breakupName(className.substring(pos+1));
	}

	public String getDisplayName(final com.exedio.copernica.Language displayLanguage, final Attribute attribute)
	{
		String name = attribute.getName();
		if(name.endsWith("Internal"))
			name = name.substring(0, name.length()-"Internal".length());

		return breakupName(name);
	}
	
	public String getDisplayName(final com.exedio.copernica.Language displayLanguage, final Item item)
	{
		final Type type = item.getType();
		final List uniqueConstraints = type.getUniqueConstraints();
		if(uniqueConstraints.isEmpty())
			return item.toString();
		else
		{
			final StringBuffer result = new StringBuffer();
			final UniqueConstraint uniqueConstraint = (UniqueConstraint)uniqueConstraints.iterator().next();
			boolean first = true;
			for(Iterator i = uniqueConstraint.getUniqueAttributes().iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					result.append(" - ");

				final ObjectAttribute attribute = (ObjectAttribute)i.next();
				final Object value = item.getAttribute(attribute);

				final String valueString;
				if(value == null)
					valueString = "NULL";
				else if(value instanceof Item)
					valueString = getDisplayName(displayLanguage, (Item)value);
				else
					valueString = value.toString();

				result.append(valueString);
			}
			return result.toString();
		}
	}
	
	public String getIconURL(final Type type)
	{
		return null;
	}

	public com.exedio.copernica.Language findLanguageByUniqueID(final String uniqueID)
	{
		return null;
	}
	
	public Category findCategoryByUniqueID(final String uniqueID)
	{
		return null;
	}
	
	public Section findSectionByUniqueID(final String uniqueID)
	{
		return null;
	}

	public void initializeExampleSystem()
	{
	}

}
