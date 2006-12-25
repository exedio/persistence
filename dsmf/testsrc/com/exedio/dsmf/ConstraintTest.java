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

package com.exedio.dsmf;


public class ConstraintTest extends SchemaReadyTest
{
	private static final Class CHECK = CheckConstraint.class;
	private static final Class PK = PrimaryKeyConstraint.class;
	private static final Class FK = ForeignKeyConstraint.class;
	private static final Class UNIQUE = UniqueConstraint.class;
	
	private static final String TABLE = "ConstraintTable";
	
	private static final String NOT_NULL_COLUMN = "notNull";
	private static final String NOT_NULL_NAME = "notNullId";

	private static final String CHECK_COLUMN = "check";
	private static final String CHECK_NAME = "check";
	
	private static final String PK_COLUMN = "primaryKey";
	private static final String PK_NAME = "primaryKey_Pk"; // must have this value to work with mysql

	private static final String FK_COLUMN = "foreignKey";
	private static final String FK_NAME = "foreignKeyId";
	private static final String FK_TARGET_TABLE = "foreignKeyTargetTable";
	private static final String FK_TARGET_COLUMN = "foreignKeyTargetColumn";

	private static final String UNIQUE_SINGLE_COLUMN = "uniqueSingle";
	private static final String UNIQUE_SINGLE_NAME = "uniqueSingleId";

	private static final String UNIQUE_DOUBLE_COLUMN1 = "uniqueDouble1";
	private static final String UNIQUE_DOUBLE_COLUMN2 = "uniqueDouble2";
	private static final String UNIQUE_DOUBLE_NAME = "uniqueDoubleId";

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();
		final Table table = new Table(result, TABLE);

		if(supportsCheckConstraints)
		{
			new Column(table, NOT_NULL_COLUMN, stringType);
			new CheckConstraint(table, NOT_NULL_NAME, p(NOT_NULL_COLUMN)+" IS NOT NULL");
			
			new Column(table, CHECK_COLUMN, stringType);
			new CheckConstraint(table, CHECK_NAME, "("+p(CHECK_COLUMN)+" IS NOT NULL) AND ("+p(CHECK_COLUMN)+" IN (0,1))");
		}
		
		new Column(table, PK_COLUMN, stringType);
		new PrimaryKeyConstraint(table, PK_NAME, PK_COLUMN);
		
		new Column(table, FK_COLUMN, stringType);
		{
			final Table targetTable = new Table(result, FK_TARGET_TABLE);
			new Column(targetTable, FK_TARGET_COLUMN, stringType);
			new PrimaryKeyConstraint(targetTable, "targetPrimaryKey_Pk", FK_TARGET_COLUMN);
		}
		new ForeignKeyConstraint(table, FK_NAME, FK_COLUMN, FK_TARGET_TABLE, FK_TARGET_COLUMN);

		new Column(table, UNIQUE_SINGLE_COLUMN, stringType);
		new UniqueConstraint(table, UNIQUE_SINGLE_NAME, "("+p(UNIQUE_SINGLE_COLUMN)+")");

		new Column(table, UNIQUE_DOUBLE_COLUMN1, stringType);
		new Column(table, UNIQUE_DOUBLE_COLUMN2, intType);
		new UniqueConstraint(table, UNIQUE_DOUBLE_NAME, "("+p(UNIQUE_DOUBLE_COLUMN1)+","+p(UNIQUE_DOUBLE_COLUMN2)+")");

		return result;
	}
	
	public void testConstraints()
	{
		final Schema schema = getVerifiedSchema();

		final Table table = schema.getTable(TABLE);
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());
		
		assertCheckConstraint(table, NOT_NULL_NAME, p(NOT_NULL_COLUMN)+" IS NOT NULL");
		if(!postgresql)
			assertCheckConstraint(table, CHECK_NAME, "("+p(CHECK_COLUMN)+" IS NOT NULL) AND ("+p(CHECK_COLUMN)+" IN (0,1))");
		assertPkConstraint(table, PK_NAME, null, PK_COLUMN);
		assertFkConstraint(table, FK_NAME, FK_COLUMN, FK_TARGET_TABLE, FK_TARGET_COLUMN);
		if(!postgresql)
		{
			assertUniqueConstraint(table, UNIQUE_SINGLE_NAME, "("+p(UNIQUE_SINGLE_COLUMN)+")");
			assertUniqueConstraint(table, UNIQUE_DOUBLE_NAME, "("+p(UNIQUE_DOUBLE_COLUMN1)+","+p(UNIQUE_DOUBLE_COLUMN2)+")");
		}
		
		table.getConstraint(FK_NAME).drop();
		table.getConstraint(FK_NAME).create();
		
		if(supportsCheckConstraints)
		{
			table.getConstraint(CHECK_NAME).drop();
			table.getConstraint(CHECK_NAME).create();
		}
		
		table.getConstraint(PK_NAME).drop();
		table.getConstraint(PK_NAME).create();
		table.getConstraint(UNIQUE_SINGLE_NAME).drop();
		table.getConstraint(UNIQUE_SINGLE_NAME).create();
		table.getConstraint(UNIQUE_DOUBLE_NAME).drop();
		table.getConstraint(UNIQUE_DOUBLE_NAME).create();
	}

	private CheckConstraint assertCheckConstraint(final Table table, final String constraintName, final String requiredCondition)
	{
		return
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
			assertEquals(constraintName, Schema.Color.OK, constraint.getParticularColor());
		}
		else
			assertEquals(constraintName, null, constraint);

		return constraint;
	}

}
