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

public final class HierarchySingleSub extends HierarchySingleSuper
{

	public static final StringField subString = new StringField().optional();
	public static final ItemField<HierarchySuper> hierarchySuper = ItemField.create(HierarchySuper.class).optional();


	public HierarchySingleSub(final int initialSuperInt, final String subString)
	{
		super(new com.exedio.cope.SetValue<?>[]{
			HierarchySingleSuper.superInt.map(initialSuperInt),
			HierarchySingleSub.subString.map(subString),
		});
	}

	/**
	 * Creates a new HierarchySingleSub with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public HierarchySingleSub()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new HierarchySingleSub and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HierarchySingleSub(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #subString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final java.lang.String getSubString()
	{
		return HierarchySingleSub.subString.get(this);
	}

	/**
	 * Sets a new value for {@link #subString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setSubString(@javax.annotation.Nullable final java.lang.String subString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HierarchySingleSub.subString.set(this,subString);
	}

	/**
	 * Returns the value of {@link #hierarchySuper}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final HierarchySuper getHierarchySuper()
	{
		return HierarchySingleSub.hierarchySuper.get(this);
	}

	/**
	 * Sets a new value for {@link #hierarchySuper}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setHierarchySuper(@javax.annotation.Nullable final HierarchySuper hierarchySuper)
	{
		HierarchySingleSub.hierarchySuper.set(this,hierarchySuper);
	}

	/**
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hierarchySingleSub.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HierarchySingleSub> TYPE = com.exedio.cope.TypesBound.newType(HierarchySingleSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HierarchySingleSub(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
