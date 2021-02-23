/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.TimeZoneStrict;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public abstract class RevisionInfo
{
	private final int number;
	private final String savepoint;
	private final long date;
	private final Map<String, String> environment;

	RevisionInfo(
			final int number,
			final String savepoint,
			final Date date,
			final Map<String, String> environment)
	{
		this.number = number;
		this.savepoint = savepoint;
		this.date = requireNonNull(date, "date").getTime();
		this.environment = requireNonNull(environment, "environment");
	}

	public final int getNumber()
	{
		return number;
	}

	/**
	 * @see Model#getSchemaSavepointNew()
	 */
	public String getSavepoint()
	{
		return savepoint;
	}

	public final Date getDate()
	{
		return new Date(date);
	}

	public final Map<String, String> getEnvironment()
	{
		return Collections.unmodifiableMap(environment);
	}

	private static final String REVISION = "revision";
	private static final String SAVEPOINT = "savepoint";
	private static final String DATE = "dateUTC";
	private static final String ENVIRONMENT_PREFIX = "env.";

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));
		return result;
	}

	Properties getStore()
	{
		final Properties store = new Properties();

		if(number>=0)
			store.setProperty(REVISION, String.valueOf(number));

		if(savepoint!=null)
			store.setProperty(SAVEPOINT, savepoint);

		store.setProperty(DATE, df().format(date));

		for(final Map.Entry<String, String> e : environment.entrySet())
			store.setProperty(ENVIRONMENT_PREFIX + e.getKey(), e.getValue());

		return store;
	}

	private static final String[] DEPRECATED_ENVIRONMENT_KEYS = {
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
		final int revision = revisionString!=null ? Integer.parseInt(revisionString) : -1;
		final Date date;
		try
		{
			date = df().parse(p.getProperty(DATE));
		}
		catch(final ParseException e)
		{
			throw new RuntimeException(e);
		}
		final HashMap<String, String> environment = new HashMap<>();
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
				RevisionInfoRevise.read(revision, p.getProperty(SAVEPOINT), date, environment, p);
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
				RevisionInfoMutex.read(p.getProperty(SAVEPOINT), date, environment, p);
			//noinspection RedundantIfStatement
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
		catch(final IOException e)
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
		catch(final UnsupportedEncodingException e)
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
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}


	final void insert(
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor)
	{
		final com.exedio.dsmf.Dialect dsmfDialect = executor.dialect.dsmfDialect;

		final Statement bf = executor.newStatement();
		bf.append("INSERT INTO ").
			append(dsmfDialect.quoteName(properties.revisionTableName)).
			append('(').
			append(dsmfDialect.quoteName(Revisions.COLUMN_NUMBER_NAME)).
			append(',').
			append(dsmfDialect.quoteName(Revisions.COLUMN_INFO_NAME)).
			append(")VALUES(").
			appendParameter(getNumber()).
			append(',').
			appendParameterBlob(toBytes()).
			append(')');

		final Connection connection = connectionPool.get(true);
		try
		{
			executor.updateStrict(connection, null, bf);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}
