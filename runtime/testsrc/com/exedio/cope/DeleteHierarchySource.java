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

public final class DeleteHierarchySource extends Item
{
	static final ItemField<DeleteHierarchyTargetSuper> target = ItemField.create(DeleteHierarchyTargetSuper.class).cascade();

	/**
	 * Creates a new DeleteHierarchySource with all the fields initially needed.
	 * @param target the initial value for field {@link #target}.
	 * @throws com.exedio.cope.MandatoryViolationException if target is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DeleteHierarchySource(
				@javax.annotation.Nonnull final DeleteHierarchyTargetSuper target)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DeleteHierarchySource.target,target),
		});
	}

	/**
	 * Creates a new DeleteHierarchySource and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DeleteHierarchySource(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #target}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	DeleteHierarchyTargetSuper getTarget()
	{
		return DeleteHierarchySource.target.get(this);
	}

	/**
	 * Sets a new value for {@link #target}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTarget(@javax.annotation.Nonnull final DeleteHierarchyTargetSuper target)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DeleteHierarchySource.target.set(this,target);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteHierarchySource.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DeleteHierarchySource> TYPE = com.exedio.cope.TypesBound.newType(DeleteHierarchySource.class,DeleteHierarchySource::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DeleteHierarchySource(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
