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

import static com.exedio.cope.DatePrecisionConditionTest.date;
import static com.exedio.cope.DatePrecisionDefaultToNowTest.AnItem.TYPE;
import static com.exedio.cope.DatePrecisionDefaultToNowTest.AnItem.future;
import static com.exedio.cope.DatePrecisionDefaultToNowTest.AnItem.none;
import static com.exedio.cope.DatePrecisionDefaultToNowTest.AnItem.past;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Date;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DatePrecisionDefaultToNowTest extends TestWithEnvironment
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	private static final Model MODEL = new Model(TYPE);

	public DatePrecisionDefaultToNowTest()
	{
		super(MODEL);
	}

	@Test void testRound()
	{
		assertEquals(true, past  .hasDefault());
		assertEquals(true, future.hasDefault());
		assertEquals(true, past  .isDefaultNow());
		assertEquals(true, future.isDefaultNow());
		assertEquals(RoundingMode.PAST  , past  .getRoundingMode());
		assertEquals(RoundingMode.FUTURE, future.getRoundingMode());
		assertEquals(null, past  .getDefaultConstant());
		assertEquals(null, future.getDefaultConstant());

		clockRule.override(clock);
		clock.add(date(11, 22, 44, 55, 66));
		final AnItem item = new AnItem();
		clock.assertEmpty();

		assertEquals(date(11, 22, 44, 0, 0), item.getPast  ());
		assertEquals(date(11, 22, 45, 0, 0), item.getFuture());
	}

	@Test void testNoRound()
	{
		clockRule.override(clock);
		clock.add(date(11, 22, 44, 0, 0));
		final AnItem item = new AnItem();
		clock.assertEmpty();

		assertEquals(date(11, 22, 44, 0, 0), item.getPast  ());
		assertEquals(date(11, 22, 44, 0, 0), item.getFuture());
	}

	@Test void testSetPast()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		item.setPastRounded(date(11, 22, 44, 55, 66));
		assertEquals(date(11, 22, 44, 0, 0), item.getPast());
	}

	@Test void testSetFuture()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		item.setFutureRounded(date(11, 22, 44, 55, 66));
		assertEquals(date(11, 22, 45, 0, 0), item.getFuture());
	}

	@Test void testSetUnnecessary()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		try
		{
			item.setNoneRounded(date(11, 22, 44, 55, 66));
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(none, e.getFeature());
			assertEquals(Precision.MINUTE, e.getPrecision());
			assertEquals(date(11, 22, 44, 55, 66), e.getValue());
			assertEquals(date(11, 22, 44,  0,  0), e.getValueAllowedInPast());
			assertEquals(date(11, 22, 45,  0,  0), e.getValueAllowedInFuture());
			assertNotNull(e.getMessage());
		}

		assertEquals(null, item.getNone());
	}

	@Test void testTouchPast()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		clockRule.override(clock);
		clock.add(date(11, 22, 44, 55, 66));
		item.touchPast();
		clock.assertEmpty();

		assertEquals(date(11, 22, 44, 0, 0), item.getPast());
	}

	@Test void testTouchFuture()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		clockRule.override(clock);
		clock.add(date(11, 22, 44, 55, 66));
		item.touchFuture();
		clock.assertEmpty();

		assertEquals(date(11, 22, 45, 0, 0), item.getFuture());
	}

	@Test void testTouchUnnecessary()
	{
		final AnItem item = new AnItem(date(9, 9, 9, 0, 0), date(9, 9, 9, 0, 0));
		clockRule.override(clock);
		clock.add(date(11, 22, 44, 55, 66));
		try
		{
			item.touchNone();
			fail();
		}
		catch(final DatePrecisionViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(none, e.getFeature());
			assertEquals(Precision.MINUTE, e.getPrecision());
			assertEquals(date(11, 22, 44, 55, 66), e.getValue());
			assertEquals(date(11, 22, 44,  0,  0), e.getValueAllowedInPast());
			assertEquals(date(11, 22, 45,  0,  0), e.getValueAllowedInFuture());
			assertNotNull(e.getMessage());
		}
		clock.assertEmpty();

		assertEquals(null, item.getNone());
	}

	static final class AnItem extends Item
	{
		static final DateField past   = new DateField().precisionMinute().roundingMode(RoundingMode.PAST  ).defaultToNow();
		static final DateField future = new DateField().precisionMinute().roundingMode(RoundingMode.FUTURE).defaultToNow();
		static final DateField none   = new DateField().precisionMinute().roundingMode(RoundingMode.UNNECESSARY).optional();

		AnItem(final Date past, final Date future)
		{
			this(
					AnItem.past  .map(past  ),
					AnItem.future.map(future));
		}

		// must not be generated by instrumentor
		void setNoneRounded(final Date value)
		{
			none.setRounded(this, value);
		}

		// must not be generated by instrumentor
		void touchNone()
		{
			none.touch(this);
		}


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #past}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getPast()
	{
		return AnItem.past.get(this);
	}

	/**
	 * Sets a new value for {@link #past}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPast(@javax.annotation.Nonnull final java.util.Date past)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.past.set(this,past);
	}

	/**
	 * Sets a new value for {@link #past}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastRounded(@javax.annotation.Nonnull final java.util.Date past)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.past.setRounded(this,past);
	}

	/**
	 * Sets a new value for {@link #past}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastRounded(@javax.annotation.Nonnull final java.util.Date past,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.past.setRounded(this,past,roundingMode);
	}

	/**
	 * Sets the current date for the date field {@link #past}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchPast()
	{
		AnItem.past.touch(this);
	}

	/**
	 * Returns the value of {@link #future}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getFuture()
	{
		return AnItem.future.get(this);
	}

	/**
	 * Sets a new value for {@link #future}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFuture(@javax.annotation.Nonnull final java.util.Date future)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.future.set(this,future);
	}

	/**
	 * Sets a new value for {@link #future}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFutureRounded(@javax.annotation.Nonnull final java.util.Date future)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AnItem.future.setRounded(this,future);
	}

	/**
	 * Sets a new value for {@link #future}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFutureRounded(@javax.annotation.Nonnull final java.util.Date future,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.future.setRounded(this,future,roundingMode);
	}

	/**
	 * Sets the current date for the date field {@link #future}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchFuture()
	{
		AnItem.future.touch(this);
	}

	/**
	 * Returns the value of {@link #none}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getNone()
	{
		return AnItem.none.get(this);
	}

	/**
	 * Sets a new value for {@link #none}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNone(@javax.annotation.Nullable final java.util.Date none)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.none.set(this,none);
	}

	/**
	 * Sets a new value for {@link #none}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNoneRounded(@javax.annotation.Nullable final java.util.Date none,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		AnItem.none.setRounded(this,none,roundingMode);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
