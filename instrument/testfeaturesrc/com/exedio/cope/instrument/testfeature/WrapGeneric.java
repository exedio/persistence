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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import java.io.Writer;
import java.util.Collection;
import java.util.function.Supplier;

public class WrapGeneric<Z, M, N> extends WrapGenericSuper<M, N, Z, Double>
{
	@Wrap(order=10)
	public void method(
			@SuppressWarnings("unused") @Parameter("m") final M m,
			@SuppressWarnings("unused") @Parameter("n") final N n,
			@SuppressWarnings("unused") @Parameter("z") final Z z,
			@SuppressWarnings("unused") @Parameter("x") final Writer x)
	{
		// empty
	}

	@Wrap(order=20)
	public Supplier/* TODO: <?> */[] varargsMethod(
			@SuppressWarnings("unused") @Parameter("varargs") final Collection<?>... varargs)
	{
		throw new RuntimeException();
	}

	@Wrap(order=30)
	public void arrayMethod(
			@SuppressWarnings("unused") @Parameter("varargs") final Collection<?>[] varargs)
	{
		throw new RuntimeException();
	}

	@Wrap(order=40)
	public void arrayAndVarargsMethod(
			@SuppressWarnings("unused") @Parameter("array") final Collection/* TODO <?> */[] array,
			@SuppressWarnings("unused") @Parameter("varargs") final Collection<?>... varargs)
	{
		throw new RuntimeException();
	}

}
