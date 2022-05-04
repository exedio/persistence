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

import com.exedio.cope.DateField.RoundingMode;

public final class DatePrecisionItemInstrumented extends Item
{
	static final DateField millis  = new DateField();
	static final DateField seconds = new DateField().precisionSecond();
	static final DateField minutes = new DateField().precisionMinute();
	static final DateField hours   = new DateField().precisionHour  ();

	static final DateField finalMillis  = new DateField().toFinal();
	static final DateField finalSeconds = new DateField().toFinal().precisionSecond();
	static final DateField finalMinutes = new DateField().toFinal().precisionMinute();
	static final DateField finalHours   = new DateField().toFinal().precisionHour  ();

	static final DateField pastMillis  = new DateField().roundingMode(RoundingMode.PAST);
	static final DateField pastSeconds = new DateField().roundingMode(RoundingMode.PAST).precisionSecond();
	static final DateField pastMinutes = new DateField().roundingMode(RoundingMode.PAST).precisionMinute();
	static final DateField pastHours   = new DateField().roundingMode(RoundingMode.PAST).precisionHour  ();


	/**
	 * Creates a new DatePrecisionItemInstrumented with all the fields initially needed.
	 * @param millis the initial value for field {@link #millis}.
	 * @param seconds the initial value for field {@link #seconds}.
	 * @param minutes the initial value for field {@link #minutes}.
	 * @param hours the initial value for field {@link #hours}.
	 * @param finalMillis the initial value for field {@link #finalMillis}.
	 * @param finalSeconds the initial value for field {@link #finalSeconds}.
	 * @param finalMinutes the initial value for field {@link #finalMinutes}.
	 * @param finalHours the initial value for field {@link #finalHours}.
	 * @param pastMillis the initial value for field {@link #pastMillis}.
	 * @param pastSeconds the initial value for field {@link #pastSeconds}.
	 * @param pastMinutes the initial value for field {@link #pastMinutes}.
	 * @param pastHours the initial value for field {@link #pastHours}.
	 * @throws com.exedio.cope.DatePrecisionViolationException if seconds, minutes, hours, finalSeconds, finalMinutes, finalHours, pastSeconds, pastMinutes, pastHours violates its precision constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if millis, seconds, minutes, hours, finalMillis, finalSeconds, finalMinutes, finalHours, pastMillis, pastSeconds, pastMinutes, pastHours is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DatePrecisionItemInstrumented(
				@javax.annotation.Nonnull final java.util.Date millis,
				@javax.annotation.Nonnull final java.util.Date seconds,
				@javax.annotation.Nonnull final java.util.Date minutes,
				@javax.annotation.Nonnull final java.util.Date hours,
				@javax.annotation.Nonnull final java.util.Date finalMillis,
				@javax.annotation.Nonnull final java.util.Date finalSeconds,
				@javax.annotation.Nonnull final java.util.Date finalMinutes,
				@javax.annotation.Nonnull final java.util.Date finalHours,
				@javax.annotation.Nonnull final java.util.Date pastMillis,
				@javax.annotation.Nonnull final java.util.Date pastSeconds,
				@javax.annotation.Nonnull final java.util.Date pastMinutes,
				@javax.annotation.Nonnull final java.util.Date pastHours)
			throws
				com.exedio.cope.DatePrecisionViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.millis,millis),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.seconds,seconds),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.minutes,minutes),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.hours,hours),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.finalMillis,finalMillis),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.finalSeconds,finalSeconds),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.finalMinutes,finalMinutes),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.finalHours,finalHours),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.pastMillis,pastMillis),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.pastSeconds,pastSeconds),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.pastMinutes,pastMinutes),
			com.exedio.cope.SetValue.map(DatePrecisionItemInstrumented.pastHours,pastHours),
		});
	}

	/**
	 * Creates a new DatePrecisionItemInstrumented and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DatePrecisionItemInstrumented(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getMillis()
	{
		return DatePrecisionItemInstrumented.millis.get(this);
	}

	/**
	 * Sets a new value for {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMillis(@javax.annotation.Nonnull final java.util.Date millis)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DatePrecisionItemInstrumented.millis.set(this,millis);
	}

	/**
	 * Sets the current date for the date field {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchMillis()
	{
		DatePrecisionItemInstrumented.millis.touch(this);
	}

	/**
	 * Returns the value of {@link #seconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getSeconds()
	{
		return DatePrecisionItemInstrumented.seconds.get(this);
	}

	/**
	 * Sets a new value for {@link #seconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSeconds(@javax.annotation.Nonnull final java.util.Date seconds)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.seconds.set(this,seconds);
	}

	/**
	 * Sets a new value for {@link #seconds}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSecondsRounded(@javax.annotation.Nonnull final java.util.Date seconds,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.seconds.setRounded(this,seconds,roundingMode);
	}

	/**
	 * Returns the value of {@link #minutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getMinutes()
	{
		return DatePrecisionItemInstrumented.minutes.get(this);
	}

	/**
	 * Sets a new value for {@link #minutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMinutes(@javax.annotation.Nonnull final java.util.Date minutes)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.minutes.set(this,minutes);
	}

	/**
	 * Sets a new value for {@link #minutes}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMinutesRounded(@javax.annotation.Nonnull final java.util.Date minutes,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.minutes.setRounded(this,minutes,roundingMode);
	}

	/**
	 * Returns the value of {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getHours()
	{
		return DatePrecisionItemInstrumented.hours.get(this);
	}

	/**
	 * Sets a new value for {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setHours(@javax.annotation.Nonnull final java.util.Date hours)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.hours.set(this,hours);
	}

	/**
	 * Sets a new value for {@link #hours}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setHoursRounded(@javax.annotation.Nonnull final java.util.Date hours,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.hours.setRounded(this,hours,roundingMode);
	}

	/**
	 * Returns the value of {@link #finalMillis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getFinalMillis()
	{
		return DatePrecisionItemInstrumented.finalMillis.get(this);
	}

	/**
	 * Returns the value of {@link #finalSeconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getFinalSeconds()
	{
		return DatePrecisionItemInstrumented.finalSeconds.get(this);
	}

	/**
	 * Returns the value of {@link #finalMinutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getFinalMinutes()
	{
		return DatePrecisionItemInstrumented.finalMinutes.get(this);
	}

	/**
	 * Returns the value of {@link #finalHours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getFinalHours()
	{
		return DatePrecisionItemInstrumented.finalHours.get(this);
	}

	/**
	 * Returns the value of {@link #pastMillis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getPastMillis()
	{
		return DatePrecisionItemInstrumented.pastMillis.get(this);
	}

	/**
	 * Sets a new value for {@link #pastMillis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastMillis(@javax.annotation.Nonnull final java.util.Date pastMillis)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DatePrecisionItemInstrumented.pastMillis.set(this,pastMillis);
	}

	/**
	 * Sets the current date for the date field {@link #pastMillis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchPastMillis()
	{
		DatePrecisionItemInstrumented.pastMillis.touch(this);
	}

	/**
	 * Returns the value of {@link #pastSeconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getPastSeconds()
	{
		return DatePrecisionItemInstrumented.pastSeconds.get(this);
	}

	/**
	 * Sets a new value for {@link #pastSeconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastSeconds(@javax.annotation.Nonnull final java.util.Date pastSeconds)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastSeconds.set(this,pastSeconds);
	}

	/**
	 * Sets a new value for {@link #pastSeconds}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastSecondsRounded(@javax.annotation.Nonnull final java.util.Date pastSeconds)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DatePrecisionItemInstrumented.pastSeconds.setRounded(this,pastSeconds);
	}

	/**
	 * Sets a new value for {@link #pastSeconds}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastSecondsRounded(@javax.annotation.Nonnull final java.util.Date pastSeconds,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastSeconds.setRounded(this,pastSeconds,roundingMode);
	}

	/**
	 * Sets the current date for the date field {@link #pastSeconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchPastSeconds()
	{
		DatePrecisionItemInstrumented.pastSeconds.touch(this);
	}

	/**
	 * Returns the value of {@link #pastMinutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getPastMinutes()
	{
		return DatePrecisionItemInstrumented.pastMinutes.get(this);
	}

	/**
	 * Sets a new value for {@link #pastMinutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastMinutes(@javax.annotation.Nonnull final java.util.Date pastMinutes)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastMinutes.set(this,pastMinutes);
	}

	/**
	 * Sets a new value for {@link #pastMinutes}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastMinutesRounded(@javax.annotation.Nonnull final java.util.Date pastMinutes)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DatePrecisionItemInstrumented.pastMinutes.setRounded(this,pastMinutes);
	}

	/**
	 * Sets a new value for {@link #pastMinutes}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastMinutesRounded(@javax.annotation.Nonnull final java.util.Date pastMinutes,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastMinutes.setRounded(this,pastMinutes,roundingMode);
	}

	/**
	 * Sets the current date for the date field {@link #pastMinutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchPastMinutes()
	{
		DatePrecisionItemInstrumented.pastMinutes.touch(this);
	}

	/**
	 * Returns the value of {@link #pastHours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getPastHours()
	{
		return DatePrecisionItemInstrumented.pastHours.get(this);
	}

	/**
	 * Sets a new value for {@link #pastHours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastHours(@javax.annotation.Nonnull final java.util.Date pastHours)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastHours.set(this,pastHours);
	}

	/**
	 * Sets a new value for {@link #pastHours}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastHoursRounded(@javax.annotation.Nonnull final java.util.Date pastHours)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DatePrecisionItemInstrumented.pastHours.setRounded(this,pastHours);
	}

	/**
	 * Sets a new value for {@link #pastHours}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPastHoursRounded(@javax.annotation.Nonnull final java.util.Date pastHours,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItemInstrumented.pastHours.setRounded(this,pastHours,roundingMode);
	}

	/**
	 * Sets the current date for the date field {@link #pastHours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchPastHours()
	{
		DatePrecisionItemInstrumented.pastHours.touch(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for datePrecisionItemInstrumented.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DatePrecisionItemInstrumented> TYPE = com.exedio.cope.TypesBound.newType(DatePrecisionItemInstrumented.class,DatePrecisionItemInstrumented::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DatePrecisionItemInstrumented(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
