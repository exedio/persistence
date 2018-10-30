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

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testfeature.WrapperParametersFeature;

class WrapperParametersItem extends Item
{
	@Wrapper(wrap="param", parameters={}, internal=true)
	@Wrapper(wrap="param", parameters=WrapperParametersFeature.class, visibility=PUBLIC)
	@Wrapper(wrap="param", parameters=Integer.class, visibility=DEFAULT)
	@Wrapper(wrap="param", parameters=int.class, visibility=PROTECTED)
	@Wrapper(wrap="param", parameters=WrapperParametersFeature.SomeEnum.class, visibility=PRIVATE)
	@Wrapper(wrap="param", parameters=byte[].class, internal=true, visibility=PUBLIC)
	@Wrapper(wrap="param", parameters=Item[][].class, internal=true, visibility=PROTECTED)
	static final WrapperParametersFeature feature = new WrapperParametersFeature();

	/**
	 * Creates a new WrapperParametersItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	WrapperParametersItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new WrapperParametersItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected WrapperParametersItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	private void paramFeatureInternal()
	{
		WrapperParametersItem.feature.param(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	public final void paramFeature(final WrapperParametersFeature feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	final void paramFeature(final java.lang.Integer feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	protected final void paramFeature(final int feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	private void paramFeature(final com.exedio.cope.instrument.testfeature.WrapperParametersFeature.SomeEnum feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	public final void paramFeatureInternal(final byte[] feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="param")
	protected final void paramFeatureInternal(final com.exedio.cope.Item[][] feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapperParametersItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapperParametersItem> TYPE = com.exedio.cope.TypesBound.newType(WrapperParametersItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected WrapperParametersItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
