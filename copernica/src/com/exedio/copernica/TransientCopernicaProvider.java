package com.exedio.copernica;

import java.util.Collection;
import java.util.Collections;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Type;


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
					result.append(' ');
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
		return breakupName(type.getID());
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
		return item.toString();
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
