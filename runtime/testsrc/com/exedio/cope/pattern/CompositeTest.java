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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static java.lang.Boolean.valueOf;
import static java.lang.Double.valueOf;
import static java.lang.Integer.valueOf;
import static java.lang.Long.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.JavaVersion;
import com.exedio.cope.LongField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
public class CompositeTest
{
	@Test void testCheck()
	{
		assertFails(
				() -> new Value("12345", 5, 0l, 0.0, false),
				StringLengthViolationException.class,
				"length violation, '12345' is too long for " + Value.string4 + ", " +
				"must be at most 4 characters, but was 5.",
				Value.string4);
		assertFails(
				() -> new Value(null, 5, 0l, 0.0, false),
				MandatoryViolationException.class,
				"mandatory violation for " + Value.string4,
				Value.string4);

		final Value value = new Value("1234", 4, 5l, 6.6, false);
		assertEquals("1234", value.getString4());
		assertEquals("defString", value.getStringDefault());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		assertFails(
				() -> value.setString4("12345"),
				StringLengthViolationException.class,
				"length violation, '12345' is too long for " + Value.string4 + ", " +
				"must be at most 4 characters, but was 5.",
				Value.string4);
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		assertFails(
				() -> value.setString4(null),
				MandatoryViolationException.class,
				"mandatory violation for " + Value.string4,
				Value.string4);
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		assertFails(
				() -> value.setIntMax4(5),
				IntegerRangeViolationException.class,
				"range violation, 5 is too big for " + Value.intMax4 + ", " +
				"must be at most 4.",
				Value.intMax4);
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
	}

