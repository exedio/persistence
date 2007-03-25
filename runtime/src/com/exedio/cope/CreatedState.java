/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Map;

final class CreatedState extends State
{
	private Row row = new Row();
	
	CreatedState(final Transaction transaction, final Item item)
	{
		super( item );
		transaction.addInvalidation(item);
	}
	
	@Override
	Object get(FunctionField field)
	{
		return field.get(row);
	}

	@Override
	<E> State put(final Transaction transaction, final FunctionField<E> field, final E value)
	{
		field.set(row, value);
		return this;
	}

	@Override
	State delete(Transaction transaction)
	{
		discard( transaction );
		return null;
	}

	@Override
	State write(final Transaction transaction, final Map<BlobColumn, byte[]> blobs)
	{
		try
		{
			type.getModel().getDatabase().store(transaction.getConnection(), this, false, blobs);
			return new WrittenState(this);
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
	public String toStringWithValues()
	{
		return toString()+row.toString();
	}
	
}
