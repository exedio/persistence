/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
		}

		restartTransaction();
		for(final StringField field : fields)
		{
			assertEquals("x"           , field.get(min));
			assertEquals(makeMax(field), field.get(max));
		}
	}

	private static String makeMax(final StringField field)
	{
		// TODO test with multi-byte characters, also 4 and 5 byte
		final int length = Math.min(field.getMaximumLength(), 3*1000*1000);
		final char[] buf = new char[length];

		char val = 'A';
		for(int i = 0; i<length; i++)
		{
			buf[i] = val;
			val++;
			if(val>'Z')
				val = 'A';
		}

		return new String(buf);
	}

	public void testSchema()
	{
		assertSchema();
	}
}
