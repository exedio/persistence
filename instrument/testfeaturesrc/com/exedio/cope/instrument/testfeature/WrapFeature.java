/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapParam;
import com.exedio.cope.instrument.Wrapper;

public class WrapFeature extends Pattern
{
	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(WrapFeature.class, this, super.getWrappers());
	}

	@Wrap(order=10)
	public int simple(
			@SuppressWarnings("unused") final Item item)
	{
		throw new RuntimeException();
	}

	@Wrap(order=20)
	public void simpleVoid(
			@SuppressWarnings("unused") final Item item)
	{
		throw new RuntimeException();
	}

	@Wrap(order=30)
	public int simpleStatic()
	{
		throw new RuntimeException();
	}

	@Wrap(order=40)
	public void simpleStaticVoid()
	{
		throw new RuntimeException();
	}

	@Wrap(order=50, doc="method documentation", docReturn="return documentation")
	public int documented(
			@SuppressWarnings("unused") @WrapParam(doc="parameter documentation") final int n)
	{
		throw new RuntimeException();
	}

	@Wrap(order=60,
			doc={
				"method documentation line 1",
				"method documentation line 2",
				"",
				"method documentation line 3"},
			docReturn={
				"return documentation line 1",
				"return documentation line 2",
				"",
				"return documentation line 3"})
	public int documentedMulti(
			@SuppressWarnings("unused") @WrapParam(doc="parameter documentation") final int n)
	{
		throw new RuntimeException();
	}


	private static final long serialVersionUID = 1l;
}
