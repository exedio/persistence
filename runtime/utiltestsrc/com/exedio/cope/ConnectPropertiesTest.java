/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import junit.framework.TestCase;

public class ConnectPropertiesTest extends TestCase
{
	/**
	 * This tests makes sure, that no properties are changed by accident.
	 * Adapt if necessary.
	 */
	public void testRegression() throws IOException
	{
		final ConnectProperties p = new ConnectProperties(loadProperties(), null);

		for(final Field field : p.getFields())
		{
			assertTrue(field.getKey(), field.isSpecified());
		}

		p.ensureValidity();
	}

	public void testPostgresqlSearchPath() throws IOException
	{
		final String propKey = "connection.postgresql.search_path";
		final Source source =
				Sources.cascade(
						new Source(){

							@Override
							public String get(final String key)
							{
								if(key.equals(propKey))
									return "123,567";

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
						},
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
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}

	private static Source loadProperties() throws IOException
	{
		return loadProperties(
				ConnectPropertiesTest.class.getResourceAsStream("connectPropertiesTest.properties"));
	}

	private static Source loadProperties(final InputStream stream) throws IOException
	{
		assertNotNull(stream);
		try
		{
			final java.util.Properties result = new java.util.Properties();
			result.load(stream);
			return Sources.view(result, "test");
		}
		finally
		{
			stream.close();
		}
	}
}
