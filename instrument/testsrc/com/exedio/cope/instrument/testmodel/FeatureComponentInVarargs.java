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
import com.exedio.cope.instrument.testfeature.FeatureWithComponent;
import com.exedio.cope.instrument.testfeature.WrapVarargs;

public class FeatureComponentInVarargs extends Item
{
	static final FeatureWithComponent withComponent = new FeatureWithComponent();

	static final WrapVarargs featureUsingComponents = new WrapVarargs(withComponent.firstComponent(), withComponent.secondComponent());

	/**
	 * Creates a new FeatureComponentInVarargs with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public FeatureComponentInVarargs()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new FeatureComponentInVarargs and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected FeatureComponentInVarargs(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	static final void simpleFeatureUsingComponents(final java.lang.String withComponent_first,final int withComponent_second)
	{
		FeatureComponentInVarargs.featureUsingComponents.simple(withComponent_first,withComponent_second);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="moreParameters")
	static final void moreFeatureUsingComponentsParameters(final int featureUsingComponents,final java.lang.String withComponent_first,final int withComponent_second)
	{
		FeatureComponentInVarargs.featureUsingComponents.moreParameters(featureUsingComponents,withComponent_first,withComponent_second);
	}

	/**
	 * @param withComponent_first myDoc/'first' of {@link #withComponent}/withComponent_first/featureComponentInVarargs/
	 * @param withComponent_second myDoc/'second' of {@link #withComponent}/withComponent_second/featureComponentInVarargs/
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="staticToken")
	static final FeatureComponentInVarargs staticFeatureUsingComponentsToken(final java.lang.String withComponent_first,final int withComponent_second)
	{
		return FeatureComponentInVarargs.featureUsingComponents.staticToken(FeatureComponentInVarargs.class,withComponent_first,withComponent_second);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for featureComponentInVarargs.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FeatureComponentInVarargs> TYPE = com.exedio.cope.TypesBound.newType(FeatureComponentInVarargs.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected FeatureComponentInVarargs(final com.exedio.cope.ActivationParameters ap){super(ap);}
}