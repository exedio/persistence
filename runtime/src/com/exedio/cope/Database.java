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

import bak.pcj.list.IntList;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.Schema;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

interface Database
{
	
	void load(Connection connection, PersistentState state);
	
	String makeName(String longName);
	String makeName(String prefix, String longName);
	
	Driver getDriver();
	ConnectionPool getConnectionPool();
	
	void addTable( Table table );
	void addUniqueConstraint( String databaseID, UniqueConstraint constraint );
	void addIntegrityConstraint(ItemColumn column);
	
	java.util.Properties getTableOptions();

	boolean supportsCheckConstraints();
	
	String getIntegerType(int precision);
	String getDoubleType(int precision);
	String getStringType(int maxLength);
	String getDayType();
	boolean supportsEmptyStrings();
	boolean supportsRightOuterJoins();

	void appendMatchClause(Statement bf, StringFunction function, String value);
	
	ArrayList search(Connection connection, Query query, boolean doCountOnly);
	
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
	
	void store(Connection connection, State state, boolean present) throws UniqueViolationException;
	void delete(Connection connection, Item item);
	
	Schema makeSchema();

	void defineColumnTypes(IntList columnTypes, java.sql.Statement statement) throws SQLException;
	boolean isDefiningColumnTypes();
}
