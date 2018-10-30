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

import static com.exedio.cope.pattern.Range.valueOf;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;

public final class RangeFieldItem extends Item
{
	@WrapperInitial static final RangeField<Integer> valid = RangeField.create(new IntegerField().optional().min(-10));
	static final RangeField<String> text = RangeField.create(new StringField().toFinal());

	RangeFieldItem(final Integer validFrom, final Integer validTo)
	{
		this(
				valueOf(validFrom, validTo),
				valueOf("alpha", "beta"));
	}

	/**
	 * Creates a new RangeFieldItem with all the fields initially needed.
	 * @param valid the initial value for field {@link #valid}.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.IntegerRangeViolationException if valid violates its range constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if text is null.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	RangeFieldItem(
				@javax.annotation.Nullable final com.exedio.cope.pattern.Range<Integer> valid,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Range<String> text)
			throws
				com.exedio.cope.IntegerRangeViolationException,
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			RangeFieldItem.valid.map(valid),
			RangeFieldItem.text.map(text),
		});
	}

	/**
	 * Creates a new RangeFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private RangeFieldItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Range<Integer> getValid()
	{
		return RangeFieldItem.valid.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setValid(@javax.annotation.Nonnull final com.exedio.cope.pattern.Range<? extends Integer> valid)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		RangeFieldItem.valid.set(this,valid);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFrom")
	@javax.annotation.Nullable
	Integer getValidFrom()
	{
		return RangeFieldItem.valid.getFrom(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getTo")
	@javax.annotation.Nullable
	Integer getValidTo()
	{
		return RangeFieldItem.valid.getTo(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setFrom")
	void setValidFrom(@javax.annotation.Nullable final Integer valid)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		RangeFieldItem.valid.setFrom(this,valid);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setTo")
	void setValidTo(@javax.annotation.Nullable final Integer valid)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		RangeFieldItem.valid.setTo(this,valid);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="doesContain")
	boolean doesValidContain(@javax.annotation.Nonnull final Integer valid)
	{
		return RangeFieldItem.valid.doesContain(this,valid);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Range<String> getText()
	{
		return RangeFieldItem.text.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFrom")
	@javax.annotation.Nonnull
	String getTextFrom()
	{
		return RangeFieldItem.text.getFrom(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getTo")
	@javax.annotation.Nonnull
	String getTextTo()
	{
		return RangeFieldItem.text.getTo(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="doesContain")
	boolean doesTextContain(@javax.annotation.Nonnull final String text)
	{
		return RangeFieldItem.text.doesContain(this,text);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for rangeFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<RangeFieldItem> TYPE = com.exedio.cope.TypesBound.newType(RangeFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private RangeFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
