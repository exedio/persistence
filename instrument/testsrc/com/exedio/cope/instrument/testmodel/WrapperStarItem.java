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
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.WrapFeature;

@WrapperType(constructor=NONE, genericConstructor=NONE)
class WrapperStarItem extends Item
{
	@Wrapper(wrap="simple", visibility=PUBLIC)
	@Wrapper(wrap="*", visibility=NONE)
	@Wrapper(wrap="simpleVoid", visibility=DEFAULT)
	static final WrapFeature field=new WrapFeature();


	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simple")
	public final int simpleField()
	{
		return WrapperStarItem.field.simple(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="simpleVoid")
	final void simpleFieldVoid()
	{
		WrapperStarItem.field.simpleVoid(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapperStarItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapperStarItem> TYPE = com.exedio.cope.TypesBound.newType(WrapperStarItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected WrapperStarItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}