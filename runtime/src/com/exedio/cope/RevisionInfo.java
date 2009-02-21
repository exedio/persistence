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
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

public final class RevisionInfo
{
	final Properties info;
	final Properties result;
	
	private static final String MAGIC = "migrationlogv01";
	
	public static final Properties parse(final byte[] info)
	{
		if(info.length<=MAGIC.length()+1)
			return null;

		if(info[0]!='#')
			return null;
		
		final byte[] magic;
		try
		{
			magic = MAGIC.getBytes("latin1");
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
	
	RevisionInfo( // mutex
			final Date date, final String hostname, final DialectParameters dialectParameters,
			final int expectedNumber, final int actualNumber)
	{
		this(-1, date, hostname, dialectParameters);
		result.setProperty("mutex", Boolean.TRUE.toString());
		result.setProperty("mutex.expected", String.valueOf(expectedNumber));
		result.setProperty("mutex.actual", String.valueOf(actualNumber));
	}
	
	RevisionInfo( // create
			final int number,
			final String hostname, final DialectParameters dialectParameters)
	{
		this(number, new Date(), hostname, dialectParameters);
		result.setProperty("create", Boolean.TRUE.toString());
	}
	
	RevisionInfo( // revise
			final int number,
			final Date date, final String hostname, final DialectParameters dialectParameters,
			final String comment)
	{
		this(number, date, hostname, dialectParameters);
		result.setProperty("comment", comment);
	}
	
	void reviseSql(final int index, final String sql, final int rows, final long elapsed)
	{
		final String bodyPrefix = "body" + index + '.';
		info.setProperty(bodyPrefix + "sql", sql);
		info.setProperty(bodyPrefix + "rows", String.valueOf(rows));
		info.setProperty(bodyPrefix + "elapsed", String.valueOf(elapsed));
	}
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	static
	{
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	private RevisionInfo(
			final int number,
			final Date date, final String hostname, final DialectParameters dialectParameters)
	{
		final Properties result = new Properties();

		if(number>=0)
			result.setProperty("revision", String.valueOf(number));

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
		
		this.info = result;
		this.result = result;
	}
	
	static byte[] toBytes(final RevisionInfo info)
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			info.info.store(baos, MAGIC);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e); // ByteArrayOutputStream cannot throw IOException
		}
		//try{System.out.println("-----------"+new String(baos.toByteArray(), "latin1")+"-----------");}catch(UnsupportedEncodingException e){throw new RuntimeException(e);};
		return baos.toByteArray();
	}
}
