/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public abstract class Condition
{
	abstract void append(Statement statment);
	
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
		return composite(CompositeCondition.Operator.AND, other);
	}
	
	public final Condition or(final Condition other)
	{
		return composite(CompositeCondition.Operator.OR, other);
	}
	
	private final Condition composite(final CompositeCondition.Operator operator, final Condition other)
	{
		if(this instanceof Literal)
			if(other instanceof Literal)
				return valueOf(
					(operator==CompositeCondition.Operator.AND)
					? ( ((Literal)this).value && ((Literal)other).value )
					: ( ((Literal)this).value || ((Literal)other).value ));
			else
				return compositeLiteral(operator, (Literal)this, other);
		else
			if(other instanceof Literal)
				return compositeLiteral(operator, (Literal)other, this);
			else
				return compositeFlattening(operator, other);
	}
		
	private static final Condition compositeLiteral(
			final CompositeCondition.Operator operator,
			final Literal literal,
			final Condition other)
	{
		return operator.absorber==literal ? literal : other;
	}
	
	private final Condition compositeFlattening(final CompositeCondition.Operator operator, final Condition other)
	{
		if(this instanceof CompositeCondition && ((CompositeCondition)this).operator==operator)
		{
			final CompositeCondition left = (CompositeCondition)this;
			
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
				c[0] = this;
				System.arraycopy(right.conditions, 0, c, 1, right.conditions.length);
				return new CompositeCondition(operator, c);
			}
			else
			{
				return new CompositeCondition(operator, new Condition[]{this, other});
			}
		}
	}
	
	public static final Literal TRUE  = new Literal(true, "TRUE");
	public static final Literal FALSE = new Literal(false, "FALSE");
	
	static class Literal extends Condition
	{
		final boolean value;
		private final String name;
		
		Literal(final boolean value, final String name)
		{
			this.value = value;
			this.name = name;
		}
		
		@Override
		void append(Statement statment)
		{
			throw new RuntimeException();
		}

		@Override
		void check(TC tc)
		{
			throw new RuntimeException();
		}

		@Override
		public boolean equals(Object o)
		{
			return this==o;
		}

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}

		@Override
		void toString(StringBuilder bf, boolean key, Type defaultType)
		{
			bf.append(name);
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
	
	abstract void toString(StringBuilder bf, boolean key, Type defaultType);

	static final String toStringForValue(final Object o, final boolean key)
	{
		if(o==null)
			return "NULL";
		else if(o instanceof Item)
			return key ? ((Item)o).getCopeID() : o.toString();
		else if(o instanceof Date)
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format((Date)o);
		else if(o instanceof byte[])
			return Arrays.toString((byte[])o);
		else
			return o.toString();
	}
}
