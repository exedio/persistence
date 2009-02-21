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
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public abstract class RevisionInfo
{
	final Properties store;
	
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
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	static
	{
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	RevisionInfo(
			final int number,
			final Date date, final Map<String, String> environment)
	{
		final Properties store = new Properties();

		if(number>=0)
			store.setProperty("revision", String.valueOf(number));

		store.setProperty("dateUTC", df.format(date));
		
		this.store = store;
	}
	
	static Map<String, String> makeEnvironment(
			final String hostname,
			final DialectParameters dialectParameters)
	{
		final Properties store = new Properties();
		
		if(hostname!=null)
			store.setProperty("hostname", hostname);
		
		store.setProperty("jdbc.url",  dialectParameters.properties.getDatabaseUrl());
		store.setProperty("jdbc.user", dialectParameters.properties.getDatabaseUser());
		store.setProperty("database.name",    dialectParameters.databaseProductName);
		store.setProperty("database.version", dialectParameters.databaseProductVersion);
		store.setProperty("database.version.major", String.valueOf(dialectParameters.databaseMajorVersion));
		store.setProperty("database.version.minor", String.valueOf(dialectParameters.databaseMinorVersion));
		store.setProperty("driver.name",    dialectParameters.driverName);
		store.setProperty("driver.version", dialectParameters.driverVersion);
		store.setProperty("driver.version.major", String.valueOf(dialectParameters.driverMajorVersion));
		store.setProperty("driver.version.minor", String.valueOf(dialectParameters.driverMinorVersion));
		
		return (Map<String, String>)((Map)store);
	}
	
	final byte[] toBytes()
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			store.store(baos, MAGIC);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e); // ByteArrayOutputStream cannot throw IOException
		}
		//try{System.out.println("-----------"+new String(baos.toByteArray(), "latin1")+"-----------");}catch(UnsupportedEncodingException e){throw new RuntimeException(e);};
		return baos.toByteArray();
	}
}
