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

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.UniqueConstraint;

public class SchemaTest extends TestmodelTest
{
	private static final String TABLE1 = "SumItem";
	private static final String TABLE1X = "SumItemX";
	private static final String COLUMN1 = "num2";
	private static final String COLUMN1X = "num2X";
	
	public static final Class CHECK = CheckConstraint.class;
	public static final Class PK = PrimaryKeyConstraint.class;
	public static final Class FK = ForeignKeyConstraint.class;
	public static final Class UNIQUE = UniqueConstraint.class;

	public void testSchema()
	{
		final String column1Type;
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			{
				final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
				final com.exedio.dsmf.Table tableX = schema.getTable(TABLE1X);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			{
				final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
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
			assertEquals(!mysql, model.supportsCheckConstraints());
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table attributeItem = schema.getTable("AttributeItem");
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Schema.COLOR_OK, attributeItem.getParticularColor());

			assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL");
			assertCheckConstraint(attributeItem, "AttribuItem_someBoolea_Ck", "("+protect("someBoolean")+" IN (0,1)) OR ("+protect("someBoolean")+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))");
			assertCheckConstraint(attributeItem, "AttributeItem_someEnum_Ck", "("+protect("someEnum")+" IN (100,200,300)) OR ("+protect("someEnum")+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullEnu_Ck", "("+protect("someNotNullEnum")+" IS NOT NULL) AND ("+protect("someNotNullEnum")+" IN (100,200,300))");
			assertCheckConstraint(attributeItem, "AttriItem_someDataMajo_Ck", "((LENGTH("+protect("someDataMajor")+")>=1) AND (LENGTH("+protect("someDataMajor")+")<=30)) OR ("+protect("someDataMajor")+" IS NULL)");

			assertPkConstraint(attributeItem, "AttributeItem_Pk", null, Table.PK_COLUMN_NAME);

			assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", Table.PK_COLUMN_NAME);

			final com.exedio.dsmf.Table uniqueItem = schema.getTable("ItemWithSingleUnique");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Schema.COLOR_OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "ItemWithSingUni_unStr_Unq", "("+protect("uniqueString")+")");
			
			final com.exedio.dsmf.Table doubleUniqueItem = schema.getTable("ItemWithDoubleUnique");
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Schema.COLOR_OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
			
			final com.exedio.dsmf.Table stringItem = schema.getTable("StringItem");
			assertNotNull(stringItem);
			assertEquals(null, stringItem.getError());
			assertEquals(Schema.COLOR_OK, stringItem.getParticularColor());

			final Column min4Max8 = stringItem.getColumn("min4Max8");
			assertEquals(null, min4Max8.getError());
			assertEquals(Schema.COLOR_OK, min4Max8.getParticularColor());
			if(hsqldb)
				assertEquals("varchar(8)", min4Max8.getType());
			else if(mysql)
				assertEquals("varchar(8) binary", min4Max8.getType());
			else
				assertEquals("VARCHAR2(8)", min4Max8.getType());

			assertCheckConstraint(stringItem, "StringItem_min4_Ck", "(LENGTH("+protect("min4")+")>=4) OR ("+protect("min4")+" IS NULL)");
			assertCheckConstraint(stringItem, "StringItem_max4_Ck", "(LENGTH("+protect("max4")+")<=4) OR ("+protect("max4")+" IS NULL)");
			assertCheckConstraint(stringItem, "StringItem_min4Max8_Ck", "((LENGTH("+protect("min4Max8")+")>=4) AND (LENGTH("+protect("min4Max8")+")<=8)) OR ("+protect("min4Max8")+" IS NULL)");
			assertCheckConstraint(stringItem, "StringItem_exact6_Ck",   "((LENGTH("+protect("exact6")+")>=6) AND (LENGTH("+protect("exact6")+")<=6)) OR ("+protect("exact6")+" IS NULL)");
		}
	}
	
	private void assertCheckConstraint(final com.exedio.dsmf.Table table, final String constraintName, final String requiredCondition)
	{
		final CheckConstraint constraint =
			(CheckConstraint)assertConstraint(table, CHECK, constraintName, requiredCondition);
	}
	
	private void assertPkConstraint(final com.exedio.dsmf.Table table, final String constraintName, final String requiredCondition, final String primaryKeyColumn)
	{
		final PrimaryKeyConstraint constraint =
			(PrimaryKeyConstraint)assertConstraint(table, PK, constraintName, requiredCondition);

		assertEquals(primaryKeyColumn, constraint.getPrimaryKeyColumn());
	}
	
	private void assertFkConstraint(final com.exedio.dsmf.Table table, final String constraintName, final String foreignKeyColumn, final String targetTable, final String targetColumn)
	{
		final ForeignKeyConstraint constraint =
			(ForeignKeyConstraint)assertConstraint(table, FK, constraintName, null);

		assertEquals(foreignKeyColumn, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
	}
	
	private void assertUniqueConstraint(final com.exedio.dsmf.Table table, final String constraintName, final String clause)
	{
		final UniqueConstraint constraint =
			(UniqueConstraint)assertConstraint(table, UNIQUE, constraintName, clause);

		assertEquals(clause, constraint.getClause());
	}
	
	private Constraint assertConstraint(final com.exedio.dsmf.Table table, final Class constraintType, final String constraintName, final String requiredCondition)
	{
		final Constraint constraint = table.getConstraint(constraintName);
		if(model.supportsCheckConstraints() || constraintType!=CHECK)
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
		return model.getDatabase().driver.protectName(name);
	}
	
}
