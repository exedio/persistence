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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

final class RevisionInfoRevise extends RevisionInfo
{
	private final String comment;
	private final Body[] body;
	
	static final class Body
	{
		private final String sql;
		private final int rows;
		private final long elapsed;
		
		Body(final String sql, final int rows, final long elapsed)
		{
			this.sql = sql;
			this.rows = rows;
			this.elapsed = elapsed;
		}
		
		String getSQL()
		{
			return sql;
		}
		
		int getRows()
		{
			return rows;
		}
		
		long getElapsed()
		{
			return elapsed;
		}
		
		void fillStore(final int index, final Properties store)
		{
			final String bodyPrefix = "body" + index + '.';
			store.setProperty(bodyPrefix + "sql", sql);
			store.setProperty(bodyPrefix + "rows", String.valueOf(rows));
			store.setProperty(bodyPrefix + "elapsed", String.valueOf(elapsed));
		}
	}
	
	RevisionInfoRevise(
			final int number,
			final Date date, final Map<String, String> environment,
			final String comment, final Body... body)
	{
		super(number, date, environment);
		this.comment = comment;
		this.body = body;
	}
	
	final String getComment()
	{
		return comment;
	}
	
	final List<Body> getBody()
	{
		return Collections.unmodifiableList(Arrays.asList(body));
	}
	
	@Override
	Properties getStore()
	{
		final Properties store = super.getStore();
		store.setProperty("comment", comment);
		for(int i = 0; i<body.length; i++)
			body[i].fillStore(i, store);
		return store;
	}
}
