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

import static com.exedio.cope.MakeMaxStringTest.makeMax;
import static com.exedio.cope.MakeMaxStringTest.makeMax2;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNotNull;
import static com.exedio.cope.SchemaTypeStringMysqlItem.TYPE;
import static com.exedio.cope.SchemaTypeStringMysqlItem.long3Max;
import static com.exedio.cope.SchemaTypeStringMysqlItem.long3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.medium3Max;
import static com.exedio.cope.SchemaTypeStringMysqlItem.medium3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.mediumExt3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.text3Max;
import static com.exedio.cope.SchemaTypeStringMysqlItem.text3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.textExt3Max;
import static com.exedio.cope.SchemaTypeStringMysqlItem.textExt3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.varchar3Max;
import static com.exedio.cope.SchemaTypeStringMysqlItem.varchar3Min;
import static com.exedio.cope.SchemaTypeStringMysqlItem.varcharExt3Max;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import java.util.ArrayList;

public class SchemaTypeStringMysqlTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringMysqlTest()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
		if(!mysql)
			return;

		assertType("varchar(1)" , varchar3Min);
		assertType("varchar(85)", varchar3Max);
		assertType("varchar(85)", varcharExt3Max);
		assertType("varchar(86)", textExt3Min);
		assertType("text", text3Min);
		assertType("text", text3Max);
		assertType("varchar(20845)", textExt3Max);
		assertType("mediumtext", medium3Min);
		assertType("mediumtext", mediumExt3Min);
		assertType("mediumtext", medium3Max);
		assertType("longtext", long3Min);
		assertType("longtext", long3Max);
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
		final SchemaTypeStringMysqlItem min = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax(field)));
		final SchemaTypeStringMysqlItem max = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax2(field)));
		final SchemaTypeStringMysqlItem max2 = TYPE.newItem(sv);

		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
			assertEquals(makeMax2(field),field.get(max2));
		}

		restartTransaction();
		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
			assertEquals(makeMax2(field),field.get(max2));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
