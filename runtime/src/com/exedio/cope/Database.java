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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import bak.pcj.list.IntList;

import com.exedio.dsmf.Driver;
import com.exedio.dsmf.Schema;

interface Database
{
	
	void load(Connection connection, PersistentState state);
	
	String makeName(String longName);
	String makeName(String prefix, String longName);
	
	Driver getDriver();
	ConnectionPool getConnectionPool();
	
	void addTable( Table table );
	void addUniqueConstraint( String databaseID, UniqueConstraint constraint );
	
	java.util.Properties getTableOptions();

	boolean supportsCheckConstraints();
	boolean supportsGetBytes();
	boolean fakesSupportReadCommitted();
	
	String getIntegerType(int precision);
	String getDoubleType(int precision);
	String getStringType(int maxLength);
	String getDayType();
	
	/**
	 * Returns a column type suitable for storing timestamps
	 * with milliseconds resolution.
	 * This method may return null,
	 * if the database does not support such a column type.
	 * The framework will then fall back to store the number of milliseconds.
	 */
	String getDateTimestampType();
	
	String getBlobType(long maximumLength);
	
	int getBlobLengthFactor();
	
	boolean supportsEmptyStrings();
	boolean supportsRightOuterJoins();

	void appendMatchClause(Statement bf, StringFunction function, String value);
	
	ArrayList<Object> search(Connection connection, Query query, boolean doCountOnly);
	
	PkSource makePkSource(Table table);
	
	void createDatabase();
	void createDatabaseConstraints();
	void checkDatabase(Connection connection);
	void checkEmptyDatabase(Connection connection);
	void dropDatabase();
	void dropDatabaseConstraints();
	void tearDownDatabase();
	void tearDownDatabaseConstraints();
	// void checkEmptyDatabase();
	
	Schema makeVerifiedSchema();
	
	int[] getMinMaxPK(Connection connection, Table table);
	
	void store(Connection connection, State state, boolean present, Map<BlobColumn, byte[]> blobs);
	void delete(Connection connection, Item item);
	
	byte[] load(Connection connection, BlobColumn column, Item item);
	void load(Connection connection, BlobColumn column, Item item, OutputStream data, DataAttribute attribute);
	long loadLength(Connection connection, BlobColumn column, Item item);
	void store(Connection connection, BlobColumn column, Item item, InputStream data, DataAttribute attribute) throws IOException;

	Schema makeSchema();

	void defineColumnTypes(IntList columnTypes, java.sql.Statement statement) throws SQLException;
	boolean isDefiningColumnTypes();
	DatabaseListener setListener(DatabaseListener listener);
}
