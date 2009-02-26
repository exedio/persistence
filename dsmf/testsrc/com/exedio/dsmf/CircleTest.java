/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

		final Table table1 = new Table(result, TABLE1);
		final Table table2 = new Table(result, TABLE2);

		new Column(table1, PK_COLUMN, stringType);
		new Column(table2, PK_COLUMN, stringType);

		new PrimaryKeyConstraint(table1, PK_NAME1, PK_COLUMN);
		new PrimaryKeyConstraint(table2, PK_NAME2, PK_COLUMN);
		
		new Column(table1, FK_COLUMN, stringType);
		new Column(table2, FK_COLUMN, stringType);

		new ForeignKeyConstraint(table1, FK_NAME1, FK_COLUMN, TABLE2, PK_COLUMN);
		new ForeignKeyConstraint(table2, FK_NAME2, FK_COLUMN, TABLE1, PK_COLUMN);

		new Column(table1, SELF_COLUMN, stringType);
		new ForeignKeyConstraint(table1, SELF_NAME, SELF_COLUMN, TABLE1, PK_COLUMN);

		return result;
	}
	
	public void testCircles()
	{
		final Schema schema = getVerifiedSchema();
		
		final Table table1 = schema.getTable(TABLE1);
		final Table table2 = schema.getTable(TABLE2);
		assertNotNull(table1);
		assertNotNull(table2);
		
		schema.drop();
	}
	
}
