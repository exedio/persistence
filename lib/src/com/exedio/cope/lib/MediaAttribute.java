
package com.exedio.cope.lib;

public final class MediaAttribute extends Attribute
{
	public MediaAttribute()
	{
	}
	
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof String)
			return cell;
		else
			throw new RuntimeException("cellToCache:"+cell);
	}

	public Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			throw new RuntimeException("not yet implemented");
	}
}
