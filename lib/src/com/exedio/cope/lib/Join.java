
package com.exedio.cope.lib;

import com.exedio.cope.lib.search.Condition;

final public class Join
{
	final Type type;
	final Condition condition;
	
	Join(final Type type, final Condition condition)
	{
		this.type = type;
		this.condition = condition;

		if(type==null)
			throw new NullPointerException();
		if(condition==null)
			throw new NullPointerException();
	}
	
	public final Type getType()
	{
		return type;
	}
	
	public final String toString()
	{
		return "join "+type+" on "+condition;
	}

}
