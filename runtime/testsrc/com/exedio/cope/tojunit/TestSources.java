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

package com.exedio.cope.tojunit;

import static com.exedio.cope.util.Sources.checkKey;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ProxyPropertiesSource;
import com.exedio.cope.util.Sources;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.opentest4j.AssertionFailedError;

// TODO move to com.exedio.cope.util.Sources
public final class TestSources
{
	public static Source minimal()
	{
		return Sources.load(new File("runtime/utiltest.properties"));
	}

	public static void setupSchemaMinimal(final Model model)
	{
		assertEquals(
				"jdbc:hsqldb:mem:copeutiltest", // to be used just with utiltest.properties
				model.getConnectProperties().getConnectionUrl());
		try
		{
			try(Connection con = SchemaInfo.newConnection(model);
				 Statement stmt = con.createStatement())
			{
				stmt.execute("DROP SCHEMA PUBLIC CASCADE");
			}
		}
		catch(final SQLException e)
		{
			throw new RuntimeException(e);
		}
		model.createSchema();
	}

	public static Source single(final String key, final boolean value)
	{
		return single(key, String.valueOf(value));
	}

	public static Source single(final String key, final int value)
	{
		return single(key, String.valueOf(value));
	}

	public static Source single(final String key, final long value)
	{
		return single(key, String.valueOf(value));
	}

	public static Source single(final String key, final Enum<?> value)
	{
		return single(key, value.name());
	}

	public static Source single(final String key, final Class<?> value)
	{
		return single(key, value.getName());
	}

	public static Source single(final String key, final File value)
	{
		return single(key, value.getPath());
	}

	public static Source single(final String key, final String value)
	{
		requireNonNull(key);
		requireNonNull(value);

		return new Source(){
			@Override
			public String get(final String keyQueried)
			{
				checkKey(key);

				if(key.equals(keyQueried))
					return value;

				return null;
			}
			@Override
			public Collection<String> keySet()
			{
				return Collections.singleton(key);
			}
			@Override
			public String getDescription()
			{
				return key + '=' + value;
			}
		};
	}

	public static Source erase(final String keyToBeErased, final Source s)
	{
		requireNonNull(keyToBeErased);
		requireNonNull(s);

		return new Source(){
			@Override
			public String get(final String key)
			{
				checkKey(key);

				if(key.equals(keyToBeErased))
					return null;

				return s.get(key);
			}
			@Override
			public Collection<String> keySet()
			{
				final LinkedHashSet<String> result = new LinkedHashSet<>(s.keySet());
				result.remove(keyToBeErased);
				return result;
			}
			@Override
			public String getDescription()
			{
				return s.getDescription() + " without " + keyToBeErased;
			}
		};
	}

	public static Source describe(final String description, final Source s)
	{
		requireNonNull(s);

		return new ProxyPropertiesSource(s){
			@Override
			protected ProxyPropertiesSource reload(final Source reloadedTarget)
			{
				throw new AssertionFailedError();
			}
			@Override
			public String getDescription()
			{
				return description;
			}
			@Override
			public String toString()
			{
				return description;
			}
		};
	}


	private TestSources()
	{
		// prevent instantiation
	}
}
