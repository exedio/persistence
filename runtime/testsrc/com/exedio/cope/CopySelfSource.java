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

final class CopySelfSource extends Item
{
	static final ItemField<CopySelfSource> selfTarget = ItemField.create(CopySelfSource.class).toFinal().optional();
	static final ItemField<CopyValue> selfTemplate = ItemField.create(CopyValue.class).toFinal().optional().copyFrom(selfTarget);

	@Override
	public String toString()
	{
		// for testing, that CopyViolation#getMessage does not call toString(), but getCopeID()
		return "toString(" + getCopeID() + ')';
	}

	/**
	 * Creates a new CopySelfSource with all the fields initially needed.
	 * @param selfTarget the initial value for field {@link #selfTarget}.
	 * @param selfTemplate the initial value for field {@link #selfTemplate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CopySelfSource(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #selfTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final CopySelfSource getSelfTarget()
	{
		return CopySelfSource.selfTarget.get(this);
	}

	/**
	 * Returns the value of {@link #selfTemplate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final CopyValue getSelfTemplate()
	{
		return CopySelfSource.selfTemplate.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copySelfSource.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopySelfSource> TYPE = com.exedio.cope.TypesBound.newType(CopySelfSource.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CopySelfSource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
