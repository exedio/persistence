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
 * The name of this item is deliberately chosen to collide with
 * {@link NameCollisionlongbItem},
 * after trimming of database names
 *
 * @author Ralf Wiebicke
 */
@CopeName("NameCollisionlooooooooooooooooooooooooooooooooooooooooongaItem")
final class NameCollisionlongaItem extends Item
{

	static final StringField code = new StringField().unique();

	/**
	 * The name of this attribute is deliberately chosen to collide with
	 * {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber},
	 * after trimming of database names
	 */
	@CopeSchemaName("collisionlongANumber")
	static final IntegerField collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber =
		new IntegerField().optional();

	@CopeSchemaName("collisionlongBNumber")
	static final IntegerField collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber =
		new IntegerField().optional();

	/**
	 * Creates a new NameCollisionlongaItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	NameCollisionlongaItem(
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			NameCollisionlongaItem.code.map(code),
		});
	}

	/**
	 * Creates a new NameCollisionlongaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private NameCollisionlongaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return NameCollisionlongaItem.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCode(@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		NameCollisionlongaItem.code.set(this,code);
	}

	/**
	 * Finds a nameCollisionlongaItem by it's {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static NameCollisionlongaItem forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return NameCollisionlongaItem.code.searchUnique(NameCollisionlongaItem.class,code);
	}

	/**
	 * Finds a nameCollisionlongaItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static NameCollisionlongaItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return NameCollisionlongaItem.code.searchUniqueStrict(NameCollisionlongaItem.class,code);
	}

	/**
	 * Returns the value of {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber()
	{
		return NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.get(this);
	}

	/**
	 * Sets a new value for {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber(@javax.annotation.Nullable final java.lang.Integer collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber)
	{
		NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.set(this,collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber);
	}

	/**
	 * Returns the value of {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber()
	{
		return NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber.get(this);
	}

	/**
	 * Sets a new value for {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber(@javax.annotation.Nullable final java.lang.Integer collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber)
	{
		NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber.set(this,collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nameCollisionlongaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NameCollisionlongaItem> TYPE = com.exedio.cope.TypesBound.newType(NameCollisionlongaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private NameCollisionlongaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
