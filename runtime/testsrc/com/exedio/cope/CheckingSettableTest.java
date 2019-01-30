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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
		final MyItem item = new MyItem(pattern.map("createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(pattern.map("setValue"));
		pattern.assertLogs(new Log("setValue", "setValue", null, null, item,
				"{MyItem.pattern-source=setValue}(" + item + ")"));

		item.set(pattern.map(null));
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
		new MyItem(pattern.map(null));
		pattern.assertLogs(new Log(null, null, null, null, null,
				"{MyItem.pattern-source=null}"));
	}

	@Test void testSource2Create()
	{
		new MyItem(pattern.map("createValue/2"));
		pattern.assertLogs(new Log("createValue/2", "createValue/2", "2(createValue/2)", null, null,
				"{MyItem.pattern-source=createValue/2, MyItem.pattern-source2=2(createValue/2)}"));
	}

	@Test void testSource2Set()
	{
		final MyItem item = new MyItem(pattern.map("createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(pattern.map("setValue/2"));
		pattern.assertLogs(new Log("setValue/2", "setValue/2", "2(setValue/2)", null, item,
				"{MyItem.pattern-source=setValue/2, MyItem.pattern-source2=2(setValue/2)}(" + item + ")"));
	}

	@Test void testSource2DirectlyCreate()
	{
		new MyItem(pattern.source2.map("createValue/2"));
		pattern.assertLogs();
	}

	@Test void testSource2DirectlySet()
	{
		final MyItem item = new MyItem(pattern.map("createValue"));
		pattern.assertLogs(new Log("createValue", "createValue", null, null, null,
				"{MyItem.pattern-source=createValue}"));

		item.set(pattern.source2.map("setValue/2"));
		pattern.assertLogs();
	}

	@Test void testField2()
	{
		final MyItem item = new MyItem(
				pattern.map("createValue"),
				field2.map("createValue2"));
		pattern.assertLogs(new Log("createValue", "createValue", null, "createValue2", null,
				"{MyItem.pattern-source=createValue, MyItem.field2=createValue2}"));

		item.set(
				pattern.map("setValue"),
				field2.map("setValue2"));
		pattern.assertLogs(new Log("setValue", "setValue", null, "setValue2", item,
				"{MyItem.pattern-source=setValue, MyItem.field2=setValue2}(" + item + ")"));

		item.set(
				pattern.map(null),
				field2.map(null));
		pattern.assertLogs(new Log(null, null, null, null, item,
				"{MyItem.pattern-source=null, MyItem.field2=null}(" + item + ")"));
	}

	@Test void testField2Only()
	{
		final MyItem item = new MyItem(field2.map("createValue2"));
		pattern.assertLogs();

		item.set(field2.map("setValue2"));
		pattern.assertLogs();

		item.set(field2.map(null));
		pattern.assertLogs();
	}


	@BeforeEach void before()
	{
		pattern.clearLogs();
	}

	static final class MyPattern extends Pattern implements CheckingSettable<String>
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

		@SuppressFBWarnings("SE_BAD_FIELD")
		private final ArrayList<Log> logs = new ArrayList<>();


		@Override
		public SetValue<?>[] execute(final String value, final Item exceptionItem)
		{
			if(value!=null && value.endsWith("/2"))
				return new SetValue<?>[]{
						source.map(value),
						source2.map("2(" + value + ")")};

			return new SetValue<?>[]{source.map(value)};
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
		@SuppressFBWarnings({"BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS", "NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT"}) // OK: just for testing
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

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final MyPattern pattern = new MyPattern();

		static final StringField field2 = new StringField().optional();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

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

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class OtherItem extends Item
	{
		static final StringField field = new StringField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private OtherItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return OtherItem.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField(@javax.annotation.Nonnull final java.lang.String field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			OtherItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<OtherItem> TYPE = com.exedio.cope.TypesBound.newType(OtherItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private OtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
