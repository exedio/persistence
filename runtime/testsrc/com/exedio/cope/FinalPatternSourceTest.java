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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
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
		final MyItem item = new MyItem(SetValue.map(pattern, "createValue"));
		assertEquals("createValue", pattern.source.get(item));

		try
		{
			item.set(SetValue.map(pattern, "setValue"));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(pattern.source, e.getFeature());
			assertEquals(pattern.source, e.getFeatureSettable());
		}
		assertEquals("createValue", pattern.source.get(item));
	}

	@Test void testBypassingPattern()
	{
		final MyItem item = new MyItem(SetValue.map(pattern, "createValue"));
		assertEquals("createValue", pattern.source.get(item));

		try
		{
			item.set(SetValue.map(pattern.source, "setValue"));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(pattern.source, e.getFeature());
			assertEquals(pattern.source, e.getFeatureSettable());
		}
		assertEquals("createValue", pattern.source.get(item));
	}


	private static final class MyPattern extends Pattern implements Settable<String>
	{
		private final StringField source = new StringField().toFinal().optional();

		MyPattern()
		{
			addSourceFeature(source, "source");
		}

		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			return new SetValue<?>[]{SetValue.map(source, value)};
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

		@Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final MyPattern pattern = new MyPattern();

		static final StringField field2 = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField2()
		{
			return MyItem.field2.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField2(@javax.annotation.Nullable final java.lang.String field2)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field2.set(this,field2);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
