
package com.exedio.cope.lib;

public final class IntegerAttribute extends Attribute
{
	Object cellToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof Integer)
			return cell;
		else
			throw new RuntimeException("cellToCache:"+cell);
	}
}
