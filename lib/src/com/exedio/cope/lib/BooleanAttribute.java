
package com.exedio.cope.lib;

public final class BooleanAttribute extends Attribute
{
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof Integer)
		{
			switch(((Integer)cell).intValue())
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cellToCache:"+cell);
			}
		}
		else
			throw new RuntimeException("cellToCache:"+cell);
	}
	
	public Object cache2Database(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Boolean)cache).booleanValue() ? "1" : "0";
	}
}
