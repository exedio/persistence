/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.FinalItem;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

public class SchemaTest extends TestmodelTest
{
	private static final String TABLE1X = "FinalItemX";
	private static final String COLUMN1X = "nonFinalIntegerX";

	public void testSchema()
	{
		if(postgresql) return;
		final String TABLE1 = getTableName(FinalItem.TYPE);
		final String COLUMN1 = getColumnName(FinalItem.nonFinalInteger);
		assertEquals(mysqlLower("FinalItem"), TABLE1);
		assertEquals("nonFinalInteger", COLUMN1);

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

			final Constraint constraint = table.getConstraint("FinalItem_nonFinalInte_Ck");
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

			final com.exedio.dsmf.Table attributeItem = schema.getTable(getTableName(AttributeItem.TYPE));
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Schema.Color.OK, attributeItem.getParticularColor());

			assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", "("+p(AttributeItem.someNotNullString)+" IS NOT NULL) AND ("+l(AttributeItem.someNotNullString)+"<="+StringField.DEFAULT_LENGTH+")");
			assertCheckConstraint(attributeItem, "AttribuItem_someBoolea_Ck", "("+p(AttributeItem.someBoolean)+" IN (0,1)) OR ("+p(AttributeItem.someBoolean)+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+p(AttributeItem.someNotNullBoolean)+" IS NOT NULL) AND ("+p(AttributeItem.someNotNullBoolean)+" IN (0,1))");
			assertCheckConstraint(attributeItem, "AttributeItem_someEnum_Ck", "("+p(AttributeItem.someEnum)+" IN (10,20,30)) OR ("+p(AttributeItem.someEnum)+" IS NULL)");
			assertCheckConstraint(attributeItem, "AttrItem_somNotNullEnu_Ck", "("+p(AttributeItem.someNotNullEnum)+" IS NOT NULL) AND ("+p(AttributeItem.someNotNullEnum)+" IN (10,20,30))");
			assertCheckConstraint(attributeItem, "AttrItem_somDataConTyp_Ck", "(("+l(AttributeItem.someData.getContentType())+">=1) AND ("+l(AttributeItem.someData.getContentType())+"<=61)) OR ("+p(AttributeItem.someData.getContentType())+" IS NULL)");

			assertPkConstraint(attributeItem, "AttributeItem_Pk", null, getPrimaryKeyColumnName(AttributeItem.TYPE));

			assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", mysqlLower("EmptyItem"), getPrimaryKeyColumnName(AttributeItem.TYPE));

			final com.exedio.dsmf.Table uniqueItem = schema.getTable(mysqlLower("UNIQUE_ITEMS"));
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Schema.Color.OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "UNIQUE_ITEMS_UNIQUE_S_Unq", "("+p("UNIQUE_S")+")");
			
			final com.exedio.dsmf.Table doubleUniqueItem = schema.getTable(mysqlLower("ItemWithDoubleUnique"));
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Schema.Color.OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+p("string")+","+p("integer")+")");
			
			final com.exedio.dsmf.Table stringItem = schema.getTable(mysqlLower("STRINGITEMS"));
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
				if(model.getProperties().getOracleVarchar())
					string8 = "VARCHAR2(24 BYTE)"; // varchar specifies bytes
				else
					string8 = "NVARCHAR2(8)"; // nvarchar specifies characters
			}
			assertEquals(string8, min4Max8.getType());

			assertCheckConstraint(stringItem, "STRINGITEMS_MIN_4_Ck",     "(("+l("MIN_4")+">=4) AND ("+l("MIN_4")+"<="+StringField.DEFAULT_LENGTH+")) OR ("+p("MIN_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_MAX_4_Ck",     "("+l("MAX_4")+"<=4) OR ("+p("MAX_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_MIN4_MAX8_Ck", "(("+l("MIN4_MAX8")+">=4) AND ("+l("MIN4_MAX8")+"<=8)) OR ("+p("MIN4_MAX8")+" IS NULL)");
			assertCheckConstraint(stringItem, "STRINGITEMS_EXACT_6_Ck",   "("+l("EXACT_6")+"=6) OR ("+p("EXACT_6")+" IS NULL)");
		}
	}
	
	private final String p(final Field attribute)
	{
		return p(getColumnName(attribute));
	}
	
	private final String p(final String name)
	{
		return model.getDatabase().getDriver().protectName(name);
	}
	
	private final String l(final FunctionField f)
	{
		return model.getDatabase().dialect.stringLength + '(' + p(f) + ')';
	}
	
	private final String l(final String f)
	{
		return model.getDatabase().dialect.stringLength + '(' + p(f) + ')';
	}
}
