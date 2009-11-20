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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public abstract class RevisionInfo
{
	private final int number;
	private final Date date;
	private final Map<String, String> environment;
	
	RevisionInfo(
			final int number,
			final Date date, final Map<String, String> environment)
	{
		if(date==null)
			throw new NullPointerException("date");
		if(environment==null)
			throw new NullPointerException("environment");
		
		this.number = number;
		this.date = date;
		this.environment = environment;
	}
	
	public final int getNumber()
	{
		return number;
	}
	
	public final Date getDate()
	{
		return date;
	}
	
	public final Map<String, String> getEnvironment()
	{
		return Collections.unmodifiableMap(environment);
	}
	
	private static final String REVISION = "revision";
	private static final String DATE = "dateUTC";
	private static final String ENVIRONMENT_PREFIX = "env.";
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	static
	{
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	Properties getStore()
	{
		final Properties store = new Properties();

		if(number>=0)
			store.setProperty(REVISION, String.valueOf(number));

		store.setProperty(DATE, df.format(date));
		
		for(final Map.Entry<String, String> e : environment.entrySet())
			store.setProperty(ENVIRONMENT_PREFIX + e.getKey(), e.getValue());
		
		return store;
	}
	
	private static final String[] DEPRECATED_ENVIRONMENT_KEYS = new String[]{
				"database.name",
				"database.version",
				"database.version.major",
				"database.version.minor",
				"driver.name",
				"driver.version",
				"driver.version.major",
				"driver.version.minor",
				"hostname",
				"jdbc.url",
				"jdbc.user"		
			};
	
	public static final RevisionInfo read(final byte[] bytes)
	{
		final Properties p = parse(bytes);
		if(p==null)
			return null;
		
		final String revisionString = p.getProperty(REVISION);
		final int revision = revisionString!=null ? Integer.valueOf(revisionString) : -1;
		final Date date;
		try
		{
			date = df.parse(p.getProperty(DATE));
		}
		catch(ParseException e)
		{
			throw new RuntimeException(e);
		}
		final HashMap<String, String> environment = new HashMap<String, String>();
		for(final String key : DEPRECATED_ENVIRONMENT_KEYS)
		{
			final String value = p.getProperty(key);
			if(value!=null)
				environment.put(key, value);
		}
		for(final Object keyObject : p.keySet())
		{
			final String key = (String)keyObject;
			if(key.startsWith(ENVIRONMENT_PREFIX))
				environment.put(key.substring(ENVIRONMENT_PREFIX.length()), p.getProperty(key));
		}
		
		{
			final RevisionInfoRevise i =
				RevisionInfoRevise.read(revision, date, environment, p);
			if(i!=null)
				return i;
		}
		{
			final RevisionInfoCreate i =
				RevisionInfoCreate.read(revision, date, environment, p);
			if(i!=null)
				return i;
		}
		{
			final RevisionInfoMutex i =
				RevisionInfoMutex.read(date, environment, p);
			if(i!=null)
				return i;
		}
		return null;
	}
	
	private static final String MAGIC = "migrationlogv01";
	
	public final byte[] toBytes()
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			getStore().store(baos, MAGIC);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e); // ByteArrayOutputStream cannot throw IOException
		}
		//try{System.out.println("-----------"+new String(baos.toByteArray(), "latin1")+"-----------");}catch(UnsupportedEncodingException e){throw new RuntimeException(e);};
		return baos.toByteArray();
	}
	
	private static final String CHARSET = "latin1";
	
	public static final Properties parse(final byte[] info)
	{
		if(info.length<=MAGIC.length()+1)
			return null;

		if(info[0]!='#')
			return null;
		
		final byte[] magic;
		try
		{
			magic = MAGIC.getBytes(CHARSET);
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(CHARSET, e);
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
