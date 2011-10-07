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
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValue;
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

	public void testOk()
	{
		assertEquals(0, getPrimaryKeyColumnValue(itema));
		assertEquals(1, getPrimaryKeyColumnValue(itemb1));
		assertEquals(2, getPrimaryKeyColumnValue(itemb2));
		assertEquals(3, getPrimaryKeyColumnValue(itemc1));
		assertEquals(0, getPrimaryKeyColumnValue(reffa));
		assertEquals(1, getPrimaryKeyColumnValue(reffb1));
		assertEquals(2, getPrimaryKeyColumnValue(reffb2));
		assertEquals(3, getPrimaryKeyColumnValue(reffc1));
		assertEquals(4, getPrimaryKeyColumnValue(reffN));

		assertEquals(false, InstanceOfAItem.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfB1Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfB2Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfC1Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfRefItem.ref.needsCheckTypeColumn());
		assertEquals(false, InstanceOfRefItem.refb2.needsCheckTypeColumn());

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
		update(InstanceOfAItem.TYPE, itema, InstanceOfB1Item.TYPE);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testWrongB1inA() throws SQLException
	{
		update(InstanceOfAItem.TYPE, itemb1, InstanceOfB2Item.TYPE);
		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testWrongB1inB1() throws SQLException
	{
		update(InstanceOfB1Item.TYPE, itemb1, InstanceOfC1Item.TYPE);
		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testWrongB2inA() throws SQLException
	{
		update(InstanceOfAItem.TYPE, itemb2, InstanceOfB1Item.TYPE);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testWrongC1inA() throws SQLException
	{
		update(InstanceOfAItem.TYPE, itemc1, InstanceOfB2Item.TYPE);
		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testWrongC1inB1() throws SQLException
	{
		update(InstanceOfB1Item.TYPE, itemc1, InstanceOfB1Item.TYPE);
		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(1, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testMissingB1() throws SQLException
	{
		// TODO should fail
		delete(InstanceOfB1Item.TYPE, itemb1);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
	}

	public void testMissingC1() throws SQLException
	{
		// TODO should fail
		delete(InstanceOfC1Item.TYPE, itemc1);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());

		// TODO should fail
		delete(InstanceOfB1Item.TYPE, itemc1);
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
	}

	// TODO refs


	private <T extends Item> void update(
			final Type<T> type,
			final T item,
			final Type<? extends T> newType)
	throws SQLException
	{
		execute(
			"update " + q(getTableName(type)) + " " +
			"set " + q(getTypeColumnName(type)) + "='" + getTypeColumnValue(newType) + "' " +
			"where " + q(getPrimaryKeyColumnName(type)) + "=" + getPrimaryKeyColumnValue(item));
	}

	private <T extends Item> void delete(
			final Type<T> type,
			final T item)
	throws SQLException
	{
		execute(
			"delete from " + q(getTableName(type)) + " " +
			"where " + q(getPrimaryKeyColumnName(type)) + "=" + getPrimaryKeyColumnValue(item));
	}

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private void execute(final String sql) throws SQLException
	{
		restartTransaction();
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
