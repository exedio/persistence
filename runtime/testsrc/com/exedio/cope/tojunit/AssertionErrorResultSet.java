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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

@SuppressWarnings("unused")
class AssertionErrorResultSet implements ResultSet
{
	public <T> T unwrap(final Class<T> iface) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isWrapperFor(final Class<?> iface) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean next() throws SQLException
	{
		throw new AssertionError();
	}

	public void close() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean wasNull() throws SQLException
	{
		throw new AssertionError();
	}

	public String getString(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean getBoolean(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public byte getByte(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public short getShort(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public int getInt(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public long getLong(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public float getFloat(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public double getDouble(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	@Deprecated
	public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException
	{
		throw new AssertionError();
	}

	public byte[] getBytes(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Date getDate(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Time getTime(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Timestamp getTimestamp(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public InputStream getAsciiStream(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	@Deprecated
	public InputStream getUnicodeStream(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public InputStream getBinaryStream(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public String getString(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean getBoolean(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public byte getByte(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public short getShort(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public int getInt(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public long getLong(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public float getFloat(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public double getDouble(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	@Deprecated
	public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException
	{
		throw new AssertionError();
	}

	public byte[] getBytes(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Date getDate(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Time getTime(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Timestamp getTimestamp(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public InputStream getAsciiStream(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	@Deprecated
	public InputStream getUnicodeStream(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public InputStream getBinaryStream(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public SQLWarning getWarnings() throws SQLException
	{
		throw new AssertionError();
	}

	public void clearWarnings() throws SQLException
	{
		throw new AssertionError();
	}

	public String getCursorName() throws SQLException
	{
		throw new AssertionError();
	}

	public ResultSetMetaData getMetaData() throws SQLException
	{
		throw new AssertionError();
	}

	public Object getObject(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Object getObject(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public int findColumn(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Reader getCharacterStream(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Reader getCharacterStream(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public BigDecimal getBigDecimal(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public BigDecimal getBigDecimal(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isBeforeFirst() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isAfterLast() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isFirst() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isLast() throws SQLException
	{
		throw new AssertionError();
	}

	public void beforeFirst() throws SQLException
	{
		throw new AssertionError();
	}

	public void afterLast() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean first() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean last() throws SQLException
	{
		throw new AssertionError();
	}

	public int getRow() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean absolute(final int row) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean relative(final int rows) throws SQLException
	{
		throw new AssertionError();
	}

	public boolean previous() throws SQLException
	{
		throw new AssertionError();
	}

	public void setFetchDirection(final int direction) throws SQLException
	{
		throw new AssertionError();
	}

	public int getFetchDirection() throws SQLException
	{
		throw new AssertionError();
	}

	public void setFetchSize(final int rows) throws SQLException
	{
		throw new AssertionError();
	}

	public int getFetchSize() throws SQLException
	{
		throw new AssertionError();
	}

	public int getType() throws SQLException
	{
		throw new AssertionError();
	}

	public int getConcurrency() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean rowUpdated() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean rowInserted() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean rowDeleted() throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNull(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBoolean(final int columnIndex, final boolean x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateByte(final int columnIndex, final byte x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateShort(final int columnIndex, final short x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateInt(final int columnIndex, final int x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateLong(final int columnIndex, final long x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateFloat(final int columnIndex, final float x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateDouble(final int columnIndex, final double x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateString(final int columnIndex, final String x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBytes(final int columnIndex, final byte[] x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateDate(final int columnIndex, final Date x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateTime(final int columnIndex, final Time x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateObject(final int columnIndex, final Object x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNull(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBoolean(final String columnLabel, final boolean x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateByte(final String columnLabel, final byte x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateShort(final String columnLabel, final short x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateInt(final String columnLabel, final int x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateLong(final String columnLabel, final long x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateFloat(final String columnLabel, final float x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateDouble(final String columnLabel, final double x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateString(final String columnLabel, final String x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBytes(final String columnLabel, final byte[] x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateDate(final String columnLabel, final Date x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateTime(final String columnLabel, final Time x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateObject(final String columnLabel, final Object x) throws SQLException
	{
		throw new AssertionError();
	}

	public void insertRow() throws SQLException
	{
		throw new AssertionError();
	}

	public void updateRow() throws SQLException
	{
		throw new AssertionError();
	}

	public void deleteRow() throws SQLException
	{
		throw new AssertionError();
	}

	public void refreshRow() throws SQLException
	{
		throw new AssertionError();
	}

	public void cancelRowUpdates() throws SQLException
	{
		throw new AssertionError();
	}

	public void moveToInsertRow() throws SQLException
	{
		throw new AssertionError();
	}

	public void moveToCurrentRow() throws SQLException
	{
		throw new AssertionError();
	}

	public Statement getStatement() throws SQLException
	{
		throw new AssertionError();
	}

	public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException
	{
		throw new AssertionError();
	}

	public Ref getRef(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Blob getBlob(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Clob getClob(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Array getArray(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException
	{
		throw new AssertionError();
	}

	public Ref getRef(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Blob getBlob(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Clob getClob(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Array getArray(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Date getDate(final int columnIndex, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public Date getDate(final String columnLabel, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public Time getTime(final int columnIndex, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public Time getTime(final String columnLabel, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException
	{
		throw new AssertionError();
	}

	public URL getURL(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public URL getURL(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateRef(final int columnIndex, final Ref x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateRef(final String columnLabel, final Ref x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final int columnIndex, final Blob x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final String columnLabel, final Blob x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final int columnIndex, final Clob x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final String columnLabel, final Clob x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateArray(final int columnIndex, final Array x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateArray(final String columnLabel, final Array x) throws SQLException
	{
		throw new AssertionError();
	}

	public RowId getRowId(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public RowId getRowId(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateRowId(final int columnIndex, final RowId x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateRowId(final String columnLabel, final RowId x) throws SQLException
	{
		throw new AssertionError();
	}

	public int getHoldability() throws SQLException
	{
		throw new AssertionError();
	}

	public boolean isClosed() throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNString(final int columnIndex, final String nString) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNString(final String columnLabel, final String nString) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException
	{
		throw new AssertionError();
	}

	public NClob getNClob(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public NClob getNClob(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public SQLXML getSQLXML(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public SQLXML getSQLXML(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException
	{
		throw new AssertionError();
	}

	public String getNString(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public String getNString(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public Reader getNCharacterStream(final int columnIndex) throws SQLException
	{
		throw new AssertionError();
	}

	public Reader getNCharacterStream(final String columnLabel) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final int columnIndex, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateClob(final String columnLabel, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final int columnIndex, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public void updateNClob(final String columnLabel, final Reader reader) throws SQLException
	{
		throw new AssertionError();
	}

	public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException
	{
		throw new AssertionError();
	}

	public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException
	{
		throw new AssertionError();
	}
}