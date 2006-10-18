/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

public final class EqualFunctionCondition<E> extends Condition // TODO remove, integrate into CompareFunctionCondition
{
	public final Function<E> left;
	public final Function<E> right;

	/**
	 * Creates a new EqualFunctionCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the convenience functions.
	 * @see Function#equal(Function)
	 */
	public EqualFunctionCondition(
				final Function<E> left,
				final Function<E> right)
	{
		if(left==null)
			throw new NullPointerException("left function must not be null");
		if(right==null)
			throw new NullPointerException("right function must not be null");

		this.left = left;
		this.right = right;
	}

	@Override
	void append(final Statement bf)
	{
		bf.append(left, (Join)null).
			append('=').
			append(right, (Join)null);
	}

	@Override
	void check(final Query query)
	{
		check(left, query);
		check(right, query);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof EqualFunctionCondition))
			return false;
		
		final EqualFunctionCondition o = (EqualFunctionCondition)other;
		
		return left.equals(o.left) && right.equals(o.right);
	}
	
	@Override
	public int hashCode()
	{
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public String toString()
	{
		return left.toString() + '=' + right.toString();
	}

}
