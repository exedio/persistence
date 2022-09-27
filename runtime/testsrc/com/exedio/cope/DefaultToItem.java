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
final class DefaultToItem extends Item
{

	static final BooleanField booleanTrue = new BooleanField().optional().defaultTo(true);
	static final BooleanField booleanNone = new BooleanField();

	static final IntegerField integerFive = new IntegerField().defaultTo(5);
	static final IntegerField integerFifty = new IntegerField().optional().defaultTo(50);
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
	 * Creates a new DefaultToItem with all the fields initially needed.
	 * @param booleanNone the initial value for field {@link #booleanNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	DefaultToItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #booleanTrue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Boolean getBooleanTrue()
	{
		return DefaultToItem.booleanTrue.get(this);
	}

	/**
	 * Sets a new value for {@link #booleanTrue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBooleanTrue(@javax.annotation.Nullable final java.lang.Boolean booleanTrue)
	{
		DefaultToItem.booleanTrue.set(this,booleanTrue);
	}

	/**
	 * Returns the value of {@link #booleanNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean getBooleanNone()
	{
		return DefaultToItem.booleanNone.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBooleanNone(final boolean booleanNone)
	{
		DefaultToItem.booleanNone.set(this,booleanNone);
	}

	/**
	 * Returns the value of {@link #integerFive}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getIntegerFive()
	{
		return DefaultToItem.integerFive.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integerFive}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntegerFive(final int integerFive)
	{
		DefaultToItem.integerFive.set(this,integerFive);
	}

	/**
	 * Returns the value of {@link #integerFifty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntegerFifty()
	{
		return DefaultToItem.integerFifty.get(this);
	}

	/**
	 * Sets a new value for {@link #integerFifty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntegerFifty(@javax.annotation.Nullable final java.lang.Integer integerFifty)
	{
		DefaultToItem.integerFifty.set(this,integerFifty);
	}

	/**
	 * Returns the value of {@link #integerNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntegerNone()
	{
		return DefaultToItem.integerNone.get(this);
	}

	/**
	 * Sets a new value for {@link #integerNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntegerNone(@javax.annotation.Nullable final java.lang.Integer integerNone)
	{
		DefaultToItem.integerNone.set(this,integerNone);
	}

	/**
	 * Returns the value of {@link #dateEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getDateEight()
	{
		return DefaultToItem.dateEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDateEight(@javax.annotation.Nonnull final java.util.Date dateEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dateEight.set(this,dateEight);
	}

	/**
	 * Sets the current date for the date field {@link #dateEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDateEight()
	{
		DefaultToItem.dateEight.touch(this);
	}

	/**
	 * Returns the value of {@link #dateEighty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getDateEighty()
	{
		return DefaultToItem.dateEighty.get(this);
	}

	/**
	 * Sets a new value for {@link #dateEighty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDateEighty(@javax.annotation.Nullable final java.util.Date dateEighty)
	{
		DefaultToItem.dateEighty.set(this,dateEighty);
	}

	/**
	 * Sets the current date for the date field {@link #dateEighty}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDateEighty()
	{
		DefaultToItem.dateEighty.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getDateNone()
	{
		return DefaultToItem.dateNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDateNone(@javax.annotation.Nullable final java.util.Date dateNone)
	{
		DefaultToItem.dateNone.set(this,dateNone);
	}

	/**
	 * Sets the current date for the date field {@link #dateNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDateNone()
	{
		DefaultToItem.dateNone.touch(this);
	}

	/**
	 * Returns the value of {@link #dayEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDayEight()
	{
		return DefaultToItem.dayEight.get(this);
	}

	/**
	 * Sets a new value for {@link #dayEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDayEight(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayEight)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.dayEight.set(this,dayEight);
	}

	/**
	 * Sets today for the date field {@link #dayEight}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDayEight(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayEight.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNone()
	{
		return DefaultToItem.dayNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDayNone(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNone)
	{
		DefaultToItem.dayNone.set(this,dayNone);
	}

	/**
	 * Sets today for the date field {@link #dayNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDayNone(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DefaultToItem.dayNone.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #longRandom}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getLongRandom()
	{
		return DefaultToItem.longRandom.get(this);
	}

	/**
	 * Sets a new value for {@link #longRandom}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLongRandom(@javax.annotation.Nullable final java.lang.Long longRandom)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		DefaultToItem.longRandom.set(this,longRandom);
	}

	/**
	 * Returns the value of {@link #enumOne}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	DefaultToEnum getEnumOne()
	{
		return DefaultToItem.enumOne.get(this);
	}

	/**
	 * Sets a new value for {@link #enumOne}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumOne(@javax.annotation.Nonnull final DefaultToEnum enumOne)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DefaultToItem.enumOne.set(this,enumOne);
	}

	/**
	 * Returns the value of {@link #enumTwo}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	DefaultToEnum getEnumTwo()
	{
		return DefaultToItem.enumTwo.get(this);
	}

	/**
	 * Sets a new value for {@link #enumTwo}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumTwo(@javax.annotation.Nullable final DefaultToEnum enumTwo)
	{
		DefaultToItem.enumTwo.set(this,enumTwo);
	}

	/**
	 * Returns the value of {@link #enumNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	DefaultToEnum getEnumNone()
	{
		return DefaultToItem.enumNone.get(this);
	}

	/**
	 * Sets a new value for {@link #enumNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumNone(@javax.annotation.Nullable final DefaultToEnum enumNone)
	{
		DefaultToItem.enumNone.set(this,enumNone);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for defaultToItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DefaultToItem> TYPE = com.exedio.cope.TypesBound.newType(DefaultToItem.class,DefaultToItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DefaultToItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
