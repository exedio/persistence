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
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfo;

final class RevisionLine
{
	final int number;
	private Revision revision = null;
	
	private String logString = null;
	private TreeMap<String, String> logProperties = null;
	private Date date = null;
	
	private boolean current = false;
	
	RevisionLine(final int number)
	{
		this.number = number;
	}
	
	Revision getRevision()
	{
		return revision;
	}
	
	void setRevision(final Revision revision)
	{
		assert revision!=null;
		assert this.revision==null;
		this.revision = revision;
	}
	
	String getLogString()
	{
		return logString;
	}
	
	TreeMap<String, String> getLogProperties()
	{
		return logProperties;
	}
	
	Date getDate()
	{
		return date;
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
			date = info.getDate();
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
}
