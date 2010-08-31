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

import java.util.Collection;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties.Source;

public class PrefixSourceTest extends CopeAssert
{
	private static class MockSource implements Source
	{
		private final List<String> keySet = listg("alpha.one", "prefix.one", "prefix.two", "prefix.");
		private final boolean keySetNull;
		private final String description;

		MockSource(final boolean keySetNull, final String description)
		{
			this.keySetNull = keySetNull;
			this.description = description;
		}

		public String get(final String key)
		{
			assertNotNull(key);

			if(keySet.contains(key))
				return key + "/val";
			else
				return null;
		}

		public Collection<String> keySet()
		{
			return keySetNull ? null : keySet;
		}

		public String getDescription()
		{
			return description;
		}
	};

	public void testIt()
	{
		final MockSource ms = new MockSource(false, "description");
		final PrefixSource ps = new PrefixSource(ms, "prefix.");

		assertEquals("prefix.one/val", ps.get("one"));
		assertEquals("prefix.two/val", ps.get("two"));
		assertEquals("prefix./val", ps.get(""));
		assertEquals(null, ps.get("none"));
		assertEqualsUnmodifiable(list("one", "two", ""), ps.keySet());
		assertEquals("description (prefix prefix.)", ps.getDescription());
	}

	public void testNull()
	{
		final MockSource ms = new MockSource(true, null);
		final PrefixSource ps = new PrefixSource(ms, "prefix.");

		assertEquals("prefix.one/val", ps.get("one"));
		assertEquals("prefix.two/val", ps.get("two"));
		assertEquals("prefix./val", ps.get(""));
		assertEquals(null, ps.get("none"));
		assertEquals(null, ps.keySet());
		assertEquals("unknown prefix prefix.", ps.getDescription());
	}

	public void testFail()
	{
		try
		{
			new PrefixSource(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}

		final MockSource ms = new MockSource(false, "description");
		try
		{
			new PrefixSource(ms, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("prefix", e.getMessage());
		}
	}
}
