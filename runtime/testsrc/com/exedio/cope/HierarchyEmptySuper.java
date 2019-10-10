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
 * Tests a sub type without any declared attributes but with inherited attributes.
 * @author Ralf Wiebicke
 */
public class HierarchyEmptySuper extends Item
{
	public static final IntegerField superInt = new IntegerField().unique();


	/**
	 * Creates a new HierarchyEmptySuper with all the fields initially needed.
	 * @param superInt the initial value for field {@link #superInt}.
	 * @throws com.exedio.cope.UniqueViolationException if superInt is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public HierarchyEmptySuper(
				final int superInt)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			HierarchyEmptySuper.superInt.map(superInt),
		});
	}

	/**
	 * Creates a new HierarchyEmptySuper and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HierarchyEmptySuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superInt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final int getSuperInt()
	{
		return HierarchyEmptySuper.superInt.getMandatory(this);
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
		HierarchyEmptySuper.superInt.set(this,superInt);
	}

	/**
	 * Finds a hierarchyEmptySuper by it's {@link #superInt}.
	 * @param superInt shall be equal to field {@link #superInt}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static final HierarchyEmptySuper forSuperInt(final int superInt)
	{
		return HierarchyEmptySuper.superInt.searchUnique(HierarchyEmptySuper.class,superInt);
	}

	/**
	 * Finds a hierarchyEmptySuper by its {@link #superInt}.
	 * @param superInt shall be equal to field {@link #superInt}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static final HierarchyEmptySuper forSuperIntStrict(final int superInt)
			throws
				java.lang.IllegalArgumentException
	{
		return HierarchyEmptySuper.superInt.searchUniqueStrict(HierarchyEmptySuper.class,superInt);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hierarchyEmptySuper.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HierarchyEmptySuper> TYPE = com.exedio.cope.TypesBound.newType(HierarchyEmptySuper.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HierarchyEmptySuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
