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
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;

@com.exedio.cope.instrument.WrapFeature
public class WrapFeature
{
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

	@Wrap(order=45, optionTagname="myOptionTagname")
	public int optionTagname()
	{
		throw new RuntimeException();
	}

	// documentation

	@Wrap(order=50,
			doc="method documentation",
			docReturn="return documentation",
			thrown=@Wrap.Thrown(value=RuntimeException.class, doc="throws documentation"))
	public int documented(
			@SuppressWarnings("unused") @Parameter(doc="parameter documentation") final int n)
	{
		throw new RuntimeException();
	}

	@Wrap(order=60,
			doc={
				"method documentation line 1 {0} {1} {2} {3} {4}",
				"method documentation line 2 {0} {1} {2} {3} {4}",
				"",
				"method documentation line 3 {0} {1} {2} {3} {4}"},
			docReturn={
				"return documentation line 1 {0} {1} {2} {3} {4}",
				"return documentation line 2 {0} {1} {2} {3} {4}",
				"",
				"return documentation line 3 {0} {1} {2} {3} {4}"},
			thrown={
				@Wrap.Thrown(value=RuntimeException.class, doc="throws documentation RuntimeException {0} {1} {2} {3} {4}"),
				@Wrap.Thrown(value=IllegalArgumentException.class, doc={
					"throws documentation IllegalArgumentException line 1 {0} {1} {2} {3} {4}",
					"throws documentation IllegalArgumentException line 2 {0} {1} {2} {3} {4}",
					"",
					"throws documentation IllegalArgumentException line 3 {0} {1} {2} {3} {4}"})})
	public int documentedMulti(
			@SuppressWarnings("unused")
			@Parameter(
				value="paramNameX{1}X{2}X{3}",
				doc={
					"parameter documentation line 1 {0} {1} {2} {3} {4}",
					"parameter documentation line 2 {0} {1} {2} {3} {4}",
					"",
					"parameter documentation line 3 {0} {1} {2} {3} {4}"
					})
			final int n)
	{
		throw new RuntimeException();
	}

	@Wrap(order=70,
			doc={
				"",
				"method documentation line 2 {0} {1} {2} {3} {4}"},
			docReturn={
				"",
				"return documentation line 2 {0} {1} {2} {3} {4}"},
			thrown=
				@Wrap.Thrown(value=IllegalArgumentException.class, doc={
					"",
					"throws documentation IllegalArgumentException line 2 {0} {1} {2} {3} {4}"}))
	public int documentedFirstLineEmpty(
			@SuppressWarnings("unused")
			@Parameter(
				doc={
					"",
					"parameter documentation line 2 {0} {1} {2} {3} {4}"})
			final int n)
	{
		throw new RuntimeException();
	}

	// suppressor

	@Wrap(order=200, hide=TrueGetter.class)
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public int hidden()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("unused")
	private static final class TrueGetter implements BooleanGetter<WrapFeature>
	{
		@Override
		public boolean get(final WrapFeature feature)
		{
			return true;
		}
	}

	@Wrap(order=210, hide=FalseGetter.class)
	public int notHidden()
	{
		throw new RuntimeException();
	}

	private static final class FalseGetter implements BooleanGetter<WrapFeature>
	{
		@Override
		public boolean get(final WrapFeature feature)
		{
			return false;
		}
	}

	@Wrap(order=220, hide={FalseGetter.class, TrueGetter.class})
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	public int hiddenPartially()
	{
		throw new RuntimeException();
	}

	// various

	@Wrap(order=320)
	public byte[] varargsMethod(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") @Parameter("array")   final String array,
			@SuppressWarnings("unused") @Parameter("varargs") final Integer... varargs)
	{
		throw new RuntimeException();
	}

	@Wrap(order=330)
	public byte[] arrayMethod(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") @Parameter("array") final Integer[] array)
	{
		throw new RuntimeException();
	}

	@Wrap(order=340)
	public byte[] arrayAndVarargsMethod(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") @Parameter("array") final Integer[] array,
			@SuppressWarnings("unused") @Parameter("varargs") final Integer... varargs)
	{
		throw new RuntimeException();
	}

	@Wrap(order=350)
	public void disabledInBuildXml(@SuppressWarnings("unused") final int[][] matrix)
	{
		throw new RuntimeException();
	}
}
