
package com.exedio.cope.lib;

public final class ItemAttribute extends Attribute
{
	private Type type;

	public void initialize(final String name, final boolean readOnly, final boolean notNull,
								  final Type type)
	{
		super.initialize(name, readOnly, notNull);
		this.type = type;
	}
	
	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getType()
	{
		return this.type;
	}

	Object databaseToCache(final Object cell)
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

	public Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return Integer.toString(((Item)cache).pk);
	}

	Object cacheToSurface(final Object cache)
	{
		return (Item)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (Item)surface;
	}
	
}
