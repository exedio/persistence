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

import java.util.Collection;

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;


public class JoinedFunction<E> implements Function<E>
{
	final Function<E> function;
	final Join join;
	
	public JoinedFunction(final Function<E> function, final Join join)
	{
		assert function!=null;
		assert join!=null;
		
		this.function = function;
		this.join = join;
	}
	
	public final E get(final Item item)
	{
		return function.get(item);
	}

	public final void append(final Statement bf, final Join join)
	{
		function.append(bf, this.join);
	}
	
	public final void appendParameter(final Statement bf, final E value)
	{
		bf.appendParameter(function, value);
	}
	
	public final Type<? extends Item> getType()
	{
		return function.getType();
	}
	
	public final boolean equals(final Object other)
	{
		if(!(other instanceof JoinedFunction))
			return false;
		
		final JoinedFunction o = (JoinedFunction)other;
		
		return function.equals(o.function) && join.equals(o.join);
	}
	
	public final int hashCode()
	{
		return function.hashCode() ^ join.hashCode();
	}

	public final String toString()
	{
		return function.toString();
	}
	
	// convenience methods for conditions and views ---------------------------------

	public final EqualCondition<E> equal(final E value)
	{
		return new EqualCondition<E>(this, value);
	}
	
	public final EqualCondition<E> equal(final Join join, final E value)
	{
		return new EqualCondition<E>(new JoinedFunction<E>(this, join), value);
	}
	
	public final CompositeCondition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public final NotEqualCondition notEqual(final E value)
	{
		return new NotEqualCondition<E>(this, value);
	}
	
	public final EqualFunctionCondition equal(final Function<E> right)
	{
		return new EqualFunctionCondition(this, right);
	}
	
	public final LessCondition less(final E value)
	{
		return new LessCondition<E>(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final E value)
	{
		return new LessEqualCondition<E>(this, value);
	}
	
	public final GreaterCondition greater(final E value)
	{
		return new GreaterCondition<E>(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final E value)
	{
		return new GreaterEqualCondition<E>(this, value);
	}
}
