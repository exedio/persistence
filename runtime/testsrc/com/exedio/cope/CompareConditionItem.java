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

final class CompareConditionItem extends Item
{
	@WrapperInitial static final StringField string = new StringField().optional();
	static final StringField otherString = new StringField().optional();
	@WrapperInitial static final IntegerField intx = new IntegerField().optional();
	@WrapperInitial static final LongField longx = new LongField().optional();
	@WrapperInitial static final DoubleField doublex = new DoubleField().optional();
	@WrapperInitial static final DateField date = new DateField().optional();
	@WrapperInitial static final DayField day = new DayField().optional();
	@WrapperInitial static final EnumField<YEnum> enumx = EnumField.create(YEnum.class).optional();

	enum YEnum
	{
		V1, V2, V3, V4, V5, VX
	}

	static final ItemField<CompareConditionItem> item = ItemField.create(CompareConditionItem.class).nullify();

	/**
	 * Creates a new CompareConditionItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param intx the initial value for field {@link #intx}.
	 * @param longx the initial value for field {@link #longx}.
	 * @param doublex the initial value for field {@link #doublex}.
	 * @param date the initial value for field {@link #date}.
	 * @param day the initial value for field {@link #day}.
	 * @param enumx the initial value for field {@link #enumx}.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CompareConditionItem(
				@javax.annotation.Nullable final java.lang.String string,
				@javax.annotation.Nullable final java.lang.Integer intx,
				@javax.annotation.Nullable final java.lang.Long longx,
				@javax.annotation.Nullable final java.lang.Double doublex,
				@javax.annotation.Nullable final java.util.Date date,
				@javax.annotation.Nullable final com.exedio.cope.util.Day day,
				@javax.annotation.Nullable final YEnum enumx)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompareConditionItem.string.map(string),
			CompareConditionItem.intx.map(intx),
			CompareConditionItem.longx.map(longx),
			CompareConditionItem.doublex.map(doublex),
			CompareConditionItem.date.map(date),
			CompareConditionItem.day.map(day),
			CompareConditionItem.enumx.map(enumx),
		});
	}

	/**
	 * Creates a new CompareConditionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompareConditionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getString()
	{
		return CompareConditionItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setString(@javax.annotation.Nullable final java.lang.String string)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CompareConditionItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #otherString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getOtherString()
	{
		return CompareConditionItem.otherString.get(this);
	}

	/**
	 * Sets a new value for {@link #otherString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOtherString(@javax.annotation.Nullable final java.lang.String otherString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CompareConditionItem.otherString.set(this,otherString);
	}

	/**
	 * Returns the value of {@link #intx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntx()
	{
		return CompareConditionItem.intx.get(this);
	}

	/**
	 * Sets a new value for {@link #intx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntx(@javax.annotation.Nullable final java.lang.Integer intx)
	{
		CompareConditionItem.intx.set(this,intx);
	}

	/**
	 * Returns the value of {@link #longx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getLongx()
	{
		return CompareConditionItem.longx.get(this);
	}

	/**
	 * Sets a new value for {@link #longx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLongx(@javax.annotation.Nullable final java.lang.Long longx)
	{
		CompareConditionItem.longx.set(this,longx);
	}

	/**
	 * Returns the value of {@link #doublex}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double getDoublex()
	{
		return CompareConditionItem.doublex.get(this);
	}

	/**
	 * Sets a new value for {@link #doublex}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDoublex(@javax.annotation.Nullable final java.lang.Double doublex)
	{
		CompareConditionItem.doublex.set(this,doublex);
	}

	/**
	 * Returns the value of {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getDate()
	{
		return CompareConditionItem.date.get(this);
	}

	/**
	 * Sets a new value for {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDate(@javax.annotation.Nullable final java.util.Date date)
	{
		CompareConditionItem.date.set(this,date);
	}

	/**
	 * Sets the current date for the date field {@link #date}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDate()
	{
		CompareConditionItem.date.touch(this);
	}

	/**
	 * Returns the value of {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDay()
	{
		return CompareConditionItem.day.get(this);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDay(@javax.annotation.Nullable final com.exedio.cope.util.Day day)
	{
		CompareConditionItem.day.set(this,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		CompareConditionItem.day.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #enumx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	YEnum getEnumx()
	{
		return CompareConditionItem.enumx.get(this);
	}

	/**
	 * Sets a new value for {@link #enumx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setEnumx(@javax.annotation.Nullable final YEnum enumx)
	{
		CompareConditionItem.enumx.set(this,enumx);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CompareConditionItem getItem()
	{
		return CompareConditionItem.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItem(@javax.annotation.Nullable final CompareConditionItem item)
	{
		CompareConditionItem.item.set(this,item);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compareConditionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CompareConditionItem> TYPE = com.exedio.cope.TypesBound.newType(CompareConditionItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CompareConditionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
