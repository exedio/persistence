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

package com.exedio.cope.pattern;

import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;

final class CompositeDefaultValue extends Composite
{
	static final StringField normal = new StringField();
	static final IntegerField deflt = new IntegerField().defaultTo(5);

	/**
	 * Creates a new CompositeDefaultValue with all the fields initially needed.
	 * @param normal the initial value for field {@link #normal}.
	 * @throws com.exedio.cope.MandatoryViolationException if normal is null.
	 * @throws com.exedio.cope.StringLengthViolationException if normal violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CompositeDefaultValue(
				@javax.annotation.Nonnull final java.lang.String normal)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompositeDefaultValue.normal.map(normal),
		});
	}

	/**
	 * Creates a new CompositeDefaultValue and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CompositeDefaultValue(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #normal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getNormal()
	{
		return get(CompositeDefaultValue.normal);
	}

	/**
	 * Sets a new value for {@link #normal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNormal(@javax.annotation.Nonnull final java.lang.String normal)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(CompositeDefaultValue.normal,normal);
	}

	/**
	 * Returns the value of {@link #deflt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getDeflt()
	{
		return getMandatory(CompositeDefaultValue.deflt);
	}

	/**
	 * Sets a new value for {@link #deflt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDeflt(final int deflt)
	{
		set(CompositeDefaultValue.deflt,deflt);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;
}
