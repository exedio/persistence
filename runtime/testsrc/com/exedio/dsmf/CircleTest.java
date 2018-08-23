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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CircleTest extends SchemaReadyTest
{
	private static final String TABLE1 = "CircleTable1";
	private static final String TABLE2 = "CircleTable2";

	private static final String PK_COLUMN = "circlePrimaryKey";
	private static final String PK_NAME1 = "circlePkId1";
	private static final String PK_NAME2 = "circlePkId2";

	private static final String FK_COLUMN = "circleForeignKey";
	private static final String FK_NAME1 = "circleForeignKeyId1";
	private static final String FK_NAME2 = "circleForeignKeyId2";

	private static final String SELF_COLUMN = "selfForeignKey";
	private static final String SELF_NAME = "selfForeignKeyId";

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		final Table table1 = result.newTable(TABLE1);
		final Table table2 = result.newTable(TABLE2);

		final Column pk1 = table1.newColumn(PK_COLUMN, stringType);
		final Column pk2 = table2.newColumn(PK_COLUMN, stringType);

		pk1.newPrimaryKey(PK_NAME1);
		pk2.newPrimaryKey(PK_NAME2);

		final Column fk1 = table1.newColumn(FK_COLUMN, stringType);
		final Column fk2 = table2.newColumn(FK_COLUMN, stringType);

		fk1.newForeignKey(FK_NAME1, TABLE2, PK_COLUMN);
		fk2.newForeignKey(FK_NAME2, TABLE1, PK_COLUMN);

		final Column fkSelf = table1.newColumn(SELF_COLUMN, stringType);
		fkSelf.newForeignKey(SELF_NAME, TABLE1, PK_COLUMN);

		return result;
	}

	@Test void testCircles()
	{
		final Schema schema = getSchema();

		final Table table1 = schema.getTable(TABLE1);
		final Table table2 = schema.getTable(TABLE2);
		assertNotNull(table1);
		assertNotNull(table2);

		schema.drop();
	}

	@Test void testVerify()
	{
		getSchema().verify();
	}
}
