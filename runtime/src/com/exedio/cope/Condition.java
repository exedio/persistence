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

import java.io.Serializable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public abstract class Condition implements Serializable
{
	private static final long serialVersionUID = 1l;

	abstract void append(Statement statment);

	public final boolean get(@Nonnull final Item item)
	{
		// ensures same behaviour for different implementations of getTri, avoid hiding bugs
		requireNonNull(item, "item");

		supportsGetTri();
		return getTri(new FieldValues(item)).applies;
	}

	/**
	 * Must throw the same {@link IllegalArgumentException} under the same circumstances as
	 * {@link #getTri(FieldValues)}.
	 */
	void supportsGetTri()
	{
		// empty default implementation means condition does always support getTri
	}

	abstract Trilean getTri(@Nonnull FieldValues item);

	abstract void check(TC tc);

	public abstract void acceptFieldsCovered(Consumer<Field<?>> consumer);

	abstract Condition copy(CopyMapper mapper);

	public final Condition not()
	{
		if(this instanceof Literal)
			return valueOf(!((Literal)this).value);
		else if(this instanceof NotCondition)
			return ((NotCondition)this).argument;

		return new NotCondition(this);
	}

	public final Condition and(final Condition other)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.AND, this, other);
	}

	public final Condition or(final Condition other)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.OR, this, other);
	}

	@SuppressWarnings("StaticInitializerReferencesSubClass") // TODO is a serious problem
	public static final Literal TRUE  = new Literal(true , "TRUE" );
	@SuppressWarnings("StaticInitializerReferencesSubClass") // TODO is a serious problem
	public static final Literal FALSE = new Literal(false, "FALSE");

	static final class Literal extends Condition
	{
		private static final long serialVersionUID = 1l;

		final boolean value;
		@SuppressWarnings("TransientFieldNotInitialized") // OK: restored by readResolve
		final transient Trilean valueTri;
		@SuppressWarnings("TransientFieldNotInitialized") // OK: restored by readResolve
		private final transient String name;

		Literal(final boolean value, final String name)
		{
			this.value = value;
			this.valueTri = Trilean.valueOf(value);
			this.name = name;
		}

		@Override
		void append(final Statement statment)
		{
			throw new RuntimeException();
		}

		@Override
		Trilean getTri(final FieldValues item)
		{
			return valueTri;
		}

		@Override
		void check(final TC tc)
		{
			throw new RuntimeException(name);
		}

		@Override
		public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
		{
		}

		@Override
		Literal copy(final CopyMapper mapper)
		{
			return this;
		}

		@Override
		public boolean equals(final Object o)
		{
			return this==o;
		}

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}

		@Override
		void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
		{
			bf.append(name);
		}

		/**
		 * Enforces singleton on deserialization,
		 * otherwise {@link #equals(Object)} would be wrong.
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return Condition.valueOf(value);
		}
	}

	/**
	 * Returns {@link #TRUE} if {@code value} is true,
	 * otherwise {@link #FALSE}.
	 */
	public static final Condition valueOf(final boolean value)
	{
		return value ? TRUE : FALSE;
	}

	@Override
	public abstract boolean equals(Object o);
	@Override
	public abstract int hashCode();

	@Override
	public final String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, false, null);
		return bf.toString();
	}

	abstract void toString(StringBuilder bf, boolean key, Type<?> defaultType);
}
