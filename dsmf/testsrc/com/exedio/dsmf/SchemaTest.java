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

package com.exedio.dsmf;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

public class SchemaTest extends TestCase
{
	private static final String TABLE1 = "SumItem";
	private static final String TABLE1X = "SumItemX";
	private static final String COLUMN1 = "num2";
	private static final String COLUMN1X = "num2X";
	
	public static final Class CHECK = CheckConstraint.class;
	public static final Class PK = PrimaryKeyConstraint.class;
	public static final Class FK = ForeignKeyConstraint.class;
	public static final Class UNIQUE = UniqueConstraint.class;
	
	Driver driver;
	String stringType;
	String intType;
	boolean supportsCheckConstraints = true;
	SimpleConnectionProvider provider;
	Connection connection1;
	Connection connection2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		final Properties config = new Properties();
		config.load(new FileInputStream(System.getProperty("com.exedio.cope.properties")));
		final String database = config.getProperty("database");
		final String url = config.getProperty("database.url");
		final String user = config.getProperty("database.user");
		final String password = config.getProperty("database.password");
		
		if("hsqldb".equals(database))
		{
			Class.forName("org.hsqldb.jdbcDriver");
			driver = new HsqldbDriver();
			stringType = "varchar(8)";
			intType = "integer";
		}
		else if("mysql".equals(database))
		{
			Class.forName("com.mysql.jdbc.Driver");
			driver = new MysqlDriver("this");
			stringType = "varchar(8) binary";
			intType = "integer";
			supportsCheckConstraints = false;
		}
		else if("oracle".equals(database))
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			driver = new OracleDriver(user.toUpperCase());
			stringType = "VARCHAR2(8)";
			intType = "NUMBER(12)";
		}
		else
			throw new RuntimeException(database);
		
		connection1 = DriverManager.getConnection(url, user, password);
		connection2 = DriverManager.getConnection(url, user, password);
		provider = new SimpleConnectionProvider(Arrays.asList(new Connection[]{connection1, connection2}));

		final Schema schema = getSchema();
		schema.tearDown();
		schema.create();
	}
	
	public void tearDown() throws Exception
	{
		getSchema().tearDown();
		
		if(connection1!=null)
			connection1.close();
		if(connection2!=null)
			connection2.close();

		super.tearDown();
	}
	
	static class SimpleConnectionProvider implements ConnectionProvider
	{
		final ArrayList connections;
		
		SimpleConnectionProvider(final List connections)
		{
			this.connections = new ArrayList(connections);
		}

		public Connection getConnection() throws SQLException
		{
			final Connection result =  (Connection)connections.get(connections.size()-1);
			connections.remove(connections.size()-1);
			return result;
		}

		public void putConnection(final Connection connection) throws SQLException
		{
			connections.add(connection);
		}
	}
	
	private final Schema getSchema()
	{
		final Schema result = new Schema(driver, provider);
		{
			final Table table = new Table(result, TABLE1);
			new Column(table, COLUMN1, intType);
			new Column(table, "otherColumn", stringType);
		}
		{
			final Table table = new Table(result, "AttributeItem");

			if(supportsCheckConstraints)
			{
				new Column(table, "someNotNullString", stringType);
				new CheckConstraint(table, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL");
				
				new Column(table, "someNotNullBoolean", stringType);
				new CheckConstraint(table, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))");
			}
			
			new Column(table, "this", stringType);
			new PrimaryKeyConstraint(table, "AttributeItem_Pk", "this");
			
			new Column(table, "someItem", stringType);
			{
				final Table targetTable = new Table(result, "EmptyItem");
				new Column(targetTable, "thus", stringType);
				new PrimaryKeyConstraint(targetTable, "EmptyItem_Pk", "thus");
			}
			new ForeignKeyConstraint(table, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", "thus");
		}
		{
			final Table table = new Table(result, "ItemWithSingleUnique");
			new Column(table, "uniqueString", stringType);
			new UniqueConstraint(table, "ItemWithSingUni_unStr_Unq", "("+protect("uniqueString")+")");
		}
		{
			final Table table = new Table(result, "ItemWithDoubleUnique");
			new Column(table, "string", stringType);
			new Column(table, "integer", intType);
			new UniqueConstraint(table, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
		}
		return result;
	}

	private final Schema getVerifiedSchema()
	{
		final Schema result = getSchema();
		result.verify();
		return result;
	}

	public void testSchema()
	{
		final String column1Type;
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.COLOR_OK, column.getParticularColor());
			column1Type = column.getType();
			assertNotNull(column1Type);
			
			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			{
				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final Column columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(Schema.COLOR_WARNING, columnX.getParticularColor());
				assertEquals(column1Type, columnX.getType());

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(Schema.COLOR_ERROR, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.create();
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
			
			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Schema schema = getVerifiedSchema();

			{
				final Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Schema.COLOR_ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final Table tableX = schema.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("not used", tableX.getError());
				assertEquals(Schema.COLOR_WARNING, tableX.getParticularColor());

				final Column column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("not used", column.getError());
				assertEquals(Schema.COLOR_WARNING, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
			
			table.drop();
		}
		// TABLE DROPPED
		{
			final Schema schema = getVerifiedSchema();

			{
				final Table table = schema.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Schema.COLOR_ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				table.create();
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			final Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.COLOR_OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
		}
		{
			final Schema schema = getVerifiedSchema();

			final Table attributeItem = schema.getTable("AttributeItem");
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Schema.COLOR_OK, attributeItem.getParticularColor());
			
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))");
			assertPkConstraint(attributeItem, "AttributeItem_Pk", null, "this");
			assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", "thus");

			final Table uniqueItem = schema.getTable("ItemWithSingleUnique");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Schema.COLOR_OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "ItemWithSingUni_unStr_Unq", "("+protect("uniqueString")+")");
			
			final Table doubleUniqueItem = schema.getTable("ItemWithDoubleUnique");
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Schema.COLOR_OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
		}
	}
	
	private void assertCheckConstraint(final Table table, final String constraintName, final String requiredCondition)
	{
		final CheckConstraint constraint =
			(CheckConstraint)assertConstraint(table, CHECK, constraintName, requiredCondition);
	}
	
	private void assertPkConstraint(final Table table, final String constraintName, final String requiredCondition, final String primaryKeyColumn)
	{
		final PrimaryKeyConstraint constraint =
			(PrimaryKeyConstraint)assertConstraint(table, PK, constraintName, requiredCondition);

		assertEquals(primaryKeyColumn, constraint.getPrimaryKeyColumn());
	}
	
	private void assertFkConstraint(final Table table, final String constraintName, final String foreignKeyColumn, final String targetTable, final String targetColumn)
	{
		final ForeignKeyConstraint constraint =
			(ForeignKeyConstraint)assertConstraint(table, FK, constraintName, null);

		assertEquals(foreignKeyColumn, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
	}
	
	private void assertUniqueConstraint(final Table table, final String constraintName, final String clause)
	{
		final UniqueConstraint constraint =
			(UniqueConstraint)assertConstraint(table, UNIQUE, constraintName, clause);

		assertEquals(clause, constraint.getClause());
	}
	
	private Constraint assertConstraint(final Table table, final Class constraintType, final String constraintName, final String requiredCondition)
	{
		final Constraint constraint = table.getConstraint(constraintName);
		if(supportsCheckConstraints || constraintType!=CHECK)
		{
			assertNotNull("no such constraint "+constraintName+", but has "+table.getConstraints(), constraint);
			assertEquals(constraintName, constraintType, constraint.getClass());
			assertEquals(constraintName, requiredCondition, constraint.getRequiredCondition());
			assertEquals(constraintName, null, constraint.getError());
			assertEquals(constraintName, Schema.COLOR_OK, constraint.getParticularColor());
		}
		else
			assertEquals(constraintName, null, constraint);

		return constraint;
	}
	
	private final String protect(final String name)
	{
		return driver.protectName(name);
	}
	
}
