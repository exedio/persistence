
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
		return cache==null ? null : type.getItem(((Integer)cache).intValue());
	}
		
	public Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Integer(((Item)surface).pk);
	}
	
}
