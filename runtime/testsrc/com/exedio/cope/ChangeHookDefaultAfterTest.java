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

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ChangeHookDefaultAfterTest extends ChangeHookAbstractTest
{
	@Test void testIt()
	{
		assertEquals(
				"MyHook(nameOfMODEL) / com.exedio.cope.DefaultChangeHook",
				MODEL.getChangeHookString());

		final MyItem i = new MyItem("onCreate");
		assertEvents(
				"Hook#beforeNew(onCreate)",
				"Item#beforeNew(onCreate / Hook#beforeNew)",
				"Hook#afterNew(" + i + ")",
				"Item#afterNew(" + i + ")");
		assertEquals("onCreate / Hook#beforeNew / Item#beforeNew", i.getField());

		i.setField("onSet");
		assertEvents(
				"Hook#beforeSet(" + i + ",onSet)",
				"Item#beforeSet(" + i + ",onSet / Hook#beforeSet)");
		assertEquals("onSet / Hook#beforeSet / Item#beforeSet", i.getField());

		i.deleteCopeItem();
		assertEvents(
				"Hook#beforeDelete(" + i + ")",
				"Item#beforeDelete(" + i + ")");
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final StringField field = new StringField().lengthMax(500);

		static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] sv)
		{
			final String value = value(field, sv);
			addEvent("Item#beforeNew(" + value + ")");
			sv[0] = SetValue.map(field, value + " / Item#beforeNew");
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
			sv[0] = SetValue.map(field, value + " / Item#beforeSet");
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
				com.exedio.cope.SetValue.map(MyItem.field,field),
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
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = Model.builder().
			name("nameOfMODEL").
			add(MyItem.TYPE).
			changeHooks(MyHook::new, DefaultChangeHook.factory()).
			build();

	public ChangeHookDefaultAfterTest()
	{
		super(MODEL);
	}
}
