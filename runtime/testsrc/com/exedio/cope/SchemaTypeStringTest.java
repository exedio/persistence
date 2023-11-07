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
import static com.exedio.cope.SchemaTypeStringItem.TYPE;
import static com.exedio.cope.SchemaTypeStringItem.f1;
import static com.exedio.cope.SchemaTypeStringItem.f10485760;
import static com.exedio.cope.SchemaTypeStringItem.f10485761;
import static com.exedio.cope.SchemaTypeStringItem.f16382Ext;
import static com.exedio.cope.SchemaTypeStringItem.f16383Ext;
import static com.exedio.cope.SchemaTypeStringItem.f2;
import static com.exedio.cope.SchemaTypeStringItem.f20845Ext;
import static com.exedio.cope.SchemaTypeStringItem.f20846Ext;
import static com.exedio.cope.SchemaTypeStringItem.f21845;
import static com.exedio.cope.SchemaTypeStringItem.f21846;
import static com.exedio.cope.SchemaTypeStringItem.f5592405;
import static com.exedio.cope.SchemaTypeStringItem.f5592406;
import static com.exedio.cope.SchemaTypeStringItem.f85;
import static com.exedio.cope.SchemaTypeStringItem.f85Ext;
import static com.exedio.cope.SchemaTypeStringItem.f86;
import static com.exedio.cope.SchemaTypeStringItem.f86Ext;
import static com.exedio.cope.SchemaTypeStringItem.fMax;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.SchemaTypeStringField.StringItem;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SchemaTypeStringTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringTest()
	{
		super(MODEL);
	}

	@Test void testSchemaTypes()
	{
		// make sure, relation types are as small as possible -
		// just the primary key and the StringField
		assertFails(
				() -> SchemaInfo.getTypeColumnName(f1.sourceType()),
				IllegalArgumentException.class,
				"no type column for SchemaTypeStringItem-f1");
		assertFails(
				() -> SchemaInfo.getUpdateCounterColumnName(f1.sourceType()),
				IllegalArgumentException.class,
				"no update counter for SchemaTypeStringItem-f1");

		assertEquals(false, f85   .isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  f85Ext.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(false, f85   .sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  f85Ext.sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));

		if(mysql)
		{
			assertType("varchar(1)",       f1);
			assertType("varchar(2)",       f2);
			assertType("varchar(85)",     f85);
			assertType("text",            f86);
			assertType("text",         f21845);
			assertType("mediumtext",   f21846);
			assertType("mediumtext", f5592405);
			assertType("longtext",   f5592406);
			assertType("longtext",   fMax);
			// @MysqlExtendedVarchar
			assertType("varchar(85)",       f85Ext);
			assertType("varchar(86)",       f86Ext);
			assertType("varchar(16382)", f16382Ext);
			assertType("text",           f16383Ext);
			assertType("text",           f20845Ext);
			assertType("mediumtext",     f20846Ext);
		}
		else if(postgresql)
		{
			assertType("character varying(1)", f1);
			assertType("character varying(2)", f2);
			assertType("character varying(10485760)", f10485760);
			assertType("\"text\"",                    f10485761);
			assertType("\"text\"",                    fMax);
		}
	}

	private void assertType(String type, final SchemaTypeStringField field)
	{
		if(mysql)
			type += " CHARACTER SET utf8mb4 COLLATE utf8mb4_bin" + NOT_NULL;
		else if(postgresql)
			type += NOT_NULL;
		else
			throw new RuntimeException();

		assertEquals(
				type,
				field.getSchemaType(),
				field.getID());
	}

	@Test void testValues()
	{
		final List<SchemaTypeStringField> fields = SchemaTypeStringField.get(TYPE);
		assertEquals(17, fields.size());

		final HashMap<SchemaTypeStringField, StringItem> min = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			min.put(field, field.add("x"));

		final HashMap<SchemaTypeStringField, StringItem> max1 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max1.put(field, field.add(makeMax1(field)));

		final HashMap<SchemaTypeStringField, StringItem> max2 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max2.put(field, field.add(makeMax2(field)));

		final HashMap<SchemaTypeStringField, StringItem> max3 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max3.put(field, field.add(makeMax3(field)));

		final HashMap<SchemaTypeStringField, StringItem> max4 = new HashMap<>();
		for(final SchemaTypeStringField field : fields)
			max4.put(field, field.add(makeMax4(field)));

		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			assertEquals(makeMax4(field), field.get(max4.get(field)));
		}

		restartTransaction();
		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			assertEquals(makeMax4(field), field.get(max4.get(field)));
		}
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
