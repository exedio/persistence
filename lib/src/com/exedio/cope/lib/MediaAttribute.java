
package com.exedio.cope.lib;

public final class MediaAttribute extends Attribute
{
	public MediaAttribute()
	{
	}
	
	Object cellToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof String)
			return cell;
		else
			throw new RuntimeException("cellToCache:"+cell);
	}
}
