
package com.exedio.cope.lib;

public final class IntegerAttribute extends Attribute
{
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else
			return (Integer)cell;
	}

	public Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}

	Object cacheToSurface(final Object cache)
	{
		return (Integer)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (Integer)surface;
	}
	
}
