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

package com.exedio.cope;

import static com.exedio.cope.MakeMaxStringTest.makeMax1;
import static com.exedio.cope.MakeMaxStringTest.makeMax2;
import static com.exedio.cope.MakeMaxStringTest.makeMax3;
import static com.exedio.cope.MakeMaxStringTest.makeMax4;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNotNull;
import static com.exedio.cope.SchemaTypeStringMysql3Item.TYPE;
import static com.exedio.cope.SchemaTypeStringMysql3Item.longMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.longMin;
import static com.exedio.cope.SchemaTypeStringMysql3Item.mediumExtMin;
import static com.exedio.cope.SchemaTypeStringMysql3Item.mediumMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.mediumMin;
import static com.exedio.cope.SchemaTypeStringMysql3Item.textExtMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.textExtMin;
import static com.exedio.cope.SchemaTypeStringMysql3Item.textMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.textMin;
import static com.exedio.cope.SchemaTypeStringMysql3Item.varcharExtMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.varcharMax;
import static com.exedio.cope.SchemaTypeStringMysql3Item.varcharMin;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import java.util.ArrayList;
import java.util.HashMap;

public class SchemaTypeStringMysql3Test extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringMysql3Test()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
		// make sure, relation types are as small as possible -
		// just the primary key and the StringField
		try
		{
			SchemaInfo.getTypeColumnName(varcharMin.sourceType());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no type column for SchemaTypeStringMysql3Item-varcharMin", e.getMessage());
		}
		try
		{
			SchemaInfo.getUpdateCounterColumnName(varcharMin.sourceType());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no update counter for SchemaTypeStringMysql3Item-varcharMin", e.getMessage());
		}

		assertEquals(false, varcharMax   .isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  varcharExtMax.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(false, varcharMax   .sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  varcharExtMax.sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));

		if(!mysql)
			return;

		assertType("varchar(1)" , varcharMin);
		assertType("varchar(85)", varcharMax);
		assertType("varchar(85)", varcharExtMax);
		assertType("varchar(86)", textExtMin);
		assertType("text", textMin);
		assertType("text", textMax);
		assertType("varchar(20845)", textExtMax);
		assertType("mediumtext", mediumMin);
		assertType("mediumtext", mediumExtMin);
		assertType("mediumtext", mediumMax);
		assertType("longtext", longMin);
		assertType("longtext", longMax);
	}

	private void assertType(final String type, final SchemaTypeStringField field)
	{
		assertEquals(
				field.getID(),
				type + " CHARACTER SET utf8 COLLATE utf8_bin" + (supportsNotNull(model) ? NOT_NULL : ""),
				model.getSchema().getTable(getTableName(field.sourceType())).getColumn(getColumnName(field.sourceField)).getType());
	}

	public void testValues()
	{
		if(oracle)
			return;

		final ArrayList<SchemaTypeStringField> fields = new ArrayList<>();
		for(final Feature feature : TYPE.getFeatures())
			if(feature instanceof SchemaTypeStringField)
				fields.add((SchemaTypeStringField)feature);

		final HashMap<SchemaTypeStringField, SchemaTypeStringField.PatternItem> min = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			min.put(field, field.add("x"));

		final HashMap<SchemaTypeStringField, SchemaTypeStringField.PatternItem> max1 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max1.put(field, field.add(makeMax1(field)));

		final HashMap<SchemaTypeStringField, SchemaTypeStringField.PatternItem> max2 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max2.put(field, field.add(makeMax2(field)));

		final HashMap<SchemaTypeStringField, SchemaTypeStringField.PatternItem> max3 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max3.put(field, field.add(makeMax3(field)));

		final HashMap<SchemaTypeStringField, SchemaTypeStringField.PatternItem> max4 = new HashMap<>();
		final boolean mb4 = model.supportsUTF8mb4();
		for(final SchemaTypeStringField field : fields)
			max4.put(field, field.add(makeMax4(field), mb4 || (field==varcharMin))); // varcharMin works because surrogates do not fit into string of length 1

		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			if(mb4 || (field==varcharMin))
				assertEquals(makeMax4(field), field.get(max4.get(field)));
		}

		restartTransaction();
		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			if(mb4 || (field==varcharMin))
				assertEquals(makeMax4(field), field.get(max4.get(field)));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
