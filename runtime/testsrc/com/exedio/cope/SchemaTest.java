/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

public class SchemaTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(SchemaItem.TYPE, SchemaTargetItem.TYPE);
	
	public SchemaTest()
	{
		super(MODEL);
	}
	
	private static final String TABLE1X = "SchemaItemX";
	private static final String COLUMN1X = "nonFinalIntegerX";

	public void testSchema()
	{
		if(postgresql) return;
		final String TABLE1 = getTableName(SchemaItem.TYPE);
		final String COLUMN1 = getColumnName(SchemaItem.nonFinalInteger);
		assertEquals(filterTableName("SchemaItem"), TABLE1);
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

			final Constraint constraint = table.getConstraint("SchemItem_nonFinalInte_Ck");
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
		// Foreign Key Constraint still missing
		{
			final Schema schema = model.getVerifiedSchema();

			final com.exedio.dsmf.Table table = schema.getTable(TABLE1);
			assertNotNull(table);
			assertEquals(true, table.required());
			assertEquals(true, table.exists());
			assertEquals(null, table.getError());
			assertEquals(Schema.Color.OK, table.getParticularColor());
			assertEquals(Schema.Color.ERROR, table.getCumulativeColor());

			final Column column = table.getColumn(COLUMN1);
			assertEquals(true, column.required());
			assertEquals(true, column.exists());
			assertEquals(null, column.getError());
			assertEquals(Schema.Color.OK, column.getParticularColor());
			assertEquals(column1Type, column.getType());
			
			table.getConstraint("SchemaItem_someItem_Fk").create();
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
			assertEquals(Schema.Color.OK, table.getCumulativeColor());

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

			final com.exedio.dsmf.Table attributeItem = schema.getTable(getTableName(SchemaItem.TYPE)); // TODO same table as above
			assertNotNull(attributeItem);
			assertEquals(null, attributeItem.getError());
			assertEquals(Schema.Color.OK, attributeItem.getParticularColor());

			String mediaContentTypeCharSet = null;
			if(mysql)
				mediaContentTypeCharSet = " AND (`someData_contentType` regexp '^[-,/,0-9,a-z]*$')";
			assertCheckConstraint(attributeItem, "ScheItem_somNotNullStr_Ck", "("+p(SchemaItem.someNotNullString)+" IS NOT NULL) AND ("+l(SchemaItem.someNotNullString)+"<="+StringField.DEFAULT_LENGTH+")");
			assertCheckConstraint(attributeItem, "SchemaItem_someBoolean_Ck", "(("+p(SchemaItem.someBoolean)+" IS NOT NULL) AND ("+p(SchemaItem.someBoolean)+" IN (0,1))) OR ("+p(SchemaItem.someBoolean)+" IS NULL)");
			assertCheckConstraint(attributeItem, "ScheItem_somNotNullBoo_Ck", "("+p(SchemaItem.someNotNullBoolean)+" IS NOT NULL) AND ("+p(SchemaItem.someNotNullBoolean)+" IN (0,1))");
			assertCheckConstraint(attributeItem, "SchemaItem_someEnum_Ck"   , "(("+p(SchemaItem.someEnum)+" IS NOT NULL) AND ("+p(SchemaItem.someEnum)+" IN (10,20,30))) OR ("+p(SchemaItem.someEnum)+" IS NULL)");
			assertCheckConstraint(attributeItem, "ScheItem_somNotNullEnu_Ck", "("+p(SchemaItem.someNotNullEnum)+" IS NOT NULL) AND ("+p(SchemaItem.someNotNullEnum)+" IN (10,20,30))");
			assertCheckConstraint(attributeItem, "ScheItem_somData_coTyp_Ck", "(("+p(SchemaItem.someData.getContentType())+" IS NOT NULL) AND (("+l(SchemaItem.someData.getContentType())+">=1) AND ("+l(SchemaItem.someData.getContentType())+"<=61)" + (mediaContentTypeCharSet!=null ? mediaContentTypeCharSet : "") + ")) OR ("+p(SchemaItem.someData.getContentType())+" IS NULL)");

			assertPkConstraint(attributeItem, "SchemaItem_Pk", null, getPrimaryKeyColumnName(SchemaItem.TYPE));

			assertFkConstraint(attributeItem, "SchemaItem_someItem_Fk", "someItem", filterTableName("SchemaTargetItem"), getPrimaryKeyColumnName(SchemaTargetItem.TYPE));

			final com.exedio.dsmf.Table uniqueItem = schema.getTable(getTableName(SchemaItem.TYPE)); // TODO same table as above
			assertNotNull(uniqueItem);
			assertEquals(null, uniqueItem.getError());
			assertEquals(Schema.Color.OK, uniqueItem.getParticularColor());
			
			assertUniqueConstraint(uniqueItem, "SchemaItem_UNIQUE_S_Unq", "("+p("UNIQUE_S")+")");
			
			final com.exedio.dsmf.Table doubleUniqueItem = schema.getTable(getTableName(SchemaItem.TYPE)); // TODO same table as above
			assertNotNull(doubleUniqueItem);
			assertEquals(null, doubleUniqueItem.getError());
			assertEquals(Schema.Color.OK, doubleUniqueItem.getParticularColor());
			
			assertUniqueConstraint(doubleUniqueItem, "SchemaItem_doublUniqu_Unq", "("+p("string")+","+p("integer")+")");
			
			final com.exedio.dsmf.Table stringItem = schema.getTable(getTableName(SchemaItem.TYPE)); // TODO same table as above
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
				string8 = "VARCHAR2(24 BYTE)"; // varchar specifies bytes
			assertEquals(string8, min4Max8.getType());

			assertCheckConstraint(stringItem, "SchemaItem_MIN_4_Ck",     "(("+p("MIN_4")+" IS NOT NULL) AND (("+l("MIN_4")+">=4) AND ("+l("MIN_4")+"<="+StringField.DEFAULT_LENGTH+"))) OR ("+p("MIN_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "SchemaItem_MAX_4_Ck",     "(("+p("MAX_4")+" IS NOT NULL) AND ("+l("MAX_4")+"<=4)) OR ("+p("MAX_4")+" IS NULL)");
			assertCheckConstraint(stringItem, "SchemaItem_MIN4_MAX8_Ck", "(("+p("MIN4_MAX8")+" IS NOT NULL) AND (("+l("MIN4_MAX8")+">=4) AND ("+l("MIN4_MAX8")+"<=8))) OR ("+p("MIN4_MAX8")+" IS NULL)");
			assertCheckConstraint(stringItem, "SchemaItem_EXACT_6_Ck",   "(("+p("EXACT_6")+" IS NOT NULL) AND ("+l("EXACT_6")+"=6)) OR ("+p("EXACT_6")+" IS NULL)");
		}
	}
	
	private final String p(final Field attribute)
	{
		return p(getColumnName(attribute));
	}
	
	private final String p(final String name)
	{
		return model.connect().database.dsmfDialect.quoteName(name);
	}
	
	private final String l(final FunctionField f)
	{
		return model.connect().database.dialect.stringLength + '(' + p(f) + ')';
	}
	
	private final String l(final String f)
	{
		return model.connect().database.dialect.stringLength + '(' + p(f) + ')';
	}
}
