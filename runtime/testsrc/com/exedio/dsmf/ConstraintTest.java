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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class ConstraintTest extends SchemaReadyTest
{
	private static final Class<?> CHECK = CheckConstraint.class;
	private static final Class<?> PK = PrimaryKeyConstraint.class;
	private static final Class<?> FK = ForeignKeyConstraint.class;
	private static final Class<?> UNIQUE = UniqueConstraint.class;

	private static final String TABLE = "ConstraintTable";

	private static final String NOT_NULL_COLUMN = "notNull";
	private static final String NOT_NULL_NAME = "notNullId";

	private static final String CHECK_COLUMN = "check";
	private static final String CHECK_NAME = "check";
	private static final String CHECK_TABLE_NAME = "checkTable";

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

	@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
	private Table table;
	private CheckConstraint nn;
	private CheckConstraint ck;
	private CheckConstraint ct;
	private PrimaryKeyConstraint pk;
	private ForeignKeyConstraint fk;
	private UniqueConstraint us;
	private UniqueConstraint ud;

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();
		table = result.newTable(TABLE);

		if(supportsCheckConstraints)
		{
			final Column nn = table.newColumn(NOT_NULL_COLUMN, stringType);
			this.nn = nn.newCheck(NOT_NULL_NAME, p(NOT_NULL_COLUMN)+" IS NOT NULL");

			final Column check = table.newColumn(CHECK_COLUMN, intType);
			ck = check.newCheck(CHECK_NAME, "("+p(CHECK_COLUMN)+" IS NOT NULL) AND ("+hp(p(CHECK_COLUMN))+" IN ("+hp("0")+","+sac()+hp("1")+"))");
			ct = table.newCheck(CHECK_TABLE_NAME, p(CHECK_COLUMN)+">0");
		}

		final Column pk = table.newColumn(PK_COLUMN, stringType);
		this.pk = pk.newPrimaryKey(PK_NAME);

		final Column fkColumn = table.newColumn(FK_COLUMN, stringType);
		{
			final Table targetTable = result.newTable(FK_TARGET_TABLE);
			final Column targetPk = targetTable.newColumn(FK_TARGET_COLUMN, stringType);
			targetPk.newPrimaryKey("targetPrimaryKey_Pk");
		}
		fk = fkColumn.newForeignKey(FK_NAME, FK_TARGET_TABLE, FK_TARGET_COLUMN);

		final Column unqCol = table.newColumn(UNIQUE_SINGLE_COLUMN, stringType);
		us = table.newUnique(unqCol, UNIQUE_SINGLE_NAME, "("+p(UNIQUE_SINGLE_COLUMN)+")");

		table.newColumn(UNIQUE_DOUBLE_COLUMN1, stringType);
		table.newColumn(UNIQUE_DOUBLE_COLUMN2, intType);
		ud = table.newUnique(null, UNIQUE_DOUBLE_NAME, "("+p(UNIQUE_DOUBLE_COLUMN1)+","+p(UNIQUE_DOUBLE_COLUMN2)+")");

		return result;
	}

	@Test void testConstraints()
	{
		final Schema schema = getVerifiedSchema();

		assertSame(table, schema.getTable(TABLE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Node.Color.OK, table.getParticularColor());

		assertSame(nn, assertCheckConstraint(table, NOT_NULL_NAME, p(NOT_NULL_COLUMN)+" IS NOT NULL"));
		assertSame(ck, assertCheckConstraint(table, CHECK_NAME, "("+p(CHECK_COLUMN)+" IS NOT NULL) AND ("+hp(p(CHECK_COLUMN))+" IN ("+hp("0")+","+sac()+hp("1")+"))"));
		assertSame(ct ,assertCheckConstraint(table, CHECK_TABLE_NAME, p(CHECK_COLUMN)+">0"));
		assertSame(pk, assertPkConstraint(table, PK_NAME, null, PK_COLUMN));
		assertSame(fk, assertFkConstraint(table, FK_NAME, FK_COLUMN, FK_TARGET_TABLE, FK_TARGET_COLUMN));
		assertSame(us, assertUniqueConstraint(table, UNIQUE_SINGLE_NAME, "("+p(UNIQUE_SINGLE_COLUMN)+")"));
		assertSame(ud, assertUniqueConstraint(table, UNIQUE_DOUBLE_NAME, "("+p(UNIQUE_DOUBLE_COLUMN1)+","+p(UNIQUE_DOUBLE_COLUMN2)+")"));

		assertEquals(
				supportsCheckConstraints
				? asList(nn, ck, ct, pk, fk, us, ud)
				: asList(pk, fk, us, ud),
				table.getConstraints());
		assertEquals(
				supportsCheckConstraints
				? asList(ct, ud)
				: asList(ud),
				table.getTableConstraints());
		if(supportsCheckConstraints)
		{
			assertEquals(asList(nn), table.getColumn(NOT_NULL_COLUMN).getConstraints());
			assertEquals(asList(ck), table.getColumn(CHECK_NAME).getConstraints());
		}
		assertEquals(asList(pk), table.getColumn(PK_COLUMN).getConstraints());
		assertEquals(asList(fk), table.getColumn(FK_COLUMN).getConstraints());
		assertEquals(asList(us), table.getColumn(UNIQUE_SINGLE_COLUMN).getConstraints());

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

	private PrimaryKeyConstraint assertPkConstraint(final Table table, final String constraintName, final String requiredCondition, final String primaryKeyColumn)
	{
		final PrimaryKeyConstraint constraint =
			(PrimaryKeyConstraint)assertConstraint(table, PK, constraintName, requiredCondition);

		assertEquals(primaryKeyColumn, constraint.getPrimaryKeyColumn());
		return constraint;
	}

	private ForeignKeyConstraint assertFkConstraint(final Table table, final String constraintName, final String foreignKeyColumn, final String targetTable, final String targetColumn)
	{
		final ForeignKeyConstraint constraint =
			(ForeignKeyConstraint)assertConstraint(table, FK, constraintName, foreignKeyColumn + "->" + targetTable + '.' + targetColumn);

		assertEquals(foreignKeyColumn, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
		return constraint;
	}

	private UniqueConstraint assertUniqueConstraint(final Table table, final String constraintName, final String clause)
	{
		final UniqueConstraint constraint =
			(UniqueConstraint)assertConstraint(table, UNIQUE, constraintName, clause);

		assertEquals(clause, constraint.getClause());
		return constraint;
	}

	private Constraint assertConstraint(final Table table, final Class<?> constraintType, final String constraintName, final String requiredCondition)
	{
		final Constraint constraint = table.getConstraint(constraintName);
		if(supportsCheckConstraints || constraintType!=CHECK)
		{
			assertNotNull(constraint, "no such constraint "+constraintName+", but has "+table.getConstraints());
			assertEquals(constraintType, constraint.getClass(), constraintName);
			assertEquals(requiredCondition, constraint.getRequiredCondition(), constraintName);
			assertEquals(null, constraint.getError(), constraintName);
			assertEquals(Node.Color.OK, constraint.getParticularColor(), constraintName);
		}
		else
			assertEquals(null, constraint, constraintName);

		return constraint;
	}

}