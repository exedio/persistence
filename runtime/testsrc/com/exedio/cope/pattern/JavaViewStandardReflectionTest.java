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

import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaViewStandardReflectionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(JavaViewStandardReflectionTest.class, "MODEL");
	}

	private static final Double d2 = Double.valueOf(2.25d);

	public JavaViewStandardReflectionTest()
	{
		super(MODEL);
	}

	MyItem item;

	@BeforeEach final void setUp()
	{
		item = new MyItem();
	}

	@Test void testNumber()
	{
		assertEquals(asList(new Feature[]{
				MyItem.TYPE.getThis(),
				MyItem.numberString,
				MyItem.number,
				MyItem.numberPrimitive,
			}), MyItem.TYPE.getDeclaredFeatures());
		assertEquals(MyItem.TYPE.getDeclaredFeatures(), MyItem.TYPE.getFeatures());

		assertEquals(MyItem.TYPE, MyItem.number.getType());
		assertEquals("number", MyItem.number.getName());
		assertEquals(null, MyItem.numberString.getPattern());
		assertEquals(Double.class, MyItem.number.getValueType());
		assertEquals(Double.class, MyItem.number.getValueGenericType());
		assertEquals(Double.class, MyItem.numberPrimitive.getValueType());
		assertEquals(Double.class, MyItem.numberPrimitive.getValueGenericType());

		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(MyItem.number.get(item));

		item.setNumberString("2.25");
		assertEquals("2.25", item.getNumberString());
		assertEquals(d2, item.getNumber());
		assertEquals(d2, MyItem.number.get(item));
		assertEqualBits(2.25, item.getNumberPrimitive());
		assertEquals(2.25, MyItem.numberPrimitive.get(item));

		item.setNumberString(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(MyItem.number.get(item));
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final StringField numberString = new StringField().optional();

		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final JavaView number = new JavaView();
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final JavaView numberPrimitive = new JavaView();

		Double getNumber()
		{
			final String s = getNumberString();
			return s!=null ? Double.valueOf(Double.parseDouble(s)) : null;
		}

		double getNumberPrimitive()
		{
			return Double.parseDouble(getNumberString());
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getNumberString()
		{
			return MyItem.numberString.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setNumberString(@javax.annotation.Nullable final java.lang.String numberString)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.numberString.set(this,numberString);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
