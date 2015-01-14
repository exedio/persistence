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
import static com.exedio.cope.SchemaTypeIntegerItem.TYPE;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte1u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte2u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4l;
import static com.exedio.cope.SchemaTypeIntegerItem.byte4u;
import static com.exedio.cope.SchemaTypeIntegerItem.byte8;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import java.util.ArrayList;
import junit.framework.AssertionFailedError;

public class SchemaTypeIntegerTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public SchemaTypeIntegerTest()
	{
		super(MODEL);
	}

	public void testType()
	{
		final String type2;
		final String type4;
		final String type8;
		switch(dialect)
		{
			case hsqldb:
				type2 = type4 = "INTEGER";
				type8 = "BIGINT";
				break;
			case mysql:
				type2 = type4 = "int";
				type8 = "bigint";
				break;
			case oracle:
				type2 = type4 = "NUMBER(10)";
				type8 = "NUMBER(20)";
				break;
			case postgresql:
				type2 = "smallint";
				type4 = "integer";
				type8 = "bigint";
				break;
			default:
				throw new AssertionFailedError(dialect.name());
		}

		assertType(type2, byte1);
		assertType(type2, byte1l);
		assertType(type2, byte1u);
		assertType(type2, byte2);
		assertType(type4, byte2l);
		assertType(type4, byte2u);
		assertType(type4, byte4);
		assertType(type8, byte4l);
		assertType(type8, byte4u);
		assertType(type8, byte8);
	}

	private void assertType(final String expected, final LongField field)
	{
		assertEquals(
				field.getID(),
				expected + (SchemaInfo.supportsNotNull(MODEL) ? NOT_NULL : ""),
				model.getSchema().
					getTable(getTableName(field.getType())).
					getColumn(getColumnName(field)).
					getType());
	}

	public void testValues()
	{
		final ArrayList<LongField> fields = new ArrayList<>();
		for(final Field<?> field : TYPE.getFields())
			fields.add((LongField)field);

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		for(final LongField field : fields)
			sv.add(field.map(field.getMinimum()));
		final SchemaTypeIntegerItem min = TYPE.newItem(sv);
		sv.clear();
		for(final LongField field : fields)
			sv.add(field.map(field.getMaximum()));
		final SchemaTypeIntegerItem max = TYPE.newItem(sv);
		sv.clear();
		for(final LongField field : fields)
			sv.add(field.map(0l));
		final SchemaTypeIntegerItem zero = TYPE.newItem(sv);

		for(final LongField field : fields)
		{
			assertEquals(field.getID(), field.getMinimum(), field.getMandatory(min ));
			assertEquals(field.getID(), field.getMaximum(), field.getMandatory(max ));
			assertEquals(field.getID(), 0                 , field.getMandatory(zero));
		}

		restartTransaction();
		for(final LongField field : fields)
		{
			assertEquals(field.getID(), field.getMinimum(), field.getMandatory(min ));
			assertEquals(field.getID(), field.getMaximum(), field.getMandatory(max ));
			assertEquals(field.getID(), 0                 , field.getMandatory(zero));
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
