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


public final class Join
{
	static enum Kind
	{
		INNER("join "),
		OUTER_LEFT("left join "),
		OUTER_RIGHT("right join ");
		
		final String sql;
		
		Kind(final String sql)
		{
			this.sql = sql;
		}
	}
	
	final int index;
	final Kind kind;
	final Type type;
	Condition condition;
	
	Join(final int index, final Kind kind, final Type type, final Condition condition)
	{
		this.index = index;
		this.kind = kind;
		this.type = type;
		this.condition = condition;

		if(kind==null)
			throw new NullPointerException("kind must not be null");
		if(type==null)
			throw new NullPointerException("type must not be null");
	}
	
	public void setCondition(final Condition condition)
	{
		this.condition = condition;
	}
	
	public Kind getKind()
	{
		return kind;
	}
	
	public Type getType()
	{
		return type;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		final Join o = (Join)other;
		return
			kind==o.kind &&
			type==o.type &&
			condition==null ? o.condition==null : condition.equals(o.condition);
	}
	
	@Override
	public int hashCode()
	{
		return
			kind.hashCode() ^
			type.hashCode() ^
			(condition==null ? 0 : condition.hashCode());
	}
	
	@Override
	public String toString()
	{
		return kind.sql + type + ' ' + getToStringAlias() + (condition!=null ? (" on "+condition) : "");
	}

	String getToStringAlias()
	{
		return String.valueOf(Character.toLowerCase(type.id.charAt(0))) + (index+1);
	}
}
