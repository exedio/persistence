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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class ConnectPropertiesDialectUrlMapperTest
{
	@Test void testA1()
	{
		assertMapped("/A1/", DialectA.class);
	}

	@Test void testA2()
	{
		assertMapped("/A2/", DialectA.class);
	}

	@Test void testB1()
	{
		assertMapped("/B1/", DialectB.class);
	}

	@Test void testB2()
	{
		assertMapped("/B2/", DialectB.class);
	}

	@Test void testA1A2()
	{
		assertMapped("/A1/A2/", DialectA.class);
	}

	@Test void testB1B2()
	{
		assertMapped("/B1/B2/", DialectB.class);
	}

	@Test void testA1B2()
	{
		assertNotMapped("/A1/B2/");
	}

	@Test void testA2B1()
	{
		assertNotMapped("/A2/B1/");
	}


	private static void assertMapped(final String url, final Class<? extends Dialect> dialect)
	{
		final Source source = source(url);
		final ConnectProperties props = ConnectProperties.create(source);
		assertEquals(dialect.getName(), props.getDialect());
	}

	private static void assertNotMapped(final String url)
	{
		final Source source = source(url);
		final IllegalPropertiesException e = assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property dialect in DESC must be specified as there is no default");
		assertEquals(null, e.getCause());
	}

	private static Source source(final String url)
	{
		return describe("DESC", cascade(
				single("connection.url", url),
				TestSources.minimal()));
	}


	@Test void testGetDialectUrlMappers()
	{
		final Iterator<?> i = ConnectProperties.getDialectUrlMappers().iterator();
		assertThrows(
				UnsupportedOperationException.class,
				i::remove);
		{
			final Object dum = i.next();
			assertEquals(HsqldbDialectUrlMapper.class, dum.getClass());
			assertEquals("jdbc:hsqldb:* -> com.exedio.cope.HsqldbDialect", dum.toString());
		}
		{
			final Object dum = i.next();
			assertEquals(MysqlDialectUrlMapper.class, dum.getClass());
			assertEquals("jdbc:mysql:* -> com.exedio.cope.MysqlDialect", dum.toString());
		}
		{
			final Object dum = i.next();
			assertEquals(PostgresqlDialectUrlMapper.class, dum.getClass());
			assertEquals("jdbc:postgresql:* -> com.exedio.cope.PostgresqlDialect", dum.toString());
		}
		{
			final Object dum = i.next();
			assertEquals(Mapper1.class, dum.getClass());
			assertEquals("Mapper1#toString()", dum.toString());
		}
		{
			final Object dum = i.next();
			assertEquals(Mapper2.class, dum.getClass());
			assertEquals("Mapper2#toString()", dum.toString());
		}
		assertFalse(i.hasNext());
	}


	static class DialectA extends AssertionFailedDialect
	{
		DialectA(@SuppressWarnings("unused") final CopeProbe probe){ super(null); }
	}

	static class DialectB extends AssertionFailedDialect
	{
		DialectB(@SuppressWarnings("unused") final CopeProbe probe){ super(null); }
	}

	static class Mapper extends DialectUrlMapper
	{
		final int number;

		Mapper(final int number)
		{
			this.number = number;
		}

		@Override
		Class<? extends Dialect> map(final String url)
		{
			if(url.contains("/A" + number + "/"))
				return DialectA.class;
			if(url.contains("/B" + number + "/"))
				return DialectB.class;

			return null;
		}

		@Override
		public final String toString()
		{
			return getClass().getSimpleName() + "#toString()";
		}
	}

	public static class Mapper1 extends Mapper { public Mapper1() { super(1); } }
	public static class Mapper2 extends Mapper { public Mapper2() { super(2); } }
}
