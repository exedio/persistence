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

final class CreatedState extends State
{
	private Row row;

	CreatedState(final Transaction transaction, final Item item)
	{
		super(item, UpdateCount.initial(item.getCopeType()));
		transaction.addInvalidation(item);
		row = new Row(item.type);
	}

	@Override
	Object get(final FunctionField<?> field)
	{
		return field.get(row);
	}

	@Override
	String get(final StringColumn column) // just for DataVault
	{
		return (String)row.get(column);
	}

	@Override
	<E> State put(final Transaction transaction, final FunctionField<E> field, final E value)
	{
		field.set(row, value);
		return this;
	}

	@Override
	State put(final Transaction transaction, final StringColumn column, final String value) // just for DataVault
	{
		row.put(column, value);
		return this;
	}

	@Override
	State delete(final Transaction transaction)
	{
		discard( transaction );
		return null;
	}

	@Override
	State write(final Transaction transaction, final IdentityHashMap<BlobColumn, byte[]> blobs)
	{
		boolean discard = true;
		final UpdateCount nextUpdateCount;
		try
		{
			nextUpdateCount = transaction.connect.database.store(transaction.getConnection(), this, false, true, blobs);
			discard = false;
		}
		finally
		{
			if(discard)
				discard( transaction );
		}
		return new WrittenState(this, nextUpdateCount);
	}

	@Override
	Object store(final Column column)
	{
		return row.get(column);
	}

	@Override
	Row stealValues()
	{
		final Row result = row;
		row = null;
		return result;
	}

	@Override
	boolean exists()
	{
		return true;
	}

	@Override
	boolean needsUpdate(final ConnectProperties properties, final Column column)
	{
		throw new RuntimeException("unexpected call");
	}
}
