/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.EnumValue;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;

public interface CopernicaProvider
{

	Model getModel();

	/**
	 * Is called once after the constructor.
	 */
	void initialize(ServletConfig config);
	
	/**
	 * @return a collection of {@link CopernicaLanguage languages}.
	 */
	Collection getDisplayLanguages();
	
	/**
	 * @return a collection of {@link CopernicaCategory categories}.
	 */
	Collection getRootCategories();
	
	/**
	 * @return a collection of {@link com.exedio.cope.Attribute attributes}.
	 */
	Collection getMainAttributes(Type type);

	/**
	 * @return a collection of {@link CopernicaSection sections}.
	 */
	Collection getSections(Type type);
	
	String getDisplayNameNull(CopernicaLanguage displayLanguage);

	String getDisplayNameOn(CopernicaLanguage displayLanguage);
	
	String getDisplayNameOff(CopernicaLanguage displayLanguage);

	String getDisplayName(CopernicaLanguage displayLanguage, Type type);

	String getDisplayName(CopernicaLanguage displayLanguage, Feature feature);
	
	String getDisplayName(RequestCache cache, CopernicaLanguage displayLanguage, Item item);

	String getDisplayName(CopernicaLanguage displayLanguage, EnumValue value);

	String getIconURL(Type type);

	CopernicaLanguage findLanguageByID(String copernicaID);
	
	CopernicaUser findUserByID(String copernicaID);

	CopernicaCategory findCategoryByID(String copernicaID);
	
	CopernicaSection findSectionByID(String copernicaID);

	void handleException(PrintStream out, CopernicaServlet servlet, HttpServletRequest request, Exception e)
		throws IOException;
	
	int getLimitCountCeiling(Type type);
	
}
