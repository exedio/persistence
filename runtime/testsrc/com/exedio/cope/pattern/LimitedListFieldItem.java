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
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;
import java.util.Date;

@WrapperType(genericConstructor=PACKAGE)
public final class LimitedListFieldItem extends Item
{
	// explicit external source

	static final IntegerField num1 = new IntegerField().optional();

	static final IntegerField num2 = new IntegerField().optional();

	static final IntegerField num3 = new IntegerField().optional();

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final LimitedListField<Integer> nums = LimitedListField.create(num1, num2, num3);

	// implicit external source

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final LimitedListField<Date> dates = LimitedListField.create(new DateField().optional(), new DateField().optional());

	// internal source

	static final LimitedListField<String> strings = LimitedListField.create(new StringField().optional().lengthRange(1, 11), 4);

	LimitedListFieldItem(final int initialNum1, final int initialNum2, final int initialNum3)
	{
		super(
			nums.getLengthIfExists().map(3),
			num1.map(initialNum1),
			num2.map(initialNum2),
			num3.map(initialNum3));
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
	 * Returns the value of {@link #num1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getNum1()
	{
		return LimitedListFieldItem.num1.get(this);
	}

	/**
	 * Sets a new value for {@link #num1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNum1(@javax.annotation.Nullable final java.lang.Integer num1)
	{
		LimitedListFieldItem.num1.set(this,num1);
	}

	/**
	 * Returns the value of {@link #num2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getNum2()
	{
		return LimitedListFieldItem.num2.get(this);
	}

	/**
	 * Sets a new value for {@link #num2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNum2(@javax.annotation.Nullable final java.lang.Integer num2)
	{
		LimitedListFieldItem.num2.set(this,num2);
	}

	/**
	 * Returns the value of {@link #num3}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getNum3()
	{
		return LimitedListFieldItem.num3.get(this);
	}

	/**
	 * Sets a new value for {@link #num3}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNum3(@javax.annotation.Nullable final java.lang.Integer num3)
	{
		LimitedListFieldItem.num3.set(this,num3);
	}

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
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for limitedListFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LimitedListFieldItem> TYPE = com.exedio.cope.TypesBound.newType(LimitedListFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LimitedListFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
