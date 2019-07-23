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
import com.exedio.cope.instrument.Wrap;

@com.exedio.cope.instrument.WrapFeature
public class WrapperParametersFeatureGeneric<C extends Number, E extends Enum<E>>
{
	@Wrap(order=100)
	public void method(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final C arg)
	{
		throw new RuntimeException();
	}

	@Wrap(order=110)
	public void methodStatic(
			@SuppressWarnings("unused") final C arg)
	{
		throw new RuntimeException();
	}

	@Wrap(order=120)
	public <P extends Item> void methodParent(
			@SuppressWarnings("unused") final Class<P> parent,
			@SuppressWarnings("unused") final C arg)
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("unused")
	@Wrap(order=130)
	public void methodEnum(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final E e)
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("unused")
	@Wrap(order=140)
	public void disabledInBuildXml(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final C arg)
	{
		throw new RuntimeException();
	}
}
