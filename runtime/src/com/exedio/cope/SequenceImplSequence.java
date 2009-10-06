/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.sql.SQLException;

import com.exedio.cope.util.Pool;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;

final class SequenceImplSequence implements SequenceImpl
{
	private final int start;
	private final Database database;
	private final Pool<Connection> connectionPool;
	private final String name;

	SequenceImplSequence(final IntegerColumn column, final int start, final Database database)
	{
		if(!database.supportsSequences)
			throw new RuntimeException("database does not support sequences");
		
		this.start = start;
		this.database = database;
		this.connectionPool = database.connectionPool;
		this.name = database.makeName(column.table.id + '_' + column.id + "_Seq");
	}
	
	public void makeSchema(final Schema schema)
	{
		new Sequence(schema, name, start);
	}

	public int next() throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.get();
			connection.setAutoCommit(false);
			final Integer result =
				database.dialect.nextSequence(database, connection, name);
			connection.commit();
			return result;
		}
		finally
		{
			if(connection!=null)
				connectionPool.put(connection);
		}
	}
	
	public int getNext() throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.get();
			connection.setAutoCommit(false);
			final Integer result =
				database.dialect.getNextSequence(database, connection, name);
			connection.commit();
			return result;
		}
		finally
		{
			if(connection!=null)
				connectionPool.put(connection);
		}
	}
	
	public void flush()
	{
		// empty
	}
}
