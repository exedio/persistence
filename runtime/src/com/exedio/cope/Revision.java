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
import java.util.List;
import java.util.Properties;

public final class Revision
{
	final int number;
	final String comment;
	final String[] body;
	
	public Revision(final int number, final String comment, final String... body)
	{
		if(number<=0)
			throw new IllegalArgumentException("number must be greater zero");
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
			final String b = body[i];
			if(b==null)
				throw new NullPointerException("body must not be null, but was at index " + i);
			bodyCopy[i] = b;
		}

		this.number = number;
		this.comment = comment;
		this.body = bodyCopy;
	}

	public int getNumber()
	{
		return number;
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
		return String.valueOf('R') + number + ':' + comment;
	}
	
	/**
	 * @deprecated Use {@link RevisionLog#parse(byte[])} instead
	 */
	@Deprecated
	public static final Properties parse(final byte[] info)
	{
		return RevisionLog.parse(info);
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #getNumber()} instead
	 */
	@Deprecated
	public int getVersion()
	{
		return getNumber();
	}

	/**
	 * @deprecated Use {@link #getNumber()} instead
	 */
	@Deprecated
	public int getRevision()
	{
		return getNumber();
	}
}
