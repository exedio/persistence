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

package com.exedio.cope.instrument.kind;

import com.exedio.cope.Item;

/**
 * This is a test case for a potential class loading problem:
 * <ul>
 *		<li>class pkg.SomeClass exists externally
 *		<li>instrumented source has class pkg.someclass.OtherClass (and a subclass)
 * </ul>
 *	(Here, we use the class {@link com.exedio.cope.instrument.Kind} for the collision - that's why the package name is "kind".)
 *
 * Depending on how names are resolved during instrumentation, we might now try to check if a class pkg.someclass exists.
 * On Linux, this gets a ClassNotFoundException (fine) - but on Windows a NoClassDefFoundError.
 *
 * This test case asserts that we don't run into that problem.
 */
public class CaseProblem extends Item
{

	/**
	 * Creates a new CaseProblem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CaseProblem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new CaseProblem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected CaseProblem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for caseProblem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CaseProblem> TYPE = com.exedio.cope.TypesBound.newType(CaseProblem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected CaseProblem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}