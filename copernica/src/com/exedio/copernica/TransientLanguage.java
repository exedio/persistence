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

import java.util.HashMap;

public class TransientLanguage
	extends TransientComponent
	implements CopernicaLanguage
{
	final String nullName;
	final String onName;
	final String offName;

	final HashMap<Enum, String> enumerationValueNames = new HashMap<Enum, String>();
	
	public TransientLanguage(final String id, final String nullName, final String onName, final String offName)
	{
		super(id);
		this.nullName = nullName;
		this.onName = onName;
		this.offName = offName;
	}
	
}
