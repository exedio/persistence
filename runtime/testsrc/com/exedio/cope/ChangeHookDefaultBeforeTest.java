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

public class ChangeHookDefaultBeforeTest extends ChangeHookAbstractTest
{
	@Test void testIt()
	{
		assertEquals(
				"com.exedio.cope.DefaultChangeHook / MyHook(nameOfMODEL)",
				MODEL.getChangeHookString());

		final MyItem i = new MyItem("onCreate");
		assertEvents(
				"Item#beforeNew(onCreate)",
				"Hook#beforeNew(onCreate / Item#beforeNew)",
				"Item#afterNew(" + i + ")",
				"Hook#afterNew(" + i + ")");
		assertEquals("onCreate / Item#beforeNew / Hook#beforeNew", i.getField());

		i.setField("onSet");
		assertEvents(
				"Item#beforeSet(" + i + ",onSet)",
				"Hook#beforeSet(" + i + ",onSet / Item#beforeSet)");
		assertEquals("onSet / Item#beforeSet / Hook#beforeSet", i.getField());

		i.deleteCopeItem();
		assertEvents(
				"Item#beforeDelete(" + i + ")",
				"Hook#beforeDelete(" + i + ")");
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	private static final class MyItem extends Item
	{
		static final StringField field = new StringField().lengthMax(500);

		static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] sv)
		{
			final String value = value(field, sv);
			addEvent("Item#beforeNew(" + value + ")");
			sv[0] = field.map(value + " / Item#beforeNew");
			return sv;
		}

		@Override protected void afterNewCopeItem()
		{
			addEvent("Item#afterNew(" + this + ")");
		}

		@Override protected SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] sv)
		{
			final String value = value(field, sv);
			addEvent("Item#beforeSet(" + this + "," + value + ")");
			sv[0] = field.map(value + " / Item#beforeSet");
			return sv;
		}

		@Override protected void beforeDeleteCopeItem()
		{
			addEvent("Item#beforeDelete(" + this + ")");
		}


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = Model.builder().
			name("nameOfMODEL").
			add(MyItem.TYPE).
			changeHooks(DefaultChangeHook.factory(), MyHook::new).
			build();

	public ChangeHookDefaultBeforeTest()
	{
		super(MODEL);
	}
}
