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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;

public abstract class TransientCopernicaProvider implements CopernicaProvider
{
	public ConnectToken connect(final ServletConfig config, final String name)
	{
		final Model model = getModel();
		final ConnectToken result = ServletUtil.connect(model, config, name);
		model.migrateIfSupported();
		return result;
	}
	
	// Transient Languages
	
	private HashMap<String, TransientLanguage> transientLanguages = null;
	
	protected final void setTransientLanguages(final TransientLanguage[] languages)
	{
		final HashMap<String, TransientLanguage> result = new HashMap<String, TransientLanguage>(languages.length);
		
		for(int i = 0; i<languages.length; i++)
			result.put(languages[i].getCopernicaID(), languages[i]);
			
		transientLanguages = result;
	}

	public Collection<? extends CopernicaLanguage> getDisplayLanguages()
	{
		return
			transientLanguages == null
				? Collections.<TransientLanguage>emptyList()
				: transientLanguages.values();
	}
	
	public CopernicaLanguage findLanguageByID(final String copernicaID)
	{
		return
			transientLanguages == null
				? null
				: transientLanguages.get(copernicaID);
	}
	
	// Transient Users
	
	private HashMap<String, TransientUser> transientUsers = null;
	
	protected final void setTransientUsers(final TransientUser[] users)
	{
		final HashMap<String, TransientUser> result = new HashMap<String, TransientUser>(users.length);
		
		for(int i = 0; i<users.length; i++)
			result.put(users[i].id, users[i]);
			
		transientUsers = result;
	}

	public CopernicaUser findUserByID(String copernicaID)
	{
		return
			transientUsers == null
				? null
				: transientUsers.get(copernicaID);
	}
	
	public Collection<CopernicaCategory> getRootCategories()
	{
		return Collections.<CopernicaCategory>emptyList();
	}
	
	// Transient Sections
	
	private HashMap<Type, Collection<? extends Field>> transientMainAttributes = null;
	private HashMap<Type, Collection<? extends CopernicaSection>> transientSections = null;
	
	protected final void setSections(final Type type, final Collection<? extends Field> mainAttributes, final Collection<? extends CopernicaSection> sections)
	{
		if(transientMainAttributes==null)
		{
			transientMainAttributes = new HashMap<Type, Collection<? extends Field>>();
			transientSections = new HashMap<Type, Collection<? extends CopernicaSection>>();
		}

		transientMainAttributes.put(type, mainAttributes);
		transientSections.put(type, sections);
	}
	
	public Collection<? extends Field> getMainAttributes(final Type type)
	{
		return transientMainAttributes==null ? null : transientMainAttributes.get(type);
	}
	
	public Collection<? extends CopernicaSection> getSections(final Type type)
	{
		return transientSections==null ? null : transientSections.get(type);
	}
	
	public static final String breakupName(final String name)
	{
		final StringBuilder result = new StringBuilder(name.length());
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
		return breakupName(type.getID());
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
			final StringBuilder result = new StringBuilder();
			final UniqueConstraint uniqueConstraint = (UniqueConstraint)uniqueConstraints.iterator().next();
			boolean first = true;
			for(final FunctionField<?> attribute : uniqueConstraint.getFields())
			{
				if(first)
					first = false;
				else
					result.append(" - ");

				final Object value = item.get(attribute);

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
	
	protected final void putDisplayName(final TransientLanguage transientLanguage, final Enum value, final String name)
	{
		transientLanguage.enumerationValueNames.put(value, name);
	}
	
	public String getDisplayName(final CopernicaLanguage displayLanguage, final Enum value)
	{
		if(displayLanguage instanceof TransientLanguage)
		{
			final TransientLanguage transientLanguage = (TransientLanguage)displayLanguage;
			final String name = transientLanguage.enumerationValueNames.get(value);
			if(name!=null)
				return name;
		}

		return value.name();
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
	{
		final boolean onPage = "jo-man".equals(request.getParameter("display_error"));
		Copernica_Jspm.writeException(out, servlet, e, onPage);
	}


	public int getLimitCeiling(final Type type)
	{
		return 500;
	}
}
