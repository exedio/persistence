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

package com.exedio.dsmf;

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.Test;

public class SQLInjectionTest extends SchemaTest
{
	private static final String MESSAGE_PREFIX = "database name contains forbidden characters: ";

	private static final String TABLE = "InjectionTable";
	private static final String COLUMN = "injectionColumn";

	@Test void testTableCreate()
	{
		final String BAD_TABLE_PRE = p("Injection1") + " (badColumn varchar(30) ) --";
		final String BAD_TABLE = BAD_TABLE_PRE.substring(1);

		final Schema schema = newSchema();
		final Table badTable = schema.newTable(BAD_TABLE);
		badTable.newColumn(COLUMN, stringType);
		assertFails(
				badTable::create,
				IllegalArgumentException.class,
				MESSAGE_PREFIX + BAD_TABLE);
	}

	@Test void testColumnCreate()
	{
		final String BAD_COLUMN_PRE = p("badColumn1")+" varchar(30), " + p("badColumn2");
		final String BAD_COLUMN = BAD_COLUMN_PRE.substring(1, BAD_COLUMN_PRE.length()-1);

		final Schema schema = newSchema();
		final Table table = schema.newTable(TABLE);
		table.newColumn(BAD_COLUMN, stringType);
		assertFails(
				table::create,
				IllegalArgumentException.class,
				MESSAGE_PREFIX + BAD_COLUMN);
	}
}
