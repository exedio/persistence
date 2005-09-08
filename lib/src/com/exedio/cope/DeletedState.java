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

import java.util.Map;


final class DeletedState extends State
{
	DeletedState(final Transaction transaction, final State original)
	{
		super( original.item );
		transaction.addInvalidation( item.type, item.pk );
	}
	
	Object get(final ObjectAttribute attribute)
	{
		throw new NoSuchItemException(item);		
	}

	State put(Transaction transaction, ObjectAttribute attribute, Object value)
	{
		throw new NoSuchItemException(item);
	}
	
	State write( Transaction transaction )
	{
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

	State delete( Transaction transaction )
	{
		throw new NoSuchItemException(item);
	}

	Object store(Column column)
	{
		throw new RuntimeException();
	}

	Map stealValues()
	{
		throw new RuntimeException();
	}

	boolean exists()
	{
		return false;
	}
}
