package com.exedio.copernica;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;


public abstract class TransientCopernicaProvider implements CopernicaProvider
{
	// Transient Languages
	
	private HashMap transientLanguages = null;
	
	protected void setTransientLanguages(final TransientLanguage[] languages)
	{
		final HashMap result = new HashMap(languages.length);
		
		for(int i = 0; i<languages.length; i++)
			result.put(languages[i].getCopernicaID(), languages[i]);
			
		transientLanguages = result;
	}

	public Collection getDisplayLanguages()
	{
		return
			transientLanguages == null
				? Collections.EMPTY_LIST
				: transientLanguages.values();
	}
	
	public com.exedio.copernica.Language findLanguageByID(final String copernicaID)
	{
		return
			transientLanguages == null
				? null
				: (TransientLanguage)transientLanguages.get(copernicaID);
	}
	
	// Transient Users
	
	private HashMap transientUsers = null;
	
	protected void setTransientUsers(final TransientUser[] users)
	{
		final HashMap result = new HashMap(users.length);
		
		for(int i = 0; i<users.length; i++)
			result.put(users[i].id, users[i]);
			
		transientUsers = result;
	}

	public User findUserByID(String copernicaID)
	{
		return
			transientUsers == null
				? null
				: (TransientUser)transientUsers.get(copernicaID);
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
	
	public String getDisplayNameNull(Language displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).nullName
			: "-";
	}

	public String getDisplayNameOn(Language displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).onName
			: "X";
	}
	
	public String getDisplayNameOff(Language displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).offName
			: "/";
	}

	public String getDisplayName(final com.exedio.copernica.Language displayLanguage, final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return breakupName(className.substring(pos+1));
	}

	public String getDisplayName(final com.exedio.copernica.Language displayLanguage, final Feature feature)
	{
		String name = feature.getName();
		return breakupName(name);
	}
	
	public String getDisplayName(final RequestCache cache, final com.exedio.copernica.Language displayLanguage, final Item item)
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
					valueString = cache.getDisplayName(displayLanguage, (Item)value);
				else
					valueString = value.toString();

				result.append(valueString);
			}
			return result.toString();
		}
	}
	
	protected final void putDisplayName(final TransientLanguage transientLanguage, final EnumerationValue value, final String name)
	{
		transientLanguage.enumerationValueNames.put(value, name);
	}
	
	public String getDisplayName(final Language displayLanguage, final EnumerationValue value)
	{
		if(displayLanguage instanceof TransientLanguage)
		{
			final TransientLanguage transientLanguage = (TransientLanguage)displayLanguage;
			final String name = (String)transientLanguage.enumerationValueNames.get(value);
			if(name!=null)
				return name;
		}

		return value.getCode();
	}

	public String getIconURL(final Type type)
	{
		return null;
	}

	public Category findCategoryByID(final String copernicaID)
	{
		return null;
	}
	
	public Section findSectionByID(final String copernicaID)
	{
		return null;
	}

	public void initializeExampleSystem()
	{
	}

}
