/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.PlusItem;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.UniqueConstraint;

public class SchemaTest extends TestmodelTest
{
	private static final String TABLE1X = "PlusItemX";
	private static final String COLUMN1X = "num2X";
	
	public static final Class CHECK = CheckConstraint.class;
	public static final Class PK = PrimaryKeyConstraint.class;
	public static final Class FK = ForeignKeyConstraint.class;
	public static final Class UNIQUE = UniqueConstraint.class;

	public void testSchema()
	{
		if(postgresql) return;
		final String LENGTH = model.getDatabase().dialect.stringLength;
		final String TABLE1 = PlusItem.TYPE.getTableName();
		final String COLUMN1 = PlusItem.num2.getColumnName();
		assertEquals("PlusItem", TABLE1);
		assertEquals("num2", COLUMN1);

		final String column1Type;
		// OK
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			column1Type = column.getType();
			assertNotNull(column1Type);
			
			column.renameTo(COLUMN1X);
		}
		// OK without verify
		{
			final Schema schema = model.getSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(false, table.exists());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals(column1Type, column.getType());
		}
		// COLUMN RENAMED
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());

			{
				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final Column columnX = table.getColumn(COLUMN1X);
				assertEquals(false, columnX.required());
				assertEquals(true, columnX.exists());
				assertEquals("not used", columnX.getError());
				assertEquals(Schema.Color.WARNING, columnX.getParticularColor());
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
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Constraint constraint = table.getConstraint("PlusItem_num2_Ck");
			if(model.supportsCheckConstraints())
				constraint.drop();
			
			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
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
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(false, column.exists());
			assertEquals("missing", column.getError());
			assertEquals(Schema.Color.ERROR, column.getParticularColor());
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
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
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
				assertEquals("missing", table.getError());
				assertEquals(Schema.Color.ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
				assertEquals(column1Type, column.getType());
			}
			{
				final com.exedio.dsmf.Table tableX = schema.getTable(TABLE1X);
				assertNotNull(tableX);
				assertEquals(false, tableX.required());
				assertEquals(true, tableX.exists());
				assertEquals("not used", tableX.getError());
				assertEquals(Schema.Color.WARNING, tableX.getParticularColor());

				final Column column = tableX.getColumn(COLUMN1);
				assertEquals(false, column.required());
				assertEquals(true, column.exists());
				assertEquals("not used", column.getError());
				assertEquals(Schema.Color.WARNING, column.getParticularColor());
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
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
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
				assertEquals("missing", table.getError());
				assertEquals(Schema.Color.ERROR, table.getParticularColor());

				final Column column = table.getColumn(COLUMN1);
				assertEquals(true, column.required());
				assertEquals(false, column.exists());
				assertEquals("missing", column.getError());
				assertEquals(Schema.Color.ERROR, column.getParticularColor());
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
			assertEquals(Schema.Color.OK, table.getParticularColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
		}
		{
			assertEquals(!mysql, model.supportsCheckConstraints());
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table attributeItem = schema.getTable(AttributeItem.TYPE.getTableName());
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Schema.Color.OK, attributeItem.getParticularColor());

			assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", "("+protect(AttributeItem.someNotNullString)+" IS NOT NULL) AND ("+LENGTH+"("+protect(AttributeItem.someNotNullString)+")<="+StringField.DEFAULT_LENGTH+")");
			assertCheckConstraint(attributeItem, "AttribuItem_someBoolea_Ck", "("+protect(AttributeItem.someBoolean)+" IN (0,1)) OR ("+protect(AttributeItem.someBoolean)+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+protect(AttributeItem.someNotNullBoolean)+" IS NOT NULL) AND ("+protect(AttributeItem.someNotNullBoolean)+" IN (0,1))");
			assertCheckConstraint(attributeItem, "AttributeItem_someEnum_Ck", "("+protect(AttributeItem.someEnum)+" IN (10,20,30)) OR ("+protect(AttributeItem.someEnum)+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullEnu_Ck", "("+protect(AttributeItem.someNotNullEnum)+" IS NOT NULL) AND ("+protect(AttributeItem.someNotNullEnum)+" IN (10,20,30))");
			assertCheckConstraint(attributeItem, "AttrItem_somDataConTyp_Ck", "(("+LENGTH+"("+protect(AttributeItem.someData.getContentType())+")>=1) AND ("+LENGTH+"("+protect(AttributeItem.someData.getContentType())+")<=61)) OR ("+protect(AttributeItem.someData.getContentType())+" IS NULL)");

			assertPkConstraint(attributeItem, "AttributeItem_Pk", null, AttributeItem.TYPE.getPrimaryKeyColumnName());

			assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", AttributeItem.TYPE.getPrimaryKeyColumnName());

			final com.exedio.dsmf.Table uniqueItem = schema.getTable("UNIQUE_ITEMS");
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Schema.Color.OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "IX_ITEMWSU_US", "("+protect("UNIQUE_STRING")+")");
			
			final com.exedio.dsmf.Table doubleUniqueItem = schema.getTable("ItemWithDoubleUnique");
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Schema.Color.OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+protect("string")+","+protect("integer")+")");
			
			final com.exedio.dsmf.Table stringItem = schema.getTable("STRINGITEMS");
			assertNotNull(stringItem);
			assertEquals(null, stringItem.getError());
			assertEquals(Schema.Color.OK, stringItem.getParticularColor());

			final Column min4Max8 = stringItem.getColumn("MIN4_MAX8");
			assertEquals(null, min4Max8.getError());
			assertEquals(Schema.Color.OK, min4Max8.getParticularColor());
			
			final String string8;
			if(hsqldb)
				string8 = "varchar(8)";
			else if(mysql)
				string8 = "varchar(8) character set utf8 binary";
			else
			{
				if(model.getProperties().getDatabaseCustomProperty("varchar")!=null)
					string8 = "VARCHAR2(8)";
				else
					string8 = "NVARCHAR2(8)";
			}
			assertEquals(string8, min4Max8.getType());

			assertCheckConstraint(stringItem, "STRINGITEMS_MIN_4_Ck",     "(("+LENGTH+"("+protect("MIN_4")+")>=4) AND ("+LENGTH+"("+protect("MIN_4")+")<="+StringField.DEFAULT_LENGTH+")) OR ("+protect("MIN_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_MAX_4_Ck",     "("+LENGTH+"("+protect("MAX_4")+")<=4) OR ("+protect("MAX_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_MIN4_MAX8_Ck", "(("+LENGTH+"("+protect("MIN4_MAX8")+")>=4) AND ("+LENGTH+"("+protect("MIN4_MAX8")+")<=8)) OR ("+protect("MIN4_MAX8")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_EXACT_6_Ck",   "("+LENGTH+"("+protect("EXACT_6")+")=6) OR ("+protect("EXACT_6")+" IS NULL)");
		}
	}
	
	private CheckConstraint assertCheckConstraint(final com.exedio.dsmf.Table table, final String constraintName, final String requiredCondition)
	{
		return
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
		final boolean expectedSupported = model.supportsCheckConstraints() || constraintType!=CHECK;
		assertNotNull("no such constraint "+constraintName+", but has "+table.getConstraints(), constraint);
		assertEquals(constraintName, constraintType, constraint.getClass());
		assertEquals(constraintName, requiredCondition, constraint.getRequiredCondition());
		assertEquals(expectedSupported, constraint.isSupported());
		assertEquals(constraintName, expectedSupported ? null : "not supported", constraint.getError());
		assertEquals(constraintName, Schema.Color.OK, constraint.getParticularColor());
		return constraint;
	}
	
	private final String protect(final Type type)
	{
		return protect(type.getTableName());
	}
	
	private final String protect(final Field attribute)
	{
		return protect(attribute.getColumnName());
	}
	
	private final String protect(final String name)
	{
		return model.getDatabase().getDriver().protectName(name);
	}
	
}
