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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

public final class Revision
{
	final int revision;
	final String comment;
	final String[] body;
	
	public Revision(final int revision, final String comment, final String... body)
	{
		if(revision<=0)
			throw new IllegalArgumentException("revision must be greater zero");
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

	public int getNumber()
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
	
	private static final String INFO_MAGIC = "migrationlogv01";
	
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
	
	static byte[] mutex(
			final Date date, final String hostname, final DialectParameters dialectParameters,
			final int expectedRevision, final int actualRevision)
	{
		final Properties result = newInfo(-1, date, hostname, dialectParameters);
		result.setProperty("mutex", Boolean.TRUE.toString());
		result.setProperty("mutex.expected", String.valueOf(expectedRevision));
		result.setProperty("mutex.actual", String.valueOf(actualRevision));
		return toBytes(result);
	}
	
	static byte[] create(
			final int revision,
			final String hostname, final DialectParameters dialectParameters)
	{
		final Properties result = newInfo(revision, new Date(), hostname, dialectParameters);
		result.setProperty("create", Boolean.TRUE.toString());
		return toBytes(result);
	}
	
	static Properties migrate(
			final int revision,
			final Date date, final String hostname, final DialectParameters dialectParameters,
			final String comment)
	{
		final Properties result = newInfo(revision, date, hostname, dialectParameters);
		result.setProperty("comment", comment);
		return result;
	}
	
	static void migrateSql(final Properties info, final int index, final String sql, final int rows, final long elapsed)
	{
		final String bodyPrefix = "body" + index + '.';
		info.setProperty(bodyPrefix + "sql", sql);
		info.setProperty(bodyPrefix + "rows", String.valueOf(rows));
		info.setProperty(bodyPrefix + "elapsed", String.valueOf(elapsed));
	}
	
	private static Properties newInfo(
			final int revision,
			final Date date, final String hostname, final DialectParameters dialectParameters)
	{
		final Properties result = new Properties();

		if(revision>0)
			result.setProperty("revision", String.valueOf(revision));

		final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		result.setProperty("dateUTC", df.format(date));

		if(hostname!=null)
			result.setProperty("hostname", hostname);
		
		result.setProperty("jdbc.url",  dialectParameters.properties.getDatabaseUrl());
		result.setProperty("jdbc.user", dialectParameters.properties.getDatabaseUser());
		result.setProperty("database.name",    dialectParameters.databaseProductName);
		result.setProperty("database.version", dialectParameters.databaseProductVersion);
		result.setProperty("database.version.major", String.valueOf(dialectParameters.databaseMajorVersion));
		result.setProperty("database.version.minor", String.valueOf(dialectParameters.databaseMinorVersion));
		result.setProperty("driver.name",    dialectParameters.driverName);
		result.setProperty("driver.version", dialectParameters.driverVersion);
		result.setProperty("driver.version.major", String.valueOf(dialectParameters.driverMajorVersion));
		result.setProperty("driver.version.minor", String.valueOf(dialectParameters.driverMinorVersion));
		
		return result;
	}
	
	static byte[] toBytes(final java.util.Properties info)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			info.store(baos, INFO_MAGIC);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e); // ByteArrayOutputStream cannot throw IOException
		}
		//try{System.out.println("-----------"+new String(baos.toByteArray(), "latin1")+"-----------");}catch(UnsupportedEncodingException e){throw new RuntimeException(e);};
		return baos.toByteArray();
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
