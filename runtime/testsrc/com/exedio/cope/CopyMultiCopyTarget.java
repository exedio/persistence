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

final class CopyMultiCopyTarget extends Item
{
	@WrapperInitial
	static final StringField copyA = new StringField().toFinal().optional();

	@WrapperInitial
	static final StringField copyB = new StringField().toFinal().optional();

	/**
	 * Creates a new CopyMultiCopyTarget with all the fields initially needed.
	 * @param copyA the initial value for field {@link #copyA}.
	 * @param copyB the initial value for field {@link #copyB}.
	 * @throws com.exedio.cope.StringLengthViolationException if copyA, copyB violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CopyMultiCopyTarget(
				@javax.annotation.Nullable final java.lang.String copyA,
				@javax.annotation.Nullable final java.lang.String copyB)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopyMultiCopyTarget.copyA.map(copyA),
			CopyMultiCopyTarget.copyB.map(copyB),
		});
	}

	/**
	 * Creates a new CopyMultiCopyTarget and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopyMultiCopyTarget(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #copyA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCopyA()
	{
		return CopyMultiCopyTarget.copyA.get(this);
	}

	/**
	 * Returns the value of {@link #copyB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCopyB()
	{
		return CopyMultiCopyTarget.copyB.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyMultiCopyTarget.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopyMultiCopyTarget> TYPE = com.exedio.cope.TypesBound.newType(CopyMultiCopyTarget.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopyMultiCopyTarget(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
