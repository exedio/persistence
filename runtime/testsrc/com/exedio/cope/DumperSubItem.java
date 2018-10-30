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

public final class DumperSubItem extends DumperItem
{
	static final StringField subString = new StringField().lengthMax(10);

	/**
	 * Creates a new DumperSubItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param unique the initial value for field {@link #unique}.
	 * @param data the initial value for field {@link #data}.
	 * @param subString the initial value for field {@link #subString}.
	 * @throws com.exedio.cope.MandatoryViolationException if string, unique, data, subString is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string, unique, subString violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if unique is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DumperSubItem(
				@javax.annotation.Nonnull final java.lang.String string,
				@javax.annotation.Nonnull final java.lang.String unique,
				@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data,
				@javax.annotation.Nonnull final java.lang.String subString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.DumperItem.string.map(string),
			com.exedio.cope.DumperItem.unique.map(unique),
			com.exedio.cope.DumperItem.data.map(data),
			DumperSubItem.subString.map(subString),
		});
	}

	/**
	 * Creates a new DumperSubItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DumperSubItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #subString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getSubString()
	{
		return DumperSubItem.subString.get(this);
	}

	/**
	 * Sets a new value for {@link #subString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSubString(@javax.annotation.Nonnull final java.lang.String subString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DumperSubItem.subString.set(this,subString);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dumperSubItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DumperSubItem> TYPE = com.exedio.cope.TypesBound.newType(DumperSubItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DumperSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
