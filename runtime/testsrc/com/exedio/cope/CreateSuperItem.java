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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

class CreateSuperItem extends Item
{
	static final StringField text = new StringField();

	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD") // called by reflection
	private static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
	{
		setValues[0] = text.map(value(setValues) + ".preCreateSuper");
		return setValues;
	}

	protected static final String value(final SetValue<?>[] setValues)
	{
		assertEquals(1, setValues.length);
		assertSame(text, setValues[0].settable);
		if("fail".equals(setValues[0].value))
			throw MandatoryViolationException.create(text, null);
		return (String)setValues[0].value;
	}

	@Override
	protected void afterNewCopeItem()
	{
		notifyAfterNewCopeItemInvoked();
		setText(getText() + ".postCreateSuper");
	}


	/**
	 * BEWARE:
	 * This code relies on the fact, that JVM initializes this variable to false
	 * even before the super constructore returns.
	 */
	private boolean afterNewCopeItemInvoked;

	protected final void notifyAfterNewCopeItemInvoked()
	{
		assertFalse(afterNewCopeItemInvoked);
		afterNewCopeItemInvoked = true;
	}

	@Override
	protected final SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] setValues)
	{
		assertTrue(afterNewCopeItemInvoked, Arrays.toString(setValues));
		return setValues;
	}


	/**
	 * Creates a new CreateSuperItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.MandatoryViolationException if text is null.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CreateSuperItem(
				@javax.annotation.Nonnull final java.lang.String text)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CreateSuperItem.text.map(text),
		});
	}

	/**
	 * Creates a new CreateSuperItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected CreateSuperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getText()
	{
		return CreateSuperItem.text.get(this);
	}

	/**
	 * Sets a new value for {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setText(@javax.annotation.Nonnull final java.lang.String text)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CreateSuperItem.text.set(this,text);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for createSuperItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CreateSuperItem> TYPE = com.exedio.cope.TypesBound.newType(CreateSuperItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CreateSuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
