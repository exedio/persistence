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

package com.exedio.cope;

import java.util.HashMap;

import bak.pcj.map.IntKeyOpenHashMap;

final class State
{
	final Transaction transaction;
	final Item item;
	final Type type;
	final int pk;

	// TODO: use arrays for String/int/double instead of the HashMap
	private final HashMap cache = new HashMap();
	boolean present;
	boolean exists;
	boolean notExists;
	private boolean dirty;
	private boolean discarded = false;

	protected State(final Transaction transaction, final Item item, final boolean present)
	{
		if(transaction==null)
			throw new NullPointerException();
		
		this.transaction = transaction;
		this.item = item;
		this.type = item.type;
		this.pk = item.pk;
		this.present = present;
		this.dirty = !present;

		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}
	
	Object get(final ObjectAttribute attribute)
	{
		checkExists();

		return attribute.cacheToSurface(cache.get(attribute.getColumn()));
	}
	
	State put(final ObjectAttribute attribute, final Object value)
	{
		checkExists();

		cache.put(attribute.getColumn(), attribute.surfaceToCache(value));
		dirty = true; // TODO: check, whether the written attribute got really a new value
		
		return this;
	}
	
	private void discard()
	{
		if(discarded)
			throw new RuntimeException();

		final IntKeyOpenHashMap rowMap = transaction.rowMaps[type.transientNumber];
		if(rowMap!=null)
			rowMap.remove(pk);
		
		discarded = true;
	}
	
	State write()
		throws UniqueViolationException
	{
		if(discarded)
			throw new RuntimeException();

		if(!dirty)
			return this;
		
		try
		{
			type.getModel().getDatabase().store(this);
		}
		catch(UniqueViolationException e)
		{
			discard();
			throw e;
		}
		catch(RuntimeException e)
		{
			discard();
			throw e;
		}
		catch(Error e)
		{
			discard();
			throw e;
		}

		present = true;
		dirty = false;
		
		return this;
	}
	
	void doesExist()
	{
		if(notExists)
			throw new RuntimeException("does exist");
		
		exists = true;
	}
	
	void doesNotExist()
	{
		if(exists)
			throw new RuntimeException("no such pk"); // TODO use a dedicated runtime exception
		
		notExists = true;
	}
	
	void load(final StringColumn column, final String value)
	{
		if(discarded)
			throw new RuntimeException();

		cache.put(column, value);
	}
	
	void load(final IntegerColumn column, final long value)
	{
		if(discarded)
			throw new RuntimeException();

		cache.put(column, column.longInsteadOfInt ? (Number)new Long(value) : new Integer((int)value));
	}
	
	void load(final DoubleColumn column, final double value)
	{
		if(discarded)
			throw new RuntimeException();

		cache.put(column, new Double(value));
	}
	
	void load(final TimestampColumn column, final long value)
	{
		if(discarded)
			throw new RuntimeException();

		//System.out.println("Row.load TimestampColumn "+value);
		cache.put(column, new Long(value));
	}
	
	Object store(final Column column)
	{
		if(discarded)
			throw new RuntimeException();

		return cache.get(column);
	}

	void close()
	{	
		if(discarded)
			throw new RuntimeException();
		if(dirty)
			throw new RuntimeException();
		discard();
	}

	State delete() throws IntegrityViolationException
	{	
		checkExists();

		type.getModel().getDatabase().delete( item );
		notExists = true;
		
		return this;
	}
	
	private final void checkExists()
	{
		if(discarded)
			throw new RuntimeException();
		if(notExists)
			throw new NoSuchItemException(item);
	}

}
