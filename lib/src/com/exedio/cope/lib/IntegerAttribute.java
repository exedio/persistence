
package com.exedio.cope.lib;

import java.math.BigDecimal;

public final class IntegerAttribute extends Attribute
{
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else
			return new Integer(((BigDecimal)cell).intValue()); // TODO: use ResultSet.getInt() somehow
	}

	Object cacheToDatabase(final Object cache)
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
