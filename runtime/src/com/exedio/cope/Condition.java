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

public abstract class Condition implements java.io.Serializable
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
		return composite(CompositeCondition.Operator.AND, this, other);
	}

	public final Condition or(final Condition other)
	{
		return composite(CompositeCondition.Operator.OR, this, other);
	}

	private static final Condition composite(
			final CompositeCondition.Operator operator,
			final Condition left,
			final Condition other)
	{
		if(left instanceof Literal)
			if(other instanceof Literal)
				return valueOf(
					(operator==CompositeCondition.Operator.AND)
					? ( ((Literal)left).value && ((Literal)other).value )
					: ( ((Literal)left).value || ((Literal)other).value ));
			else
				return compositeLiteral(operator, (Literal)left, other);
		else
			if(other instanceof Literal)
				return compositeLiteral(operator, (Literal)other, left);
			else
				return compositeFlattening(operator, left, other);
	}

	private static final Condition compositeLiteral(
			final CompositeCondition.Operator operator,
			final Literal literal,
			final Condition other)
	{
		return operator.absorber==literal ? literal : other;
	}

	private static final Condition compositeFlattening(
			final CompositeCondition.Operator operator,
			final Condition leftCondition,
			final Condition other)
	{
		if(leftCondition instanceof CompositeCondition && ((CompositeCondition)leftCondition).operator==operator)
		{
			final CompositeCondition left = (CompositeCondition)leftCondition;

			if(other instanceof CompositeCondition && ((CompositeCondition)other).operator==operator)
			{
				final CompositeCondition right = (CompositeCondition)other;

				final Condition[] c = new Condition[left.conditions.length + right.conditions.length];
				System.arraycopy(left.conditions, 0, c, 0, left.conditions.length);
				System.arraycopy(right.conditions, 0, c, left.conditions.length, right.conditions.length);
				return new CompositeCondition(operator, c);
			}
			else
			{
				final Condition[] c = new Condition[left.conditions.length + 1];
				System.arraycopy(left.conditions, 0, c, 0, left.conditions.length);
				c[left.conditions.length] = other;
				return new CompositeCondition(operator, c);
			}
		}
		else
		{
			if(other instanceof CompositeCondition && ((CompositeCondition)other).operator==operator)
			{
				final CompositeCondition right = (CompositeCondition)other;

				final Condition[] c = new Condition[1 + right.conditions.length];
				c[0] = leftCondition;
				System.arraycopy(right.conditions, 0, c, 1, right.conditions.length);
				return new CompositeCondition(operator, c);
			}
			else
			{
				return new CompositeCondition(operator, new Condition[]{leftCondition, other});
			}
		}
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
