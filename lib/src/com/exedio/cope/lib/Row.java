
package com.exedio.cope.lib;

import java.util.HashMap;

final class Row
{
	final Item item;
	final Type type;
	final int pk;

	// TODO: use arrays for String/int/double instead of the HashMap
	private final HashMap cache = new HashMap();
	boolean present;
	private boolean dirty = false;
	private boolean closed = false;

	protected Row(final Item item, final boolean present)
	{
		this.item = item;
		this.type = item.getType();
		this.pk = item.pk;
		this.present = present;
		type.putRow(this);
		//System.out.println("created row "+type+" "+pk);

		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}
	
	
	Object get(final ObjectAttribute attribute)
	{
		if(closed)
			throw new RuntimeException();

		return attribute.cacheToSurface(cache.get(attribute.getMainColumn()));
	}
	
	Object get(final Column column)
	{
		if(column==null)
			throw new NullPointerException();
		if(closed)
			throw new RuntimeException();

		return cache.get(column);
	}
	
	void put(final AttributeValue[] attributeValues)
	{
		if(closed)
			throw new RuntimeException();

		for(int i = 0; i<attributeValues.length; i++)
		{
			final ObjectAttribute attribute = attributeValues[i].attribute;
			cache.put(attribute.getMainColumn(), attribute.surfaceToCache(attributeValues[i].value));
		}
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void put(final ObjectAttribute attribute, final Object value)
	{
		if(closed)
			throw new RuntimeException();

		cache.put(attribute.getMainColumn(), attribute.surfaceToCache(value));
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void put(final Column column, final Object value)
	{
		if(column==null)
			throw new NullPointerException();
		if(closed)
			throw new RuntimeException();

		cache.put(column, value);
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	void write()
		throws UniqueViolationException
	{
		if(closed)
			throw new RuntimeException();

		if(!dirty)
			return;
		
		Database.theInstance.store(this);

		present = true;
		dirty = false;
	}
	
	void load(final StringColumn column, final String value)
	{
		if(closed)
			throw new RuntimeException();

		cache.put(column, value);
	}
	
	void load(final IntegerColumn column, final long value)
	{
		if(closed)
			throw new RuntimeException();

		cache.put(column, column.longInsteadOfInt ? (Number)new Long(value) : new Integer((int)value));
	}
	
	void load(final DoubleColumn column, final double value)
	{
		if(closed)
			throw new RuntimeException();

		cache.put(column, new Double(value));
	}
	
	void load(final TimestampColumn column, final long value)
	{
		if(closed)
			throw new RuntimeException();

		//System.out.println("Row.load TimestampColumn "+value);
		cache.put(column, new Long(value));
	}
	
	Object store(final Column column)
	{
		if(closed)
			throw new RuntimeException();

		return cache.get(column);
	}

	void close()
	{	
		if(closed)
			throw new RuntimeException();

		type.removeRow(this);
		try
		{
			write();
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
		closed = true;
	}

}
