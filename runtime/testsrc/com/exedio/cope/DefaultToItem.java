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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Day;
import com.exedio.cope.util.TimeZoneStrict;
import java.util.Date;
import java.util.Random;

@WrapperType(genericConstructor=PACKAGE)
final class DefaultToItem extends Item
{

	static final BooleanField booleanTrue = new BooleanField().optional().defaultTo(true);
	static final BooleanField booleanNone = new BooleanField();

	static final IntegerField integerFive = new IntegerField().defaultTo(5);
	static final IntegerField integerFifty = new IntegerField().optional().defaultTo(50);
	static final IntegerField integerNext = new IntegerField().optional().defaultToNext(10001);
	static final IntegerField integerNone = new IntegerField().optional();

	static final DateField dateEight = new DateField().defaultTo(new Date(8));
	@CopeSchemaName("dateEty")
	static final DateField dateEighty = new DateField().optional().defaultTo(new Date(80));
	static final DateField dateNow = new DateField().defaultToNow();
	static final DateField dateNowOpt = new DateField().optional().defaultToNow();
	static final DateField dateNone = new DateField().optional();

	static final DayField dayEight = new DayField().defaultTo(new Day(1608, 8, 8));
	static final DayField dayNow = new DayField().defaultToNow(TimeZoneStrict.getTimeZone("Europe/Berlin"));
	static final DayField dayNowOpt = new DayField().optional().defaultToNow(TimeZoneStrict.getTimeZone("Europe/Berlin"));
	static final DayField dayNone = new DayField().optional();

	static final LongField longRandom = new LongField().optional().min(0l).defaultToRandom(new Random());

	enum DefaultToEnum
	{
		ONE, TWO, THREE
	}

	static final EnumField<DefaultToEnum> enumOne = EnumField.create(DefaultToEnum.class).defaultTo(DefaultToEnum.ONE);
	static final EnumField<DefaultToEnum> enumTwo = EnumField.create(DefaultToEnum.class).optional().defaultTo(DefaultToEnum.TWO);
	static final EnumField<DefaultToEnum> enumNone = EnumField.create(DefaultToEnum.class).optional();

