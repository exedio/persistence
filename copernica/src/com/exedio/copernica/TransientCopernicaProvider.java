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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.EnumValue;
import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueConstraint;


public abstract class TransientCopernicaProvider implements CopernicaProvider
{
	
	public void initialize(final ServletConfig config)
	{
		initialize(getModel(), config);
	}
	
	private static final String DATADIR_PATH = com.exedio.cope.lib.Properties.DATADIR_PATH;
	private static final String MEDIA_URL = com.exedio.cope.lib.Properties.DATADIR_URL;
	
	public static final void initialize(final Model model, final ServletConfig config)
	{
		final ServletContext context = config.getServletContext();
		
		final File propertyFile = new File(context.getRealPath("WEB-INF/cope.properties"));
		
		final Properties p = com.exedio.cope.lib.Properties.loadProperties(propertyFile);
		if("//WEB-APP//".equals(p.getProperty(DATADIR_PATH)))
		{
			final String mediaUrl = p.getProperty(MEDIA_URL);
			// TODO: deal with web applications without media
			if(mediaUrl==null)
				throw new RuntimeException("parameter " + MEDIA_URL + " must exist in "+propertyFile.getAbsolutePath());
			
			p.setProperty(DATADIR_PATH, context.getRealPath(mediaUrl));
		}
			
		model.setPropertiesInitially(
			new com.exedio.cope.lib.Properties(p, propertyFile.getAbsolutePath()));
	}

	// Transient Languages
	
	private HashMap transientLanguages = null;
	
	protected final void setTransientLanguages(final TransientLanguage[] languages)
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
	
	public CopernicaLanguage findLanguageByID(final String copernicaID)
	{
		return
			transientLanguages == null
				? null
				: (TransientLanguage)transientLanguages.get(copernicaID);
	}
	
	// Transient Users
	
	private HashMap transientUsers = null;
	
	protected final void setTransientUsers(final TransientUser[] users)
	{
		final HashMap result = new HashMap(users.length);
		
		for(int i = 0; i<users.length; i++)
			result.put(users[i].id, users[i]);
			
		transientUsers = result;
	}

	public CopernicaUser findUserByID(String copernicaID)
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
	
	// Transient Sections
	
	private HashMap transientMainAttributes = null;
	private HashMap transientSections = null;
	
	protected final void setSections(final Type type, final Collection mainAttributes, final Collection sections)
	{
		if(transientMainAttributes==null)
		{
			transientMainAttributes = new HashMap();
			transientSections = new HashMap();
		}

		transientMainAttributes.put(type, mainAttributes);
		transientSections.put(type, sections);
	}
	
	public Collection getMainAttributes(final Type type)
	{
		return transientMainAttributes==null ? null : (Collection)transientMainAttributes.get(type);
	}
	
	public Collection getSections(final Type type)
	{
		return transientSections==null ? null : (Collection)transientSections.get(type);
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
	
	public String getDisplayNameNull(CopernicaLanguage displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).nullName
			: "-";
	}

	public String getDisplayNameOn(CopernicaLanguage displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).onName
			: "X";
	}
	
	public String getDisplayNameOff(CopernicaLanguage displayLanguage)
	{
		return
			displayLanguage instanceof TransientLanguage
			? ((TransientLanguage)displayLanguage).offName
			: "/";
	}

	public String getDisplayName(final CopernicaLanguage displayLanguage, final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return breakupName(className.substring(pos+1));
	}

	public String getDisplayName(final CopernicaLanguage displayLanguage, final Feature feature)
	{
		String name = feature.getName();
		return breakupName(name);
	}
	
	public String getDisplayName(final RequestCache cache, final CopernicaLanguage displayLanguage, final Item item)
	{
		final Type type = item.getCopeType();
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
	
	protected final void putDisplayName(final TransientLanguage transientLanguage, final EnumValue value, final String name)
	{
		transientLanguage.enumerationValueNames.put(value, name);
	}
	
	public String getDisplayName(final CopernicaLanguage displayLanguage, final EnumValue value)
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

	public CopernicaCategory findCategoryByID(final String copernicaID)
	{
		return null;
	}
	
	public CopernicaSection findSectionByID(final String copernicaID)
	{
		return null;
	}
	
	public void handleException(
			final PrintStream out, final CopernicaServlet servlet,
			final HttpServletRequest request, final Exception e)
		throws IOException
	{
		final boolean onPage = "jo-man".equals(request.getParameter("display_error"));
		Copernica_Jspm.writeException(out, servlet, e, onPage);
	}

}
