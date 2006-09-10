/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

final class DeletedState extends State
{
	DeletedState(final Transaction transaction, final State original)
	{
		super( original.item );
		transaction.addInvalidation(item);
	}
	
	@Override
	Object get(final FunctionField attribute)
	{
		throw new NoSuchItemException(item);		
	}

	@Override
	State put(Transaction transaction, FunctionField attribute, Object value)
	{
		throw new NoSuchItemException(item);
	}
	
	@Override
	State write(final Transaction transaction, final Map<BlobColumn, byte[]> blobs)
	{
		assert blobs==null;
		try
		{
			type.getModel().getDatabase().delete( transaction.getConnection(), item );
		}
		finally
		{
			discard( transaction );
		}
		return null;
	}

	@Override
	State delete( Transaction transaction )
	{
		throw new NoSuchItemException(item);
	}

	@Override
	Object store(Column column)
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
	public String toStringWithValues()
	{
		return toString();
	}
	
}
