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


/**
 * A condition matching a fulltext index. EXPERIMENTAL!!!
 *
 * Cope has no support for creating fulltext indexes
 * together with the schema yet,
 * so you have to do this manually.
 *
 * MySQL: create fulltext index <index_name> on <table_name>(<column_name>)
 * Oracle: create index <index_name> ON <table_name>(<column_name>) indextype is CTXSYS.CONTEXT
 *
 * @author Ralf Wiebicke
 */
public final class MatchCondition extends Condition
{
	public final StringFunction function;
	public final String value;

	/**
	 * Creates a new MatchCondition. EXPERIMENTAL!!!
	 */
	public MatchCondition(final StringFunction function, final String value)
	{
		this.function = function;
		this.value = value;

		if(function==null)
			throw new NullPointerException("function must ot be null");
		if(value==null)
			throw new NullPointerException("value must ot be null");
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.appendMatch(function, value);
	}

	@Override
	void check(final Query query)
	{
		check(function, query);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof MatchCondition))
			return false;
		
		final MatchCondition o = (MatchCondition)other;
		
		return function.equals(o.function) && value.equals(o.value);
	}
	
	@Override
	public int hashCode()
	{
		return function.hashCode() ^ value.hashCode() ^ 1872643;
	}

	@Override
	public String toString()
	{
		return function.toString() + " matches '" + value + '\'';
	}
	
	@Override
	String toStringForQueryKey()
	{
		return function.toString() + " matches '" + toStringForQueryKey(value) + '\'';
	}
}
