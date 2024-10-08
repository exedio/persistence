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

package com.exedio.cope.instrument.testmodel;


public final class FullQualifyInput extends com.exedio.cope.instrument.testmodel.Input
{

	/**
	 * Creates a new FullQualifyInput with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.MandatoryViolationException if string is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if string, integer is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public FullQualifyInput(
				@javax.annotation.Nonnull final java.lang.String string,
				final int integer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.Input.string,string),
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.Input.integer,integer),
		});
	}

	/**
	 * Creates a new FullQualifyInput and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private FullQualifyInput(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for fullQualifyInput.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FullQualifyInput> TYPE = com.exedio.cope.TypesBound.newType(FullQualifyInput.class,FullQualifyInput::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private FullQualifyInput(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
