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

import static com.exedio.cope.instrument.Visibility.PRIVATE;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=PRIVATE)
final class CopyMultiCopySource extends Item
{
	@WrapperInitial static final StringField copyA = new StringField().optional();
	@WrapperInitial static final StringField copyB = new StringField().optional();

	@WrapperInitial
	static final ItemField<CopyMultiCopyTarget> target = ItemField.create(CopyMultiCopyTarget.class).optional().copyTo(copyA, () -> CopyMultiCopyTarget.copyA).copyTo(copyB, () -> CopyMultiCopyTarget.copyB);


	static CopyMultiCopySource createAB(
			final String copyA,
			final String copyB,
			final CopyMultiCopyTarget target)
	{
		return new CopyMultiCopySource(copyA, copyB, target);
	}

	static CopyMultiCopySource createAB(
			final String copyA,
			final String copyB)
	{
		return new CopyMultiCopySource(
				SetValue.map(CopyMultiCopySource.copyA, copyA),
				SetValue.map(CopyMultiCopySource.copyB, copyB)
		);
	}

	static CopyMultiCopySource createB(
			final String copyB,
			final CopyMultiCopyTarget target)
	{
		return new CopyMultiCopySource(
				SetValue.map(CopyMultiCopySource.copyB, copyB),
				SetValue.map(CopyMultiCopySource.target, target)
		);
	}

	static CopyMultiCopySource createA(
			final String copyA,
			final CopyMultiCopyTarget target)
	{
		return new CopyMultiCopySource(
				SetValue.map(CopyMultiCopySource.copyA, copyA),
				SetValue.map(CopyMultiCopySource.target, target)
		);
	}

	static CopyMultiCopySource create(
			final CopyMultiCopyTarget target)
	{
		return new CopyMultiCopySource(
				SetValue.map(CopyMultiCopySource.target, target)
		);
	}

	void setCopyA(
			final String copyA,
			final CopyMultiCopyTarget target)
	{
		set(
				SetValue.map(CopyMultiCopySource.copyA, copyA),
				SetValue.map(CopyMultiCopySource.target, target));
	}

	void setCopyB(
			final String copyB,
			final CopyMultiCopyTarget target)
	{
		set(
				SetValue.map(CopyMultiCopySource.copyB, copyB),
				SetValue.map(CopyMultiCopySource.target, target));
	}

	void setCopyAB(
			final String copyA,
			final String copyB)
	{
		set(
				SetValue.map(CopyMultiCopySource.copyA, copyA),
				SetValue.map(CopyMultiCopySource.copyB, copyB));
	}

	void setCopyAB(
			final String copyA,
			final String copyB,
			final CopyMultiCopyTarget target)
	{
		set(
				SetValue.map(CopyMultiCopySource.copyA, copyA),
				SetValue.map(CopyMultiCopySource.copyB, copyB),
				SetValue.map(CopyMultiCopySource.target, target));
	}


	/**
	 * Creates a new CopyMultiCopySource with all the fields initially needed.
	 * @param copyA the initial value for field {@link #copyA}.
	 * @param copyB the initial value for field {@link #copyB}.
	 * @param target the initial value for field {@link #target}.
	 * @throws com.exedio.cope.StringLengthViolationException if copyA, copyB violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private CopyMultiCopySource(
				@javax.annotation.Nullable final java.lang.String copyA,
				@javax.annotation.Nullable final java.lang.String copyB,
				@javax.annotation.Nullable final CopyMultiCopyTarget target)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CopyMultiCopySource.copyA,copyA),
			com.exedio.cope.SetValue.map(CopyMultiCopySource.copyB,copyB),
			com.exedio.cope.SetValue.map(CopyMultiCopySource.target,target),
		});
	}

	/**
	 * Creates a new CopyMultiCopySource and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopyMultiCopySource(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #copyA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCopyA()
	{
		return CopyMultiCopySource.copyA.get(this);
	}

	/**
	 * Sets a new value for {@link #copyA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCopyA(@javax.annotation.Nullable final java.lang.String copyA)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CopyMultiCopySource.copyA.set(this,copyA);
	}

	/**
	 * Returns the value of {@link #copyB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCopyB()
	{
		return CopyMultiCopySource.copyB.get(this);
	}

	/**
	 * Sets a new value for {@link #copyB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCopyB(@javax.annotation.Nullable final java.lang.String copyB)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		CopyMultiCopySource.copyB.set(this,copyB);
	}

	/**
	 * Returns the value of {@link #target}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopyMultiCopyTarget getTarget()
	{
		return CopyMultiCopySource.target.get(this);
	}

	/**
	 * Sets a new value for {@link #target}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTarget(@javax.annotation.Nullable final CopyMultiCopyTarget target)
	{
		CopyMultiCopySource.target.set(this,target);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copyMultiCopySource.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopyMultiCopySource> TYPE = com.exedio.cope.TypesBound.newType(CopyMultiCopySource.class,CopyMultiCopySource::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopyMultiCopySource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
