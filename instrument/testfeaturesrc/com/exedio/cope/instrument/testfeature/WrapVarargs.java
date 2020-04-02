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
import com.exedio.cope.instrument.FeaturesGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import java.util.Arrays;
import java.util.List;

@com.exedio.cope.instrument.WrapFeature
public final class WrapVarargs
{
	final List<?> varargs;

	public WrapVarargs(final Object vararg1)
	{
		this.varargs = Arrays.asList(vararg1);
	}

	public WrapVarargs(final Object vararg1, final Object vararg2)
	{
		this.varargs = Arrays.asList(vararg1, vararg2);
	}

	@Wrap(order=10, varargsFeatures=VarargsGetter.class)
	public void simple(
			@SuppressWarnings("unused") final Object... values)
	{
		throw new RuntimeException();
	}

	@Wrap(order=20, varargsFeatures=VarargsGetter.class)
	public void moreParameters(
			@SuppressWarnings("unused") final int n,
			@SuppressWarnings("unused") final Object... values)
	{
		throw new RuntimeException();
	}

	@Wrap(order=30, varargsFeatures=VarargsGetter.class)
	public <P extends Item> P staticToken(
			@SuppressWarnings("unused") final Class<P> typeClass,
			@SuppressWarnings("unused") @Parameter(value="myName", doc="myDoc/{0}/{1}/{2}/") final Object... values)
	{
		throw new RuntimeException();
	}

	private static final class VarargsGetter implements FeaturesGetter<WrapVarargs>
	{
		@Override
		@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // just for test
		public List<?> get(final WrapVarargs feature)
		{
			return feature.varargs;
		}
	}
}
