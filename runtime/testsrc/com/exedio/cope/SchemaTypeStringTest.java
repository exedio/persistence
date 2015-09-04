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
import static com.exedio.cope.SchemaInfo.supportsNotNull;
import static com.exedio.cope.SchemaTypeStringItem.TYPE;
import static com.exedio.cope.SchemaTypeStringItem.f1;
import static com.exedio.cope.SchemaTypeStringItem.f10485760;
import static com.exedio.cope.SchemaTypeStringItem.f10485761;
import static com.exedio.cope.SchemaTypeStringItem.f16382Ext;
import static com.exedio.cope.SchemaTypeStringItem.f16383;
import static com.exedio.cope.SchemaTypeStringItem.f16383Ext;
import static com.exedio.cope.SchemaTypeStringItem.f16384;
import static com.exedio.cope.SchemaTypeStringItem.f16384Ext;
import static com.exedio.cope.SchemaTypeStringItem.f2;
import static com.exedio.cope.SchemaTypeStringItem.f20845Ext;
import static com.exedio.cope.SchemaTypeStringItem.f20846Ext;
import static com.exedio.cope.SchemaTypeStringItem.f21845;
import static com.exedio.cope.SchemaTypeStringItem.f21846;
import static com.exedio.cope.SchemaTypeStringItem.f4194303;
import static com.exedio.cope.SchemaTypeStringItem.f4194304;
import static com.exedio.cope.SchemaTypeStringItem.f5592405;
import static com.exedio.cope.SchemaTypeStringItem.f5592406;
import static com.exedio.cope.SchemaTypeStringItem.f85;
import static com.exedio.cope.SchemaTypeStringItem.f85Ext;
import static com.exedio.cope.SchemaTypeStringItem.f86;
import static com.exedio.cope.SchemaTypeStringItem.f86Ext;
import static com.exedio.cope.SchemaTypeStringItem.fMax;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import com.exedio.cope.SchemaTypeStringField.StringItem;
import java.util.HashMap;
import java.util.List;

public class SchemaTypeStringTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringTest()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
		// make sure, relation types are as small as possible -
		// just the primary key and the StringField
		try
		{
			SchemaInfo.getTypeColumnName(f1.sourceType());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no type column for SchemaTypeStringItem-f1", e.getMessage());
		}
		try
		{
			SchemaInfo.getUpdateCounterColumnName(f1.sourceType());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no update counter for SchemaTypeStringItem-f1", e.getMessage());
		}

		assertEquals(false, f85   .isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  f85Ext.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(false, f85   .sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));
		assertEquals(true,  f85Ext.sourceField.isAnnotationPresent(MysqlExtendedVarchar.class));

		if(mysql && !model.getConnectProperties().mysqlUtf8mb4)
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
			assertType("varchar(20845)", f20845Ext);
			assertType("mediumtext",     f20846Ext);
		}
		else if(mysql && model.getConnectProperties().mysqlUtf8mb4)
		{
			assertType("varchar(1)",       f1);
			assertType("varchar(2)",       f2);
			assertType("varchar(85)",     f85);
			assertType("text",            f86);
			assertType("text",         f16383);
			assertType("mediumtext",   f16384);
			assertType("mediumtext", f4194303);
			assertType("longtext",   f4194304);
			assertType("longtext",   fMax);
			// @MysqlExtendedVarchar
			assertType("varchar(85)",       f85Ext);
			assertType("varchar(86)",       f86Ext);
			assertType("varchar(16382)", f16382Ext);
			assertType("text",           f16383Ext);
			assertType("mediumtext",     f16384Ext);
		}
		else if(postgresql)
		{
			assertType("varchar(1)", f1);
			assertType("varchar(2)", f2);
			assertType("varchar(10485760)", f10485760);
			assertType("text",              f10485761);
			assertType("text",              fMax);
		}
	}

	private void assertType(String type, final SchemaTypeStringField field)
	{
		if(mysql)
		{
			final String mb4 = model.getConnectProperties().mysqlUtf8mb4 ? "mb4" : "";
			type = type + " CHARACTER SET utf8"+mb4+" COLLATE utf8"+mb4+"_bin" + (supportsNotNull(model) ? NOT_NULL : "");
		}
		else if(postgresql)
			type = type + (supportsNotNull(model) ? NOT_NULL : "");
		else
			throw new RuntimeException();

		assertEquals(
				field.getID(),
				type,
				field.getSchemaType());
	}

	public void testValues()
	{
		if(oracle)
			return;

		final List<SchemaTypeStringField> fields = SchemaTypeStringField.get(TYPE);
		assertEquals(22, fields.size());

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
		final boolean mb4 = model.supportsUTF8mb4();
		for(final SchemaTypeStringField field : fields)
			max4.put(field, field.add(makeMax4(field), mb4 || (field==f1))); // f1 works because surrogates do not fit into string of length 1

		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			if(mb4 || (field==f1))
				assertEquals(makeMax4(field), field.get(max4.get(field)));
		}

		restartTransaction();
		for(final SchemaTypeStringField field : fields)
		{
			assertEquals("x"            , field.get(min .get(field)));
			assertEquals(makeMax1(field), field.get(max1.get(field)));
			assertEquals(makeMax2(field), field.get(max2.get(field)));
			assertEquals(makeMax3(field), field.get(max3.get(field)));
			if(mb4 || (field==f1))
				assertEquals(makeMax4(field), field.get(max4.get(field)));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
