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

import java.sql.Connection;
import java.util.IdentityHashMap;

final class DeletedState extends State
{
	DeletedState(final Transaction transaction, final State original)
	{
		super(original.item, original.updateCount);
		transaction.addInvalidation(item);
	}

	@Override
	Object get(final FunctionField<?> field)
	{
		throw new NoSuchItemException(item);
	}

	@Override
	String get(final StringColumn column) // just for DataVault
	{
		throw new NoSuchItemException(item);
	}

	@Override
	<E> State put(final Transaction transaction, final FunctionField<E> field, final E value)
	{
		throw new NoSuchItemException(item);
	}

	@Override
	State put(final Transaction transaction, final StringColumn column, final String value) // just for DataVault
	{
		throw new NoSuchItemException(item);
	}

	@Override
	State write(final Transaction transaction, final IdentityHashMap<BlobColumn, byte[]> blobs)
	{
		assert blobs==null;
		try
		{
			doDelete(transaction.getConnection(), transaction.connect.executor);
		}
		finally
		{
			discard( transaction );
		}
		return null;
	}

	private void doDelete(final Connection connection, final Executor executor)
	{
		for(Type<?> currentType = type; currentType!=null; currentType = currentType.supertype)
		{
			final Table currentTable = currentType.getTable();
			final Statement st = executor.newStatement();
			st.append("DELETE FROM ").
				append(currentTable.quotedID).
				append(" WHERE ").
				append(currentTable.primaryKey.quotedID).
				append('=').
				appendParameter(pk);

			final IntegerColumn updateCounter = currentTable.updateCounter;
			if(updateCounter!=null)
			{
				st.append(" AND ").
					append(updateCounter.quotedID).
					append('=').
					appendParameter(updateCount.getValue(currentType));
			}

			//System.out.println("deleting "+st.toString());

			executor.updateStrict(connection, null, st);
		}
	}

	@Override
	State delete( final Transaction transaction )
	{
		throw new NoSuchItemException(item);
	}

	@Override
	Object store(final Column column)
	{
		throw new RuntimeException();
	}

	@Override
	Row stealValues()
	{
		throw new RuntimeException();
	}

	@Override
	boolean exists()
	{
		return false;
	}

	@Override
	boolean needsUpdate(final ConnectProperties properties, final Column column)
	{
		throw new RuntimeException("unexpected call");
	}
}
