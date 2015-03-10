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

import java.io.Serializable;

public abstract class Condition implements Serializable
{
	private static final long serialVersionUID = 1l;

	abstract void append(Statement statment);

	public abstract boolean get(Item item);

	abstract void check(TC tc);

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

	public static final Literal TRUE  = new Literal(true , "TRUE" );
	public static final Literal FALSE = new Literal(false, "FALSE");

	static class Literal extends Condition
	{
		private static final long serialVersionUID = 1l;

		final boolean value;
		private final transient String name; // restored by readResolve

		Literal(final boolean value, final String name)
		{
			this.value = value;
			this.name = name;
		}

		@Override
		void append(final Statement statment)
		{
			throw new RuntimeException();
		}

		@Override
		public boolean get(final Item item)
		{
			// NOTE
			// all other implementations of get will fail when
			// item==null, so this method fails as well, to avoid
			// hiding bugs.
			if(item==null)
				throw new NullPointerException();

			return value;
		}

		@Override
		void check(final TC tc)
		{
			throw new RuntimeException(name);
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
		 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return Condition.valueOf(value);
		}
	}

	/**
	 * Returns {@link #TRUE} if <tt>value</tt> is true,
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

	static final boolean equals(final Object o1, final Object o2)
	{
		return o1==null ? o2==null : o1.equals(o2);
	}

	static final int hashCode(final Object o)
	{
		return o==null ? 0 : o.hashCode();
	}

	@Override
	public final String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, false, null);
		return bf.toString();
	}

	abstract void toString(StringBuilder bf, boolean key, Type<?> defaultType);
}
