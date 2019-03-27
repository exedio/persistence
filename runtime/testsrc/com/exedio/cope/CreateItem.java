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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@WrapperType(genericConstructor=PACKAGE)
final class CreateItem extends CreateSuperItem
{
	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD") // called by reflection
	@SuppressWarnings("MethodOverridesInaccessibleMethodOfSuper")
	private static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
	{
		setValues[0] = text.map(value(setValues) + ".preCreate");
		return setValues;
	}

	@Override
	protected void afterNewCopeItem()
	{
		// do not call super.afterNewCopeItem here for testing
		// in a normal production environment you should definitely not forget this
		notifyAfterNewCopeItemInvoked();
		setText(getText() + ".postCreate");
	}

	/**
	 * Creates a new CreateItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.MandatoryViolationException if text is null.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CreateItem(
				@javax.annotation.Nonnull final java.lang.String text)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.CreateSuperItem.text.map(text),
		});
	}

	/**
	 * Creates a new CreateItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	CreateItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for createItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CreateItem> TYPE = com.exedio.cope.TypesBound.newType(CreateItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CreateItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
