
package com.exedio.cope.lib;

public final class ItemAttribute extends Attribute
{
	Object cellToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof Integer)
		{
			// TODO: This is nonsense, must retrieve the correct Item, but first I need the Type here.
			// TODO: Item objects should not go into item cache, use thier pks instead.
			return cell;
		}
		else
			throw new RuntimeException("cellToCache:"+cell);
	}
}
