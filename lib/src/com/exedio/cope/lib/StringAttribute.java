
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
