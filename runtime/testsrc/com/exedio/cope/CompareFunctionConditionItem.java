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

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.util.Day;
import java.util.Date;

final class CompareFunctionConditionItem extends Item
{
	@WrapperInitial static final StringField stringA = new StringField().optional();
	@WrapperInitial static final StringField stringB = new StringField().optional();

	@WrapperInitial static final IntegerField intA = new IntegerField().optional();
	@WrapperInitial static final IntegerField intB = new IntegerField().optional();

	@WrapperInitial static final LongField longA = new LongField().optional();
	@WrapperInitial static final LongField longB = new LongField().optional();

	@WrapperInitial static final DoubleField doubleA = new DoubleField().optional();
	@WrapperInitial static final DoubleField doubleB = new DoubleField().optional();

	@WrapperInitial static final DateField dateA = new DateField().optional();
	@WrapperInitial static final DateField dateB = new DateField().optional();

	@WrapperInitial static final DayField dayA = new DayField().optional();
	@WrapperInitial static final DayField dayB = new DayField().optional();

	@WrapperInitial static final EnumField<XEnum> enumA = EnumField.create(XEnum.class).optional();
	@WrapperInitial static final EnumField<XEnum> enumB = EnumField.create(XEnum.class).optional();

	enum XEnum
	{
		V1, V2, V3, V4, V5
	}

	static final ItemField<CompareFunctionConditionItem> itemA = ItemField.create(CompareFunctionConditionItem.class).nullify();
	static final ItemField<CompareFunctionConditionItem> itemB = ItemField.create(CompareFunctionConditionItem.class).nullify();

	static final Date date = new Date(1087365298214l);
	static final Day day = new Day(2007, 4, 28);

