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

@SuppressWarnings({"UnnecessarilyQualifiedInnerClassAccess", "unused"}) // OK: test bad API usage
final class CollisionBase extends Item
{
	enum ACollide1 { one, two } // collides with item, but is early in alphabet
	enum XCollide1 { one, two } // does not collide with any item
	enum XCollide2 { one, two }

	static final EnumField<ACollide1> innerA1 = EnumField.create(ACollide1.class).defaultTo(ACollide1.one);
	static final EnumField<XCollide1> innerX1 = EnumField.create(XCollide1.class).defaultTo(XCollide1.one);
	static final EnumField<XCollide2> innerX2 = EnumField.create(XCollide2.class).defaultTo(XCollide2.one);

	static final EnumField<CollisionBase.ACollide1> topA1 = EnumField.create(CollisionBase.ACollide1.class).defaultTo(CollisionBase.ACollide1.one);
	static final EnumField<CollisionBase.XCollide1> topX1 = EnumField.create(CollisionBase.XCollide1.class).defaultTo(CollisionBase.XCollide1.one);
	static final EnumField<CollisionBase.XCollide2> topX2 = EnumField.create(CollisionBase.XCollide2.class).defaultTo(CollisionBase.XCollide2.one);

	static final EnumField<com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.ACollide1> fullA1 = EnumField.create(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.ACollide1.class).defaultTo(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.ACollide1.one);
	static final EnumField<com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide1> fullX1 = EnumField.create(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide1.class).defaultTo(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide1.one);
	static final EnumField<com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide2> fullX2 = EnumField.create(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide2.class).defaultTo(com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide2.one);


	/**
	 * Creates a new CollisionBase with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CollisionBase()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new CollisionBase and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CollisionBase(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #innerA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	ACollide1 getInnerA1()
	{
		return CollisionBase.innerA1.get(this);
	}

	/**
	 * Sets a new value for {@link #innerA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInnerA1(@javax.annotation.Nonnull final ACollide1 innerA1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.innerA1.set(this,innerA1);
	}

	/**
	 * Returns the value of {@link #innerX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	XCollide1 getInnerX1()
	{
		return CollisionBase.innerX1.get(this);
	}

	/**
	 * Sets a new value for {@link #innerX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInnerX1(@javax.annotation.Nonnull final XCollide1 innerX1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.innerX1.set(this,innerX1);
	}

	/**
	 * Returns the value of {@link #innerX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	XCollide2 getInnerX2()
	{
		return CollisionBase.innerX2.get(this);
	}

	/**
	 * Sets a new value for {@link #innerX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInnerX2(@javax.annotation.Nonnull final XCollide2 innerX2)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.innerX2.set(this,innerX2);
	}

	/**
	 * Returns the value of {@link #topA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CollisionBase.ACollide1 getTopA1()
	{
		return CollisionBase.topA1.get(this);
	}

	/**
	 * Sets a new value for {@link #topA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTopA1(@javax.annotation.Nonnull final CollisionBase.ACollide1 topA1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.topA1.set(this,topA1);
	}

	/**
	 * Returns the value of {@link #topX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CollisionBase.XCollide1 getTopX1()
	{
		return CollisionBase.topX1.get(this);
	}

	/**
	 * Sets a new value for {@link #topX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTopX1(@javax.annotation.Nonnull final CollisionBase.XCollide1 topX1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.topX1.set(this,topX1);
	}

	/**
	 * Returns the value of {@link #topX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CollisionBase.XCollide2 getTopX2()
	{
		return CollisionBase.topX2.get(this);
	}

	/**
	 * Sets a new value for {@link #topX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTopX2(@javax.annotation.Nonnull final CollisionBase.XCollide2 topX2)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.topX2.set(this,topX2);
	}

	/**
	 * Returns the value of {@link #fullA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.ACollide1 getFullA1()
	{
		return CollisionBase.fullA1.get(this);
	}

	/**
	 * Sets a new value for {@link #fullA1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFullA1(@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.ACollide1 fullA1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.fullA1.set(this,fullA1);
	}

	/**
	 * Returns the value of {@link #fullX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide1 getFullX1()
	{
		return CollisionBase.fullX1.get(this);
	}

	/**
	 * Sets a new value for {@link #fullX1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFullX1(@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide1 fullX1)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.fullX1.set(this,fullX1);
	}

	/**
	 * Returns the value of {@link #fullX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide2 getFullX2()
	{
		return CollisionBase.fullX2.get(this);
	}

	/**
	 * Sets a new value for {@link #fullX2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFullX2(@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.enumcollision.CollisionBase.XCollide2 fullX2)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CollisionBase.fullX2.set(this,fullX2);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for collisionBase.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CollisionBase> TYPE = com.exedio.cope.TypesBound.newType(CollisionBase.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CollisionBase(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
