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

package com.exedio.cope.instrument.testmodel.enumcollision;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;

class SameInnerTypeCollision extends Item
{
	/**
	 uncommenting this makes resolving "CollisionBase.XCollide1" fail - see JavaClass.NS
	static class XCollide1
	{
	}*/

	static final EnumField<CollisionBase.XCollide1> innerX1 = EnumField.create(CollisionBase.XCollide1.class).defaultTo(CollisionBase.XCollide1.one);

	/**
	 * Creates a new SameInnerTypeCollision with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SameInnerTypeCollision()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new SameInnerTypeCollision and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SameInnerTypeCollision(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #innerX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final CollisionBase.XCollide1 getInnerX1()
	{
		return SameInnerTypeCollision.innerX1.get(this);
	}

	/**
	 * Sets a new value for {@link #innerX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setInnerX1(@javax.annotation.Nonnull final CollisionBase.XCollide1 innerX1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SameInnerTypeCollision.innerX1.set(this,innerX1);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for sameInnerTypeCollision.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SameInnerTypeCollision> TYPE = com.exedio.cope.TypesBound.newType(SameInnerTypeCollision.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SameInnerTypeCollision(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
