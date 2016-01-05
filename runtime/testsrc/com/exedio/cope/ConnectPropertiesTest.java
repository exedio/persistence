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

import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.junit.Test;

public class ConnectPropertiesTest
{
	/**
	 * This tests makes sure, that no properties are changed by accident.
	 * Adapt if necessary.
	 */
	@Test public void testRegression()
	{
		final ConnectProperties p = new ConnectProperties(loadProperties(), null);

		for(final Field field : p.getFields())
		{
			assertTrue(field.getKey(), field.isSpecified());
		}

		p.ensureValidity();
	}

	@Test public void testConnectionUrlMissingPrefix()
	{
		assertConnectionUrlFailure(
				"someUrl",
				"property connection.url in DESC cannot parse someUrl, missing prefix 'jdbc:'",
				null);
	}

	@Test public void testConnectionUrlMissingColon()
	{
		assertConnectionUrlFailure(
				"jdbc:someCode",
				"property connection.url in DESC cannot parse jdbc:someCode, missing second colon",
				null);
	}

	@Test public void testConnectionUrlTwoCharacters()
	{
		assertConnectionUrlFailure(
				"jdbc:a:",
				"property dialect in DESC must have at least two characters, but was a",
				null);
	}

	@Test public void testConnectionUrlClassNotFound()
	{
		assertConnectionUrlFailure(
				"jdbc:classNotFound:",
				"property dialect in DESC class com.exedio.cope.ClassNotFoundDialect not found.",
				ClassNotFoundException.class);
	}

	@Test public void testConnectionUrlClassNotDialect()
	{
		assertConnectionUrlFailure(
				"jdbc:connectPropertiesTestClassNotDialect:",
				"property dialect in DESC " + "class " + ConnectPropertiesTestClassNotDialectDialect.class.getName() +
				" not a subclass of com.exedio.cope.Dialect.",
				null);
	}

	@Test public void testConnectionUrlClassNoConstructor()
	{
		assertConnectionUrlFailure(
				"jdbc:connectPropertiesTestClassNoConstructor:",
				"property dialect in DESC " + "class " + ConnectPropertiesTestClassNoConstructorDialect.class.getName() +
				" does not have the required constructor.",
				NoSuchMethodException.class);
	}

	private static void assertConnectionUrlFailure(
			final String url,
			final String message,
			final Class<? extends Exception> cause)
	{
		final String propKey = "connection.url";
		final Source source =
				desc(cascade(
						source("dialect", "from url"),
						source(propKey, url),
						loadProperties()
				));
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					message,
					e.getMessage());

			final Throwable actualCause = e.getCause();
			assertEquals(cause, actualCause!=null ? actualCause.getClass() : null);
		}
	}

	@Test public void testPostgresqlSearchPath()
	{
		final String propKey = "connection.postgresql.search_path";
		final Source source =
				desc(cascade(
						source(propKey, "123,567"),
						loadProperties()
				));
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property " + propKey + " in DESC value '123,567' contains forbidden comma on position 3.",
					e.getMessage());
		}
	}

	@Test public void testConnectionPoolIdleInitial()
	{
		final String propKey = "connectionPool.idleInitial";
		final Source source =
				desc(cascade(
						source(propKey, "51"),
						loadProperties()
				));
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property " + propKey + " in DESC value must not be greater than connectionPool.idleLimit",
					e.getMessage());
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
				return key1;
			}
		};
	}

	private static Source desc(final Source s)
	{
		return new Source(){
			@Override
			public String get(final String key)
			{
				return s.get(key);
			}
			@Override
			public Collection<String> keySet()
			{
				return s.keySet();
			}
			@Override
			public String getDescription()
			{
				return "DESC";
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

	@Test public void testProbe() throws Exception
	{
		final ConnectProperties p = new ConnectProperties(
				Sources.load(new File("runtime/utiltest.properties")), null);

		final Callable<?> test = p.getProbeTest();
		assertEquals(
				"HSQL Database Engine 2.2.9 " +
				"HSQL Database Engine Driver 2.2.9",
				test.call());
		assertEquals("probe", test.toString());
	}
}
