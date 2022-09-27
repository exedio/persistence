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
public final class HierarchyEmptySub extends HierarchyEmptySuper
{

	/**
	 * Creates a new HierarchyEmptySub with all the fields initially needed.
	 * @param superInt the initial value for field {@link #superInt}.
	 * @throws com.exedio.cope.UniqueViolationException if superInt is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public HierarchyEmptySub(
				final int superInt)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.HierarchyEmptySuper.superInt.map(superInt),
		});
	}

	/**
	 * Creates a new HierarchyEmptySub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HierarchyEmptySub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hierarchyEmptySub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HierarchyEmptySub> TYPE = com.exedio.cope.TypesBound.newType(HierarchyEmptySub.class,HierarchyEmptySub::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HierarchyEmptySub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
