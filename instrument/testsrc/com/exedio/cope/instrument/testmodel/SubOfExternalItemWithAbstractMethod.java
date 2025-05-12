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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import com.exedio.cope.instrument.testlib.ExternalInterface;
import com.exedio.cope.instrument.testlib.ExternalItemWithAbstractMethod;

class SubOfExternalItemWithAbstractMethod extends ExternalItemWithAbstractMethod
{
	@WrapInterim
	@UsageEntryPoint
	private static final ImplementationOfExternalInterface object = new ImplementationOfExternalInterface();

	@WrapInterim // makes sure, there is no instrumentor error:
	// java.lang.RuntimeException: don't call in interim code
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	private static final int fortyTwo = object.methodInInterface();

	private static final SimpleSettable justToMakeSureClassGetsLoaded = new SimpleSettable();

	@Override
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	protected void abstractMethod()
	{
		// method body must not be copied to interim code
		new DontInstrument();
	}


	@Override
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public void doubleAbstract()
	{
		// method body must not be copied to interim code
		new DontInstrument();
	}

	@WrapInterim
	static class ImplementationOfExternalInterface implements ExternalInterface
	{
		@WrapInterim // makes sure, there is no instrumentor error:
		// error: method methodInInterface() is already defined in class ...
		@Override
		public int methodInInterface()
		{
			return 42;
		}
	}

	/**
	 * Creates a new SubOfExternalItemWithAbstractMethod with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SubOfExternalItemWithAbstractMethod()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new SubOfExternalItemWithAbstractMethod and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SubOfExternalItemWithAbstractMethod(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private java.lang.String oneJustToMakeSureClassGetsLoaded()
	{
		return SubOfExternalItemWithAbstractMethod.justToMakeSureClassGetsLoaded.one(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for subOfExternalItemWithAbstractMethod.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SubOfExternalItemWithAbstractMethod> TYPE = com.exedio.cope.TypesBound.newType(SubOfExternalItemWithAbstractMethod.class,SubOfExternalItemWithAbstractMethod::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SubOfExternalItemWithAbstractMethod(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