	@Test void testGetSet()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);

		assertEquals("1234", value.getString4());
		assertEquals("defString", value.getStringDefault());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		assertEquals("1234",         value.get(Value.string4));
		assertEquals(valueOf(4),     value.get(Value.intMax4));
		assertEquals(valueOf(5l),    value.get(Value.longField));
		assertEquals(valueOf(6.6),   value.get(Value.doubleField));
		assertEquals(valueOf(false), value.get(Value.booleanField));
		assertEquals(4,     value.getMandatory(Value.intMax4));
		assertEquals(5l,    value.getMandatory(Value.longField));
		assertEqualBits(6.6,value.getMandatory(Value.doubleField));
		assertEquals(false, value.getMandatory(Value.booleanField));
		assertEquals(null, value.get(Value.intOptional));
		assertEquals(null, value.get(Value.longOptional));
		assertEquals(null, value.get(Value.doubleOptional));
		assertEquals(null, value.get(Value.booleanOptional));
		assertFails(
				() -> value.getMandatory(Value.intOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.longOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.doubleOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.booleanOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name


		value.setIntOptional    (valueOf(44));
		value.setLongOptional   (valueOf(55l));
		value.setDoubleOptional (valueOf(66.0));
		value.setBooleanOptional(valueOf(true));

		assertEquals("1234", value.getString4());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(valueOf(44),   value.getIntOptional());
		assertEquals(valueOf(55l),  value.getLongOptional());
		assertEquals(valueOf(66.0), value.getDoubleOptional());
		assertEquals(valueOf(true), value.getBooleanOptional());

		assertEquals("1234",         value.get(Value.string4));
		assertEquals(valueOf(4),     value.get(Value.intMax4));
		assertEquals(valueOf(5l),    value.get(Value.longField));
		assertEquals(valueOf(6.6),   value.get(Value.doubleField));
		assertEquals(valueOf(false), value.get(Value.booleanField));
		assertEquals(4,     value.getMandatory(Value.intMax4));
		assertEquals(5l,    value.getMandatory(Value.longField));
		assertEqualBits(6.6,value.getMandatory(Value.doubleField));
		assertEquals(false, value.getMandatory(Value.booleanField));
		assertEquals(valueOf(44),   value.get(Value.intOptional));
		assertEquals(valueOf(55l),  value.get(Value.longOptional));
		assertEquals(valueOf(66.0), value.get(Value.doubleOptional));
		assertEquals(valueOf(true), value.get(Value.booleanOptional));
		assertFails(
				() -> value.getMandatory(Value.intOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.longOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.doubleOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
		assertFails(
				() -> value.getMandatory(Value.booleanOptional),
				IllegalArgumentException.class,
				"member is not mandatory"); // TODO message with member name
	}

	@Test void testOverrideDefault()
	{
		final Value value = new Value("overrideDefault", false);

		assertEquals("1234", value.getString4());
		assertEquals("overrideDefault", value.getStringDefault());
		assertEquals("defStringOpt", value.getStringDefaultOptional());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
	}

	@Test void testOverrideDefaultOptional()
	{
		final Value value = new Value("overrideDefault", true);

		assertEquals("1234", value.getString4());
		assertEquals("defString", value.getStringDefault());
		assertEquals("overrideDefault", value.getStringDefaultOptional());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
	}

	@Test void testOverrideDefaultNull()
	{
		assertFails(
				() -> new Value(null, false),
				MandatoryViolationException.class,
				"mandatory violation for " + Value.stringDefault,
				Value.stringDefault);
	}

	@Test void testOverrideDefaultNullOptional()
	{
		final Value value = new Value(null, true);

		assertEquals("1234", value.getString4());
		assertEquals("defString", value.getStringDefault());
		assertEquals(null,   value.getStringDefaultOptional());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
	}

	@Test void testWrong()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);

		// get
		assertFails(() -> value.get(ValueX.stringField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.get(ValueX.intField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.get(ValueX.longField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.get(ValueX.doubleField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.get(ValueX.booleanField), IllegalArgumentException.class, "not a member");

		// getMandatory
		assertFails(() -> value.getMandatory(ValueX.intField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.getMandatory(ValueX.longField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.getMandatory(ValueX.doubleField), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.getMandatory(ValueX.booleanField), IllegalArgumentException.class, "not a member");

		// set
		assertFails(() -> value.set(ValueX.stringField, "77s"), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.set(ValueX.intField, valueOf(7)), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.set(ValueX.doubleField, valueOf(7.7)), IllegalArgumentException.class, "not a member");
		assertFails(() -> value.set(ValueX.booleanField, valueOf(true)), IllegalArgumentException.class, "not a member");
	}

	@Test void testTouchDate()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		assertEquals(null, value.getDate());
		assertEquals(null, value.getDay());

		final Date before = new Date();
		value.touchDate();
		final Date after = new Date();
		assertWithin(before, after, value.getDate());
		assertEquals(null, value.getDay());
	}

	@Test void testTouchDay()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		assertEquals(null, value.getDate());
		assertEquals(null, value.getDay());

		value.touchDay(getTimeZone("Europe/Berlin"));
		assertEquals(null, value.getDate());
		assertEquals(new Day(getTimeZone("Europe/Berlin")), value.getDay());
	}

	@Test void testSettableNonFunctionFieldCreate()
	{
		JavaVersion.assertThrowsClassCastException(
				() -> new Value(PRICE_FIELD.map(Price.ZERO)),
				PriceField.class,
				FunctionField.class);
	}

	private static final PriceField PRICE_FIELD = new PriceField();

	@SuppressWarnings("unchecked") // OK: testing bad api usage
	@Test void testWrongValueTypeCreate()
	{
		assertFails(
				() -> new Value(
						Value.string4.map("1234"),
						Value.intMax4.map(4),
						Value.longField.map(5l),
						Value.doubleField.map(6.6),
						Value.booleanField.map(false),
						((FunctionField)Value.booleanOptional).map("")
				),
				ClassCastException.class,
				"expected a java.lang.Boolean, " +
				"but was a java.lang.String for " +
				Value.booleanOptional + ".");
	}

	@SuppressWarnings("unchecked") // OK: testing bad api usage
	@Test void testWrongValueTypeSet()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		assertFails(
				() -> value.set((FunctionField)Value.booleanOptional, ""),
				ClassCastException.class,
				"expected a java.lang.Boolean, " +
				"but was a java.lang.String for " +
				Value.booleanOptional + ".");
	}

	@WrapperType(indent=2)
	static final class Value extends Composite
	{
		static final StringField string4 = new StringField().lengthMax(4);
		static final IntegerField intMax4 = new IntegerField().max(4);
		static final StringField stringDefault = new StringField().defaultTo("defString");
		static final StringField stringDefaultOptional = new StringField().optional().defaultTo("defStringOpt");
		static final LongField longField = new LongField();
		static final DoubleField doubleField = new DoubleField();
		static final BooleanField booleanField = new BooleanField();

		static final IntegerField intOptional = new IntegerField().optional();
		static final LongField longOptional = new LongField().optional();
		static final DoubleField doubleOptional = new DoubleField().optional();
		static final BooleanField booleanOptional = new BooleanField().optional();

		static final DateField date = new DateField().optional();
		static final DayField  day  = new DayField().optional();

		Value(final String stringDefault, final boolean optional)
		{
			//noinspection UnnecessarilyQualifiedStaticUsage
			this(new com.exedio.cope.SetValue<?>[]{
					Value.string4.map("1234"),
					(optional ? Value.stringDefaultOptional : Value.stringDefault).map(stringDefault),
					Value.intMax4.map(4),
					Value.longField.map(5l),
					Value.doubleField.map(6.6),
					Value.booleanField.map(false),
			});
		}

		/**
		 * Creates a new Value with all the fields initially needed.
		 * @param string4 the initial value for field {@link #string4}.
		 * @param intMax4 the initial value for field {@link #intMax4}.
		 * @param longField the initial value for field {@link #longField}.
		 * @param doubleField the initial value for field {@link #doubleField}.
		 * @param booleanField the initial value for field {@link #booleanField}.
		 * @throws com.exedio.cope.IntegerRangeViolationException if intMax4 violates its range constraint.
		 * @throws com.exedio.cope.MandatoryViolationException if string4 is null.
		 * @throws com.exedio.cope.StringLengthViolationException if string4 violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		Value(
					@javax.annotation.Nonnull final java.lang.String string4,
					final int intMax4,
					final long longField,
					final double doubleField,
					final boolean booleanField)
				throws
					com.exedio.cope.IntegerRangeViolationException,
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Value.string4.map(string4),
				Value.intMax4.map(intMax4),
				Value.longField.map(longField),
				Value.doubleField.map(doubleField),
				Value.booleanField.map(booleanField),
			});
		}

		/**
		 * Creates a new Value and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private Value(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #string4}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getString4()
		{
			return get(Value.string4);
		}

		/**
		 * Sets a new value for {@link #string4}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setString4(@javax.annotation.Nonnull final java.lang.String string4)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			set(Value.string4,string4);
		}

		/**
		 * Returns the value of {@link #intMax4}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntMax4()
		{
			return getMandatory(Value.intMax4);
		}

		/**
		 * Sets a new value for {@link #intMax4}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntMax4(final int intMax4)
				throws
					com.exedio.cope.IntegerRangeViolationException
		{
			set(Value.intMax4,intMax4);
		}

		/**
		 * Returns the value of {@link #stringDefault}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getStringDefault()
		{
			return get(Value.stringDefault);
		}

		/**
		 * Sets a new value for {@link #stringDefault}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setStringDefault(@javax.annotation.Nonnull final java.lang.String stringDefault)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			set(Value.stringDefault,stringDefault);
		}

		/**
		 * Returns the value of {@link #stringDefaultOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getStringDefaultOptional()
		{
			return get(Value.stringDefaultOptional);
		}

		/**
		 * Sets a new value for {@link #stringDefaultOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setStringDefaultOptional(@javax.annotation.Nullable final java.lang.String stringDefaultOptional)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			set(Value.stringDefaultOptional,stringDefaultOptional);
		}

		/**
		 * Returns the value of {@link #longField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getLongField()
		{
			return getMandatory(Value.longField);
		}

		/**
		 * Sets a new value for {@link #longField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setLongField(final long longField)
		{
			set(Value.longField,longField);
		}

		/**
		 * Returns the value of {@link #doubleField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		double getDoubleField()
		{
			return getMandatory(Value.doubleField);
		}

		/**
		 * Sets a new value for {@link #doubleField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDoubleField(final double doubleField)
		{
			set(Value.doubleField,doubleField);
		}

		/**
		 * Returns the value of {@link #booleanField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean getBooleanField()
		{
			return getMandatory(Value.booleanField);
		}

		/**
		 * Sets a new value for {@link #booleanField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setBooleanField(final boolean booleanField)
		{
			set(Value.booleanField,booleanField);
		}

		/**
		 * Returns the value of {@link #intOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getIntOptional()
		{
			return get(Value.intOptional);
		}

		/**
		 * Sets a new value for {@link #intOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntOptional(@javax.annotation.Nullable final java.lang.Integer intOptional)
		{
			set(Value.intOptional,intOptional);
		}

		/**
		 * Returns the value of {@link #longOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Long getLongOptional()
		{
			return get(Value.longOptional);
		}

		/**
		 * Sets a new value for {@link #longOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setLongOptional(@javax.annotation.Nullable final java.lang.Long longOptional)
		{
			set(Value.longOptional,longOptional);
		}

		/**
		 * Returns the value of {@link #doubleOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Double getDoubleOptional()
		{
			return get(Value.doubleOptional);
		}

		/**
		 * Sets a new value for {@link #doubleOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDoubleOptional(@javax.annotation.Nullable final java.lang.Double doubleOptional)
		{
			set(Value.doubleOptional,doubleOptional);
		}

		/**
		 * Returns the value of {@link #booleanOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Boolean getBooleanOptional()
		{
			return get(Value.booleanOptional);
		}

		/**
		 * Sets a new value for {@link #booleanOptional}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setBooleanOptional(@javax.annotation.Nullable final java.lang.Boolean booleanOptional)
		{
			set(Value.booleanOptional,booleanOptional);
		}

		/**
		 * Returns the value of {@link #date}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.util.Date getDate()
		{
			return get(Value.date);
		}

		/**
		 * Sets a new value for {@link #date}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDate(@javax.annotation.Nullable final java.util.Date date)
		{
			set(Value.date,date);
		}

		/**
		 * Sets the current date for the date field {@link #date}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void touchDate()
		{
			touch(Value.date);
		}

		/**
		 * Returns the value of {@link #day}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		com.exedio.cope.util.Day getDay()
		{
			return get(Value.day);
		}

		/**
		 * Sets a new value for {@link #day}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDay(@javax.annotation.Nullable final com.exedio.cope.util.Day day)
		{
			set(Value.day,day);
		}

		/**
		 * Sets today for the date field {@link #day}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
		{
			touch(Value.day,zone);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(indent=2, comments=false)
	static final class ValueX extends Composite
	{
		static final  StringField  stringField = new  StringField();
		static final IntegerField     intField = new IntegerField();
		static final    LongField    longField = new    LongField();
		static final  DoubleField  doubleField = new  DoubleField();
		static final BooleanField booleanField = new BooleanField();


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		ValueX(
					@javax.annotation.Nonnull final java.lang.String stringField,
					final int intField,
					final long longField,
					final double doubleField,
					final boolean booleanField)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ValueX.stringField.map(stringField),
				ValueX.intField.map(intField),
				ValueX.longField.map(longField),
				ValueX.doubleField.map(doubleField),
				ValueX.booleanField.map(booleanField),
			});
		}

		@com.exedio.cope.instrument.Generated
		private ValueX(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getStringField()
		{
			return get(ValueX.stringField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setStringField(@javax.annotation.Nonnull final java.lang.String stringField)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			set(ValueX.stringField,stringField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntField()
		{
			return getMandatory(ValueX.intField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntField(final int intField)
		{
			set(ValueX.intField,intField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getLongField()
		{
			return getMandatory(ValueX.longField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setLongField(final long longField)
		{
			set(ValueX.longField,longField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		double getDoubleField()
		{
			return getMandatory(ValueX.doubleField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDoubleField(final double doubleField)
		{
			set(ValueX.doubleField,doubleField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean getBooleanField()
		{
			return getMandatory(ValueX.booleanField);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setBooleanField(final boolean booleanField)
		{
			set(ValueX.booleanField,booleanField);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}
}
