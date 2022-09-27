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
@SuppressWarnings("JavadocReference") // OK: don't care in tests
public class CaseProblem extends Item
{

	/**
	 * Creates a new CaseProblem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public CaseProblem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new CaseProblem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected CaseProblem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for caseProblem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CaseProblem> TYPE = com.exedio.cope.TypesBound.newType(CaseProblem.class,CaseProblem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CaseProblem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
