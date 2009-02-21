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

package com.exedio.cope;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class RevisionInfoRevise extends RevisionInfo
{
	private final String comment;
	final Properties store = new Properties();
	
	RevisionInfoRevise(
			final int number,
			final Date date, final Map<String, String> environment,
			final String comment)
	{
		super(number, date, environment);
		this.comment = comment;
	}
	
	@Override
	Properties getStore()
	{
		final Properties store = super.getStore();
		store.setProperty("comment", comment);
		for(final String k : (Set<String>)(Set)this.store.keySet())
			store.setProperty(k, this.store.getProperty(k));
		return store;
	}
	
	void reviseSql(final int index, final String sql, final int rows, final long elapsed)
	{
		final String bodyPrefix = "body" + index + '.';
		store.setProperty(bodyPrefix + "sql", sql);
		store.setProperty(bodyPrefix + "rows", String.valueOf(rows));
		store.setProperty(bodyPrefix + "elapsed", String.valueOf(elapsed));
	}
}
