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

package com.exedio.cope.sampler;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Media;

@WrapperType(constructor=NONE, genericConstructor=NONE, comments=false)
public class SampledModelItem extends Item
{
	@WrapperIgnore
	static final StringField code = new StringField();
	@WrapperIgnore
	static final Media mediaA = new Media();
	@WrapperIgnore
	static final Media mediaB = new Media();

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	@com.exedio.cope.instrument.Generated
	public static final com.exedio.cope.Type<SampledModelItem> TYPE = com.exedio.cope.TypesBound.newType(SampledModelItem.class);

	@com.exedio.cope.instrument.Generated
	protected SampledModelItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}