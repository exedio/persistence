
package com.exedio.cope.lib;

public final class StringAttribute extends Attribute
{
	public StringAttribute()
	{
	}
	
	public StringAttribute(final AttributeMapping mapping)
	{
		super(mapping);
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
			return "'" + ((String)cache) + '\'';
	}

	Object cacheToSurface(final Object cache)
	{
		return (String)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (String)surface;
	}
	
}
