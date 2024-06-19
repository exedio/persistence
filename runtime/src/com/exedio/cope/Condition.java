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

	void appendAfterFrom(final Statement statement) {}
	abstract void append(Statement statement);

	public final boolean get(@Nonnull final Item item)
	{
		// ensures same behaviour for different implementations of getTri, avoid hiding bugs
		requireNonNull(item, "item");

		try
		{
			requireSupportForGetTri();
			return getTri(new FieldValues(item)).applies;
		}
		catch(final UnsupportedGetException e)
		{
			throw new IllegalArgumentException(
					"condition contains unsupported function: " + e.function);
		}
	}

	/**
	 * Must throw the same {@link IllegalArgumentException} under the same circumstances as
	 * {@link #getTri(FieldValues)}.
	 */
	abstract void requireSupportForGetTri() throws UnsupportedGetException;

	abstract Trilean getTri(@Nonnull FieldValues item) throws UnsupportedGetException;

	abstract void check(TC tc);

	/**
	 * @see Iterable#forEach(Consumer)
	 */
	public abstract void forEachFieldCovered(Consumer<Field<?>> consumer);

	/**
	 * @deprecated Use {@link #forEachFieldCovered(Consumer)} instead.
	 */
	@Deprecated
	public final void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		forEachFieldCovered(consumer);
	}

	abstract Condition copy(CopyMapper mapper);

	public abstract Condition bind(Join join);

	public Condition not()
	{
		// various optimizations in overriding methods are tested for validity by CompareConditionNotTest
		return new NotCondition(this, 0.0);
	}

	public final Condition and(final Condition other)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.AND, this, other);
	}

	public final Condition or(final Condition other)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.OR, this, other);
	}

	/**
	 * @deprecated Use {@link #ofTrue()} instead.
	 */
	@Deprecated
	@SuppressWarnings({"ClassEscapesDefinedScope", "StaticInitializerReferencesSubClass"}) // TODO StaticInitializerReferencesSubClass is a serious problem
	public static final Literal TRUE  = new Literal(true , "TRUE" );
	/**
	 * @deprecated Use {@link #ofFalse()}} instead.
	 */
	@Deprecated
	@SuppressWarnings({"ClassEscapesDefinedScope", "StaticInitializerReferencesSubClass"}) // StaticInitializerReferencesSubClass TODO is a serious problem
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
		void append(final Statement statement)
		{
			throw new RuntimeException();
		}

		@Override
		void requireSupportForGetTri()
		{
			// always support getTri
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
		public void forEachFieldCovered(final Consumer<Field<?>> consumer)
		{
		}

		@Override
		Literal copy(final CopyMapper mapper)
		{
			return this;
		}

		@Override
		public Condition bind(final Join join)
		{
			requireNonNull(join);
			return this;
		}

		@Override
		public Condition not()
		{
			return of(!value);
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
			return Condition.of(value);
		}
	}

	public static final Condition ofTrue()
	{
		return TRUE;
	}

	public static final Condition ofFalse()
	{
		return FALSE;
	}

	/**
	 * Returns {@link #ofTrue()} if {@code value} is true,
	 * otherwise {@link #ofFalse()}.
	 */
	public static final Condition of(final boolean value)
	{
		return value ? ofTrue() : ofFalse();
	}

	/**
	 * @deprecated Use {@link #of(boolean)} instead.
	 */
	@Deprecated
	public static final Condition valueOf(final boolean value)
	{
		return of(value);
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
