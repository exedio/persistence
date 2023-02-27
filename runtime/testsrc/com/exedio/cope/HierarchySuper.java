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

public abstract class HierarchySuper extends Item
{
	public static final IntegerField superInt = new IntegerField().unique();

	public static final StringField superString = new StringField().optional();

	public static final UppercaseView superStringUpper = superString.toUpperCase();

	/**
	 * Creates a new HierarchySuper with all the fields initially needed.
	 * @param superInt the initial value for field {@link #superInt}.
	 * @throws com.exedio.cope.UniqueViolationException if superInt is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	protected HierarchySuper(
				final int superInt)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(HierarchySuper.superInt,superInt),
		});
	}

	/**
	 * Creates a new HierarchySuper and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HierarchySuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superInt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final int getSuperInt()
	{
		return HierarchySuper.superInt.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #superInt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperInt(final int superInt)
			throws
				com.exedio.cope.UniqueViolationException
	{
		HierarchySuper.superInt.set(this,superInt);
	}

	/**
	 * Finds a hierarchySuper by its {@link #superInt}.
	 * @param superInt shall be equal to field {@link #superInt}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static final HierarchySuper forSuperInt(final int superInt)
	{
		return HierarchySuper.superInt.searchUnique(HierarchySuper.class,superInt);
	}

	/**
	 * Finds a hierarchySuper by its {@link #superInt}.
	 * @param superInt shall be equal to field {@link #superInt}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static final HierarchySuper forSuperIntStrict(final int superInt)
			throws
				java.lang.IllegalArgumentException
	{
		return HierarchySuper.superInt.searchUniqueStrict(HierarchySuper.class,superInt);
	}

	/**
	 * Returns the value of {@link #superString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final java.lang.String getSuperString()
	{
		return HierarchySuper.superString.get(this);
	}

	/**
	 * Sets a new value for {@link #superString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperString(@javax.annotation.Nullable final java.lang.String superString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HierarchySuper.superString.set(this,superString);
	}

	/**
	 * Returns the value of {@link #superStringUpper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final java.lang.String getSuperStringUpper()
	{
		return HierarchySuper.superStringUpper.getSupported(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for hierarchySuper.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HierarchySuper> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(HierarchySuper.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HierarchySuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
