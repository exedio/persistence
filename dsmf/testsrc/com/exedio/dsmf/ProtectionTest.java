/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

/**
 * Test whether all names are protected with quotes,
 * by using sql keyword for names.
 */
public class ProtectionTest extends SchemaReadyTest
{
	private static final String TABLE = "select";
	private static final String PK_COLUMN = "from";
	private static final String PK_NAME = "from_Pk"; // mysql does not store the name of pk constraint, so it created by convention
	private static final String FK_COLUMN = "order";
	private static final String FK_NAME = "desc";
	private static final String UNQ_NAME = "asc";
	private static final String CHK_NAME = "group";

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		final Table table = new Table(result, TABLE);

		new Column(table, PK_COLUMN, stringType);
		new PrimaryKeyConstraint(table, PK_NAME, PK_COLUMN);

		new Column(table, FK_COLUMN, stringType);
		new ForeignKeyConstraint(table, FK_NAME, FK_COLUMN, TABLE, PK_COLUMN);
		new UniqueConstraint(table, UNQ_NAME, "("+p(FK_COLUMN)+")");
		new CheckConstraint(table, CHK_NAME, p(FK_COLUMN)+" IS NOT NULL");

		return result;
	}
	
	public void testIt()
	{
		final Schema schema = getVerifiedSchema();
		
		final Table table = schema.getTable(TABLE);
		assertNotNull(table);
		assertEquals(TABLE, table.getName());
		
		final Column pk = table.getColumn(PK_COLUMN);
		assertNotNull(pk);
		assertSame(table, pk.getTable());
		assertEquals(PK_COLUMN, pk.getName());
		assertEquals(stringType, pk.getType());
		assertEquals(null, table.getError());
		
		final PrimaryKeyConstraint pkc = (PrimaryKeyConstraint)table.getConstraint(PK_NAME);
		assertNotNull(pkc);
		assertSame(table, pkc.getTable());
		assertEquals(PK_NAME, pkc.getName());
		assertEquals(PK_COLUMN, pkc.getPrimaryKeyColumn());
		assertEquals(null, pkc.getError());

		final Column fk = table.getColumn(FK_COLUMN);
		assertNotNull(fk);
		assertSame(table, fk.getTable());
		assertEquals(FK_COLUMN, fk.getName());
		assertEquals(stringType, fk.getType());
		assertEquals(null, fk.getError());

		final ForeignKeyConstraint fkc = (ForeignKeyConstraint)table.getConstraint(FK_NAME);
		assertNotNull(fkc);
		assertSame(table, fkc.getTable());
		assertEquals(FK_NAME, fkc.getName());
		assertEquals(FK_COLUMN, fkc.getForeignKeyColumn());
		assertEquals(null, fkc.getError());
		
		final UniqueConstraint unc = (UniqueConstraint)table.getConstraint(UNQ_NAME);
		assertNotNull(unc);
		assertSame(table, unc.getTable());
		assertEquals(UNQ_NAME, unc.getName());
		assertEquals("("+p(FK_COLUMN)+")", unc.getClause());
		if(!postgresql)
			assertEquals(null, unc.getError());
		
		final CheckConstraint ckc = (CheckConstraint)table.getConstraint(CHK_NAME);
		assertNotNull(ckc);
		assertSame(table, ckc.getTable());
		assertEquals(CHK_NAME, ckc.getName());
		assertEquals(p(FK_COLUMN)+" IS NOT NULL", ckc.getRequiredCondition());
		assertEquals(supportsCheckConstraints ? null : "not supported", ckc.getError());
		
		if(supportsCheckConstraints)
			ckc.drop();
		fkc.drop(); // in mysql fk constraint must be dropped before unique constraint on the same column
		unc.drop();
		pkc.drop();
		fk.drop();
		
		fk.create();
		pkc.create();
		fkc.create();
		unc.create();
		if(supportsCheckConstraints)
			ckc.create();
		
		schema.drop();
	}
	
}
