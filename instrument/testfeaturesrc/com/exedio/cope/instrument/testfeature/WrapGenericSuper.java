/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Reader;

@WrapFeature
public class WrapGenericSuper<A, B, Z, F>
{
	@Wrap(order=10)
	public void method(
			@SuppressWarnings("unused") @Parameter("a") final A a,
			@SuppressWarnings("unused") @Parameter("b") final B b,
			@SuppressWarnings("unused") @Parameter("z") final Z z,
			@SuppressWarnings("unused") @Parameter("f") final F f,
			@SuppressWarnings("unused") @Parameter("x") final Reader x)
	{
		// empty
	}
}
