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
import com.exedio.cope.instrument.WrapFeature;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@WrapFeature
public final class GenericFeatureReference<E> extends AssertionFailedSettable<E>
{
	public final Class<E> valueClass;
	public final Type valueType;

	public static <E> GenericFeatureReference<E> create(final Class<E> valueClass, final Type valueType)
	{
		return new GenericFeatureReference<>(valueClass, valueType);
	}

	private GenericFeatureReference(final Class<E> valueClass, final Type valueType)
	{
		this.valueClass = valueClass;
		this.valueType = valueType;

		final Class<?> superClass = Item.class;
		if(!superClass.isAssignableFrom(valueClass))
			throw new RuntimeException("is not a subclass of " + superClass.getName() + ": "+valueClass.getName());
		if(superClass.equals(valueClass))
			throw new RuntimeException("is not a real subclass of " + superClass.getName() + ": "+valueClass.getName());
	}

	@Wrap(order=10)
	public E method(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final E value)
	{
		throw new RuntimeException();
	}


	// implements Settable

	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public Type getInitialType()
	{
		return valueType;
	}

	@Override
	public boolean isInitial()
	{
		return true;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return Collections.emptySet();
	}
}
