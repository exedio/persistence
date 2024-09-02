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

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

public class AssertionFailedSettable<E> extends Feature implements Settable<E>
{
	@Override
	@SuppressWarnings({"deprecation","unused"}) // OK: testing deprecated API
	public SetValue<E> map(final E value)
	{
		throw new AssertionError(Objects.toString(value));
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		throw new AssertionError(Objects.toString(value));
	}

	@Override
	public boolean isFinal()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isMandatory()
	{
		throw new AssertionError();
	}

	@Override
	public Type getInitialType()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isInitial()
	{
		throw new AssertionError();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		throw new AssertionError();
	}

	@Serial
	private static final long serialVersionUID = 1l;
}
