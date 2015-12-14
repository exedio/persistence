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

package com.exedio.cope.pattern;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MediaPathConditionUnsupportedTest extends CopeAssert
{
	@Test public void testNull()
	{
		try
		{
			AnItem.path.isNull();
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals(
					"condition not supported by AnItem.path of com.exedio.cope.pattern.MediaPathConditionUnsupportedTest$APath",
					e.getMessage());
		}
	}

	@Test public void testNullJoin()
	{
		try
		{
			AnItem.path.isNull((Join)null);
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals(
					"condition not supported by AnItem.path of com.exedio.cope.pattern.MediaPathConditionUnsupportedTest$APath",
					e.getMessage());
		}
	}

	@Test public void testNotNull()
	{
		try
		{
			AnItem.path.isNotNull();
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals(
					"condition not supported by AnItem.path of com.exedio.cope.pattern.MediaPathConditionUnsupportedTest$APath",
					e.getMessage());
		}
	}

	@Test public void testNotNullJoin()
	{
		try
		{
			AnItem.path.isNotNull((Join)null);
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals(
					"condition not supported by AnItem.path of com.exedio.cope.pattern.MediaPathConditionUnsupportedTest$APath",
					e.getMessage());
		}
	}


	static final class APath extends MediaPath
	{
		@Override
		public String getContentType(final Item item)
		{
			throw new RuntimeException();
		}

		@Override
		public void doGetAndCommit(
				final HttpServletRequest request,
				final HttpServletResponse response,
				final Item item)
		{
			throw new RuntimeException();
		}

		private static final long serialVersionUID = 1l;
	}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final APath path = new APath();

		private static final long serialVersionUID = 1l;
		static final Type<?> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
