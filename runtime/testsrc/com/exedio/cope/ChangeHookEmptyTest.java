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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class ChangeHookEmptyTest extends ChangeHookAbstractTest
{
	@Test void testIt()
	{
		assertEquals(
				"empty",
				MODEL.getChangeHookString());

		final MyItem i = new MyItem("onCreate");
		assertEvents();
		assertEquals("onCreate", i.getField());

		i.setField("onSet");
		assertEvents();
		assertEquals("onSet", i.getField());

		i.deleteCopeItem();
		assertEvents();
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField field = new StringField().lengthMax(500);

		static SetValue<?>[] beforeNewCopeItem(@SuppressWarnings("unused") final SetValue<?>[] sv)
		{
			throw new AssertionFailedError();
		}

		@Override protected void afterNewCopeItem()
		{
			throw new AssertionFailedError();
		}

		@Override protected SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] sv)
		{
			throw new AssertionFailedError();
		}

		@Override protected void beforeDeleteCopeItem()
		{
			throw new AssertionFailedError();
		}


		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = Model.builder().
			name("nameOfMODEL").
			add(MyItem.TYPE).
			changeHooks().
			build();

	public ChangeHookEmptyTest()
	{
		super(MODEL);
	}
}
