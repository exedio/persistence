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

import static com.exedio.cope.pattern.EnumMapField.create;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

public class EnumMapFieldFallbackAnnotationTest
{
	@Test void testOk()
	{
		assertEquals(Ok.fall, create(Ok.class, VALUE).fallback().getFallback());
	}

	@SuppressWarnings("unused")
	enum Ok
	{
		one, @CopeEnumFallback fall, two
	}


	@Test void testMissing()
	{
		final EnumMapField<?,?> f = create(Missing.class, VALUE);
		try
		{
			f.fallback();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"missing @CopeEnumFallback in " + Missing.class.getName(),
					e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	enum Missing
	{
		one, two
	}


	@Test void testDuplicate()
	{
		final EnumMapField<?,?> f = create(Duplicate.class, VALUE);
		try
		{
			f.fallback();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"duplicate @CopeEnumFallback in " + Duplicate.class.getName() + " " +
					"at one and two",
					e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	enum Duplicate
	{
		@CopeEnumFallback one,
		@CopeEnumFallback two
	}



	private static final StringField VALUE = new StringField();
}
