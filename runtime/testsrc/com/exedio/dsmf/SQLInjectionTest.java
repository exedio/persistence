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

package com.exedio.dsmf;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.SchemaInfo;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SQLInjectionTest extends SchemaTest
{
	private static final String MESSAGE_PREFIX = "database name contains forbidden characters: ";

	private static final String TABLE = "InjectionTable";
	private static final String COLUMN = "injectionColumn";

	@Test void testTableCreate()
	{
		final String BAD_TABLE_PRE = p("Injection1") + " (badColumn varchar(30) ) --";
		final String BAD_TABLE = BAD_TABLE_PRE.substring(1);

		final Schema schema = newSchema();
		final Table badTable = schema.newTable(BAD_TABLE);
		badTable.newColumn(COLUMN, stringType);
		assertFails(
				badTable::create,
				IllegalArgumentException.class,
				MESSAGE_PREFIX + BAD_TABLE);
	}

	@Test void testColumnCreate()
	{
		final String BAD_COLUMN_PRE = p("badColumn1")+" varchar(30), " + p("badColumn2");
		final String BAD_COLUMN = BAD_COLUMN_PRE.substring(1, BAD_COLUMN_PRE.length()-1);

		final Schema schema = newSchema();
		final Table table = schema.newTable(TABLE);
		table.newColumn(BAD_COLUMN, stringType);
		assertFails(
				table::create,
				IllegalArgumentException.class,
				MESSAGE_PREFIX + BAD_COLUMN);
	}

	@Test void testColumnCreateSemicolon()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		table.newColumn(COLUMN, intType + "); DROP TABLE ZACK " + (mysql ? "#" : "--"));

		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				table::create);
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals( // TODO injection possible !!!
				switch(dialect)
				{
					case hsqldb -> "user lacks privilege or object not found: ZACK in statement [DROP TABLE ZACK --)]";
					case mysql -> "Unknown table '" + model.getEnvironmentInfo().getCatalog() + ".ZACK'";
					case postgresql -> "ERROR: table \"zack\" does not exist";
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@Test void testColumnCreateComma()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		table.newColumn(COLUMN, intType + ", badColumn "+(mysql ? "varchar(16384)" : "notAType"));

		assumeTrue(!mysql || atLeastMysql8());
		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				table::create);
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals( // TODO injection possible !!!
				switch(dialect)
				{
					case hsqldb -> "type not found or user lacks privilege: NOTATYPE";
					case mysql -> "Column length too big for column 'badColumn' (max = 16383); use BLOB or TEXT instead";
					case postgresql -> "ERROR: type \"notatype\" does not exist";
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@Test void testColumnModifySemicolon()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		final Column column = table.newColumn(COLUMN, intType);
		table.create();

		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				() -> column.modify(intType + "; DROP TABLE ZACK"));
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals( // TODO injection possible !!!
				switch(dialect)
				{
					case hsqldb -> "user lacks privilege or object not found: ZACK in statement [DROP TABLE ZACK]";
					case mysql -> "Unknown table '" + model.getEnvironmentInfo().getCatalog() + ".ZACK'";
					case postgresql -> "ERROR: table \"zack\" does not exist";
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@Test void testColumnModifyComma()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		final Column column = table.newColumn(COLUMN, intType);
		table.create();

		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				() -> column.modify(intType + ", DROP COLUMN ZACK"));
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals(
				switch(dialect)
				{
					case hsqldb -> "unexpected token: ,";
					case mysql -> "Can't DROP 'ZACK'; check that column/key exists"; // TODO injection possible !!!
					case postgresql -> "ERROR: column \"zack\" of relation \"InjectionTable\" does not exist"; // TODO injection possible !!!
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@Test void testColumnRename()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		final Column column = table.newColumn(COLUMN, intType);
		column.notifyExists(intType);
		table.create();
		final char quoteChar = column.quoteName("").charAt(0);

		assertFails(
				() -> column.renameTo(COLUMN + quoteChar + ";ZACK"),
				IllegalArgumentException.class,
				"database name contains forbidden characters: " +
				"injectionColumn" + quoteChar + ";ZACK");
	}

	@Test void testConstraintCreateSemicolon()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		table.newColumn(COLUMN, intType);
		table.newCheck(COLUMN + "_CHECK", SchemaInfo.quoteName(model, COLUMN) + ">0)); DROP TABLE ZACK " + (mysql ? "#" : "--"));

		assumeTrue(SchemaInfo.supportsCheckConstraint(model));
		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				table::create);
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals( // TODO injection possible !!!
				switch(dialect)
				{
					case hsqldb -> "user lacks privilege or object not found: ZACK in statement [DROP TABLE ZACK --))]";
					case mysql -> "Unknown table '" + model.getEnvironmentInfo().getCatalog() + ".ZACK'";
					case postgresql -> "ERROR: table \"zack\" does not exist";
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@Test void testConstraintCreateComma()
	{
		final Schema result = newSchema();
		final Table table = result.newTable(TABLE);
		table.newColumn(COLUMN, intType);
		table.newCheck(COLUMN + "_CHECK", SchemaInfo.quoteName(model, COLUMN) + ">0),CONSTRAINT whatever CHECK(zack>55");

		assumeTrue(SchemaInfo.supportsCheckConstraint(model));
		final SQLRuntimeException e = assertThrows(
				SQLRuntimeException.class,
				table::create);
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals( // TODO injection possible !!!
				switch(dialect)
				{
					case hsqldb -> "user lacks privilege or object not found: ZACK in statement " +
										"[CREATE TABLE \"InjectionTable\"(" +
										"\"injectionColumn\" INTEGER," +
										"CONSTRAINT \"injectionColumn_CHECK\" CHECK(\"injectionColumn\">0)," +
										"CONSTRAINT whatever CHECK(zack>55))]";
					case mysql -> "Check constraint 'whatever' refers to non-existing column 'zack'.";
					case postgresql -> "ERROR: column \"zack\" does not exist";
				},
				dropMariaConnectionId(cause.getMessage()));
	}

	@BeforeEach @AfterEach void cleanUp() throws SQLException
	{
		final Connection con = getConnection();
		try(Statement stmt = con.createStatement())
		{
			stmt.execute("DROP TABLE IF EXISTS " + SchemaInfo.quoteName(model, TABLE));
		}
		finally
		{
			putConnection(con);
		}
	}
}
