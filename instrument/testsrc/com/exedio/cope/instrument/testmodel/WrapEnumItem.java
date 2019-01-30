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
import com.exedio.cope.instrument.testfeature.FeatureEnum;
import com.exedio.cope.instrument.testfeature.WrapEnumFeature;

final class WrapEnumItem extends Item
{
	enum Normal
	{
		one, two
	}

	static final WrapEnumFeature<Normal> normal = WrapEnumFeature.create(Normal.class);


	enum WithImplements implements CharSequence
	{
		one, two;

		@Override
		public int length()
		{
			throw new RuntimeException();
		}

		@Override
		public char charAt(final int index)
		{
			throw new RuntimeException();
		}

		@Override
		public CharSequence subSequence(final int start, final int end)
		{
			throw new RuntimeException();
		}
	}

	static final WrapEnumFeature<WithImplements> withImplements = WrapEnumFeature.create(WithImplements.class);


	static final WrapEnumFeature<FeatureEnum> fromFeature =
			WrapEnumFeature.create(FeatureEnum.class,
			"com.exedio.cope.instrument.testfeature.FeatureEnum");


	/**
	 * Creates a new WrapEnumItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	WrapEnumItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new WrapEnumItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private WrapEnumItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static Normal methodNormal(final Normal normal)
	{
		return WrapEnumItem.normal.method(normal);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static WithImplements methodWithImplements(final WithImplements withImplements)
	{
		return WrapEnumItem.withImplements.method(withImplements);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static FeatureEnum methodFromFeature(final FeatureEnum fromFeature)
	{
		return WrapEnumItem.fromFeature.method(fromFeature);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapEnumItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapEnumItem> TYPE = com.exedio.cope.TypesBound.newType(WrapEnumItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private WrapEnumItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
