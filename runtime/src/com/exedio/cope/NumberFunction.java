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

package com.exedio.cope;

import java.io.Serial;

public interface NumberFunction<E extends Number> extends Function<E>
{
	// convenience methods for conditions and views ---------------------------------

	@Override
	NumberFunction<E> bind(Join join); // narrows return type for implementations

	default AsStringView asString()
	{
		return new AsStringView(this);
	}

	/**
	 * You may want to use {@link PlusLiteralView#plus(Function, Number)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default PlusLiteralView<E> plus(final E value)
	{
		return PlusLiteralView.plus(this, value);
	}

	/**
	 * You may want to use {@link MultiplyLiteralView#multiply(Function, Number)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default MultiplyLiteralView<E> multiply(final E value)
	{
		return MultiplyLiteralView.multiply(this, value);
	}

	/**
	 * You may want to use {@link PlusView#plus(Function, Function)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default PlusView<E> plus(final NumberFunction<E> other)
	{
		return PlusView.plus(this, other);
	}

	/**
	 * You may want to use {@link MinusView#minus(Function, Function)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default MinusView<E> minus(final NumberFunction<E> other)
	{
		return MinusView.minus(this, other);
	}

	/**
	 * You may want to use {@link MultiplyView#multiply(Function, Function)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default MultiplyView<E> multiply(final NumberFunction<E> other)
	{
		return MultiplyView.multiply(this, other);
	}

	/**
	 * You may want to use {@link DivideView#divide(Function, Function)} instead, if you do not have {@code NumberFunction}s available.
	 */
	default DivideView<E> divide(final NumberFunction<E> other)
	{
		return DivideView.divide(this, other);
	}

	@SuppressWarnings("deprecation")
	default SumAggregate<E> sum()
	{
		return new SumAggregate<>(this);
	}

	@SuppressWarnings("deprecation")
	default AverageAggregate<E> average()
	{
		return new AverageAggregate<>(this);
	}

	@Serial
	long serialVersionUID = 3484464008830007161L;
}
