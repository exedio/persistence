/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import java.util.IdentityHashMap;
import java.util.Map;

final class Entity
{
	private final Transaction transaction;
	private State state;

	Entity( final Transaction transaction, final State state )
	{
		this.transaction = transaction;
		this.state = state;
	}

	Object get(final FunctionField<?> field)
	{
		return state.get(field);
	}

	String get(final StringColumn column) // just for DataVault
	{
		return state.get(column);
	}

	<E> void put(final FunctionField<E> field, final E value)
	{
		state = state.put(transaction, field, value);
	}

	void put(final StringColumn column, final String value) // just for DataVault
	{
		state = state.put(transaction, column, value);
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // TODO dont know how
	void put(final FieldValues fieldValues)
	{
		for(final Map.Entry<Field<?>, Object> e : fieldValues.dirtySet())
		{
			final Field<?> f = e.getKey();
			if(f instanceof FunctionField)
				put((FunctionField)f, e.getValue());
			else
				((DataField)f).put(this, (DataField.Value)e.getValue(), fieldValues.getBackingItem()); // just for DataVault
		}
	}

	void write(final IdentityHashMap<BlobColumn, byte[]> blobs)
	{
		boolean discard = true;
		try
		{
			state = state.write(transaction, blobs);
			discard = false;
		}
		finally
		{
			if(discard)
				transaction.connect.itemCache.remove(state.item);
		}
	}

	void delete()
	{
		state = state.delete( transaction );
	}

	Item getItem()
	{
		return state.item;
	}

	boolean exists()
	{
		return state.exists();
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	int getUpdateCount()
	{
		return state.updateCount;
	}

	@Override
	public String toString()
	{
		return "Entity["+(state==null?"no state":state.toString())+"]";
	}
}
