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

package com.exedio.cope.instrument.parameters;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(
		constructorSuppressWarnings="deprecation") // OK: testing deprecated API
@SuppressWarnings({"FinalMethodInFinalClass", "UnnecessarilyQualifiedStaticUsage"})
final class SuppressWarningsItem extends Item
{
	static final StringField field = new StringField();

	/**
	 * Creates a new SuppressWarningsItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"constructor-first","constructor-second","deprecation"})
	SuppressWarningsItem(
				@javax.annotation.Nonnull final java.lang.String field)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			SuppressWarningsItem.field.map(field),
		});
	}

	/**
	 * Creates a new SuppressWarningsItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(genericConstructor=...)
	private SuppressWarningsItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings("wrapper-single")
	@javax.annotation.Nonnull
	final java.lang.String getField()
	{
		return SuppressWarningsItem.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings("wrapper-single")
	final void setField(@javax.annotation.Nonnull final java.lang.String field)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		SuppressWarningsItem.field.set(this,field);
	}

	@com.exedio.cope.instrument.GeneratedClass
	private static final long serialVersionUID = 1;

	/**
	 * The persistent type information for suppressWarningsItem.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(type=...)
	@java.lang.SuppressWarnings("type-single")
	static final com.exedio.cope.Type<SuppressWarningsItem> TYPE = com.exedio.cope.TypesBound.newType(SuppressWarningsItem.class,SuppressWarningsItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.GeneratedClass
	private SuppressWarningsItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
