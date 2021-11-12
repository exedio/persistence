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

package com.exedio.cope.instrument.testlib;

public abstract class ExternalItemWithAbstractMethod extends ExternalParentWithAbstractMethod
{
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	protected abstract void abstractMethod();

	// test that an abstract method present in two parents doesn't get added twice in interim code:
	@SuppressWarnings({"AbstractMethodOverridesAbstractMethod", "unused"}) // OK: just for testing instrumentor
	@Override
	public abstract void doubleAbstract();

	@Override
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public final void overwrittenExternally()
	{
	}

	/**
	 * Creates a new ExternalItemWithAbstractMethod with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	protected ExternalItemWithAbstractMethod()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ExternalItemWithAbstractMethod and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected ExternalItemWithAbstractMethod(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2L;

	/**
	 * The persistent type information for externalItemWithAbstractMethod.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<ExternalItemWithAbstractMethod> TYPE = com.exedio.cope.TypesBound.newType(ExternalItemWithAbstractMethod.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected ExternalItemWithAbstractMethod(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
