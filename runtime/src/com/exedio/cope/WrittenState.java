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
import java.util.Objects;

final class WrittenState extends State
{
	private final Row row;

	WrittenState(final Item item, final Row row, final int updateCount)
	{
		super(item, updateCount);
		this.row = row;
	}

	WrittenState(final State original)
	{
		super(original.item, original.updateCountNext());
		row = original.stealValues();
		if(row==null) throw new RuntimeException(original.getClass().getName());
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
		if (Objects.equals(value, get(field)))
			return this;
		else
			return new ModifiedState(transaction, this).put(transaction, field, value);
	}

	@Override
	State put(final Transaction transaction, final StringColumn column, final String value) // just for DataVault
	{
		if (Objects.equals(value, get(column)))
			return this;
		else
			return new ModifiedState(transaction, this).put(transaction, column, value);
	}

	@Override
	State write(final Transaction transaction, final IdentityHashMap<BlobColumn, byte[]> blobs)
	{
		if(blobs!=null && !blobs.isEmpty())
			transaction.connect.database.store(transaction.getConnection(), this, true, false, blobs);

		return this;
	}

	@Override
	State delete(final Transaction transaction)
	{
		return new DeletedState( transaction, this );
	}

	@Override
	Object store(final Column column)
	{
		//throw new RuntimeException();
		// needed for blobs
		return row.get(column);
	}

	@Override
	Row stealValues()
	{
		return new Row(row);
	}

	@Override
	boolean exists()
	{
		return true;
	}
}
