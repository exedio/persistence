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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValue;
import static com.exedio.cope.SchemaInfo.getTableName;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.tojunit.ConnectionRule;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class CheckTypeColumnAbstractTest extends TestWithEnvironment
{
	public CheckTypeColumnAbstractTest()
	{
		super(HierarchyTest.MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(connection);

	private HierarchySingleSub item;

	@Before public final void setUp()
	{
		item = new HierarchySingleSub();
	}

	@After public final void tearDown()
	{
		model.commit();
		model.deleteSchemaForTest();
		model.startTransaction("CheckTypeColumnAbstractTest");
	}

	@Test public void testIt() throws SQLException
	{
		assertEquals(0, HierarchySingleSuper.TYPE.checkCompleteness(HierarchySingleSub.TYPE));

		deleteRow(HierarchySingleSub.TYPE, item);
		assertEquals(1, HierarchySingleSuper.TYPE.checkCompleteness(HierarchySingleSub.TYPE));
	}


	private <T extends Item> void deleteRow(
			final Type<T> type,
			final T item)
	throws SQLException
	{
		execute(
			"delete from " + q(getTableName(type)) + " " +
			"where " + q(getPrimaryKeyColumnName(type)) + "=" + getPrimaryKeyColumnValue(item));
	}

	private void execute(final String sql) throws SQLException
	{
		final String transactionName = model.currentTransaction().getName();
		model.commit();
		assertEquals(1, connection.executeUpdate(sql));
		model.startTransaction(transactionName);
	}

	private String q(final String s)
	{
		return SchemaInfo.quoteName(model, s);
	}
}
