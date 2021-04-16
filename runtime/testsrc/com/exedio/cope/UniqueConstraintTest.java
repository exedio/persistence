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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class UniqueConstraintTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(UniqueConstraintItem.TYPE);

	public UniqueConstraintTest()
	{
		super(MODEL);
	}

	@Test void search()
	{
		final UniqueConstraintItem item = new UniqueConstraintItem("a", 1);
		assertEquals(item, UniqueConstraintItem.aAndB.search("a", 1));
		assertEquals(null, UniqueConstraintItem.aAndB.search("a", 2));
		assertEquals(null, UniqueConstraintItem.aAndB.search("b", 1));
		assertEquals(item, UniqueConstraintItem.forAAndB("a", 1));
		assertEquals(null, UniqueConstraintItem.forAAndB("a", 2));
		assertEquals(null, UniqueConstraintItem.forAAndB("b", 1));
	}

	@Test void searchStrict()
	{
		final UniqueConstraintItem item = new UniqueConstraintItem("a", 1);
		assertEquals(item, UniqueConstraintItem.aAndB.searchStrict("a", 1));
		try
		{
			UniqueConstraintItem.aAndB.searchStrict("a", 2);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from UniqueConstraintItem where (a='a' AND b='2')", e.getMessage());
		}
		try
		{
			UniqueConstraintItem.aAndB.searchStrict("b", 1);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from UniqueConstraintItem where (a='b' AND b='1')", e.getMessage());
		}
		assertEquals(item, UniqueConstraintItem.forAAndBStrict("a", 1));
		try
		{
			UniqueConstraintItem.forAAndBStrict("a", 2);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from UniqueConstraintItem where (a='a' AND b='2')", e.getMessage());
		}
		try
		{
			UniqueConstraintItem.forAAndBStrict("b", 1);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from UniqueConstraintItem where (a='b' AND b='1')", e.getMessage());
		}
	}

	@SuppressWarnings("null")
	@Test void argumentIsNull()
	{
		try
		{
			UniqueConstraintItem.aAndB.search((Object)null, 0);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueConstraintItem.aAndB for UniqueConstraintItem.a", e.getMessage());
		}
		try
		{
			UniqueConstraintItem.aAndB.search("x", null);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueConstraintItem.aAndB for UniqueConstraintItem.b", e.getMessage());
		}
		try
		{
			UniqueConstraintItem.forAAndB(null, 0);
			fail();
		}
		catch (final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueConstraintItem.aAndB for UniqueConstraintItem.a", e.getMessage());
		}
	}

	@WrapperType(indent=2, comments=false)
	private static class UniqueConstraintItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final StringField a = new StringField();

		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField b = new IntegerField();

		static final UniqueConstraint aAndB = UniqueConstraint.create(a, b);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private UniqueConstraintItem(
					@javax.annotation.Nonnull final java.lang.String a,
					final int b)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				UniqueConstraintItem.a.map(a),
				UniqueConstraintItem.b.map(b),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected UniqueConstraintItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static final UniqueConstraintItem forAAndB(@javax.annotation.Nonnull final java.lang.String a,final int b)
		{
			return UniqueConstraintItem.aAndB.search(UniqueConstraintItem.class,a,b);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final UniqueConstraintItem forAAndBStrict(@javax.annotation.Nonnull final java.lang.String a,final int b)
				throws
					java.lang.IllegalArgumentException
		{
			return UniqueConstraintItem.aAndB.searchStrict(UniqueConstraintItem.class,a,b);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<UniqueConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueConstraintItem.class);

		@com.exedio.cope.instrument.Generated
		protected UniqueConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
