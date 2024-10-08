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

package com.exedio.cope.instrument.testmodel.initialVisibility;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.SettableFixed;

public final class FieldPrivatePublic extends Item
{
	private static final SettableFixed field = new SettableFixed();
	public static final SettableFixed fieldPublic = new SettableFixed();


	/**
	 * Creates a new FieldPrivatePublic with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @param fieldPublic the initial value for field {@link #fieldPublic}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private FieldPrivatePublic(
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicBoolean field,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicBoolean fieldPublic)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(FieldPrivatePublic.field,field),
			com.exedio.cope.SetValue.map(FieldPrivatePublic.fieldPublic,fieldPublic),
		});
	}

	/**
	 * Creates a new FieldPrivatePublic and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private FieldPrivatePublic(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for fieldPrivatePublic.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FieldPrivatePublic> TYPE = com.exedio.cope.TypesBound.newType(FieldPrivatePublic.class,FieldPrivatePublic::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private FieldPrivatePublic(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
