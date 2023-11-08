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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaTypeIntegerItem.TYPE;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte3;
import static com.exedio.cope.SchemaTypeIntegerItem.byte3l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte3u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte8;
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class SchemaTypeIntegerTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeIntegerTest()
	{
		super(MODEL);
	}

	@Test void testTypeByte()
	{
		final String type1;
		final String type2;
		final String type3;
		final String type4;
		final String type8;
		switch(dialect)
		{
			case hsqldb:
				type1 = "TINYINT";
				type2 = "SMALLINT";
				type3 = type4 = "INTEGER";
				type8 = "BIGINT";
				break;
			case mysql:
				type1 = "tinyint";
				type2 = "smallint";
				type3 = "mediumint";
				type4 = "int";
				type8 = "bigint";
				break;
			case postgresql:
				type1 = type2 = "smallint";
				type3 = type4 = "integer";
				type8 = "bigint";
				break;
			default:
				throw new AssertionError(dialect.name());
		}

		assertType(type1, byte1);
		assertType(type2, byte1l);
		assertType(type2, byte1u);
		assertType(type2, byte2);
		assertType(type3, byte2l);
		assertType(type3, byte2u);
		assertType(type3, byte3);
		assertType(type4, byte3l);
		assertType(type4, byte3u);
		assertType(type4, byte4);
		assertType(type8, byte4l);
		assertType(type8, byte4u);
		assertType(type8, byte8);
	}

	private void assertType(final String expected, final LongField field)
	{
		assertEquals(
				expected + NOT_NULL,
				model.getSchema().getTable(getTableName(TYPE)).getColumn(getColumnName(field)).getType(),
				field.getID());
	}

	@Test void testValues()
	{
		final ArrayList<LongField> fields = new ArrayList<>();
		for(final Field<?> field : TYPE.getFields())
			fields.add((LongField)field);

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		for(final LongField field : fields)
			sv.add(SetValue.map(field, field.getMinimum()));
		final SchemaTypeIntegerItem min = TYPE.newItem(sv);
		sv.clear();
		for(final LongField field : fields)
			sv.add(SetValue.map(field, field.getMaximum()));
		final SchemaTypeIntegerItem max = TYPE.newItem(sv);
		sv.clear();
		for(final LongField field : fields)
			sv.add(SetValue.map(field, 0l));
		final SchemaTypeIntegerItem zero = TYPE.newItem(sv);

		for(final LongField field : fields)
		{
			assertEquals(field.getMinimum(), field.getMandatory(min ), field.getID());
			assertEquals(field.getMaximum(), field.getMandatory(max ), field.getID());
			assertEquals(0,                  field.getMandatory(zero), field.getID());
		}

		restartTransaction();
		for(final LongField field : fields)
		{
			assertEquals(field.getMinimum(), field.getMandatory(min ), field.getID());
			assertEquals(field.getMaximum(), field.getMandatory(max ), field.getID());
			assertEquals(0,                  field.getMandatory(zero), field.getID());
		}
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
