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

public interface Component
{
	/**
	 * Returns a string unique for all instances.
	 * Is used by the web application as a persistent qualifier for
	 * building urls and form values.
	 */
	String getCopernicaID();
	
	/**
	 * Returns the name of this component to be displayed to the user.
	 */
	String getCopernicaName(CopernicaLanguage displayLanguage);

	/**
	 * Returns the url of an icon suitable to display with or instead of
	 * the display name.
	 */
	String getCopernicaIconURL();

}
