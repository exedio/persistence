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


public final class NotCondition extends Condition
{
	private final Condition argument;

	/**
	 * Creates a new NotCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper method.
	 * @see Condition#not()
	 * @throws NullPointerException if <tt>argument</tt> is null.
	 */
	public NotCondition(final Condition argument)
	{
		if(argument==null)
			throw new NullPointerException();
		
		this.argument = argument;
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.append("not(");
		argument.append(bf);
		bf.append(')');
	}

	@Override
	void check(final TC tc)
	{
		argument.check(tc);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof NotCondition))
			return false;
		
		final NotCondition o = (NotCondition)other;
		
		return argument.equals(o.argument);
	}
	
	@Override
	public int hashCode()
	{
		return argument.hashCode() ^ 8432756;
	}

	@Override
	public String toString()
	{
		return "!(" + argument + ')';
	}

	@Override
	String toStringForQueryKey()
	{
		return "!(" + argument.toStringForQueryKey() + ')';
	}
}
