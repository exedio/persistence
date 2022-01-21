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

final class CopySelfSource extends Item
{
	@WrapperInitial
	static final ItemField<CopySelfSource> selfTarget = ItemField.create(CopySelfSource.class).optional();
	static final ItemField<CopyValue> selfTemplate = ItemField.create(CopyValue.class).toFinal().optional().copyFrom(selfTarget);

	@Override
	public String toString()
	{
		// for testing, that CopyViolation#getMessage does not call toString(), but getCopeID()
		return "toString(" + getCopeID() + ')';
	}

	static CopySelfSource omitCopy(final CopySelfSource selfTarget)
	{
		return new CopySelfSource(
			CopySelfSource.selfTarget.map(selfTarget)
		);
	}

	static CopySelfSource omitTarget(final CopyValue selfTemplate)
	{
		return new CopySelfSource(
			CopySelfSource.selfTemplate.map(selfTemplate)
		);
	}

	static CopySelfSource omitAll()
	{
		return new CopySelfSource(SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new CopySelfSource with all the fields initially needed.
	 * @param selfTarget the initial value for field {@link #selfTarget}.
	 * @param selfTemplate the initial value for field {@link #selfTemplate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CopySelfSource(
				@javax.annotation.Nullable final CopySelfSource selfTarget,
				@javax.annotation.Nullable final CopyValue selfTemplate)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopySelfSource.selfTarget.map(selfTarget),
			CopySelfSource.selfTemplate.map(selfTemplate),
		});
	}

	/**
	 * Creates a new CopySelfSource and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopySelfSource(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #selfTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopySelfSource getSelfTarget()
	{
		return CopySelfSource.selfTarget.get(this);
	}

	/**
	 * Sets a new value for {@link #selfTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSelfTarget(@javax.annotation.Nullable final CopySelfSource selfTarget)
	{
		CopySelfSource.selfTarget.set(this,selfTarget);
	}

	/**
	 * Returns the value of {@link #selfTemplate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopyValue getSelfTemplate()
	{
		return CopySelfSource.selfTemplate.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copySelfSource.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopySelfSource> TYPE = com.exedio.cope.TypesBound.newType(CopySelfSource.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopySelfSource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
