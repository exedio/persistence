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

import static com.exedio.cope.UniqueDoubleNullTest.MyItem.TYPE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperInitial;
import org.junit.jupiter.api.Test;

/**
 * See https://bugs.mysql.com/bug.php?id=8173 as well.
 */
public class UniqueDoubleNullTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(UniqueDoubleNullTest.class, "MODEL");
	}

	public UniqueDoubleNullTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		assumeTrue(!oracle, "not oracle"); // Oracle violates SQL standard about this

		assertEquals(asList(), TYPE.search(null, TYPE.getThis(), true));

		final MyItem aN1 = new MyItem("a", null);
		assertEquals("a", aN1.getString());
		assertAll(aN1);

		final MyItem aN2 = new MyItem("a", null);
		assertEquals("a", aN2.getString());
		assertAll(aN1, aN2);

		final MyItem N11 = new MyItem(null, 1);
		assertAll(aN1, aN2, N11);

		final MyItem N12 = new MyItem(null, 1);
		assertAll(aN1, aN2, N11, N12);

		final MyItem NN1 = new MyItem(null, null);
		assertAll(aN1, aN2, N11, N12, NN1);

		final MyItem NN2 = new MyItem(null, null);
		assertAll(aN1, aN2, N11, N12, NN1, NN2);

		aN1.setString("b");
		assertEquals("b", aN1.getString());

		aN2.setString("b");
		assertEquals("b", aN2.getString());

		aN1.setString(null);
		assertEquals(null, aN1.getString());

		aN2.setString(null);
		assertEquals(null, aN2.getString());
	}

	private static void assertAll(final MyItem... expected)
	{
		assertEquals(asList(expected), TYPE.search(null, TYPE.getThis(), true));
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		@WrapperInitial static final StringField string = new StringField().optional();

		@WrapperInitial static final IntegerField integer = new IntegerField().optional();

		static final UniqueConstraint constraint = new UniqueConstraint(string, integer);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					@javax.annotation.Nullable final java.lang.String string,
					@javax.annotation.Nullable final java.lang.Integer integer)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.string.map(string),
				MyItem.integer.map(integer),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getString()
		{
			return MyItem.string.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setString(@javax.annotation.Nullable final java.lang.String string)
				throws
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.string.set(this,string);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.Integer getInteger()
		{
			return MyItem.integer.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setInteger(@javax.annotation.Nullable final java.lang.Integer integer)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.integer.set(this,integer);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final java.lang.Integer integer)
		{
			return MyItem.constraint.search(MyItem.class,string,integer);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraintStrict(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final java.lang.Integer integer)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint.searchStrict(MyItem.class,string,integer);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
