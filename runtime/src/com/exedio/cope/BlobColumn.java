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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class BlobColumn extends Column
{
	static final int JDBC_TYPE = Types.BLOB;
	
	BlobColumn(final Table table, final String id, final boolean notNull)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		
		if(table.database.getBlobType()==null)
			throw new RuntimeException("database does not support BLOBs for "+table.id+'.'+id+'.');
	}
	
	final String getDatabaseType()
	{
		return table.database.getBlobType();
	}

	final String getCheckConstraintIfNotNull()
	{
		return null;
	}
	
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		throw new RuntimeException(id);
	}
	
	final String cacheToDatabase(final Object cache)
	{
		throw new RuntimeException(id);
	}
	
	Object cacheToDatabasePrepared(final Object cache)
	{
		throw new RuntimeException(id);
	}

	Object getCheckValue()
	{
		throw new RuntimeException(id);
	}
	
}
