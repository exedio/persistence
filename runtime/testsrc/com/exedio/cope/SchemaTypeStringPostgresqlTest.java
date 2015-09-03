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
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.TYPE;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.textMax;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.textMin;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.varcharMax;
import static com.exedio.cope.SchemaTypeStringPostgresqlItem.varcharMin;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import com.exedio.cope.SchemaTypeStringField.StringItem;
import java.util.HashMap;
import java.util.List;

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

	private void assertType(final String type, final SchemaTypeStringField field)
	{
		assertEquals(
				field.getID(),
				type + (supportsNotNull(model) ? NOT_NULL : ""),
				field.getSchemaType());
	}

	public void testValues()
	{
		if(oracle)
			return;

		final List<SchemaTypeStringField> fields = SchemaTypeStringField.get(TYPE);
		assertEquals(4, fields.size());

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
