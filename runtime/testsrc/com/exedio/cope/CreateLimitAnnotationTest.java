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

import static com.exedio.cope.TypesBound.newType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CreateLimitAnnotationTest
{
	@Test public void testDefault()
	{
		assertEquals(Integer.MAX_VALUE, DefaultType.getCreateLimit());
	}

	static final Type<DefaultItem> DefaultType = newType(DefaultItem.class);

	static class DefaultItem extends Item
	{
		private static final long serialVersionUID = 1l;
		DefaultItem(final SetValue<?>[] setValues) { super(setValues); }
		DefaultItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testOk()
	{
		assertEquals(5, OkType.getCreateLimit());
	}

	static final Type<OkItem> OkType = newType(OkItem.class);

	@CopeCreateLimit(5)
	static class OkItem extends Item
	{
		private static final long serialVersionUID = 1l;
		OkItem(final SetValue<?>[] setValues) { super(setValues); }
		OkItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testMinimum()
	{
		assertEquals(0, MinimumType.getCreateLimit());
	}

	static final Type<MinimumItem> MinimumType = newType(MinimumItem.class);

	@CopeCreateLimit(0)
	static class MinimumItem extends Item
	{
		private static final long serialVersionUID = 1l;
		MinimumItem(final SetValue<?>[] setValues) { super(setValues); }
		MinimumItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testLessMinimum()
	{
		try
		{
			newType(LessMinimumItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"@CopeCreateLimit of LessMinimumItem must not be negative, but was -1",
					e.getMessage());
		}
	}

	@CopeCreateLimit(-1)
	static class LessMinimumItem extends Item
	{
		private static final long serialVersionUID = 1l;
		LessMinimumItem(final SetValue<?>[] setValues) { super(setValues); }
		LessMinimumItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testSubOk()
	{
		assertEquals(5, SubOkType.getCreateLimit());
	}

	static final Type<SubOkItem> SubOkType = newType(SubOkItem.class);

	static class SubOkItem extends OkItem
	{
		private static final long serialVersionUID = 1l;
		SubOkItem(final SetValue<?>[] setValues) { super(setValues); }
		SubOkItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testSub()
	{
		try
		{
			newType(SubItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"@CopeCreateLimit is allowed on top-level types only, " +
					"but SubItem has super type OkItem",
					e.getMessage());
		}
	}

	@CopeCreateLimit(5)
	static class SubItem extends OkItem
	{
		private static final long serialVersionUID = 1l;
		SubItem(final SetValue<?>[] setValues) { super(setValues); }
		SubItem(final ActivationParameters ap) { super(ap); }
	}
}