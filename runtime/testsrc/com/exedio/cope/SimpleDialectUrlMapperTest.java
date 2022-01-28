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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class SimpleDialectUrlMapperTest
{
	@Test void testSingle()
	{
		final Mapper m = new Mapper(new String[]{"proto"}, dialectClass);
		assertSame(dialectClass, m.map("jdbc:proto:"));
		assertSame(dialectClass, m.map("jdbc:proto:X"));
		assertSame(null,         m.map("jdbc:proto"));
		assertSame(null,         m.map("jdbc:prot:"));
		assertSame(null,         m.map(""));
		assertFails(
				() -> m.map(null),
				NullPointerException.class, null);
		assertEquals("jdbc:proto:* -> " + dialectClass.getName(), m.toString());
	}

	@Test void testMultiple()
	{
		final Mapper m = new Mapper(new String[]{"proto1","proto2"}, dialectClass);
		assertSame(dialectClass, m.map("jdbc:proto1:"));
		assertSame(dialectClass, m.map("jdbc:proto2:"));
		assertSame(null,         m.map("jdbc:proto:"));
		assertSame(null,         m.map(""));
		assertFails(
				() -> m.map(null),
				NullPointerException.class, null);
		assertEquals("jdbc:[proto1|proto2]:* -> " + dialectClass.getName(), m.toString());
	}

	@Test void testPrefixNullArray()
	{
		assertFails(
				() -> new Mapper(null, null),
				NullPointerException.class, "subprotocols");
	}

	@Test void testPrefixNull()
	{
		assertFails(
				() -> new Mapper(new String[]{null}, null),
				NullPointerException.class, "subprotocols[0]");
	}

	@Test void testPrefixEmpty()
	{
		assertFails(
				() -> new Mapper(new String[]{""}, null),
				IllegalArgumentException.class, "subprotocols[0] must not be empty");
	}

	@Test void testDialectClassNull()
	{
		assertFails(
				() -> new Mapper(new String[]{"prefix"}, null),
				NullPointerException.class, "dialectClass");
	}


	private static final Class<? extends Dialect> dialectClass = ConnectPropertiesTestClassNoConstructorDialect.class;

	private static final class Mapper extends SimpleDialectUrlMapper
	{
		Mapper(
				final String[] urlPrefix,
				final Class<? extends Dialect> dialectClass)
		{
			super(urlPrefix, dialectClass);
		}
	}
}
