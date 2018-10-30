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

package com.exedio.cope.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;

/**
 * Test for database name collisions
 * by using the same attributes names
 * in different persistent classes.
 * @author Ralf Wiebicke
 */
public final class CollisionItem1 extends Item
{

	public static final ItemField<EmptyItem> collisionAttribute = ItemField.create(EmptyItem.class).toFinal().unique();

	/**
	 * Creates a new CollisionItem1 with all the fields initially needed.
	 * @param collisionAttribute the initial value for field {@link #collisionAttribute}.
	 * @throws com.exedio.cope.MandatoryViolationException if collisionAttribute is null.
	 * @throws com.exedio.cope.UniqueViolationException if collisionAttribute is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CollisionItem1(
				@javax.annotation.Nonnull final EmptyItem collisionAttribute)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CollisionItem1.collisionAttribute.map(collisionAttribute),
		});
	}

	/**
	 * Creates a new CollisionItem1 and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CollisionItem1(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #collisionAttribute}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public EmptyItem getCollisionAttribute()
	{
		return CollisionItem1.collisionAttribute.get(this);
	}

	/**
	 * Finds a collisionItem1 by it's {@link #collisionAttribute}.
	 * @param collisionAttribute shall be equal to field {@link #collisionAttribute}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	public static CollisionItem1 forCollisionAttribute(@javax.annotation.Nonnull final EmptyItem collisionAttribute)
	{
		return CollisionItem1.collisionAttribute.searchUnique(CollisionItem1.class,collisionAttribute);
	}

	/**
	 * Finds a collisionItem1 by its {@link #collisionAttribute}.
	 * @param collisionAttribute shall be equal to field {@link #collisionAttribute}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	public static CollisionItem1 forCollisionAttributeStrict(@javax.annotation.Nonnull final EmptyItem collisionAttribute)
			throws
				java.lang.IllegalArgumentException
	{
		return CollisionItem1.collisionAttribute.searchUniqueStrict(CollisionItem1.class,collisionAttribute);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for collisionItem1.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CollisionItem1> TYPE = com.exedio.cope.TypesBound.newType(CollisionItem1.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CollisionItem1(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
