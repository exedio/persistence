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

package com.exedio.cope.instanceOfQuery;

import com.exedio.cope.ItemField;

final class IoqSourceSubB extends IoqSourceSuper
{
	static final ItemField<IoqSourceSubA> brother = ItemField.create(IoqSourceSubA.class);

	/**
	 * Creates a new IoqSourceSubB with all the fields initially needed.
	 * @param ref the initial value for field {@link #ref}.
	 * @param code the initial value for field {@link #code}.
	 * @param brother the initial value for field {@link #brother}.
	 * @throws com.exedio.cope.MandatoryViolationException if ref, code, brother is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	IoqSourceSubB(
				@javax.annotation.Nonnull final com.exedio.cope.instanceOfQuery.IoqTargetSuper ref,
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final IoqSourceSubA brother)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.instanceOfQuery.IoqSourceSuper.ref,ref),
			com.exedio.cope.SetValue.map(com.exedio.cope.instanceOfQuery.IoqSourceSuper.code,code),
			com.exedio.cope.SetValue.map(IoqSourceSubB.brother,brother),
		});
	}

	/**
	 * Creates a new IoqSourceSubB and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private IoqSourceSubB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #brother}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	IoqSourceSubA getBrother()
	{
		return IoqSourceSubB.brother.get(this);
	}

	/**
	 * Sets a new value for {@link #brother}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBrother(@javax.annotation.Nonnull final IoqSourceSubA brother)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		IoqSourceSubB.brother.set(this,brother);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for ioqSourceSubB.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<IoqSourceSubB> TYPE = com.exedio.cope.TypesBound.newType(IoqSourceSubB.class,IoqSourceSubB::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private IoqSourceSubB(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
