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

import static com.exedio.cope.Executor.NO_SUCH_ROW;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.Executor.ResultSetHandler;

final class BlobColumn extends Column
{
	final long maximumLength;
	final long lengthFactor;
	
	BlobColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final long maximumLength)
	{
		super(table, field, id, false, optional);
		this.maximumLength = maximumLength;
		this.lengthFactor = table.database.dialect.getBlobLengthFactor();
		
		if(table.database.dialect.getBlobType(maximumLength)==null)
			throw new RuntimeException("database does not support BLOBs for "+table.id+'.'+id+'.');
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getBlobType(maximumLength);
	}

	@Override
	final String getCheckConstraintIfNotNull()
	{
		return "LENGTH(" + quotedID + ")<=" + (maximumLength*lengthFactor);
	}
	
	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
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
	

	byte[] load(final Connection connection, final Executor executor, final Item item)
	{
		// TODO reuse code in load blob methods
		final Table table = this.table;
		final Statement bf = executor.newStatement();
		bf.append("select ").
			append(quotedID).
			append(" from ").
			append(table.quotedID).
			append(" where ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
			
		return executor.query(connection, bf, null, false, new ResultSetHandler<byte[]>()
		{
			public byte[] handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				return executor.dialect.getBytes(resultSet, 1);
			}
		});
	}
	
	void load(final Connection connection, final Executor executor, final Item item, final OutputStream data, final DataField field)
	{
		final Table table = this.table;
		final Statement bf = executor.newStatement();
		bf.append("select ").
			append(quotedID).
			append(" from ").
			append(table.quotedID).
			append(" where ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
		
		executor.query(connection, bf, null, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				executor.dialect.fetchBlob(resultSet, 1, item, data, field);
				
				return null;
			}
		});
	}
	
	long loadLength(final Connection connection, final Executor executor, final Item item)
	{
		final Table table = this.table;
		final Statement bf = executor.newStatement();
		bf.append("select length(").
			append(quotedID).
			append(") from ").
			append(table.quotedID).
			append(" where ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
			
		return executor.query(connection, bf, null, false, new ResultSetHandler<Long>()
		{
			public Long handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
	
				final Object o = resultSet.getObject(1);
				if(o==null)
					return -1l;
	
				long result = ((Number)o).longValue();
				final long factor = lengthFactor;
				if(factor!=1)
				{
					if(result%factor!=0)
						throw new RuntimeException("not dividable "+result+'/'+factor);
					result /= factor;
				}
				return result;
			}
		});
	}
	
	void store(
			final Connection connection, final Executor executor, final Item item,
			final DataField.Value data, final DataField field)
	{
		final Table table = this.table;
		final Statement bf = executor.newStatement();
		bf.append("update ").
			append(table.quotedID).
			append(" set ").
			append(quotedID).
			append('=');
		
		if(data!=null)
			bf.appendParameterBlob(data.asArray(field, item));
		else
			bf.append("NULL");
		
		bf.append(" where ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
		
		//System.out.println("storing "+bf.toString());
		executor.update(connection, bf, true);
	}
}
