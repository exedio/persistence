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

/**
 * An item having two fields and a unique constraint over these fields.
 * @author Ralf Wiebicke
 */
final class UniqueDoubleItem extends Item
{
	static final StringField string = new StringField();

	static final IntegerField integer = new IntegerField();

	static final UniqueConstraint constraint = new UniqueConstraint(string, integer);

	/**
	 * Creates a new UniqueDoubleItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.MandatoryViolationException if string is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if string, integer is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	UniqueDoubleItem(
				@javax.annotation.Nonnull final java.lang.String string,
				final int integer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			UniqueDoubleItem.string.map(string),
			UniqueDoubleItem.integer.map(integer),
		});
	}

	/**
	 * Creates a new UniqueDoubleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private UniqueDoubleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getString()
	{
		return UniqueDoubleItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueDoubleItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getInteger()
	{
		return UniqueDoubleItem.integer.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger(final int integer)
			throws
				com.exedio.cope.UniqueViolationException
	{
		UniqueDoubleItem.integer.set(this,integer);
	}

	/**
	 * Finds a uniqueDoubleItem by it's unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param integer shall be equal to field {@link #integer}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static UniqueDoubleItem forConstraint(@javax.annotation.Nonnull final java.lang.String string,final int integer)
	{
		return UniqueDoubleItem.constraint.search(UniqueDoubleItem.class,string,integer);
	}

	/**
	 * Finds a uniqueDoubleItem by its unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param integer shall be equal to field {@link #integer}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static UniqueDoubleItem forConstraintStrict(@javax.annotation.Nonnull final java.lang.String string,final int integer)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueDoubleItem.constraint.searchStrict(UniqueDoubleItem.class,string,integer);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueDoubleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<UniqueDoubleItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueDoubleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private UniqueDoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
