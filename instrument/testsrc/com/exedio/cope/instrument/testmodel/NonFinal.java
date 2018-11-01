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
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testfeature.OptionFeature;

/**
 * This class must get a protected
 * {@link NonFinal#NonFinal(com.exedio.cope.SetValue[]) generic constructor} and
 * {@link NonFinal#NonFinal(com.exedio.cope.ActivationParameters) activation constructor},
 * and final methods (except when set to {@link Wrapper#asFinal() Wrapper.asFinal}=false,
 * since it is not final.
 */
public class NonFinal extends Item
{
	private   static final OptionFeature barePrivate   = new OptionFeature();
	          static final OptionFeature barePackage   = new OptionFeature();
	protected static final OptionFeature bareProtected = new OptionFeature();
	public    static final OptionFeature barePublic    = new OptionFeature();

	@Wrapper(wrap="*", asFinal=false)         static final OptionFeature nonFinal        = new OptionFeature();
	@Wrapper(wrap="*", asFinal=false) private static final OptionFeature nonFinalPrivate = new OptionFeature();

	/**
	 * Creates a new NonFinal with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public NonFinal()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new NonFinal and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected NonFinal(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleBarePrivate()
	{
		NonFinal.barePrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	final void simpleBarePackage()
	{
		NonFinal.barePackage.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	protected final void simpleBareProtected()
	{
		NonFinal.bareProtected.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	public final void simpleBarePublic()
	{
		NonFinal.barePublic.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	void simpleNonFinal()
	{
		NonFinal.nonFinal.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	private void simpleNonFinalPrivate()
	{
		NonFinal.nonFinalPrivate.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nonFinal.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<NonFinal> TYPE = com.exedio.cope.TypesBound.newType(NonFinal.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected NonFinal(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
