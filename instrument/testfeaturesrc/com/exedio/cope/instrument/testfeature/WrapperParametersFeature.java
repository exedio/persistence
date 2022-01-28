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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;

@com.exedio.cope.instrument.WrapFeature
public class WrapperParametersFeature
{
	@Wrap(order=350)
	public void param(
			@SuppressWarnings("unused") final Item item)
	{
		throw new RuntimeException();
	}

	@Wrap(order=360)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final WrapperParametersFeature wrapFeature)
	{
		throw new RuntimeException();
	}

	@Wrap(order=370)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final Integer integer)
	{
		throw new RuntimeException();
	}

	@Wrap(order=380)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final int i)
	{
		throw new RuntimeException();
	}

	@Wrap(order=390)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final SomeEnum someEnum)
	{
		throw new RuntimeException();
	}

	@Wrap(order=400)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final byte[] bytes)
	{
		throw new RuntimeException();
	}

	@Wrap(order=410)
	public void param(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final Item[][] twoDimensional)
	{
		throw new RuntimeException();
	}

	@SuppressWarnings({"EmptyClass", "RedundantSuppression"}) // OK: just for testing instrumentor
	public enum SomeEnum { }

	@Wrap(order=500)
	public void param(
			@SuppressWarnings("unused") final float arg)
	{
		throw new RuntimeException();
	}

	@Wrap(order=510)
	public <P extends Item> void param(
			@SuppressWarnings("unused") final Class<P> parent,
			@SuppressWarnings("unused") final double arg)
	{
		throw new RuntimeException();
	}

	@Wrap(order=520)
	public <P extends Item> void param(
			@SuppressWarnings("unused") final Class<P> parent,
			@SuppressWarnings("unused") @Parameter("parentItem") final P parentItem,
			@SuppressWarnings("unused") final short arg)
	{
		throw new RuntimeException();
	}
}
