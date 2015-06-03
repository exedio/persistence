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

import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import junit.framework.TestCase;

public class ConnectPropertiesTest extends TestCase
{
	/**
	 * This tests makes sure, that no properties are changed by accident.
	 * Adapt if necessary.
	 */
	public void testRegression()
	{
		final ConnectProperties p = new ConnectProperties(loadProperties(), null);

		for(final Field field : p.getFields())
		{
			assertTrue(field.getKey(), field.isSpecified());
		}

		p.ensureValidity();
	}

	public void testConnectionUrlMissingPrefix()
	{
		assertConnectionUrlFailure(
				"someUrl",
				"cannot parse connection.url=someUrl, missing prefix 'jdbc:'",
				null);
	}

	public void testConnectionUrlMissingColon()
	{
		assertConnectionUrlFailure(
				"jdbc:someCode",
				"cannot parse connection.url=jdbc:someCode, missing second colon",
				null);
	}

	public void testConnectionUrlTwoCharacters()
	{
		assertConnectionUrlFailure(
				"jdbc:a:",
				"dialect from getDescription / getDescription / connectPropertiesTest.properties must have at least two characters, but was a",
				null);
	}

	public void testConnectionUrlClassNotFound()
	{
		assertConnectionUrlFailure(
				"jdbc:classNotFound:",
				"class com.exedio.cope.ClassNotFoundDialect from getDescription / getDescription / connectPropertiesTest.properties not found.",
				ClassNotFoundException.class);
	}

	public void testConnectionUrlClassNotDialect()
	{
		assertConnectionUrlFailure(
				"jdbc:connectPropertiesTestClassNotDialect:",
				"class " + ConnectPropertiesTestClassNotDialectDialect.class.getName() +
				" from getDescription / getDescription / connectPropertiesTest.properties not a subclass of com.exedio.cope.Dialect.",
				null);
	}

	public void testConnectionUrlClassNoConstructor()
	{
		assertConnectionUrlFailure(
				"jdbc:connectPropertiesTestClassNoConstructor:",
				"class " + ConnectPropertiesTestClassNoConstructorDialect.class.getName() +
				" from getDescription / getDescription / connectPropertiesTest.properties does not have the required constructor.",
				NoSuchMethodException.class);
	}

	private static void assertConnectionUrlFailure(
			final String url,
			final String message,
			final Class<? extends Exception> cause)
	{
		final String propKey = "connection.url";
		final Source source =
				Sources.cascade(
						source("dialect", "from url"),
						source(propKey, url),
						loadProperties()
				);
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					message,
					e.getMessage());
			// TODO use IllegalPropertiesException when available in copeutil
			assertEquals(IllegalArgumentException.class, e.getClass());

			final Throwable actualCause = e.getCause();
			assertEquals(cause, actualCause!=null ? actualCause.getClass() : null);
		}
	}

	public void testPostgresqlSearchPath()
	{
		final String propKey = "connection.postgresql.search_path";
		final Source source =
				Sources.cascade(
						source(propKey, "123,567"),
						loadProperties()
				);
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"value for " + propKey + " '123,567' contains forbidden comma on position 3.",
					e.getMessage());
			// TODO use IllegalPropertiesException when available in copeutil
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}

	public void testConnectionPoolIdleInitial()
	{
		final String propKey = "connectionPool.idleInitial";
		final Source source =
				Sources.cascade(
						source(propKey, "51"),
						loadProperties()
				);
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"value for " + propKey + " must not be greater than connectionPool.idleLimit",
					e.getMessage());
			// TODO use IllegalPropertiesException when available in copeutil
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}

	private static Source source(
			final String key1, final String value1)
	{
		return new Source(){
			@Override
			public String get(final String key)
			{
				if(key.equals(key1))
					return value1;

				return null;
			}
			@Override
			public Collection<String> keySet()
			{
				throw new RuntimeException();
			}
			@Override
			public String getDescription()
			{
				return "getDescription";
			}
		};
	}

	private static Source loadProperties()
	{
		final String name = "connectPropertiesTest.properties";
		final Source s = Sources.load(
				ConnectPropertiesTest.class.getResource(name));

		return new Source(){
			@Override public String get(final String key)
			{
				return s.get(key);
			}
			@Override public Collection<String> keySet()
			{
				return s.keySet();
			}
			@Override public String getDescription()
			{
				return name;
			}
		};
	}

	public void testProbe() throws Exception
	{
		final ConnectProperties p = new ConnectProperties(
				Sources.load(new File("runtime/utiltest.properties")), null);

		final Callable<?> test = p.getProbeTest();
		assertEquals(
				"HSQL Database Engine 2.2.9 (2.2) " +
				"HSQL Database Engine Driver 2.2.9 (2.2)",
				test.call());
		assertEquals("probe", test.toString());
	}
}
