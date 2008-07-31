/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public final class PlusLiteralView<E extends Number> extends NumberView<E> implements NumberFunction<E>
{
	private final NumberFunction<E> left;
	private final E right;
	
	/**
	 * Creates a new PlusView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods.
	 * @see NumberFunction#plus(Number)
	 */
	public PlusLiteralView(final NumberFunction<E> left, final E right)
	{
		super(new Function[]{left}, "plus", left.getValueClass());
		
		if(left==null)
			throw new NullPointerException("left function must not be null");
		if(right==null)
			throw new NullPointerException("right literal must not be null");
		
		this.left = left;
		this.right = right;
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
			return (E)Integer.valueOf(((Integer)leftValue).intValue() + right.intValue());
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf(((Long)leftValue).longValue() + right.longValue());
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf(((Double)leftValue).doubleValue() + right.doubleValue());
		}
		else
			throw new RuntimeException(vc.getName());
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append('(');
		bf.append(left, join);
		bf.append('+');
		bf.appendParameter(right);
		bf.append(')');
	}
}
