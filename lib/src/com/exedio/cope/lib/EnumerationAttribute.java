
package com.exedio.cope.lib;

import java.math.BigDecimal;

public final class EnumerationAttribute extends Attribute
{
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else
		{
			// TODO: This is nonsense, must retrieve the correct EnumerationValue
			return new Integer(((BigDecimal)cell).intValue()); // TODO: use ResultSet.getInt() somehow
		}
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return Integer.toString(((EnumerationValue)cache).number);
	}

	Object cacheToSurface(final Object cache)
	{
		return (EnumerationValue)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (EnumerationValue)surface;
	}
	
}
