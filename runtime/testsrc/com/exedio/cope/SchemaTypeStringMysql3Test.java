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
import static com.exedio.cope.MakeMaxStringTest.newItem;
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

public class SchemaTypeStringMysql3Test extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringMysql3Test()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
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

	private void assertType(final String type, final StringField field)
	{
		assertEquals(
				field.getID(),
				type + " CHARACTER SET utf8 COLLATE utf8_bin" + (supportsNotNull(model) ? NOT_NULL : ""),
				model.getSchema().getTable(getTableName(TYPE)).getColumn(getColumnName(field)).getType());
	}

	public void testValues()
	{
		if(oracle)
			return;

		final ArrayList<StringField> fields = new ArrayList<>();
		for(final Field<?> field : TYPE.getFields())
			fields.add((StringField)field);

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		for(final StringField field : fields)
			sv.add(field.map("x"));
		final SchemaTypeStringMysql3Item min = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax1(field)));
		final SchemaTypeStringMysql3Item max1 = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax2(field)));
		final SchemaTypeStringMysql3Item max2 = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax3(field)));
		final SchemaTypeStringMysql3Item max3 = TYPE.newItem(sv);
		sv.clear();
		final boolean mb4 = model.supportsUTF8mb4();
		for(final StringField field : fields)
			sv.add(field.map(makeMax4(field)));
		final SchemaTypeStringMysql3Item max4 = newItem(TYPE, sv, mb4);

		for(final StringField field : fields)
		{
			assertEquals("x"            , field.get(min));
			assertEquals(makeMax1(field), field.get(max1));
			assertEquals(makeMax2(field), field.get(max2));
			assertEquals(makeMax3(field), field.get(max3));
			if(mb4)
				assertEquals(makeMax4(field), field.get(max4));
		}

		restartTransaction();
		for(final StringField field : fields)
		{
			assertEquals("x"            , field.get(min));
			assertEquals(makeMax1(field), field.get(max1));
			assertEquals(makeMax2(field), field.get(max2));
			assertEquals(makeMax3(field), field.get(max3));
			if(mb4)
				assertEquals(makeMax4(field), field.get(max4));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
