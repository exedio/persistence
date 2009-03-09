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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class RevisionInfoRevise extends RevisionInfo
{
	private final String comment;
	private final Body[] body;
	
	public static final class Body
	{
		private final String sql;
		private final int rows;
		private final long elapsed;
		
		public Body(final String sql, final int rows, final long elapsed)
		{
			if(sql==null)
				throw new NullPointerException("sql must not be null");
			if(sql.length()==0)
				throw new IllegalArgumentException("sql must not be empty");
			if(rows<0)
				throw new IllegalArgumentException("rows must be greater or equal zero, but was " + rows);
			if(elapsed<0)
				throw new IllegalArgumentException("elapsed must be greater or equal zero, but was " + elapsed);
			
			this.sql = sql;
			this.rows = rows;
			this.elapsed = elapsed;
		}
		
		public String getSQL()
		{
			return sql;
		}
		
		public int getRows()
		{
			return rows;
		}
		
		public long getElapsed()
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
		
		static final Body read(final int index, final Properties p)
		{
			final String bodyPrefix = "body" + index + '.';
			final String sql = p.getProperty(bodyPrefix + "sql");
			if(sql==null)
				return null;
			return new Body(
					sql,
					Integer.valueOf(p.getProperty(bodyPrefix + "rows")),
					Long   .valueOf(p.getProperty(bodyPrefix + "elapsed")));
		}
		
		@Override
		public String toString()
		{
			return sql + '(' + rows + '/' + elapsed + ')';
		}
	}
	
	public RevisionInfoRevise(
			final int number,
			final Date date, final Map<String, String> environment,
			final String comment, final Body... body)
	{
		super(number, date, environment);
		
		if(number<=0)
			throw new IllegalArgumentException("number must be greater zero, but was " + number);
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		if(body==null)
			throw new NullPointerException("body must not be null");
		if(body.length==0)
			throw new IllegalArgumentException("body must not be empty");
		
		// make a copy to avoid modifications afterwards
		final Body[] bodyCopy = new Body[body.length];
		for(int i = 0; i<body.length; i++)
		{
			final Body b = body[i];
			if(b==null)
				throw new NullPointerException("body must not be null, but was at index " + i);
			bodyCopy[i] = b;
		}
		
		this.comment = comment;
		this.body = bodyCopy;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public List<Body> getBody()
	{
		return Collections.unmodifiableList(Arrays.asList(body));
	}
	
	private static final String COMMENT = "comment";
	
	@Override
	Properties getStore()
	{
		final Properties store = super.getStore();
		store.setProperty(COMMENT, comment);
		for(int i = 0; i<body.length; i++)
			body[i].fillStore(i, store);
		return store;
	}
	
	static final RevisionInfoRevise read(
			final int number,
			final Date date,
			final Map<String, String> environment,
			final Properties p)
	{
		final String comment = p.getProperty(COMMENT);
		if(comment==null)
			return null;
		
		final ArrayList<Body> body = new ArrayList<Body>();
		for(int i = 0; ; i++)
		{
			final Body b = Body.read(i, p);
			if(b==null)
				break;
			body.add(b);
		}
				
		return new RevisionInfoRevise(
				number,
				date,
				environment,
				comment,
				body.toArray(new Body[body.size()]));
	}
}
