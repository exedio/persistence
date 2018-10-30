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

final class CopyMultiTargetSource extends Item
{
	@WrapperInitial static final ItemField<CopyMultiTargetA> targetA = ItemField.create(CopyMultiTargetA.class).optional();
	@WrapperInitial static final ItemField<CopyMultiTargetB> targetB = ItemField.create(CopyMultiTargetB.class).optional();

	@WrapperInitial
	static final StringField copy = new StringField().optional().copyFrom(targetA).copyFrom(targetB);


	static CopyMultiTargetSource omitCopy(
			final CopyMultiTargetA targetA,
			final CopyMultiTargetB targetB)
	{
		return new CopyMultiTargetSource(
				CopyMultiTargetSource.targetA.map(targetA),
				CopyMultiTargetSource.targetB.map(targetB)
		);
	}

	static CopyMultiTargetSource omitCopy(
			final CopyMultiTargetA targetA)
	{
		return new CopyMultiTargetSource(
				CopyMultiTargetSource.targetA.map(targetA)
		);
	}

	static CopyMultiTargetSource omitCopy(
			final CopyMultiTargetB targetB)
	{
		return new CopyMultiTargetSource(
				CopyMultiTargetSource.targetB.map(targetB)
		);
	}

	static CopyMultiTargetSource omitCopy()
	{
		return new CopyMultiTargetSource(new SetValue<?>[]{});
	}

	void setTargetAB(
			final CopyMultiTargetA targetA,
			final CopyMultiTargetB targetB)
	{
		set(
				CopyMultiTargetSource.targetA.map(targetA),
				CopyMultiTargetSource.targetB.map(targetB));
	}

	void setTargetABandCopy(
			final CopyMultiTargetA targetA,
			final CopyMultiTargetB targetB,
			final String copy)
	{
		set(
				CopyMultiTargetSource.targetA.map(targetA),
				CopyMultiTargetSource.targetB.map(targetB),
				CopyMultiTargetSource.copy.map(copy));
	}

	void setTargetAandCopy(
			final CopyMultiTargetA targetA,
			final String copy)
	{
		set(
				CopyMultiTargetSource.targetA.map(targetA),
				CopyMultiTargetSource.copy.map(copy));
	}

	void setTargetBandCopy(
			final CopyMultiTargetB targetB,
			final String copy)
	{
		set(
				CopyMultiTargetSource.targetB.map(targetB),
				CopyMultiTargetSource.copy.map(copy));
	}


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
	CopyMultiTargetA getTargetA()
	{
		return CopyMultiTargetSource.targetA.get(this);
	}

	/**
	 * Sets a new value for {@link #targetA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setTargetA(@javax.annotation.Nullable final CopyMultiTargetA targetA)
	{
		CopyMultiTargetSource.targetA.set(this,targetA);
	}

	/**
	 * Returns the value of {@link #targetB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	CopyMultiTargetB getTargetB()
	{
		return CopyMultiTargetSource.targetB.get(this);
	}

	/**
	 * Sets a new value for {@link #targetB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setTargetB(@javax.annotation.Nullable final CopyMultiTargetB targetB)
	{
		CopyMultiTargetSource.targetB.set(this,targetB);
	}

	/**
	 * Returns the value of {@link #copy}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getCopy()
	{
		return CopyMultiTargetSource.copy.get(this);
	}

	/**
	 * Sets a new value for {@link #copy}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCopy(@javax.annotation.Nullable final java.lang.String copy)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CopyMultiTargetSource.copy.set(this,copy);
	}

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
