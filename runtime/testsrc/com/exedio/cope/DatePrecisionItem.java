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

@CopeName("Main")
public final class DatePrecisionItem extends Item
{
	static final DateField millis  = new DateField().optional();
	static final DateField seconds = new DateField().optional().precisionSecond();
	static final DateField minutes = new DateField().optional().precisionMinute();
	static final DateField hours   = new DateField().optional().precisionHour  ();

	/**
	 * Creates a new DatePrecisionItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public DatePrecisionItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new DatePrecisionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DatePrecisionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getMillis()
	{
		return DatePrecisionItem.millis.get(this);
	}

	/**
	 * Sets a new value for {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMillis(@javax.annotation.Nullable final java.util.Date millis)
	{
		DatePrecisionItem.millis.set(this,millis);
	}

	/**
	 * Sets the current date for the date field {@link #millis}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchMillis()
	{
		DatePrecisionItem.millis.touch(this);
	}

	/**
	 * Returns the value of {@link #seconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getSeconds()
	{
		return DatePrecisionItem.seconds.get(this);
	}

	/**
	 * Sets a new value for {@link #seconds}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSeconds(@javax.annotation.Nullable final java.util.Date seconds)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.seconds.set(this,seconds);
	}

	/**
	 * Sets a new value for {@link #seconds}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSecondsRounded(@javax.annotation.Nullable final java.util.Date seconds,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.seconds.setRounded(this,seconds,roundingMode);
	}

	/**
	 * Returns the value of {@link #minutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getMinutes()
	{
		return DatePrecisionItem.minutes.get(this);
	}

	/**
	 * Sets a new value for {@link #minutes}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMinutes(@javax.annotation.Nullable final java.util.Date minutes)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.minutes.set(this,minutes);
	}

	/**
	 * Sets a new value for {@link #minutes}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMinutesRounded(@javax.annotation.Nullable final java.util.Date minutes,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.minutes.setRounded(this,minutes,roundingMode);
	}

	/**
	 * Returns the value of {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getHours()
	{
		return DatePrecisionItem.hours.get(this);
	}

	/**
	 * Sets a new value for {@link #hours}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setHours(@javax.annotation.Nullable final java.util.Date hours)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.hours.set(this,hours);
	}

	/**
	 * Sets a new value for {@link #hours}, but rounds it before according to the precision of the field.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setRounded")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setHoursRounded(@javax.annotation.Nullable final java.util.Date hours,@javax.annotation.Nonnull final com.exedio.cope.DateField.RoundingMode roundingMode)
			throws
				com.exedio.cope.DatePrecisionViolationException
	{
		DatePrecisionItem.hours.setRounded(this,hours,roundingMode);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for datePrecisionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DatePrecisionItem> TYPE = com.exedio.cope.TypesBound.newType(DatePrecisionItem.class,DatePrecisionItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DatePrecisionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
