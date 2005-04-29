/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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
		this.type = item.getItemType();
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
		
		type.getModel().getDatabase().store(this);

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
			throw new NestingRuntimeException(e);
		}
		closed = true;
	}

	void delete() throws IntegrityViolationException
	{	
		if(closed)
			throw new RuntimeException();

		type.getModel().getDatabase().delete(type, pk);
		type.removeRow(this);
		closed = true;
	}

}
