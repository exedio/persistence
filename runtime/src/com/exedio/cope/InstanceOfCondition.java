/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.TreeSet;

public final class InstanceOfCondition<E extends Item> extends Condition
{
	private final ItemFunction<E> function;
	private final boolean not;
	private Type<E>[] types;

	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see ItemFunction#instanceOf(Type)
	 * @see ItemFunction#instanceOf(Type, Type)
	 * @see ItemFunction#instanceOf(Type, Type, Type)
	 * @see ItemFunction#instanceOf(Type, Type, Type, Type)
	 * @see ItemFunction#instanceOf(Type[])
	 * @see ItemFunction#notInstanceOf(Type)
	 * @see ItemFunction#notInstanceOf(Type, Type)
	 * @see ItemFunction#notInstanceOf(Type, Type, Type)
	 * @see ItemFunction#notInstanceOf(Type, Type, Type, Type)
	 * @see ItemFunction#notInstanceOf(Type[])
	 */
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type[] types)
	{
		if(function==null)
			throw new NullPointerException("function must not be null");
		if(types==null)
			throw new NullPointerException("types must not be null");
		if(types.length==0)
			throw new IllegalArgumentException("types must not be empty");

		this.function = function;
		this.not = not;
		this.types = InstanceOfCondition.<E>cast(types);
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private final static <X extends Item> Type<X>[] cast(final Type[] o)
	{
		return o;
	}
	
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1)
	{
		this(function, not, new Type[]{type1});
	}
	
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2)
	{
		this(function, not, new Type[]{type1, type2});
	}
	
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		this(function, not, new Type[]{type1, type2, type3});
	}
	
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<? extends E> type4)
	{
		this(function, not, new Type[]{type1, type2, type3, type4});
	}
	
	@SuppressWarnings("deprecation") // OK: For internal use within COPE only
	private void appendType(final Statement bf)
	{
		function.appendType(bf, null);
	}
	
	@Override
	void append(final Statement bf)
	{
		final Type type = function.getValueType();
		appendType(bf);
		if(not)
			bf.append(" not");
		bf.append(" in(");
		
		final TreeSet<String> typeIds = new TreeSet<String>(); // order ids to produce canonical queries for query cache
		for(final Type<E> t : types)
		{
			if(!type.isAssignableFrom(t))
				throw new IllegalArgumentException("type " + type + " is not assignable from type " + t);
			
			for(final Type ti : t.getTypesOfInstances())
				typeIds.add(ti.id);
		}
		
		if(typeIds.isEmpty())
			throw new RuntimeException("no concrete type for " + types);

		boolean first = true;
		for(final String id : typeIds)
		{
			if(first)
				first = false;
			else
				bf.append(',');
			
			bf.appendParameter(id);
		}
		bf.append(')');
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof InstanceOfCondition))
			return false;
		
		final InstanceOfCondition o = (InstanceOfCondition)other;
		
		return function.equals(o.function) && not==o.not && equals(types, o.types);
	}
	
	@Override
	public int hashCode()
	{
		return function.hashCode() ^ (not?21365:237634)^ hashCode(types);
	}

	@Override
	public String toString()
	{
		return function.toString() + (not?" not":"") + " instanceOf " + (types.length==1 ? types[0].toString() : Arrays.toString(types));
	}
	
	@Override
	String toStringForQueryKey()
	{
		return toString();
	}
}
