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
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			column1Type = column.getDatabaseType();
			assertNotNull(column1Type);
			
			column.renameTo(COLUMN1X);
		}
		// COLUMN RENAMED
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			{
				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportColumn columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(Report.COLOR_WARNING, columnX.getParticularColor());
				assertEquals(column1Type, columnX.getDatabaseType());

				columnX.renameTo(COLUMN1);
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());

			column.drop();
		}
		// COLUMN DROPPED
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(Report.COLOR_ERROR, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());

			column.create();
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
			
			table.renameTo(TABLE1X);
		}
		// TABLE RENAMED
		{
			final Report report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportTable tableX = report.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("not used", tableX.getError());
				assertEquals(Report.COLOR_WARNING, tableX.getParticularColor());

				final ReportColumn column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("not used", column.getError());
				assertEquals(Report.COLOR_WARNING, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());

				tableX.renameTo(TABLE1);
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
			
			table.drop();
		}
		// TABLE DROPPED
		{
			final Report report = model.reportDatabase();

			{
				final ReportTable table = report.getTable(TABLE1);
				assertNotNull(table);
				assertEquals(true, table.required());
				assertEquals(false, table.exists());
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());

				table.create();
			}
		}
		// OK
		{
			final Report report = model.reportDatabase();

			final ReportTable table = report.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Report.COLOR_OK, column.getParticularColor());
			assertEquals(column1Type, column.getDatabaseType());
		}
		{
			assertEquals(!mysql, model.supportsCheckConstraints());
			final Report report = model.reportDatabase();

			final ReportTable attributeItem = report.getTable("AttributeItem");
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Report.COLOR_OK, attributeItem.getParticularColor());
			
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL");
			assertConstraint(attributeItem, CHECK, "AttribuItem_someBoolea_Ck", "("+protect("someBoolean")+" IN (0,1)) OR ("+protect("someBoolean")+" IS NULL)");
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))");
			assertConstraint(attributeItem, CHECK, "AttribuItem_someEnumer_Ck", "("+protect("someEnumeration")+" IN (100,200,300)) OR ("+protect("someEnumeration")+" IS NULL)");
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullEnu_Ck", "("+protect("someNotNullEnumeration")+" IS NOT NULL) AND ("+protect("someNotNullEnumeration")+" IN (100,200,300))");
			assertConstraint(attributeItem, CHECK, "AttriItem_someDataMajo_Ck", "((LENGTH("+protect("someDataMajor")+")>=1) AND (LENGTH("+protect("someDataMajor")+")<=30)) OR ("+protect("someDataMajor")+" IS NULL)");

			assertConstraint(attributeItem, PK, "AttributeItem_Pk", null);

			assertConstraint(attributeItem, FK, "AttributeItem_someItem_Fk", null);

			final ReportTable uniqueItem = report.getTable("ItemWithSingleUnique");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Report.COLOR_OK, uniqueItem.getParticularColor());
			
			assertConstraint(uniqueItem, UNIQUE, "ItemWithSingUni_unStr_Unq", "("+protect("uniqueString")+")");
			
			final ReportTable doubleUniqueItem = report.getTable("ItemWithDoubleUnique");
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Report.COLOR_OK, doubleUniqueItem.getParticularColor());
			
			assertConstraint(doubleUniqueItem, UNIQUE, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
			
			final ReportTable stringItem = report.getTable("StringItem");
			assertNotNull(stringItem);
			assertEquals(null, stringItem.getError());
			assertEquals(Report.COLOR_OK, stringItem.getParticularColor());

			final ReportColumn min4Max8 = stringItem.getColumn("min4Max8");
			assertEquals(null, min4Max8.getError());
			assertEquals(Report.COLOR_OK, min4Max8.getParticularColor());
			if(hsqldb)
				assertEquals("varchar(8)", min4Max8.getDatabaseType());
			else if(mysql)
				assertEquals("varchar(8) binary", min4Max8.getDatabaseType());
			else
				assertEquals("VARCHAR2(8)", min4Max8.getDatabaseType());

			assertConstraint(stringItem, CHECK, "StringItem_min4_Ck", "(LENGTH("+protect("min4")+")>=4) OR ("+protect("min4")+" IS NULL)");
			assertConstraint(stringItem, CHECK, "StringItem_max4_Ck", "(LENGTH("+protect("max4")+")<=4) OR ("+protect("max4")+" IS NULL)");
			assertConstraint(stringItem, CHECK, "StringItem_min4Max8_Ck", "((LENGTH("+protect("min4Max8")+")>=4) AND (LENGTH("+protect("min4Max8")+")<=8)) OR ("+protect("min4Max8")+" IS NULL)");
		}
	}
	
	private void assertConstraint(final ReportTable table, final int constraintType, final String constraintName, final String requiredCondition)
	{
		final ReportConstraint constraint = table.getConstraint(constraintName);
		if(model.supportsCheckConstraints() || constraintType!=CHECK)
		{
			assertNotNull("no such constraint "+constraintName+", but has "+table.getConstraints(), constraint);
			assertEquals(constraintName, constraintType, constraint.type);
			assertEquals(constraintName, requiredCondition, constraint.requiredCondition);
			assertEquals(constraintName, Report.COLOR_OK, constraint.getParticularColor());
		}
		else
			assertEquals(constraintName, null, constraint);
	}
	
	private final String protect(final String name)
	{
		return model.getDatabase().protectName(name);
	}
	
}
