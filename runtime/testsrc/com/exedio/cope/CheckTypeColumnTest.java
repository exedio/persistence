/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckTypeColumnTest extends AbstractRuntimeTest
{
	public CheckTypeColumnTest()
	{
		super(InstanceOfTest.MODEL);
	}

	InstanceOfAItem itema;
	InstanceOfB1Item itemb1;
	InstanceOfB2Item itemb2;
	InstanceOfC1Item itemc1;

	InstanceOfRefItem reffa;
	InstanceOfRefItem reffb1;
	InstanceOfRefItem reffb2;
	InstanceOfRefItem reffc1;
	InstanceOfRefItem reffN;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		itema = new InstanceOfAItem("itema");
		itemb1 = new InstanceOfB1Item("itemb1");
		itemb2 = new InstanceOfB2Item("itemb2");
		itemc1 = new InstanceOfC1Item("itemc1");

		reffa = new InstanceOfRefItem(itema);
		reffb1 = new InstanceOfRefItem(itemb1);
		reffb2 = new InstanceOfRefItem(itemb2);
		reffc1 = new InstanceOfRefItem(itemc1);
		reffN = new InstanceOfRefItem(null);
	}

	@Override
	public void tearDown() throws Exception
	{
		model.commit();
		model.deleteSchema();
		model.startTransaction("CheckTypeColumnTest");
		super.tearDown();
	}

	private static final int pka = 0;
	private static final int pkb1 = 1;
	private static final int pkb2 = 2;
	private static final int pkc1 = 3;
	private static final int pkrefa = 0;
	private static final int pkrefb1 = 1;
	private static final int pkrefb2 = 2;
	private static final int pkrefc1 = 3;
	private static final int pkrefn = 4;

	public void testOk()
	{
		assertEquals("InstanceOfAItem-" + pka, itema.getCopeID());
		assertEquals("InstanceOfB1Item-" + pkb1, itemb1.getCopeID());
		assertEquals("InstanceOfB2Item-" + pkb2, itemb2.getCopeID());
		assertEquals("InstanceOfC1Item-" + pkc1, itemc1.getCopeID());
		assertEquals("InstanceOfRefItem-" + pkrefa, reffa.getCopeID());
		assertEquals("InstanceOfRefItem-" + pkrefb1, reffb1.getCopeID());
		assertEquals("InstanceOfRefItem-" + pkrefb2, reffb2.getCopeID());
		assertEquals("InstanceOfRefItem-" + pkrefc1, reffc1.getCopeID());
		assertEquals("InstanceOfRefItem-" + pkrefn, reffN.getCopeID());

		try
		{
			InstanceOfAItem.TYPE.getThis().checkTypeColumn();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfAItem.this", e.getMessage());
		}

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
		try
		{
			assertEquals(0, InstanceOfRefItem.refb2.checkTypeColumn());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfRefItem.refb2", e.getMessage());
		}
	}

	public void testWrongA() throws SQLException
	{
		// TODO should fail earlier
		restartTransaction();
		execute(
			"update " + q(getTableName(InstanceOfAItem.TYPE)) + " " +
			"set " + q(getTypeColumnName(InstanceOfAItem.TYPE)) + "='" + getTypeColumnValue(InstanceOfB1Item.TYPE) + "' " +
			"where " + q(getPrimaryKeyColumnName(InstanceOfAItem.TYPE)) + "=" + pka);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumn());
	}


	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void execute(final String sql) throws SQLException
	{
		Connection connection = null;
		try
		{
			connection = SchemaInfo.newConnection(model);
			connection.setAutoCommit(true);
			final Statement statement = connection.createStatement();
			try
			{
				assertEquals(1, statement.executeUpdate(sql));
			}
			finally
			{
				statement.close();
			}
		}
		finally
		{
			if(connection!=null)
				connection.close();
		}
	}

	private String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}
}
