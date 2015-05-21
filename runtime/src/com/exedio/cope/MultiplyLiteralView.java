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

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class MultiplyLiteralView<E extends Number> extends NumberView<E>
{
	public static final <E extends Number> MultiplyLiteralView<E> multiply(final Function<E> multiplier1, final E multiplier2)
	{
		return new MultiplyLiteralView<>(multiplier1, multiplier2);
	}


	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings("SE_BAD_FIELD") // TODO Function should implement Serializable
	private final Function<E> left;
	private final E right;

	private MultiplyLiteralView(final Function<E> left, final E right)
	{
		super(new Function<?>[]{left}, "multiply", left.getValueClass());

		this.left = left;
		this.right = requireNonNull(right, "right");
	}

	public SelectType<E> getValueType()
	{
		return left.getValueType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Number leftValue = (Number)sourceValues[0];
		if(leftValue==null)
			return null;
		final Class<E> vc = valueClass;
		if(valueClass==Integer.class)
		{
			return (E)Integer.valueOf(((Integer)leftValue).intValue() * right.intValue());
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf(((Long)leftValue).longValue() * right.longValue());
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf(((Double)leftValue).doubleValue() * right.doubleValue());
		}
		else
			throw new RuntimeException(vc.getName());
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append('(');
		left.toString(bf, defaultType);
		bf.append('*');
		bf.append(right);
		bf.append(')');
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append('(');
		bf.append(left, join);
		bf.append('*');
		bf.appendParameter(right);
		bf.append(')');
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #MultiplyLiteralView(Function,Number)} instead.
	 */
	@Deprecated
	public MultiplyLiteralView(final NumberFunction<E> left, final E right)
	{
		this((Function<E>)left, right);
	}
}
