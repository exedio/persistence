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

abstract class State
{
	final Item item;
	final Type<?> type;
	final long pk;
	final int updateCount;

	protected State(final Item item, final int updateCount)
	{
		this.item = item;
		this.type = item.type;
		this.pk = item.pk;
		this.updateCount = updateCount;

		assert PK.isValid(pk) : pk;
		assert updateCount>=0 : updateCount;
	}

	int updateCountNext()
	{
		return updateCount!=Integer.MAX_VALUE ? updateCount+1 : 0;
	}

	abstract Object get(FunctionField<?> field);
	abstract String get(StringColumn column); // just for DataVault

	abstract <E> State put(Transaction transaction, FunctionField<E> field, E value);
	abstract State put(Transaction transaction, StringColumn column, String value); // just for DataVault

	abstract State write(Transaction transaction, IdentityHashMap<BlobColumn, byte[]> blobs);


	abstract Object store(final Column column);

	abstract State delete( Transaction transaction );

	void discard( final Transaction transaction )
	{
		transaction.removeEntity( item );
	}

	abstract Row stealValues();

	abstract boolean exists();

	@Override
	public final String toString()
	{
		return getClass().getName()+"-"+item.getCopeID();
	}
}
