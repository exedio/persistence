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

import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.function.Consumer;

public final class InstanceOfCondition<E extends Item> extends Condition
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final ItemFunction<E> function;
	private final boolean not;
	private final Type<E>[] types;

	/**
	 * @deprecated
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
	@Deprecated
	public InstanceOfCondition(
			final ItemFunction<E> function,
			final boolean not,
			final Type<?>[] types)
	{
		this.function = requireNonNull(function, "function");
		this.not = not;
		this.types = cast(requireNonEmptyAndCopy(types, "types"));
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic array creation
	private static <T extends Item> Type<T>[] cast(final Type[] o)
	{
		return o;
	}

	/**
	 * @deprecated Instead of using this constructor directly, use the convenience methods.
	 */
	@Deprecated
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1)
	{
		this(function, not, new Type<?>[]{type1});
	}

	/**
	 * @deprecated Instead of using this constructor directly, use the convenience methods.
	 */
	@Deprecated
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2)
	{
		this(function, not, new Type<?>[]{type1, type2});
	}

	/**
	 * @deprecated Instead of using this constructor directly, use the convenience methods.
	 */
	@Deprecated
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		this(function, not, new Type<?>[]{type1, type2, type3});
	}

	/**
	 * @deprecated Instead of using this constructor directly, use the convenience methods.
	 */
	@Deprecated
	public InstanceOfCondition(final ItemFunction<E> function, final boolean not, final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<? extends E> type4)
	{
		this(function, not, new Type<?>[]{type1, type2, type3, type4});
	}

	@SuppressWarnings("deprecation") // OK: For internal use within COPE only
	private void appendType(final Statement bf)
	{
		function.appendType(bf, null);
	}

	@SuppressWarnings("ExtractMethodRecommender")
	@Override
	void append(final Statement bf)
	{
		final Type<?> type = function.getValueType();

		final TreeSet<String> typeIds = new TreeSet<>(); // order ids to produce canonical queries for query cache
		for(final Type<E> t : types)
		{
			if(!type.isAssignableFrom(t))
				throw new IllegalArgumentException("type " + type + " is not assignable from type " + t);

			for(final Type<?> ti : t.getTypesOfInstances())
				typeIds.add(ti.schemaId);
		}

		if(typeIds.isEmpty())
			throw new RuntimeException("no concrete type for " + Arrays.toString(types));

		if(typeIds.size()==1)
		{
			appendType(bf);
			bf.append(not ? "<>" : "=");
			bf.appendParameter(typeIds.iterator().next());
		}
		else
		{
			appendType(bf);
			if(not)
				bf.append(" NOT");
			bf.append(" IN (");

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
	}

	@Override
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		function.requireSupportForGet();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		final Item value = function.get(item);
		if(value==null)
			return Trilean.Null;

		final Type<?> valueType = value.getCopeType();
		for(final Type<?> t : types)
			if(t.isAssignableFrom(valueType))
				return Trilean.valueOf(!not);
		return Trilean.valueOf(not);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public void forEachFieldCovered(final Consumer<Field<?>> action)
	{
		function.forEachFieldCovered(action);
	}

	@Override
	CharSetCondition copy(final CopyMapper mapper)
	{
		throw new RuntimeException("not yet implemented"); // TODO
	}

	@Override
	public Condition bind(final Join join)
	{
		return new InstanceOfCondition<>(function.bind(join), not, types);
	}

	@Override
	public Condition not()
	{
		return new InstanceOfCondition<>(function, !not, types);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final InstanceOfCondition<?> o))
			return false;

		return function.equals(o.function) && not==o.not && Arrays.equals(types, o.types);
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ (not?21365:237634) ^ Arrays.hashCode(types);
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);

		if(not)
			bf.append(" not");

		bf.append(" instanceOf ");

		if(types.length==1)
			bf.append(types[0]);
		else
		{
			bf.append('[').
				append(types[0]);

			for(int i = 1; i<types.length; i++)
			{
				bf.append(", ").
					append(types[i]);
			}
			bf.append(']');
		}
	}
}
