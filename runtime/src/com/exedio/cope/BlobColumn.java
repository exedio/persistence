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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class BlobColumn extends Column
{
	static final int JDBC_TYPE = Types.BLOB;
	
	final long maximumLength;
	
	BlobColumn(final Table table, final String id, final boolean optional, final long maximumLength)
	{
		super(table, id, false, optional, JDBC_TYPE);
		this.maximumLength = maximumLength;
		
		if(table.database.getBlobType(maximumLength)==null)
			throw new RuntimeException("database does not support BLOBs for "+table.id+'.'+id+'.');
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.getBlobType(maximumLength);
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
	{
		return "LENGTH(" + protectedID + ")<=" + (maximumLength*table.database.blobLengthFactor);
	}
	
	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		throw new RuntimeException(id);
	}
	
	@Override
	final String cacheToDatabase(final Object cache)
	{
		throw new RuntimeException(id);
	}
	
	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		throw new RuntimeException(id);
	}

	@Override
	Object getCheckValue()
	{
		throw new RuntimeException(id);
	}
	
}
