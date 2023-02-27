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

import com.exedio.cope.instrument.WrapperInitial;

final class CopyMultiTargetA extends Item
{
	@WrapperInitial
	static final StringField copy = new StringField().toFinal().optional();

	/**
	 * Creates a new CopyMultiTargetA with all the fields initially needed.
	 * @param copy the initial value for field {@link #copy}.
	 * @throws com.exedio.cope.StringLengthViolationException if copy violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CopyMultiTargetA(
				@javax.annotation.Nullable final java.lang.String copy)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CopyMultiTargetA.copy,copy),
		});
	}

	/**
	 * Creates a new CopyMultiTargetA and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopyMultiTargetA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #copy}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCopy()
	{
		return CopyMultiTargetA.copy.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyMultiTargetA.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopyMultiTargetA> TYPE = com.exedio.cope.TypesBound.newType(CopyMultiTargetA.class,CopyMultiTargetA::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopyMultiTargetA(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
