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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;
import java.util.Date;

@WrapperType(genericConstructor=PACKAGE)
public final class LimitedListFieldItem extends Item
{
	static final LimitedListField<Integer> nums = LimitedListField.create(new IntegerField().optional(), 3);

	static final LimitedListField<Date> dates = LimitedListField.create(new DateField(), 2);

	static final LimitedListField<String> strings = LimitedListField.create(new StringField().optional().lengthRange(1, 11), 4);

	LimitedListFieldItem(final int initialNum1, final int initialNum2, final int initialNum3)
	{
		super(
			SetValue.map(nums.getLengthIfExists(), 3),
			SetValue.map(nums.getListSources().get(0), initialNum1),
			SetValue.map(nums.getListSources().get(1), initialNum2),
			SetValue.map(nums.getListSources().get(2), initialNum3));
	}

	Integer getNum1()
	{
		return getNum(0);
	}

	Integer getNum2()
	{
		return getNum(1);
	}

	Integer getNum3()
	{
		return getNum(2);
	}

	private Integer getNum(final int i)
	{
		return nums.getListSources().get(i).get(this);
	}

	Date getDate0()
	{
		return getDate(0);
	}

	Date getDate1()
	{
		return getDate(1);
	}

	private Date getDate(final int i)
	{
		return dates.getListSources().get(i).get(this);
	}

	String getString0()
	{
		return getString(0);
	}

	String getString1()
	{
		return getString(1);
	}

	String getString2()
	{
		return getString(2);
	}

	String getString3()
	{
		return getString(3);
	}

	private String getString(final int i)
	{
		return strings.getListSources().get(i).get(this);
	}

	int getStringLength()
	{
		return strings.getLengthIfExists().getMandatory(this);
	}

	/**
	 * Creates a new LimitedListFieldItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public LimitedListFieldItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new LimitedListFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	LimitedListFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #nums}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<Integer> getNums()
	{
		return LimitedListFieldItem.nums.get(this);
	}

	/**
	 * Sets a new value for {@link #nums}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNums(@javax.annotation.Nonnull final java.util.Collection<? extends Integer> nums)
			throws
				java.lang.ClassCastException,
				com.exedio.cope.pattern.ListSizeViolationException
	{
		LimitedListFieldItem.nums.set(this,nums);
	}

	/**
	 * Returns the value of {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<Date> getDates()
	{
		return LimitedListFieldItem.dates.get(this);
	}

	/**
	 * Sets a new value for {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDates(@javax.annotation.Nonnull final java.util.Collection<? extends Date> dates)
			throws
				java.lang.ClassCastException,
				com.exedio.cope.pattern.ListSizeViolationException
	{
		LimitedListFieldItem.dates.set(this,dates);
	}

	/**
	 * Returns the value of {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<String> getStrings()
	{
		return LimitedListFieldItem.strings.get(this);
	}

	/**
	 * Sets a new value for {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStrings(@javax.annotation.Nonnull final java.util.Collection<? extends String> strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException,
				com.exedio.cope.pattern.ListSizeViolationException
	{
		LimitedListFieldItem.strings.set(this,strings);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for limitedListFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LimitedListFieldItem> TYPE = com.exedio.cope.TypesBound.newType(LimitedListFieldItem.class,LimitedListFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LimitedListFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
