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
			type += " COLLATE \"ucs_basic\"" + NOT_NULL;
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


	/**
	 * Tests, whether trailing / leading spaces are significant in comparisons.
	 * <p>
	 * On HsqlDB trailing spaces are significant, if sql.pad_space is set to false:
	 * <a href="https://hsqldb.org/doc/guide/dbproperties-chapt.html#N169B5">String Comparison with Padding</a>
	 * <p>
	 * On MySQL trailing spaces are significant, if the collation is NO PAD:
	 * <a href="https://dev.mysql.com/doc/refman/8.0/en/charset-binary-collations.html#charset-binary-collations-trailing-space-comparisons">Trailing Space Handling in Comparisons</a>
	 */
	@Test void testTrailingPad()
	{
		final var emptyMap        = spaceItems(""     );
		final var spaceMap        = spaceItems(" "    );
		final var space2Map       = spaceItems("  "   );
		final var spaceBeforeMap  = spaceItems( " x"  );
		final var space2BeforeMap = spaceItems("  x"  );
		final var spaceAfterMap   = spaceItems(  "x " );
		final var space2AfterMap  = spaceItems(  "x  ");
		final var spaceAroundMap  = spaceItems( " x " );
		final var space2AroundMap = spaceItems("  x  ");
		final var spaceNoneMap    = spaceItems(  "x"  );
		final var spaceWithinMap  = spaceItems( "x x" );
		final var space2WithinMap = spaceItems("x  x" );
		final var upperMap        = spaceItems(  "X"  );
		restartTransaction();

		for(final SchemaTypeStringField f : searchFields())
		{
			final StringItem empty        = emptyMap.get(f);
			final StringItem space        = spaceMap.get(f);
			final StringItem space2       = space2Map.get(f);
			final StringItem spaceBefore  = spaceBeforeMap.get(f);
			final StringItem space2Before = space2BeforeMap.get(f);
			final StringItem spaceAfter   = spaceAfterMap.get(f);
			final StringItem space2After  = space2AfterMap.get(f);
			final StringItem spaceAround  = spaceAroundMap.get(f);
			final StringItem space2Around = space2AroundMap.get(f);
			final StringItem spaceNone    = spaceNoneMap.get(f);
			final StringItem spaceWithin  = spaceWithinMap.get(f);
			final StringItem space2Within = space2WithinMap.get(f);
			final StringItem upper        = upperMap.get(f);

			assertEquals("",      empty       .get()); //  0
			assertEquals(" ",     space       .get()); //  1
			assertEquals("  ",    space2      .get()); //  2
			assertEquals( " x",   spaceBefore .get()); //  3
			assertEquals("  x",   space2Before.get()); //  4
			assertEquals(  "x ",  spaceAfter  .get()); //  5
			assertEquals(  "x  ", space2After .get()); //  6
			assertEquals( " x ",  spaceAround .get()); //  7
			assertEquals("  x  ", space2Around.get()); //  8
			assertEquals(  "x",   spaceNone   .get()); //  9
			assertEquals( "x x",  spaceWithin .get()); // 10
			assertEquals("x  x",  space2Within.get()); // 11
			assertEquals(  "X",   upper       .get()); // 12

			assertEquals(List.of(empty),        f.searchEqual(""));
			assertEquals(List.of(space),        f.searchEqual(" "));
			assertEquals(List.of(space2),       f.searchEqual("  "));
			assertEquals(List.of(spaceBefore),  f.searchEqual( " x"));
			assertEquals(List.of(space2Before), f.searchEqual("  x"));
			assertEquals(List.of(spaceAfter),   f.searchEqual(  "x "));
			assertEquals(List.of(space2After),  f.searchEqual(  "x  "));
			assertEquals(List.of(spaceAround),  f.searchEqual( " x "));
			assertEquals(List.of(space2Around), f.searchEqual("  x  "));
			assertEquals(List.of(spaceNone),    f.searchEqual(  "x"));

			assertEquals(List.of(space2After), f.searchIn("x  ", "y"));
			assertEquals(List.of(spaceNone),   f.searchIn("x",   "y"));

			assertEquals(List.of(       space, space2, spaceBefore, space2Before, spaceAfter, space2After, spaceAround, space2Around, spaceNone, spaceWithin, space2Within, upper), f.searchNotEqual(""));
			assertEquals(List.of(empty, space, space2, spaceBefore, space2Before, spaceAfter, space2After,              space2Around, spaceNone, spaceWithin, space2Within, upper), f.searchNotEqual(" x "));

			assertEquals(List.of(spaceWithin),  f.searchEqual("x x"));
			assertEquals(List.of(space2Within), f.searchEqual("x  x"));
			assertEquals(List.of(),             f.searchEqual("x   x"));
			assertEquals(List.of(),             f.searchEqual("xx"));

			assertEquals(List.of(upper), f.searchEqual("X"));
		}
	}

	private static HashMap<SchemaTypeStringField, StringItem> spaceItems(final String value)
	{
		final HashMap<SchemaTypeStringField, StringItem> result = new HashMap<>();
		for(final SchemaTypeStringField field : searchFields())
			result.put(field, field.add(value));
		return result;
	}

	private static List<SchemaTypeStringField> searchFields()
	{
		return SchemaTypeStringField.get(TYPE).
				stream().
				filter(f -> f!=f1 && f!=f2). // too short for containing the values and has no additional relevance for test
				toList();
	}


	@Test void testSchema()
	{
		assertSchema();
	}
}
