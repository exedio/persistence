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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

public class MediaPathConditionUnsupportedTest
{
	@Test void testNull()
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

	@Test void testNullJoin()
	{
		try
		{
			AnItem.path.isNull(null);
			fail();
		}
		catch(final UnsupportedOperationException e)
		{
			assertEquals(
					"condition not supported by AnItem.path of com.exedio.cope.pattern.MediaPathConditionUnsupportedTest$APath",
					e.getMessage());
		}
	}

	@Test void testNotNull()
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

	@Test void testNotNullJoin()
	{
		try
		{
			AnItem.path.isNotNull(null);
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
		public boolean isMandatory()
		{
			return false;
		}

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

	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final APath path = new APath();

		private static final long serialVersionUID = 1l;
		@SuppressWarnings("unused") // OK: TYPE without Model
		static final Type<?> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
