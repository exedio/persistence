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
import java.io.PrintStream;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.lib.EnumValue;
import com.exedio.cope.lib.Feature;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;

public interface CopernicaProvider
{

	public Model getModel();

	/**
	 * Is called once after the constructor.
	 */
	public void initialize(ServletConfig config);
	
	/**
	 * @return a collection of {@link CopernicaLanguage languages}.
	 */
	public Collection getDisplayLanguages();
	
	/**
	 * @return a collection of {@link CopernicaCategory categories}.
	 */
	public Collection getRootCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.lib.Attribute attributes}.
	 */
	public Collection getMainAttributes(Type type);

	/**
	 * @return a collection of {@link CopernicaSection sections}.
	 */
	public Collection getSections(Type type);
	
	public String getDisplayNameNull(CopernicaLanguage displayLanguage);

	public String getDisplayNameOn(CopernicaLanguage displayLanguage);
	
	public String getDisplayNameOff(CopernicaLanguage displayLanguage);

	public String getDisplayName(CopernicaLanguage displayLanguage, Type type);

	public String getDisplayName(CopernicaLanguage displayLanguage, Feature feature);
	
	public String getDisplayName(RequestCache cache, CopernicaLanguage displayLanguage, Item item);

	public String getDisplayName(CopernicaLanguage displayLanguage, EnumValue value);

	public String getIconURL(Type type);

	public CopernicaLanguage findLanguageByID(String copernicaID);
	
	public CopernicaUser findUserByID(String copernicaID);

	public CopernicaCategory findCategoryByID(String copernicaID);
	
	public CopernicaSection findSectionByID(String copernicaID);

	public void handleException(PrintStream out, CopernicaServlet servlet, HttpServletRequest request, Exception e)
		throws IOException;
}
