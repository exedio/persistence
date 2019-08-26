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

import com.exedio.cope.instrument.testlib.ExternalItemWithAbstractMethod;

class SubOfExternalItemWithAbstractMethod extends ExternalItemWithAbstractMethod
{
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

	/**
	 * Creates a new SubOfExternalItemWithAbstractMethod with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	SubOfExternalItemWithAbstractMethod()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new SubOfExternalItemWithAbstractMethod and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected SubOfExternalItemWithAbstractMethod(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for subOfExternalItemWithAbstractMethod.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SubOfExternalItemWithAbstractMethod> TYPE = com.exedio.cope.TypesBound.newType(SubOfExternalItemWithAbstractMethod.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected SubOfExternalItemWithAbstractMethod(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
