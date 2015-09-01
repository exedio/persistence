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
import static com.exedio.cope.MakeMaxStringTest.makeMax3;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNotNull;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.TYPE;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.textMax;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.textMin;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.varcharMax;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.varcharMin;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import java.util.ArrayList;

public class SchemaTypeStringPostgresqlTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeStringPostgresqlTest()
	{
		super(MODEL);
	}

	public void testSchemaTypes()
	{
		if(!postgresql)
			return;

		assertType("varchar(1)" , varcharMin);
		assertType("varchar(10485760)", varcharMax);
		assertType("text", textMin);
		assertType("text", textMax);
	}

	private void assertType(final String type, final StringField field)
	{
		assertEquals(
				field.getID(),
				type + (supportsNotNull(model) ? NOT_NULL : ""),
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
		final SchemaTypeStringPostgresqlItem min = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax(field)));
		final SchemaTypeStringPostgresqlItem max = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax2(field)));
		final SchemaTypeStringPostgresqlItem max2 = TYPE.newItem(sv);
		sv.clear();
		for(final StringField field : fields)
			sv.add(field.map(makeMax3(field)));
		final SchemaTypeStringPostgresqlItem max3 = TYPE.newItem(sv);

		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
			assertEquals(makeMax2(field),field.get(max2));
			assertEquals(makeMax3(field),field.get(max3));
		}

		restartTransaction();
		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
			assertEquals(makeMax2(field),field.get(max2));
			assertEquals(makeMax3(field),field.get(max3));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
