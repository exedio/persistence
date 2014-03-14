/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import com.exedio.cope.search.AverageAggregate;
import com.exedio.cope.search.SumAggregate;

public final class BindNumberFunction<E extends Number> extends BindFunction<E>
	implements NumberFunction<E>
{
	private static final long serialVersionUID = 1l;

	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see NumberFunction#bind(Join)
	 */
	public BindNumberFunction(final NumberFunction<E> function, final Join join)
	{
		super(function, join);
	}

	// convenience methods for conditions and views ---------------------------------

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public final BindNumberFunction<E> bind(final Join join)
	{
		return this;
	}

	@Override
	public final AsStringView asString()
	{
		return new AsStringView(this);
	}

	/**
	 * You may want to use {@link PlusLiteralView#plus(Function, Number)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final PlusLiteralView<E> plus(final E value)
	{
		return PlusLiteralView.plus(this, value);
	}

	/**
	 * You may want to use {@link MultiplyLiteralView#multiply(Function, Number)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final MultiplyLiteralView<E> multiply(final E value)
	{
		return MultiplyLiteralView.multiply(this, value);
	}

	/**
	 * You may want to use {@link PlusView#plus(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final PlusView<E> plus(final NumberFunction<E> other)
	{
		return PlusView.plus(this, other);
	}

	/**
	 * You may want to use {@link MinusView#minus(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final MinusView<E> minus(final NumberFunction<E> other)
	{
		return MinusView.minus(this, other);
	}

	/**
	 * You may want to use {@link MultiplyView#multiply(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final MultiplyView<E> multiply(final NumberFunction<E> other)
	{
		return MultiplyView.multiply(this, other);
	}

	/**
	 * You may want to use {@link DivideView#divide(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	@Override
	public final DivideView<E> divide(final NumberFunction<E> other)
	{
		return DivideView.divide(this, other);
	}

	/**
	 * @deprecated renamed to {@link #plus(NumberFunction)}.
	 */
	@Override
	@Deprecated
	public final PlusView<E> sum(final NumberFunction<E> other)
	{
		return plus(other);
	}

	@Override
	public final SumAggregate<E> sum()
	{
		return new SumAggregate<E>(this);
	}

	@Override
	public final AverageAggregate<E> average()
	{
		return new AverageAggregate<E>(this);
	}
}
