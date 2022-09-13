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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This class is not complete. Extend when needed.
 */
public class ProxyResultSet extends AssertionErrorResultSet
{
	private final ResultSet origin;

	ProxyResultSet(final ResultSet origin)
	{
		this.origin = requireNonNull(origin, "origin");
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException
	{
		return origin.getMetaData();
	}
	@Override
	public boolean next() throws SQLException
	{
		return origin.next();
	}
	@Override
	public void close() throws SQLException
	{
		origin.close();
	}
	@Override
	public String getString(final int columnIndex) throws SQLException
	{
		return origin.getString(columnIndex);
	}
	@Override
	public int getInt(final int columnIndex) throws SQLException
	{
		return origin.getInt(columnIndex);
	}
	@Override
	public long getLong(final int columnIndex) throws SQLException
	{
		return origin.getLong(columnIndex);
	}
	@Override
	public java.sql.Date getDate(final int columnIndex) throws SQLException
	{
		return origin.getDate(columnIndex);
	}
	@Override
	public byte[] getBytes(final int columnIndex) throws SQLException
	{
		return origin.getBytes(columnIndex);
	}
	@Override
	public Object getObject(final int columnIndex) throws SQLException
	{
		return origin.getObject(columnIndex);
	}
	@Override
	public boolean wasNull() throws SQLException
	{
		return origin.wasNull();
	}
}
