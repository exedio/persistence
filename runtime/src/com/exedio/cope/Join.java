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


public final class Join
{
	public static final int KIND_INNER = 0;
	public static final int KIND_OUTER_LEFT = 1;
	public static final int KIND_OUTER_RIGHT = 2;
	
	final int kind;
	final Type type;
	Condition condition;
	
	Join(final int kind, final Type type, final Condition condition)
	{
		this.kind = kind;
		this.type = type;
		this.condition = condition;

		switch(kind)
		{
			case KIND_INNER:
			case KIND_OUTER_LEFT:
			case KIND_OUTER_RIGHT:
				break;
			default:
				throw new RuntimeException("illegal value for kind: "+String.valueOf(kind));
		}
		if(type==null)
			throw new NullPointerException();
	}
	
	public void setCondition(final Condition condition)
	{
		this.condition = condition;
	}
	
	public final int getKind()
	{
		return kind;
	}
	
	public final Type getType()
	{
		return type;
	}
	
	final String getKindString()
	{
		switch(kind)
		{
			case KIND_INNER: return "inner";
			case KIND_OUTER_LEFT: return "left outer";
			case KIND_OUTER_RIGHT: return "right outer";
			default:
				throw new RuntimeException(String.valueOf(kind));
		}
	}
	
	public final String toString()
	{
		return getKindString() + " join " + type + (condition!=null ? (" on "+condition) : "");
	}

}
