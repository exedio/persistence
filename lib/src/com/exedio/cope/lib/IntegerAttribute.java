
package com.exedio.cope.lib;

public final class IntegerAttribute extends Attribute
{
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof Integer)
			return cell;
		else
			throw new RuntimeException("cellToCache:"+cell);
	}

	public Object cache2Database(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}
}
