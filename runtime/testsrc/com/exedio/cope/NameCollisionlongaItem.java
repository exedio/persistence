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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private NameCollisionlongaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return NameCollisionlongaItem.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber()
	{
		return NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.get(this);
	}

	/**
	 * Sets a new value for {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber(@javax.annotation.Nullable final java.lang.Integer collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber)
	{
		NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber.set(this,collisionloooooooooooooooooooooooooooooooooooooooooooooooongaNumber);
	}

	/**
	 * Returns the value of {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getCollisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber()
	{
		return NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber.get(this);
	}

	/**
	 * Sets a new value for {@link #collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCollisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber(@javax.annotation.Nullable final java.lang.Integer collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber)
	{
		NameCollisionlongaItem.collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber.set(this,collisionloooooooooooooooooooooooooooooooooooooooooooooooongbNumber);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nameCollisionlongaItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NameCollisionlongaItem> TYPE = com.exedio.cope.TypesBound.newType(NameCollisionlongaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private NameCollisionlongaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
