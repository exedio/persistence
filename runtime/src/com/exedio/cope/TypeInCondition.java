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

import java.util.TreeSet;

public final class TypeInCondition<E extends Item> extends Condition
{
	private final ItemFunction<E> function;
	private final boolean not;
	private Type<E>[] excludedTypes; // TODO SOON rename

	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see ItemFunction#typeIn(Type)
	 * @see ItemFunction#typeIn(Type, Type)
	 * @see ItemFunction#typeIn(Type, Type, Type)
	 * @see ItemFunction#typeIn(Type, Type, Type, Type)
	 * @see ItemFunction#typeIn(Type[])
	 * @see ItemFunction#typeNotIn(Type)
	 * @see ItemFunction#typeNotIn(Type, Type)
	 * @see ItemFunction#typeNotIn(Type, Type, Type)
	 * @see ItemFunction#typeNotIn(Type, Type, Type, Type)
	 * @see ItemFunction#typeNotIn(Type[])
	 */
	public TypeInCondition(final ItemFunction<E> function, final boolean not, final Type[] excludedTypes)
	{
		if(function==null)
			throw new NullPointerException("function must not be null");
		if(excludedTypes==null)
			throw new NullPointerException("types must not be null");
		if(excludedTypes.length==0)
			throw new NullPointerException("types must not be empty");

		this.function = function;
		this.not = not;
		this.excludedTypes = TypeInCondition.<E>cast(excludedTypes);
	}
	
	@SuppressWarnings("unchecked") // OK: no generic array creation
	private final static <X extends Item> Type<X>[] cast(final Type[] o)
	{
		return (Type<X>[])o;
	}
	
	public TypeInCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1)
	{
		this(function, not, new Type[]{type1});
	}
	
	public TypeInCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2)
	{
		this(function, not, new Type[]{type1, type2});
	}
	
	public TypeInCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		this(function, not, new Type[]{type1, type2, type3});
	}
	
	public TypeInCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<? extends E> type4)
	{
		this(function, not, new Type[]{type1, type2, type3, type4});
	}
	
	@Override
	void append(final Statement bf)
	{
		final Type type = function.getValueType();
		final StringColumn column = function.getTypeColumnIfExists();
		
		if(column==null)
			throw new RuntimeException("type " + type + " has no subtypes, therefore a TypeInCondition makes no sense");
		
		bf.append(column, null);
		if(not)
			bf.append(" not");
		bf.append(" in(");
		
		final TreeSet<String> typeIds = new TreeSet<String>(); // order ids to produce canonical queries for query cache
		for(final Type<E> t : excludedTypes)
		{
			if(!type.isAssignableFrom(t))
				throw new RuntimeException("type " + type + " is not assignable from excluded type " + t);
			
			for(final Type ti : t.getTypesOfInstances())
				typeIds.add(ti.id);
		}
		
		if(typeIds.isEmpty())
			throw new RuntimeException("no concrete type for " + excludedTypes);

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
	void check(final Query query)
	{
		check(function, query);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof TypeInCondition))
			return false;
		
		final TypeInCondition o = (TypeInCondition)other;
		
		return function.equals(o.function) && equals(excludedTypes, o.excludedTypes);
	}
	
	@Override
	public int hashCode()
	{
		return function.hashCode() ^ hashCode(excludedTypes);
	}

	@Override
	public String toString()
	{
		return function.toString() + " not in (" + excludedTypes + ')';
	}
	
}