	/**
	 * Creates a new DefaultToItem with all the fields initially needed.
	 * @param booleanNone the initial value for field {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DefaultToItem(
				final boolean booleanNone)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DefaultToItem.booleanNone.map(booleanNone),
		});
	}

	/**
	 * Creates a new DefaultToItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	DefaultToItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #booleanTrue}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Boolean getBooleanTrue()
	{
		return DefaultToItem.booleanTrue.get(this);
	}

	/**
	 * Sets a new value for {@link #booleanTrue}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanTrue(@javax.annotation.Nullable final java.lang.Boolean booleanTrue)
	{
		DefaultToItem.booleanTrue.set(this,booleanTrue);
	}

	/**
	 * Returns the value of {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getBooleanNone()
	{
		return DefaultToItem.booleanNone.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanNone(final boolean booleanNone)
	{
		DefaultToItem.booleanNone.set(this,booleanNone);
	}

	/**
	 * Returns the value of {@link #integerFive}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getIntegerFive()
	{
		return DefaultToItem.integerFive.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integerFive}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerFive(final int integerFive)
	{
		DefaultToItem.integerFive.set(this,integerFive);
	}

	/**
	 * Returns the value of {@link #integerFifty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerFifty()
	{
		return DefaultToItem.integerFifty.get(this);
	}

	/**
	 * Sets a new value for {@link #integerFifty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerFifty(@javax.annotation.Nullable final java.lang.Integer integerFifty)
	{
		DefaultToItem.integerFifty.set(this,integerFifty);
	}

	/**
	 * Returns the value of {@link #integerNext}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerNext()
	{
		return DefaultToItem.integerNext.get(this);
	}

	/**
	 * Sets a new value for {@link #integerNext}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerNext(@javax.annotation.Nullable final java.lang.Integer integerNext)
	{
		DefaultToItem.integerNext.set(this,integerNext);
	}

	/**
	 * Returns the value of {@link #integerNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerNone()
	{
		return DefaultToItem.integerNone.get(this);
	}

	/**
	 * Sets a new value for {@link #integerNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerNone(@javax.annotation.Nullable final java.lang.Integer integerNone)
	{
		DefaultToItem.integerNone.set(this,integerNone);
	}

	/**
	 * Returns the value of {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getDateEight()
	{
		return DefaultToItem.dateEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateEight(@javax.annotation.Nonnull final java.util.Date dateEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dateEight.set(this,dateEight);
	}

	/**
	 * Sets the current date for the date field {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateEight()
	{
		DefaultToItem.dateEight.touch(this);
	}

	/**
	 * Returns the value of {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateEighty()
	{
		return DefaultToItem.dateEighty.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateEighty(@javax.annotation.Nullable final java.util.Date dateEighty)
	{
		DefaultToItem.dateEighty.set(this,dateEighty);
	}

	/**
	 * Sets the current date for the date field {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateEighty()
	{
		DefaultToItem.dateEighty.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getDateNow()
	{
		return DefaultToItem.dateNow.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNow(@javax.annotation.Nonnull final java.util.Date dateNow)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dateNow.set(this,dateNow);
	}

	/**
	 * Sets the current date for the date field {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNow()
	{
		DefaultToItem.dateNow.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateNowOpt()
	{
		return DefaultToItem.dateNowOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNowOpt(@javax.annotation.Nullable final java.util.Date dateNowOpt)
	{
		DefaultToItem.dateNowOpt.set(this,dateNowOpt);
	}

	/**
	 * Sets the current date for the date field {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNowOpt()
	{
		DefaultToItem.dateNowOpt.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateNone()
	{
		return DefaultToItem.dateNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNone(@javax.annotation.Nullable final java.util.Date dateNone)
	{
		DefaultToItem.dateNone.set(this,dateNone);
	}

	/**
	 * Sets the current date for the date field {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNone()
	{
		DefaultToItem.dateNone.touch(this);
	}

	/**
	 * Returns the value of {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDayEight()
	{
		return DefaultToItem.dayEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayEight(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dayEight.set(this,dayEight);
	}

	/**
	 * Sets today for the date field {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayEight(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayEight.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDayNow()
	{
		return DefaultToItem.dayNow.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNow(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayNow)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dayNow.set(this,dayNow);
	}

	/**
	 * Sets today for the date field {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNow(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayNow.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNowOpt()
	{
		return DefaultToItem.dayNowOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNowOpt(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNowOpt)
	{
		DefaultToItem.dayNowOpt.set(this,dayNowOpt);
	}

	/**
	 * Sets today for the date field {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNowOpt(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayNowOpt.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNone()
	{
		return DefaultToItem.dayNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNone(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNone)
	{
		DefaultToItem.dayNone.set(this,dayNone);
	}

	/**
	 * Sets today for the date field {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNone(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayNone.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #longRandom}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Long getLongRandom()
	{
		return DefaultToItem.longRandom.get(this);
	}

	/**
	 * Sets a new value for {@link #longRandom}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLongRandom(@javax.annotation.Nullable final java.lang.Long longRandom)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		DefaultToItem.longRandom.set(this,longRandom);
	}

	/**
	 * Returns the value of {@link #enumOne}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	DefaultToEnum getEnumOne()
	{
		return DefaultToItem.enumOne.get(this);
	}

	/**
	 * Sets a new value for {@link #enumOne}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumOne(@javax.annotation.Nonnull final DefaultToEnum enumOne)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.enumOne.set(this,enumOne);
	}

	/**
	 * Returns the value of {@link #enumTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DefaultToEnum getEnumTwo()
	{
		return DefaultToItem.enumTwo.get(this);
	}

	/**
	 * Sets a new value for {@link #enumTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumTwo(@javax.annotation.Nullable final DefaultToEnum enumTwo)
	{
		DefaultToItem.enumTwo.set(this,enumTwo);
	}

	/**
	 * Returns the value of {@link #enumNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DefaultToEnum getEnumNone()
	{
		return DefaultToItem.enumNone.get(this);
	}

	/**
	 * Sets a new value for {@link #enumNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumNone(@javax.annotation.Nullable final DefaultToEnum enumNone)
	{
		DefaultToItem.enumNone.set(this,enumNone);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for defaultToItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DefaultToItem> TYPE = com.exedio.cope.TypesBound.newType(DefaultToItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DefaultToItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
