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

final class GroupItem extends Item
{
	static final DayField day = new DayField();
	static final IntegerField number = new IntegerField();
	static final DoubleField optionalDouble = new DoubleField().optional();

	/**
	 * Creates a new GroupItem with all the fields initially needed.
	 * @param day the initial value for field {@link #day}.
	 * @param number the initial value for field {@link #number}.
	 * @throws com.exedio.cope.MandatoryViolationException if day is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	GroupItem(
				@javax.annotation.Nonnull final com.exedio.cope.util.Day day,
				final int number)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(GroupItem.day,day),
			com.exedio.cope.SetValue.map(GroupItem.number,number),
		});
	}

	/**
	 * Creates a new GroupItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private GroupItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDay()
	{
		return GroupItem.day.get(this);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDay(@javax.annotation.Nonnull final com.exedio.cope.util.Day day)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		GroupItem.day.set(this,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		GroupItem.day.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #number}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getNumber()
	{
		return GroupItem.number.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #number}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNumber(final int number)
	{
		GroupItem.number.set(this,number);
	}

	/**
	 * Returns the value of {@link #optionalDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double getOptionalDouble()
	{
		return GroupItem.optionalDouble.get(this);
	}

	/**
	 * Sets a new value for {@link #optionalDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOptionalDouble(@javax.annotation.Nullable final java.lang.Double optionalDouble)
	{
		GroupItem.optionalDouble.set(this,optionalDouble);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for groupItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<GroupItem> TYPE = com.exedio.cope.TypesBound.newType(GroupItem.class,GroupItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private GroupItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
