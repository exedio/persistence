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

import static com.exedio.cope.FinalPatternSourceTest.MyItem.pattern;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

public class FinalPatternSourceTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE);

	public FinalPatternSourceTest()
	{
		super(MODEL);
	}

	@Test void testViaPattern()
	{
		final MyItem item = new MyItem(pattern.map("createValue"));
		assertEquals("createValue", pattern.source.get(item));

		try
		{
			item.set(pattern.map("setValue"));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(pattern.source, e.getFeature());
		}
		assertEquals("createValue", pattern.source.get(item));
	}

	@Test void testBypassingPattern()
	{
		final MyItem item = new MyItem(pattern.map("createValue"));
		assertEquals("createValue", pattern.source.get(item));

		try
		{
			item.set(pattern.source.map("setValue"));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(pattern.source, e.getFeature());
		}
		assertEquals("createValue", pattern.source.get(item));
	}


	static final class MyPattern extends Pattern implements Settable<String>
	{
		private final StringField source = new StringField().toFinal().optional();

		MyPattern()
		{
			addSourceFeature(source, "source");
		}

		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			return new SetValue<?>[]{source.map(value)};
		}

		@Override
		public boolean isFinal()
		{
			return false; // would typically return source.isFinal() but this test is for that special scenario
		}

		@Override
		public boolean isMandatory()
		{
			return source.isMandatory();
		}

		@Override
		public java.lang.reflect.Type getInitialType()
		{
			return source.getInitialType();
		}

		@Override
		public boolean isInitial()
		{
			return source.isInitial();
		}

		@Override
		public Set<Class<? extends Throwable>> getInitialExceptions()
		{
			return source.getInitialExceptions();
		}

		private static final long serialVersionUID = 1l;
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final MyPattern pattern = new MyPattern();

		static final StringField field2 = new StringField().optional();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getField2()
		{
			return MyItem.field2.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField2(@javax.annotation.Nullable final java.lang.String field2)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field2.set(this,field2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
