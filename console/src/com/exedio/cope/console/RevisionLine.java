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

package com.exedio.cope.console;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoCreate;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.RevisionInfoRevise.Body;

final class RevisionLine
{
	final int number;
	
	private boolean current = false;
	
	private Revision revision = null;
	
	private String logString = null;
	private TreeMap<String, String> logProperties = null;
	private Date date = null;
	private Map<String, String> environment = null;
	private String content;
	private List<Body> body = Collections.<Body>emptyList();
	private int  rows    = -1;
	private long elapsed = -1;
	
	RevisionLine(final int number)
	{
		this.number = number;
	}
	
	boolean isCurrent()
	{
		return current;
	}
	
	void setCurrent()
	{
		assert !current;
		current = true;
	}
	
	boolean hasRevision()
	{
		return revision!=null;
	}
	
	String getContent()
	{
		return content;
	}
	
	void setRevision(final Revision revision)
	{
		assert revision!=null;
		assert this.revision==null;
		this.revision = revision;
		this.content = revision.getComment();
		final ArrayList<Body> body = new ArrayList<Body>();
		for(final String sql : revision.getBody())
			body.add(new Body(sql, 0, 0));
		this.body = body;
	}
	
	String getLogString()
	{
		return logString;
	}
	
	Map<String, String> getLogProperties()
	{
		return logProperties;
	}
	
	Date getDate()
	{
		return date;
	}
	
	Map<String, String> getEnvironment()
	{
		return environment;
	}
	
	int getBodyCount()
	{
		return body!=null ? body.size() : 0;
	}
	
	List<Body> getBody()
	{
		return body;
	}
	
	int getRows()
	{
		return rows;
	}
	
	long getElapsed()
	{
		return elapsed;
	}
	
	void setInfo(final byte[] infoBytes)
	{
		assert infoBytes!=null;
		assert this.logString==null;
		assert this.logProperties==null;
		assert this.date==null;
		
		try
		{
			this.logString = new String(infoBytes, "latin1");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		final Properties infoProperties = RevisionInfo.parse(infoBytes);
		if(infoProperties!=null)
		{
			final TreeMap<String, String> map = new TreeMap<String, String>();
			for(final Map.Entry<Object, Object> entry : infoProperties.entrySet())
				map.put((String)entry.getKey(), (String)entry.getValue());
			this.logProperties = map;
		}
		RevisionInfo info;
		try
		{
			info = RevisionInfo.read(infoBytes);
		}
		catch(Exception e)
		{
			info = null;
		}
		if(info!=null)
		{
			date = info.getDate();
			environment = info.getEnvironment();
			if(info instanceof RevisionInfoRevise)
			{
				final RevisionInfoRevise infoRevise = (RevisionInfoRevise)info;
				this.content = infoRevise.getComment();
				this.body = infoRevise.getBody();
				int rows = 0;
				long elapsed = 0;
				for(final Body body : this.body)
				{
					rows += body.getRows();
					elapsed += body.getElapsed();
				}
				this.rows = rows;
				this.elapsed = elapsed;
			}
			else if(info instanceof RevisionInfoCreate)
			{
				if(content==null)
					content = "Created Schema";
				else
					content = "Created Schema (" + content + ')';
			}
		}
	}
	
	static final <K,V> Set<K> diff(final Map<K,V> left, final Map<K,V> right)
	{
		final TreeSet<K> result = new TreeSet<K>();
		for(final K key : left.keySet())
		{
			if(!right.containsKey(key))
			{
				result.add(key);
			}
			else
			{
				final V l = left.get(key);
				final V r = right.get(key);
				if(r!=null ? !r.equals(l) : l!=null)
					result.add(key);
			}
		}
		for(final K key : right.keySet())
		{
			if(!left.containsKey(key))
				result.add(key);
		}
		return result;
	}
}
