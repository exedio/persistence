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
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({
		"NP_NULL_PARAM_DEREF_NONVIRTUAL",
		"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS",
		"RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT"})
public class SimpleDialectUrlMapperTest
{
	@Test void testNormal()
	{
		final Mapper m = new Mapper("prefix", dialectClass);
		assertSame(dialectClass, m.map("prefix"));
		assertSame(dialectClass, m.map("prefixX"));
		assertSame(null,         m.map("prefi"));
		assertSame(null,         m.map(""));
		assertFails(
				() -> m.map(null),
				NullPointerException.class, null);
	}

	@Test void testPrefixNull()
	{
		assertFails(
				() -> new Mapper(null, null),
				NullPointerException.class, "urlPrefix");
	}

	@Test void testPrefixEmpty()
	{
		assertFails(
				() -> new Mapper("", null),
				IllegalArgumentException.class, "urlPrefix must not be empty");
	}

	@Test void testDialectClassNull()
	{
		assertFails(
				() -> new Mapper("prefix", null),
				NullPointerException.class, "dialectClass");
	}


	private static final Class<? extends Dialect> dialectClass = ConnectPropertiesTestClassNoConstructorDialect.class;

	private static final class Mapper extends SimpleDialectUrlMapper
	{
		Mapper(
				final String urlPrefix,
				final Class<? extends Dialect> dialectClass)
		{
			super(urlPrefix, dialectClass);
		}
	}
}
