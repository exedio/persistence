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

package com.exedio.cope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public final class Migration
{
	final int revision;
	final String comment;
	final String[] body;
	
	public Migration(final int revision, final String comment, final String... body)
	{
		if(revision<0)
			throw new IllegalArgumentException("revision must not be negative");
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		if(body==null)
			throw new NullPointerException("body must not be null");
		if(body.length==0)
			throw new IllegalArgumentException("body must not be empty");
		
		// make a copy to avoid modifications afterwards
		final String[] bodyCopy = new String[body.length];
		for(int i = 0; i<body.length; i++)
		{
			if(body[i]==null)
				throw new NullPointerException("body must not be null, but was at index " + i);
			bodyCopy[i] = body[i];
		}

		this.revision = revision;
		this.comment = comment;
		this.body = bodyCopy;
	}
	
	/**
	 * @deprecated Use {@link #getRevision()} instead
	 */
	@Deprecated
	public int getVersion()
	{
		return getRevision();
	}

	public int getRevision()
	{
		return revision;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public List<String> getBody()
	{
		return Collections.unmodifiableList(Arrays.asList(body));
	}
	
	@Override
	public String toString()
	{
		return String.valueOf('M') + revision + ':' + comment;
	}
	
	// logs
	
	static final String INFO_MAGIC = "migrationlogv01";
	
	public static final Properties parse(final byte[] info)
	{
		if(info.length<=INFO_MAGIC.length()+1)
			return null;

		if(info[0]!='#')
			return null;
		
		final byte[] magic;
		try
		{
			magic = INFO_MAGIC.getBytes("latin1");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		for(int i = 0; i<magic.length; i++)
			if(info[i+1]!=magic[i])
				return null;
		
		final Properties result = new Properties();
		try
		{
			result.load(new ByteArrayInputStream(info));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}
}
