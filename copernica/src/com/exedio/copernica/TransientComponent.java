/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class TransientComponent implements Component
{
	private final String id;
	private final HashMap<TransientLanguage, String> names = new HashMap<TransientLanguage, String>();
	
	TransientComponent(final String id)
	{
		this.id = id;
	}

	public String getCopernicaID()
	{
		return id;
	}
	
	public void putName(final TransientLanguage language, final String name)
	{
		names.put(language, name);
	}
	
	public String getCopernicaName(final CopernicaLanguage displayLanguage)
	{
		{
			final String name = names.get(displayLanguage);
			if(name!=null)
				return name;
		}
		{
			final String name = names.get(this);
			if(name!=null)
				return name;
		}
		return id;
	}

	public String getCopernicaIconURL()
	{
		return null;
	}

}
