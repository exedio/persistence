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

import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static java.lang.Boolean.valueOf;
import static java.lang.Double.valueOf;
import static java.lang.Integer.valueOf;
import static java.lang.Long.valueOf;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.LongField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.util.Day;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class CompositeTest
{
	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test void testCheck()
	{
		try
		{
			new Value("12345", 5, 0l, 0.0, false);
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals("length violation, '12345' is too long for " + Value.string4 + ", must be at most 4 characters, but was 5.", e.getMessage());
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}

		try
		{
			new Value(null, 5, 0l, 0.0, false);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for " + Value.string4, e.getMessage());
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}

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

		try
		{
			value.setString4("12345");
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals("length violation, '12345' is too long for " + Value.string4 + ", must be at most 4 characters, but was 5.", e.getMessage());
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		try
		{
			value.setString4(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for " + Value.string4, e.getMessage());
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEqualBits(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());

		try
		{
			value.setIntMax4(5);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals("range violation, 5 is too big for " + Value.intMax4 + ", must be at most 4.", e.getMessage());
			assertSame(Value.intMax4, e.getFeature());
			assertSame(null, e.getItem());
		}
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
		try
		{
			value.getMandatory(Value.intOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.longOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.doubleOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.booleanOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}


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
		try
		{
			value.getMandatory(Value.intOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.longOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.doubleOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.booleanOptional);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
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
		try
		{
			new Value(null, false);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for " + Value.stringDefault, e.getMessage());
			assertSame(Value.stringDefault, e.getFeature());
			assertSame(null, e.getItem());
		}
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
		try
		{
			value.get(ValueX.stringField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.get(ValueX.intField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.get(ValueX.longField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.get(ValueX.doubleField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.get(ValueX.booleanField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}

		// getMandatory
		try
		{
			value.getMandatory(ValueX.intField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.longField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.doubleField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.booleanField);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}

		// set
		try
		{
			value.set(ValueX.stringField, "77s");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.set(ValueX.intField, valueOf(7));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.set(ValueX.longField, valueOf(7l));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.set(ValueX.doubleField, valueOf(7.7));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
		try
		{
			value.set(ValueX.booleanField, valueOf(true));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a member", e.getMessage());
		}
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

	@SuppressWarnings("unchecked") // OK: testing bad api usage
	@Test void testIt()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		try
		{
			value.set((FunctionField)Value.booleanOptional, "");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a java.lang.Boolean, " +
					"but was a java.lang.String for " +
					Value.booleanOptional + ".",
				e.getMessage());
		}
	}

	static final class Value extends Composite
	{
		public static final StringField string4 = new StringField().lengthMax(4);
		public static final IntegerField intMax4 = new IntegerField().max(4);
		public static final StringField stringDefault = new StringField().defaultTo("defString");
		public static final StringField stringDefaultOptional = new StringField().optional().defaultTo("defStringOpt");
		public static final LongField longField = new LongField();
		public static final DoubleField doubleField = new DoubleField();
		public static final BooleanField booleanField = new BooleanField();

		public static final IntegerField intOptional = new IntegerField().optional();
		public static final LongField longOptional = new LongField().optional();
		public static final DoubleField doubleOptional = new DoubleField().optional();
		public static final BooleanField booleanOptional = new BooleanField().optional();

		public static final DateField date = new DateField().optional();
		public static final DayField  day  = new DayField().optional();

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
	public java.lang.String getString4()
	{
		return get(Value.string4);
	}

	/**
	 * Sets a new value for {@link #string4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setString4(@javax.annotation.Nonnull final java.lang.String string4)
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
	public int getIntMax4()
	{
		return getMandatory(Value.intMax4);
	}

	/**
	 * Sets a new value for {@link #intMax4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setIntMax4(final int intMax4)
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
	public java.lang.String getStringDefault()
	{
		return get(Value.stringDefault);
	}

	/**
	 * Sets a new value for {@link #stringDefault}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setStringDefault(@javax.annotation.Nonnull final java.lang.String stringDefault)
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
	public java.lang.String getStringDefaultOptional()
	{
		return get(Value.stringDefaultOptional);
	}

	/**
	 * Sets a new value for {@link #stringDefaultOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setStringDefaultOptional(@javax.annotation.Nullable final java.lang.String stringDefaultOptional)
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
	public long getLongField()
	{
		return getMandatory(Value.longField);
	}

	/**
	 * Sets a new value for {@link #longField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLongField(final long longField)
	{
		set(Value.longField,longField);
	}

	/**
	 * Returns the value of {@link #doubleField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public double getDoubleField()
	{
		return getMandatory(Value.doubleField);
	}

	/**
	 * Sets a new value for {@link #doubleField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDoubleField(final double doubleField)
	{
		set(Value.doubleField,doubleField);
	}

	/**
	 * Returns the value of {@link #booleanField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean getBooleanField()
	{
		return getMandatory(Value.booleanField);
	}

	/**
	 * Sets a new value for {@link #booleanField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setBooleanField(final boolean booleanField)
	{
		set(Value.booleanField,booleanField);
	}

	/**
	 * Returns the value of {@link #intOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getIntOptional()
	{
		return get(Value.intOptional);
	}

	/**
	 * Sets a new value for {@link #intOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setIntOptional(@javax.annotation.Nullable final java.lang.Integer intOptional)
	{
		set(Value.intOptional,intOptional);
	}

	/**
	 * Returns the value of {@link #longOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getLongOptional()
	{
		return get(Value.longOptional);
	}

	/**
	 * Sets a new value for {@link #longOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLongOptional(@javax.annotation.Nullable final java.lang.Long longOptional)
	{
		set(Value.longOptional,longOptional);
	}

	/**
	 * Returns the value of {@link #doubleOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getDoubleOptional()
	{
		return get(Value.doubleOptional);
	}

	/**
	 * Sets a new value for {@link #doubleOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDoubleOptional(@javax.annotation.Nullable final java.lang.Double doubleOptional)
	{
		set(Value.doubleOptional,doubleOptional);
	}

	/**
	 * Returns the value of {@link #booleanOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Boolean getBooleanOptional()
	{
		return get(Value.booleanOptional);
	}

	/**
	 * Sets a new value for {@link #booleanOptional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setBooleanOptional(@javax.annotation.Nullable final java.lang.Boolean booleanOptional)
	{
		set(Value.booleanOptional,booleanOptional);
	}

	/**
	 * Returns the value of {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.util.Date getDate()
	{
		return get(Value.date);
	}

	/**
	 * Sets a new value for {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDate(@javax.annotation.Nullable final java.util.Date date)
	{
		set(Value.date,date);
	}

	/**
	 * Sets the current date for the date field {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void touchDate()
	{
		touch(Value.date);
	}

	/**
	 * Returns the value of {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public com.exedio.cope.util.Day getDay()
	{
		return get(Value.day);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDay(@javax.annotation.Nullable final com.exedio.cope.util.Day day)
	{
		set(Value.day,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		touch(Value.day,zone);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;
}

	static final class ValueX extends Composite
	{
		public static final  StringField  stringField = new  StringField();
		public static final IntegerField     intField = new IntegerField();
		public static final    LongField    longField = new    LongField();
		public static final  DoubleField  doubleField = new  DoubleField();
		public static final BooleanField booleanField = new BooleanField();


	/**
	 * Creates a new ValueX with all the fields initially needed.
	 * @param stringField the initial value for field {@link #stringField}.
	 * @param intField the initial value for field {@link #intField}.
	 * @param longField the initial value for field {@link #longField}.
	 * @param doubleField the initial value for field {@link #doubleField}.
	 * @param booleanField the initial value for field {@link #booleanField}.
	 * @throws com.exedio.cope.MandatoryViolationException if stringField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if stringField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
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

	/**
	 * Creates a new ValueX and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ValueX(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #stringField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getStringField()
	{
		return get(ValueX.stringField);
	}

	/**
	 * Sets a new value for {@link #stringField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setStringField(@javax.annotation.Nonnull final java.lang.String stringField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(ValueX.stringField,stringField);
	}

	/**
	 * Returns the value of {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public int getIntField()
	{
		return getMandatory(ValueX.intField);
	}

	/**
	 * Sets a new value for {@link #intField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setIntField(final int intField)
	{
		set(ValueX.intField,intField);
	}

	/**
	 * Returns the value of {@link #longField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public long getLongField()
	{
		return getMandatory(ValueX.longField);
	}

	/**
	 * Sets a new value for {@link #longField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLongField(final long longField)
	{
		set(ValueX.longField,longField);
	}

	/**
	 * Returns the value of {@link #doubleField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public double getDoubleField()
	{
		return getMandatory(ValueX.doubleField);
	}

	/**
	 * Sets a new value for {@link #doubleField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDoubleField(final double doubleField)
	{
		set(ValueX.doubleField,doubleField);
	}

	/**
	 * Returns the value of {@link #booleanField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean getBooleanField()
	{
		return getMandatory(ValueX.booleanField);
	}

	/**
	 * Sets a new value for {@link #booleanField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setBooleanField(final boolean booleanField)
	{
		set(ValueX.booleanField,booleanField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;
}

}
