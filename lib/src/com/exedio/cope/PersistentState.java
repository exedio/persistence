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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

final class PersistentState extends State implements AbstractDatabase.ResultSetHandler
{
	
	private final Row row;
	private long lastUsageMillis;
	
	PersistentState( final Connection connection, final Item item )
	{
		super( item );
		row = new Row();
		type.getModel().getDatabase().load( connection, this );
		lastUsageMillis = System.currentTimeMillis();
	}
	
	PersistentState( final State original )
	{
		super( original.item );
		row = original.stealValues();
		if(row==null) throw new RuntimeException(original.getClass().getName());
		lastUsageMillis = System.currentTimeMillis();
	}
	
	Object get(ObjectAttribute attribute)
	{
		return attribute.cacheToSurface(row);
	}

	public final State put(final Transaction transaction, final ObjectAttribute attribute, final Object value)
	{
		return new ModifiedState( transaction, this ).put(transaction, attribute, value);
	}

	State write( final Transaction transaction ) throws UniqueViolationException
	{
		return this;
	}

	State delete(Transaction transaction)
	{
		return new DeletedState( transaction, this );
	}

	Object store(final Column column)
	{
		throw new RuntimeException();
	}

	Row stealValues()
	{
		return new Row(row);
	}

	boolean exists()
	{
		return true;
	}
	
	// implementation of ResultSetHandler
	public void run(final ResultSet resultSet) throws SQLException
	{
		if(!resultSet.next())
			throw new NoSuchItemException(item);
		else
		{
			int columnIndex = 1;
			for(Type itype = type; itype!=null; itype = itype.getSupertype())
			{
				for(Iterator i = itype.getTable().getColumns().iterator(); i.hasNext(); )
					((Column)i.next()).load(resultSet, columnIndex++, row);
			}
		}
	}
	
	void notifyUsed()
	{
		lastUsageMillis = System.currentTimeMillis();
	}
	
	long getLastUsageMillis()
	{
		return lastUsageMillis;
	}
	
	public String toStringWithValues()
	{
		return toString()+row.toString();
	}
	
}
