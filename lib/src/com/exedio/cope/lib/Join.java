
package com.exedio.cope.lib;

import com.exedio.cope.lib.search.Condition;

final public class Join
{
	public static final int KIND_INNER = 0;
	public static final int KIND_OUTER_LEFT = 1;
	public static final int KIND_OUTER_RIGHT = 2;
	
	final int kind;
	final Type type;
	final Condition condition;
	
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
		if(condition==null)
			throw new NullPointerException();
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
		return getKindString() + " join "+type+" on "+condition;
	}

}
