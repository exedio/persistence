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

import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;

/**
 * @cope.type private
 * @cope.generic.constructor none
 */
public final class TypePrivate extends Item
{
	public static final StringField defaultString = new StringField().optional();

	/**
	 * Creates a new TypeNone and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 */
	private TypePrivate(final SetValue<?>... initialAttributes)
	{
		super(initialAttributes);
		// here one could do additional things
	}

	static void useFeaturesToAvoidWarning()
	{
		System.out.println(TYPE);
	}


	/**
	 * Creates a new TypePrivate with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public TypePrivate()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Returns the value of {@link #defaultString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final java.lang.String getDefaultString()
	{
		return TypePrivate.defaultString.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setDefaultString(@javax.annotation.Nullable final java.lang.String defaultString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		TypePrivate.defaultString.set(this,defaultString);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for typePrivate.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<TypePrivate> TYPE = com.exedio.cope.TypesBound.newType(TypePrivate.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private TypePrivate(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
