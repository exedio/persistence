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

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.testfeature.GenericInterface;
import java.util.function.Supplier;

@SuppressWarnings({"EmptyClass", "unused"})
public class GenericImplementations
{
	@WrapInterim
	class DirectImplementation implements GenericInterface<Integer>
	{
		@Override
		public Integer getNumber()
		{
			return 42;
		}
	}

	@WrapInterim
	@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
	interface SubInterface extends GenericInterface<Long>
	{
	}

	@WrapInterim
	abstract class AbstractImplementation implements SubInterface
	{
	}

	@WrapInterim
	class IndirectImplementation extends AbstractImplementation
	{
		@Override
		public Long getNumber()
		{
			return 42L;
		}
	}

	@WrapInterim
	static <E> E genericStatic(final E e)
	{
		return e;
	}

	@WrapInterim
	<E, F extends Supplier<E>> E genericTwoTypeParams(final F f)
	{
		return f.get();
	}
}
