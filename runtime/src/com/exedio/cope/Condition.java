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


public abstract class Condition
{
	abstract void append(Statement statment);
	
	abstract void check(TC tc);

	public final NotCondition not()
	{
		return new NotCondition(this);
	}
	
	public final CompositeCondition and(final Condition other)
	{
		return composite(CompositeCondition.Operator.AND, other);
	}
	
	public final CompositeCondition or(final Condition other)
	{
		return composite(CompositeCondition.Operator.OR, other);
	}
	
	private final CompositeCondition composite(final CompositeCondition.Operator operator, final Condition other)
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

	/**
	 * Does the same as {@link #toString()} with one exception:
	 * it calls {@link #toStringForQueryKey(Object)}
	 * instead of {@link Object#toString()} for
	 * literal values in conditions.
	 * This avoids errors, if {@link Object#toString()} of
	 * items is implemented in a way, that fails without
	 * an active transaction bound to the current thread.
	 * Such errors would occur in the query cache statistics tab
	 * of the COPE Console.
	 */
	abstract String toStringForQueryKey();
	
	static final String toStringForQueryKey(final Object o)
	{
		if(o==null)
			return "NULL";
		else if(o instanceof Item)
			return ((Item)o).getCopeID();
		else
			return o.toString();
	}
}
