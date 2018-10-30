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

	static final LimitedListField<Integer> nums = LimitedListField.create(num1, num2, num3);

	// implicit external source

	static final LimitedListField<Date> dates = LimitedListField.create(new DateField().optional(), new DateField().optional());

	// internal source

	static final LimitedListField<String> strings = LimitedListField.create(new StringField().optional().lengthRange(1, 11), 4);

	LimitedListFieldItem(final int initialNum1, final int initialNum2, final int initialNum3)
	{
		super(
			nums.getLength().map(3),
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
		return strings.getLength().getMandatory(this);
	}

	/**
	 * Creates a new LimitedListFieldItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public LimitedListFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new LimitedListFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	LimitedListFieldItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #num1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getNum1()
	{
		return LimitedListFieldItem.num1.get(this);
	}

	/**
	 * Sets a new value for {@link #num1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNum1(@javax.annotation.Nullable final java.lang.Integer num1)
	{
		LimitedListFieldItem.num1.set(this,num1);
	}

	/**
	 * Returns the value of {@link #num2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getNum2()
	{
		return LimitedListFieldItem.num2.get(this);
	}

	/**
	 * Sets a new value for {@link #num2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNum2(@javax.annotation.Nullable final java.lang.Integer num2)
	{
		LimitedListFieldItem.num2.set(this,num2);
	}

	/**
	 * Returns the value of {@link #num3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getNum3()
	{
		return LimitedListFieldItem.num3.get(this);
	}

	/**
	 * Sets a new value for {@link #num3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNum3(@javax.annotation.Nullable final java.lang.Integer num3)
	{
		LimitedListFieldItem.num3.set(this,num3);
	}

	/**
	 * Returns the value of {@link #nums}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.List<Integer> getNums()
	{
		return LimitedListFieldItem.nums.get(this);
	}

	/**
	 * Sets a new value for {@link #nums}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.List<Date> getDates()
	{
		return LimitedListFieldItem.dates.get(this);
	}

	/**
	 * Sets a new value for {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.List<String> getStrings()
	{
		return LimitedListFieldItem.strings.get(this);
	}

	/**
	 * Sets a new value for {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStrings(@javax.annotation.Nonnull final java.util.Collection<? extends String> strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException,
				com.exedio.cope.pattern.ListSizeViolationException
	{
		LimitedListFieldItem.strings.set(this,strings);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for limitedListFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LimitedListFieldItem> TYPE = com.exedio.cope.TypesBound.newType(LimitedListFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private LimitedListFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