	CompareFunctionConditionItem(
			final java.lang.String leftString,
			final java.lang.Integer leftInt,
			final java.lang.Long leftLong,
			final java.lang.Double leftDouble,
			final java.util.Date leftDate,
			final Day leftDay,
			final XEnum leftEnum)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(leftString, "string3", leftInt, 3, leftLong, 13l, leftDouble, 2.3, leftDate, date, leftDay, day, leftEnum, XEnum.V3);
		//noinspection ThisEscapedInObjectConstruction
		setItemA(this);
	}

	/**
	 * Creates a new CompareFunctionConditionItem with all the fields initially needed.
	 * @param stringA the initial value for field {@link #stringA}.
	 * @param stringB the initial value for field {@link #stringB}.
	 * @param intA the initial value for field {@link #intA}.
	 * @param intB the initial value for field {@link #intB}.
	 * @param longA the initial value for field {@link #longA}.
	 * @param longB the initial value for field {@link #longB}.
	 * @param doubleA the initial value for field {@link #doubleA}.
	 * @param doubleB the initial value for field {@link #doubleB}.
	 * @param dateA the initial value for field {@link #dateA}.
	 * @param dateB the initial value for field {@link #dateB}.
	 * @param dayA the initial value for field {@link #dayA}.
	 * @param dayB the initial value for field {@link #dayB}.
	 * @param enumA the initial value for field {@link #enumA}.
	 * @param enumB the initial value for field {@link #enumB}.
	 * @throws com.exedio.cope.StringLengthViolationException if stringA, stringB violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CompareFunctionConditionItem(
				@javax.annotation.Nullable final java.lang.String stringA,
				@javax.annotation.Nullable final java.lang.String stringB,
				@javax.annotation.Nullable final java.lang.Integer intA,
				@javax.annotation.Nullable final java.lang.Integer intB,
				@javax.annotation.Nullable final java.lang.Long longA,
				@javax.annotation.Nullable final java.lang.Long longB,
				@javax.annotation.Nullable final java.lang.Double doubleA,
				@javax.annotation.Nullable final java.lang.Double doubleB,
				@javax.annotation.Nullable final java.util.Date dateA,
				@javax.annotation.Nullable final java.util.Date dateB,
				@javax.annotation.Nullable final com.exedio.cope.util.Day dayA,
				@javax.annotation.Nullable final com.exedio.cope.util.Day dayB,
				@javax.annotation.Nullable final XEnum enumA,
				@javax.annotation.Nullable final XEnum enumB)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompareFunctionConditionItem.stringA.map(stringA),
			CompareFunctionConditionItem.stringB.map(stringB),
			CompareFunctionConditionItem.intA.map(intA),
			CompareFunctionConditionItem.intB.map(intB),
			CompareFunctionConditionItem.longA.map(longA),
			CompareFunctionConditionItem.longB.map(longB),
			CompareFunctionConditionItem.doubleA.map(doubleA),
			CompareFunctionConditionItem.doubleB.map(doubleB),
			CompareFunctionConditionItem.dateA.map(dateA),
			CompareFunctionConditionItem.dateB.map(dateB),
			CompareFunctionConditionItem.dayA.map(dayA),
			CompareFunctionConditionItem.dayB.map(dayB),
			CompareFunctionConditionItem.enumA.map(enumA),
			CompareFunctionConditionItem.enumB.map(enumB),
		});
	}

	/**
	 * Creates a new CompareFunctionConditionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompareFunctionConditionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #stringA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getStringA()
	{
		return CompareFunctionConditionItem.stringA.get(this);
	}

	/**
	 * Sets a new value for {@link #stringA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStringA(@javax.annotation.Nullable final java.lang.String stringA)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CompareFunctionConditionItem.stringA.set(this,stringA);
	}

	/**
	 * Returns the value of {@link #stringB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getStringB()
	{
		return CompareFunctionConditionItem.stringB.get(this);
	}

	/**
	 * Sets a new value for {@link #stringB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStringB(@javax.annotation.Nullable final java.lang.String stringB)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CompareFunctionConditionItem.stringB.set(this,stringB);
	}

	/**
	 * Returns the value of {@link #intA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntA()
	{
		return CompareFunctionConditionItem.intA.get(this);
	}

	/**
	 * Sets a new value for {@link #intA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntA(@javax.annotation.Nullable final java.lang.Integer intA)
	{
		CompareFunctionConditionItem.intA.set(this,intA);
	}

	/**
	 * Returns the value of {@link #intB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntB()
	{
		return CompareFunctionConditionItem.intB.get(this);
	}

	/**
	 * Sets a new value for {@link #intB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntB(@javax.annotation.Nullable final java.lang.Integer intB)
	{
		CompareFunctionConditionItem.intB.set(this,intB);
	}

	/**
	 * Returns the value of {@link #longA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getLongA()
	{
		return CompareFunctionConditionItem.longA.get(this);
	}

	/**
	 * Sets a new value for {@link #longA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLongA(@javax.annotation.Nullable final java.lang.Long longA)
	{
		CompareFunctionConditionItem.longA.set(this,longA);
	}

	/**
	 * Returns the value of {@link #longB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getLongB()
	{
		return CompareFunctionConditionItem.longB.get(this);
	}

	/**
	 * Sets a new value for {@link #longB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLongB(@javax.annotation.Nullable final java.lang.Long longB)
	{
		CompareFunctionConditionItem.longB.set(this,longB);
	}

	/**
	 * Returns the value of {@link #doubleA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double getDoubleA()
	{
		return CompareFunctionConditionItem.doubleA.get(this);
	}

	/**
	 * Sets a new value for {@link #doubleA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDoubleA(@javax.annotation.Nullable final java.lang.Double doubleA)
	{
		CompareFunctionConditionItem.doubleA.set(this,doubleA);
	}

	/**
	 * Returns the value of {@link #doubleB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double getDoubleB()
	{
		return CompareFunctionConditionItem.doubleB.get(this);
	}

	/**
	 * Sets a new value for {@link #doubleB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDoubleB(@javax.annotation.Nullable final java.lang.Double doubleB)
	{
		CompareFunctionConditionItem.doubleB.set(this,doubleB);
	}

	/**
	 * Returns the value of {@link #dateA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getDateA()
	{
		return CompareFunctionConditionItem.dateA.get(this);
	}

	/**
	 * Sets a new value for {@link #dateA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDateA(@javax.annotation.Nullable final java.util.Date dateA)
	{
		CompareFunctionConditionItem.dateA.set(this,dateA);
	}

	/**
	 * Sets the current date for the date field {@link #dateA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDateA()
	{
		CompareFunctionConditionItem.dateA.touch(this);
	}

	/**
	 * Returns the value of {@link #dateB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getDateB()
	{
		return CompareFunctionConditionItem.dateB.get(this);
	}

	/**
	 * Sets a new value for {@link #dateB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDateB(@javax.annotation.Nullable final java.util.Date dateB)
	{
		CompareFunctionConditionItem.dateB.set(this,dateB);
	}

	/**
	 * Sets the current date for the date field {@link #dateB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDateB()
	{
		CompareFunctionConditionItem.dateB.touch(this);
	}

	/**
	 * Returns the value of {@link #dayA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayA()
	{
		return CompareFunctionConditionItem.dayA.get(this);
	}

	/**
	 * Sets a new value for {@link #dayA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDayA(@javax.annotation.Nullable final com.exedio.cope.util.Day dayA)
	{
		CompareFunctionConditionItem.dayA.set(this,dayA);
	}

	/**
	 * Sets today for the date field {@link #dayA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDayA(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		CompareFunctionConditionItem.dayA.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayB()
	{
		return CompareFunctionConditionItem.dayB.get(this);
	}

	/**
	 * Sets a new value for {@link #dayB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDayB(@javax.annotation.Nullable final com.exedio.cope.util.Day dayB)
	{
		CompareFunctionConditionItem.dayB.set(this,dayB);
	}

	/**
	 * Sets today for the date field {@link #dayB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDayB(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		CompareFunctionConditionItem.dayB.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #enumA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	XEnum getEnumA()
	{
		return CompareFunctionConditionItem.enumA.get(this);
	}

	/**
	 * Sets a new value for {@link #enumA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumA(@javax.annotation.Nullable final XEnum enumA)
	{
		CompareFunctionConditionItem.enumA.set(this,enumA);
	}

	/**
	 * Returns the value of {@link #enumB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	XEnum getEnumB()
	{
		return CompareFunctionConditionItem.enumB.get(this);
	}

	/**
	 * Sets a new value for {@link #enumB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumB(@javax.annotation.Nullable final XEnum enumB)
	{
		CompareFunctionConditionItem.enumB.set(this,enumB);
	}

	/**
	 * Returns the value of {@link #itemA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CompareFunctionConditionItem getItemA()
	{
		return CompareFunctionConditionItem.itemA.get(this);
	}

	/**
	 * Sets a new value for {@link #itemA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItemA(@javax.annotation.Nullable final CompareFunctionConditionItem itemA)
	{
		CompareFunctionConditionItem.itemA.set(this,itemA);
	}

	/**
	 * Returns the value of {@link #itemB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CompareFunctionConditionItem getItemB()
	{
		return CompareFunctionConditionItem.itemB.get(this);
	}

	/**
	 * Sets a new value for {@link #itemB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItemB(@javax.annotation.Nullable final CompareFunctionConditionItem itemB)
	{
		CompareFunctionConditionItem.itemB.set(this,itemB);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compareFunctionConditionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CompareFunctionConditionItem> TYPE = com.exedio.cope.TypesBound.newType(CompareFunctionConditionItem.class,CompareFunctionConditionItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CompareFunctionConditionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
