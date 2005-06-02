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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			{
				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportColumn columnX = table.getColumn(COLUMN1X);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
				assertEquals("missing", column.getError());
				assertEquals(Report.COLOR_ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getDatabaseType());
			}
			{
				final ReportTable tableX = report.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals("not used", tableX.getError());
				assertEquals(Report.COLOR_WARNING, tableX.getParticularColor());

				final ReportColumn column = tableX.getColumn(COLUMN1);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
				assertEquals("MISSING !!!", table.getError());
				assertEquals(Report.COLOR_ERROR, table.getParticularColor());

				final ReportColumn column = table.getColumn(COLUMN1);
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
			assertEquals(null, table.getError());
			assertEquals(Report.COLOR_OK, table.getParticularColor());

			final ReportColumn column = table.getColumn(COLUMN1);
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
			
			final boolean checkOk = !mysql;
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullStr_Ck", protect("someNotNullString")+" IS NOT NULL", checkOk);
			assertConstraint(attributeItem, CHECK, "AttribuItem_someBoolea_Ck", "("+protect("someBoolean")+" IN (0,1)) OR ("+protect("someBoolean")+" IS NULL)", checkOk);
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullBoo_Ck", "("+protect("someNotNullBoolean")+" IS NOT NULL) AND ("+protect("someNotNullBoolean")+" IN (0,1))", checkOk);
			assertConstraint(attributeItem, CHECK, "AttribuItem_someEnumer_Ck", "("+protect("someEnumeration")+" IN (100,200,300)) OR ("+protect("someEnumeration")+" IS NULL)", checkOk);
			assertConstraint(attributeItem, CHECK, "AttrItem_somNotNullEnu_Ck", "("+protect("someNotNullEnumeration")+" IS NOT NULL) AND ("+protect("someNotNullEnumeration")+" IN (100,200,300))", checkOk);
			assertConstraint(attributeItem, CHECK, "AttriItem_someDataMajo_Ck", "((LENGTH("+protect("someDataMajor")+")>=1) AND (LENGTH("+protect("someDataMajor")+")<=30)) OR ("+protect("someDataMajor")+" IS NULL)", checkOk);

			final boolean pkOk = true;
			assertConstraint(attributeItem, PK, "AttributeItem_Pk", null, pkOk);

			final boolean fkOk = true;
			assertConstraint(attributeItem, FK, "AttributeItem_someItem_Fk", null, fkOk);

			final ReportTable uniqueItem = report.getTable("ItemWithSingleUnique");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Report.COLOR_OK, uniqueItem.getParticularColor());
			
			final boolean uniqueOk = true;
			assertConstraint(uniqueItem, UNIQUE, "ItemWithSingUni_unStr_Unq", null, uniqueOk);
			
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

			assertConstraint(stringItem, CHECK, "StringItem_min4_Ck", "(LENGTH("+protect("min4")+")>=4) OR ("+protect("min4")+" IS NULL)", checkOk);
			assertConstraint(stringItem, CHECK, "StringItem_max4_Ck", "(LENGTH("+protect("max4")+")<=4) OR ("+protect("max4")+" IS NULL)", checkOk);
			assertConstraint(stringItem, CHECK, "StringItem_min4Max8_Ck", "((LENGTH("+protect("min4Max8")+")>=4) AND (LENGTH("+protect("min4Max8")+")<=8)) OR ("+protect("min4Max8")+" IS NULL)", checkOk);
		}
	}
	
	private void assertConstraint(final ReportTable table, final int constraintType, final String constraintName, final String requiredCondition, final boolean ok)
	{
		final ReportConstraint constraint = table.getConstraint(constraintName);
		if(ok)
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
