
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class Row
{
	final Type type;

	final int pk;

	// TODO: use arrays for String/int/double instead of the HashMap
	private final HashMap cache = new HashMap();
	boolean present;
	private boolean dirty = false;

	protected Row(final Type type, final int pk, final boolean present)
	{
		this.type = type;
		this.pk = pk;
		this.present = present;
		//System.out.println("created row "+type+" "+pk);
	}
	
	
	Object get(final Attribute attribute)
	{
		return attribute.cacheToSurface(cache.get(attribute.getMainColumn()));
	}
	
	Object get(final Column column)
	{
		return cache.get(column);
	}
	
	void put(final AttributeValue[] attributeValues)
	{
		for(int i = 0; i<attributeValues.length; i++)
		{
			final Attribute attribute = attributeValues[i].attribute;
			cache.put(attribute.getMainColumn(), attribute.surfaceToCache(attributeValues[i].value));
		}
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void put(final Attribute attribute, final Object value)
	{
		cache.put(attribute.getMainColumn(), attribute.surfaceToCache(value));
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void put(final Column column, final Object value)
	{
		cache.put(column, value);
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void write()
		throws UniqueViolationException
	{
		if(!dirty)
			return;
		
		Database.theInstance.store(this);

		present = true;
		dirty = false;
	}
	
	void load(final StringColumn column, final String value)
	{
		cache.put(column, value);
	}
	
	void load(final IntegerColumn column, final int value)
	{
		cache.put(column, new Integer(value));
	}
	
	Object store(final Column column)
	{
		return cache.get(column);
	}
	
}
