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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@WrapFeature
public final class OwnerTypeFeaturePlain<E> extends Pattern implements Settable<E>
{
	private final Class<E> valueClass;

	private OwnerTypeFeaturePlain(final Class<E> valueClass)
	{
		this.valueClass = requireNonNull(valueClass);
	}

	public static <E> OwnerTypeFeaturePlain<E> create(final Class<E> valueClass)
	{
		return new OwnerTypeFeaturePlain<>(valueClass);
	}

	@Override
	public boolean isInitial()
	{
		return true;
	}

	@Override
	public boolean isMandatory()
	{
		return false;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.emptySet();
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		throw new AssertionError();
	}

	@Override
	public boolean isFinal()
	{
		throw new AssertionError();
	}

	@Override
	public Type getInitialType()
	{
		return valueClass;
	}

	@Serial
	private static final long serialVersionUID = 1L;
}
