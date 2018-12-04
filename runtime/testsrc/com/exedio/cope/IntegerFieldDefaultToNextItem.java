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
import java.util.Date;
import java.util.Random;

@WrapperType(genericConstructor=PACKAGE)
@CopeSchemaName("DefaultToItem")
final class IntegerFieldDefaultToNextItem extends Item
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
	static final DateField dateNone = new DateField().optional();

	static final DayField dayEight = new DayField().defaultTo(new Day(1608, 8, 8));
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
	 * Creates a new IntegerFieldDefaultToNextItem with all the fields initially needed.
	 * @param booleanNone the initial value for field {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	IntegerFieldDefaultToNextItem(
				final boolean booleanNone)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			IntegerFieldDefaultToNextItem.booleanNone.map(booleanNone),
		});
	}

	/**
	 * Creates a new IntegerFieldDefaultToNextItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	IntegerFieldDefaultToNextItem(final com.exedio.cope.SetValue<?>... setValues)
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
		return IntegerFieldDefaultToNextItem.booleanTrue.get(this);
	}

	/**
	 * Sets a new value for {@link #booleanTrue}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanTrue(@javax.annotation.Nullable final java.lang.Boolean booleanTrue)
	{
		IntegerFieldDefaultToNextItem.booleanTrue.set(this,booleanTrue);
	}

	/**
	 * Returns the value of {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getBooleanNone()
	{
		return IntegerFieldDefaultToNextItem.booleanNone.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanNone(final boolean booleanNone)
	{
		IntegerFieldDefaultToNextItem.booleanNone.set(this,booleanNone);
	}

	/**
	 * Returns the value of {@link #integerFive}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getIntegerFive()
	{
		return IntegerFieldDefaultToNextItem.integerFive.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integerFive}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerFive(final int integerFive)
	{
		IntegerFieldDefaultToNextItem.integerFive.set(this,integerFive);
	}

	/**
	 * Returns the value of {@link #integerFifty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerFifty()
	{
		return IntegerFieldDefaultToNextItem.integerFifty.get(this);
	}

	/**
	 * Sets a new value for {@link #integerFifty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerFifty(@javax.annotation.Nullable final java.lang.Integer integerFifty)
	{
		IntegerFieldDefaultToNextItem.integerFifty.set(this,integerFifty);
	}

	/**
	 * Returns the value of {@link #integerNext}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerNext()
	{
		return IntegerFieldDefaultToNextItem.integerNext.get(this);
	}

	/**
	 * Sets a new value for {@link #integerNext}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerNext(@javax.annotation.Nullable final java.lang.Integer integerNext)
	{
		IntegerFieldDefaultToNextItem.integerNext.set(this,integerNext);
	}

	/**
	 * Returns the value of {@link #integerNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerNone()
	{
		return IntegerFieldDefaultToNextItem.integerNone.get(this);
	}

	/**
	 * Sets a new value for {@link #integerNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerNone(@javax.annotation.Nullable final java.lang.Integer integerNone)
	{
		IntegerFieldDefaultToNextItem.integerNone.set(this,integerNone);
	}

	/**
	 * Returns the value of {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getDateEight()
	{
		return IntegerFieldDefaultToNextItem.dateEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateEight(@javax.annotation.Nonnull final java.util.Date dateEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		IntegerFieldDefaultToNextItem.dateEight.set(this,dateEight);
	}

	/**
	 * Sets the current date for the date field {@link #dateEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateEight()
	{
		IntegerFieldDefaultToNextItem.dateEight.touch(this);
	}

	/**
	 * Returns the value of {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateEighty()
	{
		return IntegerFieldDefaultToNextItem.dateEighty.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateEighty(@javax.annotation.Nullable final java.util.Date dateEighty)
	{
		IntegerFieldDefaultToNextItem.dateEighty.set(this,dateEighty);
	}

	/**
	 * Sets the current date for the date field {@link #dateEighty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateEighty()
	{
		IntegerFieldDefaultToNextItem.dateEighty.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateNone()
	{
		return IntegerFieldDefaultToNextItem.dateNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNone(@javax.annotation.Nullable final java.util.Date dateNone)
	{
		IntegerFieldDefaultToNextItem.dateNone.set(this,dateNone);
	}

	/**
	 * Sets the current date for the date field {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNone()
	{
		IntegerFieldDefaultToNextItem.dateNone.touch(this);
	}

	/**
	 * Returns the value of {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDayEight()
	{
		return IntegerFieldDefaultToNextItem.dayEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayEight(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		IntegerFieldDefaultToNextItem.dayEight.set(this,dayEight);
	}

	/**
	 * Sets today for the date field {@link #dayEight}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayEight(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		IntegerFieldDefaultToNextItem.dayEight.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNone()
	{
		return IntegerFieldDefaultToNextItem.dayNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNone(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNone)
	{
		IntegerFieldDefaultToNextItem.dayNone.set(this,dayNone);
	}

	/**
	 * Sets today for the date field {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNone(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		IntegerFieldDefaultToNextItem.dayNone.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #longRandom}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Long getLongRandom()
	{
		return IntegerFieldDefaultToNextItem.longRandom.get(this);
	}

	/**
	 * Sets a new value for {@link #longRandom}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLongRandom(@javax.annotation.Nullable final java.lang.Long longRandom)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		IntegerFieldDefaultToNextItem.longRandom.set(this,longRandom);
	}

	/**
	 * Returns the value of {@link #enumOne}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	DefaultToEnum getEnumOne()
	{
		return IntegerFieldDefaultToNextItem.enumOne.get(this);
	}

	/**
	 * Sets a new value for {@link #enumOne}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumOne(@javax.annotation.Nonnull final DefaultToEnum enumOne)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		IntegerFieldDefaultToNextItem.enumOne.set(this,enumOne);
	}

	/**
	 * Returns the value of {@link #enumTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DefaultToEnum getEnumTwo()
	{
		return IntegerFieldDefaultToNextItem.enumTwo.get(this);
	}

	/**
	 * Sets a new value for {@link #enumTwo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumTwo(@javax.annotation.Nullable final DefaultToEnum enumTwo)
	{
		IntegerFieldDefaultToNextItem.enumTwo.set(this,enumTwo);
	}

	/**
	 * Returns the value of {@link #enumNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DefaultToEnum getEnumNone()
	{
		return IntegerFieldDefaultToNextItem.enumNone.get(this);
	}

	/**
	 * Sets a new value for {@link #enumNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEnumNone(@javax.annotation.Nullable final DefaultToEnum enumNone)
	{
		IntegerFieldDefaultToNextItem.enumNone.set(this,enumNone);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerFieldDefaultToNextItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<IntegerFieldDefaultToNextItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerFieldDefaultToNextItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private IntegerFieldDefaultToNextItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
