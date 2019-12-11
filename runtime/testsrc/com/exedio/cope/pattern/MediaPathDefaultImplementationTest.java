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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

@SuppressWarnings("Convert2MethodRef") // OK: easier to read
public class MediaPathDefaultImplementationTest
{
	@Test void testIsContentTypeWrapped()
	{
		assertEquals(true, AnItem.path.isContentTypeWrapped());
	}

	@Test void testIsFinal()
	{
		assertEquals(false, AnItem.path.isFinal());
	}

	@Test void testGetLastModified()
	{
		assertEquals(null, AnItem.path.getLastModified(null));
	}

	@Test void testNull()
	{
		assertFails(() ->
			AnItem.path.isNull(),
			UnsupportedOperationException.class,
			"condition not supported by AnItem.path of " + APath.class.getName());
	}

	@Test void testNullJoin()
	{
		assertFails(() ->
			AnItem.path.isNull(null),
			UnsupportedOperationException.class,
			"condition not supported by AnItem.path of " + APath.class.getName());
	}

	@Test void testNotNull()
	{
		assertFails(() ->
			AnItem.path.isNotNull(),
			UnsupportedOperationException.class,
			"condition not supported by AnItem.path of " + APath.class.getName());
	}

	@Test void testNotNullJoin()
	{
		assertFails(() ->
			AnItem.path.isNotNull(null),
			UnsupportedOperationException.class,
			"condition not supported by AnItem.path of " + APath.class.getName());
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

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AnItem extends Item
	{
		@WrapperIgnore
		static final APath path = new APath();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
