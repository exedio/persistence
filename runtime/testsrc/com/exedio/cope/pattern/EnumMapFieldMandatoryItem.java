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
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;
// EnumMapFieldItem.Language has to be used with full name below, otherwise instrumentation fails on Windows:
// import com.exedio.cope.pattern.EnumMapFieldItem.Language;

public final class EnumMapFieldMandatoryItem extends Item
{
	/**
	 * TODO The initial should be determined automatically by the instrumentor.
	 */
	@WrapperInitial
	static final EnumMapField<EnumMapFieldItem.Language, String> text =
			EnumMapField.create(EnumMapFieldItem.Language.class, new StringField()).
				defaultTo(EnumMapFieldItem.Language.DE, "defaultDE").
				defaultTo(EnumMapFieldItem.Language.EN, "defaultEN");

	EnumMapFieldMandatoryItem()
	{
		this(new SetValue<?>[]{
		});
	}

	/**
	 * Creates a new EnumMapFieldMandatoryItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.MandatoryViolationException if text is null.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	EnumMapFieldMandatoryItem(
				@javax.annotation.Nonnull final java.util.EnumMap<EnumMapFieldItem.Language,String> text)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			EnumMapFieldMandatoryItem.text.map(text),
		});
	}

	/**
	 * Creates a new EnumMapFieldMandatoryItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private EnumMapFieldMandatoryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	String getText(@javax.annotation.Nonnull final EnumMapFieldItem.Language k)
	{
		return EnumMapFieldMandatoryItem.text.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setText(@javax.annotation.Nonnull final EnumMapFieldItem.Language k,@javax.annotation.Nonnull final String text)
	{
		EnumMapFieldMandatoryItem.text.set(this,k,text);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMap")
	@javax.annotation.Nonnull
	java.util.Map<EnumMapFieldItem.Language,String> getTextMap()
	{
		return EnumMapFieldMandatoryItem.text.getMap(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setMap")
	void setTextMap(@javax.annotation.Nonnull final java.util.Map<? extends EnumMapFieldItem.Language,? extends String> text)
	{
		EnumMapFieldMandatoryItem.text.setMap(this,text);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumMapFieldMandatoryItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<EnumMapFieldMandatoryItem> TYPE = com.exedio.cope.TypesBound.newType(EnumMapFieldMandatoryItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private EnumMapFieldMandatoryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
