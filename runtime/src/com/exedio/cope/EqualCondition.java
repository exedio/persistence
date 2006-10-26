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

public final class EqualCondition<E> extends Condition // TODO remove, integrate into CompareCondition
{
	public final Function<E> function;
	private final boolean not;
	public final E value;

	/**
	 * Creates a new EqualCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see FunctionField#isNull()
	 * @see FunctionField#equal(Object)
	 * @see FunctionField#isNotNull()
	 * @see FunctionField#notEqual(Object)
	 */
	public EqualCondition(final Function<E> function, final boolean not, final E value)
	{
		if(function==null)
			throw new NullPointerException("function must not be null");

		this.function = function;
		this.not = not;
		this.value = value;
	}
	
	@Override
	void append(final Statement bf)
	{
		if(!not)
		{
			function.append(bf, null);
			if(value!=null)
				bf.append('=').
					appendParameter(function, value);
			else
				bf.append(" is null");
		}
		else
		{
			if(value!=null)
			{
				// IMPLEMENTATION NOTE
				// the "or is null" is needed since without this oracle
				// does not find results with null.
				bf.append("(").
					append(function, (Join)null).
					append("<>").
					appendParameter(function, value).
					append(" or ").
					append(function, (Join)null).
					append(" is null)");
			}
			else
				bf.append(function, (Join)null).
					append(" is not null");
		}
	}

	@Override
	void check(final Query query)
	{
		query.check(function);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof EqualCondition))
			return false;
		
		final EqualCondition o = (EqualCondition)other;
		
		return function.equals(o.function) && not==o.not && equals(value, o.value);
	}
	
	@Override
	public int hashCode()
	{
		return function.hashCode() ^ (not ? 823658266 : 328451237) ^ hashCode(value);
	}

	@Override
	public String toString()
	{
		return function.toString() + (not ? "!='" : "='") + value + '\'';
	}
	
	@Override
	String toStringForQueryKey()
	{
		return function.toString() + (not ? "!='" : "='") + toStringForQueryKey(value) + '\'';
	}
}
