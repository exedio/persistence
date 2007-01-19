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

import java.util.Collection;
import java.util.List;

public final class CompositeCondition extends Condition
{
	public final Operator operator;
	final Condition[] conditions;

	/**
	 * @throws NullPointerException if <tt>conditions==null</tt>
	 * @throws IllegalArgumentException if <tt>conditions.size()==0</tt>
	 */
	public CompositeCondition(final Operator operator, final List<? extends Condition> conditions)
	{
		this(operator, conditions.toArray(new Condition[conditions.size()]));
	}
	
	/**
	 * @throws NullPointerException if <tt>conditions==null</tt>
	 * @throws IllegalArgumentException if <tt>conditions.length==0</tt>
	 */
	public CompositeCondition(final Operator operator, final Condition[] conditions)
	{
		if(operator==null)
			throw new NullPointerException("operator must not be null");
		if(conditions==null)
			throw new NullPointerException("conditions must not be null");
		if(conditions.length==0)
			throw new IllegalArgumentException("composite condition must have at least one subcondition");
		for(int i = 0; i<conditions.length; i++)
			if(conditions[i]==null)
				throw new NullPointerException("condition " + i + " must not be null");
				

		this.operator = operator;
		this.conditions = conditions;
	}

	@Override
	void append(final Statement bf)
	{
		bf.append('(');
		conditions[0].append(bf);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(operator.sql);
			conditions[i].append(bf);
		}
		bf.append(')');
	}

	@Override
	void check(final TC tc)
	{
		for(int i = 0; i<conditions.length; i++)
			conditions[i].check(tc);
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompositeCondition))
			return false;
		
		final CompositeCondition o = (CompositeCondition)other;
		
		if(!operator.equals(o.operator) || conditions.length!=o.conditions.length)
			return false;
		
		for(int i = 0; i<conditions.length; i++)
		{
			if(!conditions[i].equals(o.conditions[i]))
				return false;
		}

		return true;
	}
	
	@Override
	public int hashCode()
	{
		int result = operator.hashCode();
		
		for(int i = 0; i<conditions.length; i++)
			result = (31*result) + conditions[i].hashCode(); // may not be commutative

		return result;
	}

	@Override
	void toString(final StringBuffer bf, final boolean key)
	{
		bf.append('(');
		conditions[0].toString(bf, key);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(' ').
				append(operator).
				append(' ');
			conditions[i].toString(bf, key);
		}
		bf.append(')');
	}

	public static final <E> CompositeCondition in(final Function<E> function, final Collection<E> values)
	{
		final Condition[] result = new Condition[values.size()];

		int i = 0;
		for(E value : values)
			result[i++] = function.equal(value);
		
		return new CompositeCondition(Operator.OR, result);
	}
	
	public static enum Operator
	{
		AND(" and "),
		OR(" or ");
		
		private final String sql;
		
		private Operator(final String sql)
		{
			this.sql = sql;
		}
	}
}
