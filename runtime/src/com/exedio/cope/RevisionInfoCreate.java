/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

public final class RevisionInfoCreate extends RevisionInfo
{
	public RevisionInfoCreate(
			final int number,
			final Date date,
			final Map<String, String> environment)
	{
		super(number, date, environment);
		
		if(number<0)
			throw new IllegalArgumentException("number must be greater or equal zero, but was " + number);
	}
	
	private static final String CREATE = "create";
	
	@Override
	Properties getStore()
	{
		final Properties store = super.getStore();
		store.setProperty(CREATE, Boolean.TRUE.toString());
		return store;
	}
	
	static final RevisionInfoCreate read(
			final int number,
			final Date date,
			final Map<String, String> environment,
			final Properties p)
	{
		if(p.getProperty(CREATE)==null)
			return null;
		
		return new RevisionInfoCreate(
				number,
				date,
				environment);
	}
}
