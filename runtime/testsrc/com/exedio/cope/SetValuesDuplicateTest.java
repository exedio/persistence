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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class SetValuesDuplicateTest extends TestWithEnvironment
{
	@Test void testCreateFirstSecond()
	{
		assertFailsDup(() -> new AnItem(FIRST_SECOND));
		assertEquals(asList(), AnItem.TYPE.search());
	}
	@Test void testCreateSameSame()
	{
		assertFailsDup(() -> new AnItem(SAME_SAME));
		assertEquals(asList(), AnItem.TYPE.search());
	}
	@Test void testCreateFirstNull()
	{
		assertFailsDup(() -> new AnItem(FIRST_NULL));
		assertEquals(asList(), AnItem.TYPE.search());
	}
	@Test void testCreateNullSecond()
	{
		assertFailsDup(() -> new AnItem(NULL_SECOND));
		assertEquals(asList(), AnItem.TYPE.search());
	}
	@Test void testCreateNullNull()
	{
		assertFailsDup(() -> new AnItem(NULL_NULL));
		assertEquals(asList(), AnItem.TYPE.search());
	}

	@Test void testSetFirstSecond()
	{
		final AnItem i = new AnItem();
		assertFailsDup(() -> i.set(FIRST_SECOND));
		assertEquals("initial", i.getField());
	}
	@Test void testSetSameSame()
	{
		final AnItem i = new AnItem();
		assertFailsDup(() -> i.set(SAME_SAME));
		assertEquals("initial", i.getField());
	}
	@Test void testSetFirstNull()
	{
		final AnItem i = new AnItem();
		assertFailsDup(() -> i.set(FIRST_NULL));
		assertEquals("initial", i.getField());
	}
	@Test void testSetNullSecond()
	{
		final AnItem i = new AnItem();
		assertFailsDup(() -> i.set(NULL_SECOND));
		assertEquals("initial", i.getField());
	}
	@Test void testSetNullNull()
	{
		final AnItem i = new AnItem();
		assertFailsDup(() -> i.set(NULL_NULL));
		assertEquals("initial", i.getField());
	}

	private static final SetValue<?>[] FIRST_SECOND = { SetValue.map(AnItem.field, "first"), SetValue.map(AnItem.field, "second") };
	private static final SetValue<?>[] SAME_SAME    = { SetValue.map(AnItem.field, "same"),  SetValue.map(AnItem.field, "same")   };
	private static final SetValue<?>[] FIRST_NULL   = { SetValue.map(AnItem.field, "first"), AnItem.field.mapNull()               };
	private static final SetValue<?>[] NULL_SECOND  = { AnItem.field.mapNull(),              SetValue.map(AnItem.field, "second") };
	private static final SetValue<?>[] NULL_NULL    = { AnItem.field.mapNull(),              AnItem.field.mapNull()               };

	private static void assertFailsDup(final Executable executable)
	{
		assertFails(
				executable,
				IllegalArgumentException.class,
				"SetValues contain duplicate settable " + AnItem.field
		);
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap="set", visibility=NONE)
		static final StringField field = new StringField().optional().defaultTo("initial");

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return AnItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	SetValuesDuplicateTest()
	{
		super(MODEL);
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
