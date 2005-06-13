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

import com.exedio.dsmf.ReportCheckConstraint;
import com.exedio.dsmf.ReportColumn;
import com.exedio.dsmf.ReportConstraint;
import com.exedio.dsmf.ReportForeignKeyConstraint;
import com.exedio.dsmf.ReportPrimaryKeyConstraint;
import com.exedio.dsmf.ReportSchema;
import com.exedio.dsmf.ReportTable;
import com.exedio.dsmf.ReportUniqueConstraint;

public class ReportTest extends DatabaseLibTest
{
	private static final String TABLE1 = "SumItem";
	private static final String TABLE1X = "SumItemX";
	private static final String COLUMN1 = "num2";
	private static final String COLUMN1X = "num2X";
	
	public static final int CHECK = ReportConstraint.TYPE_CHECK;
	public static final int PK = ReportConstraint.TYPE_PRIMARY_KEY;
	public static final int FK = ReportConstraint.TYPE_FOREIGN_KEY;
	public static final int UNIQUE = ReportConstraint.TYPE_UNIQUE;

	public void testReport()
	{
		final String column1Type;
		// OK
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(ReportSchema.COLOR_OK, column.getParticularColor());
			column1Type = column.getType();
			assertNotNull(column1Type);
			
			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			{
				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ReportSchema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final ReportColumn columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(ReportSchema.COLOR_WARNING, columnX.getParticularColor());
				assertEquals(column1Type, columnX.getType());

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(ReportSchema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(ReportSchema.COLOR_ERROR, column.getParticularColor());
			assertEquals(column1Type, column.getType());

			column.create();
		}
		// OK
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(ReportSchema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
			
			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final ReportSchema report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(ReportSchema.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ReportSchema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final ReportTable tableX = report.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("not used", tableX.getError());
				assertEquals(ReportSchema.COLOR_WARNING, tableX.getParticularColor());

				final ReportColumn column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("not used", column.getError());
				assertEquals(ReportSchema.COLOR_WARNING, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(ReportSchema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
			
			table.drop();
		}
		// TABLE DROPPED
		{
			final ReportSchema report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(ReportSchema.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(ReportSchema.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());

				table.create();
			}
		}
		// OK
		{
			final ReportSchema report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(ReportSchema.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(ReportSchema.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
		}
		{
			assertEquals(!mysql, model.supportsCheckConstraints());
			final ReportSchema report = model.reportDatabase();

			final ReportTable attributeItem = report.getTable("AttributeItem");
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(ReportSchema.COLOR_OK, attributeItem.getParticularColor());
			
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL");
			assertCheckConstraint(attributeItem, "AttribuItem_someBoolea_Ck", "("+protect("someBoolean")+" IN (0,1)) OR ("+protect("someBoolean")+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))");
			assertCheckConstraint(attributeItem, "AttribuItem_someEnumer_Ck", "("+protect("someEnumeration")+" IN (100,200,300)) OR ("+protect("someEnumeration")+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullEnu_Ck", "("+protect("someNotNullEnumeration")+" IS NOT NULL) AND ("+protect("someNotNullEnumeration")+" IN (100,200,300))");
			assertCheckConstraint(attributeItem, "AttriItem_someDataMajo_Ck", "((LENGTH("+protect("someDataMajor")+")>=1) AND (LENGTH("+protect("someDataMajor")+")<=30)) OR ("+protect("someDataMajor")+" IS NULL)");

			assertPkConstraint(attributeItem, "AttributeItem_Pk", null, Table.PK_COLUMN_NAME);

			assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", Table.PK_COLUMN_NAME);

			final ReportTable uniqueItem = report.getTable("ItemWithSingleUnique");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(ReportSchema.COLOR_OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "ItemWithSingUni_unStr_Unq", "("+protect("uniqueString")+")");
			
			final ReportTable doubleUniqueItem = report.getTable("ItemWithDoubleUnique");
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(ReportSchema.COLOR_OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
			
			final ReportTable stringItem = report.getTable("StringItem");
			assertNotNull(stringItem);
			assertEquals(null, stringItem.getError());
			assertEquals(ReportSchema.COLOR_OK, stringItem.getParticularColor());

			final ReportColumn min4Max8 = stringItem.getColumn("min4Max8");
			assertEquals(null, min4Max8.getError());
			assertEquals(ReportSchema.COLOR_OK, min4Max8.getParticularColor());
			if(hsqldb)
				assertEquals("varchar(8)", min4Max8.getType());
			else if(mysql)
				assertEquals("varchar(8) binary", min4Max8.getType());
			else
				assertEquals("VARCHAR2(8)", min4Max8.getType());

			assertCheckConstraint(stringItem, "StringItem_min4_Ck", "(LENGTH("+protect("min4")+")>=4) OR ("+protect("min4")+" IS NULL)");
			assertCheckConstraint(stringItem, "StringItem_max4_Ck", "(LENGTH("+protect("max4")+")<=4) OR ("+protect("max4")+" IS NULL)");
			assertCheckConstraint(stringItem, "StringItem_min4Max8_Ck", "((LENGTH("+protect("min4Max8")+")>=4) AND (LENGTH("+protect("min4Max8")+")<=8)) OR ("+protect("min4Max8")+" IS NULL)");
		}
	}
	
	private void assertCheckConstraint(final ReportTable table, final String constraintName, final String requiredCondition)
	{
		final ReportCheckConstraint constraint =
			(ReportCheckConstraint)assertConstraint(table, CHECK, constraintName, requiredCondition);
	}
	
	private void assertPkConstraint(final ReportTable table, final String constraintName, final String requiredCondition, final String primaryKeyColumn)
	{
		final ReportPrimaryKeyConstraint constraint =
			(ReportPrimaryKeyConstraint)assertConstraint(table, PK, constraintName, requiredCondition);

		assertEquals(primaryKeyColumn, constraint.getPrimaryKeyColumn());
	}
	
	private void assertFkConstraint(final ReportTable table, final String constraintName, final String foreignKeyColumn, final String targetTable, final String targetColumn)
	{
		final ReportForeignKeyConstraint constraint =
			(ReportForeignKeyConstraint)assertConstraint(table, FK, constraintName, null);

		assertEquals(foreignKeyColumn, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
	}
	
	private void assertUniqueConstraint(final ReportTable table, final String constraintName, final String clause)
	{
		final ReportUniqueConstraint constraint =
			(ReportUniqueConstraint)assertConstraint(table, UNIQUE, constraintName, clause);

		assertEquals(clause, constraint.getClause());
	}
	
	private ReportConstraint assertConstraint(final ReportTable table, final int constraintType, final String constraintName, final String requiredCondition)
	{
		final ReportConstraint constraint = table.getConstraint(constraintName);
		if(model.supportsCheckConstraints() || constraintType!=CHECK)
		{
			assertNotNull("no such constraint "+constraintName+", but has "+table.getConstraints(), constraint);
			assertEquals(constraintName, constraintType, constraint.getType());
			assertEquals(constraintName, requiredCondition, constraint.getRequiredCondition());
			assertEquals(constraintName, null, constraint.getError());
			assertEquals(constraintName, ReportSchema.COLOR_OK, constraint.getParticularColor());
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
