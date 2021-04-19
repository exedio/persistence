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

public final class PlusLiteralView<E extends Number> extends NumberView<E>
{
	public static <E extends Number> PlusLiteralView<E> plus(final Function<E> addend1, final E addend2)
	{
		return new PlusLiteralView<>(addend1, addend2);
	}


	private static final long serialVersionUID = 1l;

	private final Function<E> left;
	private final E right;

	private PlusLiteralView(final Function<E> left, final E right)
	{
		super(new Function<?>[]{left}, "plus", PlusView.checkClass(Number.class, left.getValueClass()));

		this.left = left;
		this.right = requireNonNull(right, "right");
	}

	@Override
	public SelectType<E> getValueType()
	{
		return left.getValueType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public E mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Number leftValue = (Number)sourceValues[0];
		if(leftValue==null)
			return null;

		if(valueClass==Integer.class)
		{
			return (E)Integer.valueOf((Integer)leftValue + (Integer)right);
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf((Long)leftValue + (Long)right);
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf((Double)leftValue + (Double)right);
		}
		else
			throw new RuntimeException(valueClass.getName());
	}

	@Override
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append('(');
		left.toString(bf, defaultType);
		bf.append('+');
		bf.append(right);
		bf.append(')');
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.append('(');
		bf.append(left, join);
		bf.append('+');
		bf.appendParameter(right);
		bf.append(')');
	}
}
