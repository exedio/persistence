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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.WrapLiteralFeature;

@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE)
@SuppressWarnings("unused") // OK: just test instrumentor running through
final class WrapLiteralItem extends Item
{
	static final WrapLiteralFeature normalString = new WrapLiteralFeature("normal");
	static final WrapLiteralFeature normalChar   = new WrapLiteralFeature('n');

	static final WrapLiteralFeature doubleQuoteString = new WrapLiteralFeature("doubleQuote\"String");
	static final WrapLiteralFeature doubleQuoteChar   = new WrapLiteralFeature('"');

	static final WrapLiteralFeature singleQuoteString = new WrapLiteralFeature("singleQuote'String");
	static final WrapLiteralFeature singleQuoteChar   = new WrapLiteralFeature('\'');

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;
}
