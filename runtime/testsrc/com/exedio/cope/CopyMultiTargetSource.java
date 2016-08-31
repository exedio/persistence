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

final class CopyMultiTargetSource extends Item
{
	static final ItemField<CopyMultiTargetA> targetA = ItemField.create(CopyMultiTargetA.class).toFinal().optional();
	static final ItemField<CopyMultiTargetB> targetB = ItemField.create(CopyMultiTargetB.class).toFinal().optional();

	static final StringField copy = new StringField().toFinal().optional().copyFrom(targetA).copyFrom(targetB);

	/**
	 * Creates a new CopyMultiTargetSource with all the fields initially needed.
	 * @param targetA the initial value for field {@link #targetA}.
	 * @param targetB the initial value for field {@link #targetB}.
	 * @param copy the initial value for field {@link #copy}.
	 * @throws com.exedio.cope.StringLengthViolationException if copy violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CopyMultiTargetSource(
				@javax.annotation.Nullable final CopyMultiTargetA targetA,
				@javax.annotation.Nullable final CopyMultiTargetB targetB,
				@javax.annotation.Nullable final java.lang.String copy)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopyMultiTargetSource.targetA.map(targetA),
			CopyMultiTargetSource.targetB.map(targetB),
			CopyMultiTargetSource.copy.map(copy),
		});
	}

	/**
	 * Creates a new CopyMultiTargetSource and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CopyMultiTargetSource(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #targetA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final CopyMultiTargetA getTargetA()
	{
		return CopyMultiTargetSource.targetA.get(this);
	}

	/**
	 * Returns the value of {@link #targetB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final CopyMultiTargetB getTargetB()
	{
		return CopyMultiTargetSource.targetB.get(this);
	}

	/**
	 * Returns the value of {@link #copy}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getCopy()
	{
		return CopyMultiTargetSource.copy.get(this);
	}

	/**
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyMultiTargetSource.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopyMultiTargetSource> TYPE = com.exedio.cope.TypesBound.newType(CopyMultiTargetSource.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CopyMultiTargetSource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
