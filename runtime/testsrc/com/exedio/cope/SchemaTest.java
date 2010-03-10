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
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class SchemaTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(SchemaItem.TYPE, SchemaTargetItem.TYPE);
	
	public SchemaTest()
	{
		super(MODEL);
	}

	public void testSchema()
	{
		if(postgresql) return;
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(SchemaItem.TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		String mediaContentTypeCharSet = null;
		if(mysql)
			mediaContentTypeCharSet = " AND (`someData_contentType` regexp '^[-,/,0-9,a-z]*$')";
		assertCheckConstraint(table, "ScheItem_somNotNullStr_Ck", "("+p(SchemaItem.someNotNullString)+" IS NOT NULL) AND ("+l(SchemaItem.someNotNullString)+"<="+StringField.DEFAULT_LENGTH+")");
		assertCheckConstraint(table, "SchemaItem_someBoolean_Ck", "(("+p(SchemaItem.someBoolean)+" IS NOT NULL) AND ("+p(SchemaItem.someBoolean)+" IN (0,1))) OR ("+p(SchemaItem.someBoolean)+" IS NULL)");
		assertCheckConstraint(table, "ScheItem_somNotNullBoo_Ck", "("+p(SchemaItem.someNotNullBoolean)+" IS NOT NULL) AND ("+p(SchemaItem.someNotNullBoolean)+" IN (0,1))");
		assertCheckConstraint(table, "SchemaItem_someEnum_Ck"   , "(("+p(SchemaItem.someEnum)+" IS NOT NULL) AND ("+p(SchemaItem.someEnum)+" IN (10,20,30))) OR ("+p(SchemaItem.someEnum)+" IS NULL)");
		assertCheckConstraint(table, "ScheItem_somNotNullEnu_Ck", "("+p(SchemaItem.someNotNullEnum)+" IS NOT NULL) AND ("+p(SchemaItem.someNotNullEnum)+" IN (10,20,30))");
		assertCheckConstraint(table, "ScheItem_somData_coTyp_Ck", "(("+p(SchemaItem.someData.getContentType())+" IS NOT NULL) AND (("+l(SchemaItem.someData.getContentType())+">=1) AND ("+l(SchemaItem.someData.getContentType())+"<=61)" + (mediaContentTypeCharSet!=null ? mediaContentTypeCharSet : "") + ")) OR ("+p(SchemaItem.someData.getContentType())+" IS NULL)");

		assertPkConstraint(table, "SchemaItem_Pk", null, getPrimaryKeyColumnName(SchemaItem.TYPE));

		assertFkConstraint(table, "SchemaItem_someItem_Fk", "someItem", filterTableName("SchemaTargetItem"), getPrimaryKeyColumnName(SchemaTargetItem.TYPE));

		assertUniqueConstraint(table, "SchemaItem_UNIQUE_S_Unq", "("+p("UNIQUE_S")+")");
		
		assertUniqueConstraint(table, "SchemaItem_doublUniqu_Unq", "("+p("string")+","+p("integer")+")");
		
		final Column min4Max8 = table.getColumn("MIN4_MAX8");
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

		assertCheckConstraint(table, "SchemaItem_MIN_4_Ck",     "(("+p("MIN_4")+" IS NOT NULL) AND (("+l("MIN_4")+">=4) AND ("+l("MIN_4")+"<="+StringField.DEFAULT_LENGTH+"))) OR ("+p("MIN_4")+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_MAX_4_Ck",     "(("+p("MAX_4")+" IS NOT NULL) AND ("+l("MAX_4")+"<=4)) OR ("+p("MAX_4")+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_MIN4_MAX8_Ck", "(("+p("MIN4_MAX8")+" IS NOT NULL) AND (("+l("MIN4_MAX8")+">=4) AND ("+l("MIN4_MAX8")+"<=8))) OR ("+p("MIN4_MAX8")+" IS NULL)");
		assertCheckConstraint(table, "SchemaItem_EXACT_6_Ck",   "(("+p("EXACT_6")+" IS NOT NULL) AND ("+l("EXACT_6")+"=6)) OR ("+p("EXACT_6")+" IS NULL)");
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
