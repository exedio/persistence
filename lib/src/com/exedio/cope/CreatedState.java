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
import java.util.Map;

final class CreatedState extends State
{
	
	// TODO: use arrays for String/int/double instead of the HashMap
	private HashMap cache = new HashMap();
	
	CreatedState(final Transaction transaction, final Item item)
	{
		super( item );
		transaction.addInvalidation( item.type, item.pk );
	}
	
	Object get(ObjectAttribute attribute)
	{
		return attribute.cacheToSurface(cache.get(attribute.getColumn()));
	}

	public final State put(Transaction transaction, ObjectAttribute attribute, Object value)
	{
		cache.put(attribute.getColumn(), attribute.surfaceToCache(value));
		return this;
	}

	State delete(Transaction transaction)
	{
		discard( transaction );
		return null;
	}

	State write(Transaction transaction) throws UniqueViolationException
	{
		try
		{
			type.getModel().getDatabase().store( transaction.getConnection(), this, false );
			return new PersistentState( this );
		}
		catch ( UniqueViolationException e )
		{
			discard( transaction );
			throw e;
		}
		catch ( RuntimeException e )
		{
			discard( transaction );
			throw e;
		}
		catch ( Error e )
		{
			discard( transaction );
			throw e;
		}
	}

	Object store(final Column column)
	{
		return cache.get(column);
	}
	
	Map stealValues()
	{
		Map result = cache;
		cache = null;
		return result;
	}

	boolean exists()
	{
		return true;
	}
}
