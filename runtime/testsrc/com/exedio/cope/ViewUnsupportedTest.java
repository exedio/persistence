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

import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ViewUnsupportedTest
{
	@Test void test()
	{
		assertEquals(Integer.class, MyItem.view.getValueClass());
		assertEquals("(sum(" + MyItem.field + ")+5)", MyItem.view.toString());
		assertFails(
				() -> newType(MyItem.class, failingActivator()),
				IllegalArgumentException.class,
				"view contains unsupported function: sum(MyItem.field)");
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		static final IntegerField field = new IntegerField();

		static final View<Integer> view = field.sum().plus(5);

		MyItem(final ActivationParameters ap)
		{
			super(new ActivationParameters(ap.type, ap.pk-1));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final int getField()
		{
			return MyItem.field.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(final int field)
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final Integer getView()
		{
			return MyItem.view.getSupported(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}
}
