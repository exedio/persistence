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

import static com.exedio.cope.CheckingSettableTest.MyItem.field2;
import static com.exedio.cope.CheckingSettableTest.MyItem.pattern;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class CheckingSettableTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE);

	public CheckingSettableTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final MyItem item = new MyItem(SetValue.map(pattern, "createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(SetValue.map(pattern, "setValue"));
		pattern.assertLogs(new Log("setValue", "setValue", null, null, item,
				"{MyItem.pattern-source=setValue}(" + item + ")"));

		item.set(SetValue.map(pattern, null));
		pattern.assertLogs(new Log(null, null, null, null, item,
				"{MyItem.pattern-source=null}(" + item + ")"));
	}

	@Test void testCreateNone()
	{
		new MyItem();
		pattern.assertLogs();
	}

	@Test void testCreateNull()
	{
		new MyItem(SetValue.map(pattern, null));
		pattern.assertLogs(new Log(null, null, null, null, null,
				"{MyItem.pattern-source=null}"));
	}

	@Test void testSource2Create()
	{
		new MyItem(SetValue.map(pattern, "createValue/2"));
		pattern.assertLogs(new Log("createValue/2", "createValue/2", "2(createValue/2)", null, null,
				"{MyItem.pattern-source=createValue/2, MyItem.pattern-source2=2(createValue/2)}"));
	}

	@Test void testSource2Set()
	{
		final MyItem item = new MyItem(SetValue.map(pattern, "createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(SetValue.map(pattern, "setValue/2"));
		pattern.assertLogs(new Log("setValue/2", "setValue/2", "2(setValue/2)", null, item,
				"{MyItem.pattern-source=setValue/2, MyItem.pattern-source2=2(setValue/2)}(" + item + ")"));
	}

	@Test void testSource2DirectlyCreate()
	{
		new MyItem(SetValue.map(pattern.source2, "createValue/2"));
		pattern.assertLogs();
	}

	@Test void testSource2DirectlySet()
	{
		final MyItem item = new MyItem(SetValue.map(pattern, "createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(SetValue.map(pattern.source2, "setValue/2"));
		pattern.assertLogs();
	}

	@Test void testField2()
	{
		final MyItem item = new MyItem(
				SetValue.map(pattern, "createValue"),
				SetValue.map(field2, "createValue2"));
		pattern.assertLogs(new Log("createValue", "createValue", null, "createValue2", null,
				"{MyItem.pattern-source=createValue, MyItem.field2=createValue2}"));

		item.set(
				SetValue.map(pattern, "setValue"),
				SetValue.map(field2, "setValue2"));
		pattern.assertLogs(new Log("setValue", "setValue", null, "setValue2", item,
				"{MyItem.pattern-source=setValue, MyItem.field2=setValue2}(" + item + ")"));

		item.set(
				SetValue.map(pattern, null),
				SetValue.map(field2, null));
		pattern.assertLogs(new Log(null, null, null, null, item,
				"{MyItem.pattern-source=null, MyItem.field2=null}(" + item + ")"));
	}

	@Test void testField2Only()
	{
		final MyItem item = new MyItem(SetValue.map(field2, "createValue2"));
		pattern.assertLogs();

		item.set(SetValue.map(field2, "setValue2"));
		pattern.assertLogs();

		item.set(SetValue.map(field2, null));
		pattern.assertLogs();
	}


	@BeforeEach void before()
	{
		pattern.clearLogs();
	}

	private static final class MyPattern extends Pattern implements CheckingSettable<String>
	{
		private final StringField source = new StringField().optional();
		private final StringField source2 = new StringField().optional();

		MyPattern()
		{
			addSourceFeature(source, "source");
			addSourceFeature(source2, "source2");
		}

		@Override
		public void check(final String value, final FieldValues fieldValues)
		{
			try
			{
				fieldValues.get(null);
				fail();
			}
			catch(final NullPointerException e)
			{
				assertEquals("field", e.getMessage());
			}
			try
			{
				fieldValues.get(OtherItem.field);
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("field OtherItem.field does not belong to type MyItem", e.getMessage());
			}
			try
			{
				fieldValues.get(new StringField());
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals("feature not mounted", e.getMessage());
			}

			logs.add(new Log(
					value,
					fieldValues.get(source),
					fieldValues.get(source2),
					fieldValues.get(field2),
					fieldValues.getBackingItem(),
					fieldValues.toString()));
		}

		void assertLogs(final Log... expected)
		{
			assertEquals(Arrays.asList(expected), logs);
			logs.clear();
		}

		private void clearLogs()
		{
			logs.clear();
		}

		private final ArrayList<Log> logs = new ArrayList<>();


		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			if(value!=null && value.endsWith("/2"))
				return new SetValue<?>[]{
						SetValue.map(source, value),
						SetValue.map(source2, "2(" + value + ")")};

			return new SetValue<?>[]{SetValue.map(source, value)};
		}

		@Override
		public boolean isFinal()
		{
			return source.isFinal();
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

	static final class Log
	{
		final String value;
		final String sourceValue;
		final String sourceValue2;
		final String fieldValue2;
		final Item item;
		final String toString;

		Log(
				final String value,
				final String sourceValue,
				final String sourceValue2,
				final String fieldValue2,
				final Item item,
				final String toString)
		{
			this.value = value;
			this.sourceValue = sourceValue;
			this.sourceValue2 = sourceValue2;
			this.fieldValue2 = fieldValue2;
			this.item = item;
			this.toString = toString;
		}

		@Override
		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass") // OK: just for testing
		public boolean equals(final Object other)
		{
			final Log o = (Log)other;
			return
					Objects.equals(value, o.value) &&
					Objects.equals(sourceValue, o.sourceValue) &&
					Objects.equals(sourceValue2, o.sourceValue2) &&
					Objects.equals(fieldValue2, o.fieldValue2) &&
					Objects.equals(item, o.item) &&
					Objects.equals(toString, o.toString);
		}

		@Override
		public String toString()
		{
			return value + "/" + sourceValue + "/" + sourceValue2 + "/" + fieldValue2 + "/" + item + "/" + toString;
		}

		@Override
		public int hashCode()
		{
			throw new AssertionFailedError();
		}
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

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class OtherItem extends Item
	{
		static final StringField field = new StringField();

		@com.exedio.cope.instrument.Generated
		private OtherItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return OtherItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			OtherItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<OtherItem> TYPE = com.exedio.cope.TypesBound.newType(OtherItem.class,OtherItem::new);

		@com.exedio.cope.instrument.Generated
		private OtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
