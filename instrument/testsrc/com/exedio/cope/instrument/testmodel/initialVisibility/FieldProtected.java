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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("ProtectedMemberInFinalClass")
@SuppressFBWarnings("CI_CONFUSED_INHERITANCE")
public final class FieldProtected extends Item
{
	protected static final SettableFixed field = new SettableFixed();


	/**
	 * Creates a new FieldProtected with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	protected FieldProtected(
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicBoolean field)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			FieldProtected.field.map(field),
		});
	}

	/**
	 * Creates a new FieldProtected and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private FieldProtected(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for fieldProtected.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FieldProtected> TYPE = com.exedio.cope.TypesBound.newType(FieldProtected.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private FieldProtected(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
