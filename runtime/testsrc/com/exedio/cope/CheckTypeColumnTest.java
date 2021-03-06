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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class CheckTypeColumnTest extends TestWithEnvironment
{
	public CheckTypeColumnTest()
	{
		super(InstanceOfModelTest.MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	InstanceOfAItem itema;
	InstanceOfB1Item itemb1;
	InstanceOfB2Item itemb2;
	InstanceOfC1Item itemc1;

	InstanceOfRefItem reffa;
	InstanceOfRefItem reffb1;
	InstanceOfRefItem reffb2;
	InstanceOfRefItem reffc1;
	InstanceOfRefItem reffN;

	@BeforeEach final void setUp()
	{
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

	@AfterEach final void tearDown()
	{
		model.commit();
		model.deleteSchemaForTest();
		model.startTransaction("CheckTypeColumnTest");
	}

	@Test void testOk()
	{
		assertEquals(0, getPrimaryKeyColumnValueL(itema));
		assertEquals(1, getPrimaryKeyColumnValueL(itemb1));
		assertEquals(2, getPrimaryKeyColumnValueL(itemb2));
		assertEquals(3, getPrimaryKeyColumnValueL(itemc1));
		assertEquals(0, getPrimaryKeyColumnValueL(reffa));
		assertEquals(1, getPrimaryKeyColumnValueL(reffb1));
		assertEquals(2, getPrimaryKeyColumnValueL(reffb2));
		assertEquals(3, getPrimaryKeyColumnValueL(reffc1));
		assertEquals(4, getPrimaryKeyColumnValueL(reffN));

		assertEquals(false, InstanceOfAItem.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfB1Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfB2Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfC1Item.TYPE.getThis().needsCheckTypeColumn());
		assertEquals(true, InstanceOfRefItem.ref.needsCheckTypeColumn());
		assertEquals(false, InstanceOfRefItem.refb2.needsCheckTypeColumn());

		try
		{
			InstanceOfAItem.TYPE.getThis().checkTypeColumnL();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfAItem.this", e.getMessage());
		}
		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		try
		{
			InstanceOfAItem.TYPE.checkCompletenessL(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("subType", e.getMessage());
		}
		try
		{
			InstanceOfAItem.TYPE.checkCompletenessL(InstanceOfAItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected instantiable subtype of InstanceOfAItem, but was InstanceOfAItem", e.getMessage());
		}
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());
		try
		{
			assertEquals(0, InstanceOfRefItem.refb2.checkTypeColumnL());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfRefItem.refb2", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: testing unchecked usage of api
	@Test void testUnchecked()
	{
		try
		{
			InstanceOfB1Item.TYPE.checkCompletenessL((Type)InstanceOfAItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected instantiable subtype of InstanceOfB1Item, but was InstanceOfAItem", e.getMessage());
		}
		try
		{
			InstanceOfB1Item.TYPE.checkCompletenessL((Type)InstanceOfB2Item.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected instantiable subtype of InstanceOfB1Item, but was InstanceOfB2Item", e.getMessage());
		}
	}

	@Test void testWrongA() throws SQLException
	{
		updateTypeColumn(InstanceOfAItem.TYPE, itema, InstanceOfB1Item.TYPE);

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongB1inA() throws SQLException
	{
		updateTypeColumn(InstanceOfAItem.TYPE, itemb1, InstanceOfB2Item.TYPE);

		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongB1inB1() throws SQLException
	{
		updateTypeColumn(InstanceOfB1Item.TYPE, itemb1, InstanceOfC1Item.TYPE);

		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(1, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongB2inA() throws SQLException
	{
		updateTypeColumn(InstanceOfAItem.TYPE, itemb2, InstanceOfB1Item.TYPE);

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(1, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongC1inA() throws SQLException
	{
		updateTypeColumn(InstanceOfAItem.TYPE, itemc1, InstanceOfB2Item.TYPE);

		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongC1inB1() throws SQLException
	{
		updateTypeColumn(InstanceOfB1Item.TYPE, itemc1, InstanceOfB1Item.TYPE);

		assertEquals(1, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(1, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testMissingB1() throws SQLException
	{
		deleteRow(InstanceOfB1Item.TYPE, itemb1);

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testMissingC1() throws SQLException
	{
		deleteRow(InstanceOfC1Item.TYPE, itemc1);

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(1, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());


		deleteRow(InstanceOfB1Item.TYPE, itemc1);

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(1, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumnL());
	}

	@Test void testWrongRef() throws SQLException
	{
		execute(
				"update " + SI.tab(InstanceOfRefItem.TYPE) + " " +
				"set " + SI.type(InstanceOfRefItem.ref) + "='" + getTypeColumnValue(InstanceOfB1Item.TYPE) + "' " +
				"where " + SI.pk(InstanceOfRefItem.TYPE) + "=" + getPrimaryKeyColumnValueL(reffa));

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumnL());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumnL());

		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB1Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfB2Item.TYPE));
		assertEquals(0, InstanceOfAItem .TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));
		assertEquals(0, InstanceOfB1Item.TYPE.checkCompletenessL(InstanceOfC1Item.TYPE));

		assertEquals(1, InstanceOfRefItem.ref.checkTypeColumnL());
	}


	private <T extends Item> void updateTypeColumn(
			final Type<T> type,
			final T item,
			final Type<? extends T> newType)
	throws SQLException
	{
		execute(
			"update " + SI.tab(type) + " " +
			"set " + SI.type(type) + "='" + getTypeColumnValue(newType) + "' " +
			"where " + SI.pk(type) + "=" + getPrimaryKeyColumnValueL(item));
	}

	private <T extends Item> void deleteRow(
			final Type<T> type,
			final T item)
	throws SQLException
	{
		execute(
			"delete from " + SI.tab(type) + " " +
			"where " + SI.pk(type) + "=" + getPrimaryKeyColumnValueL(item));
	}

	private void execute(final String sql) throws SQLException
	{
		final String transactionName = model.currentTransaction().getName();
		model.commit();
		assertEquals(1, connection.executeUpdate(sql));
		model.startTransaction(transactionName);
	}
}
