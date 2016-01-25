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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;

import com.exedio.cope.tojunit.ConnectionRule;
import java.sql.SQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class SchemaInfoConnectionTest extends TestWithEnvironment
{
	public SchemaInfoConnectionTest()
	{
		super(InstanceOfModelTest.MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(connection);

	@Test public void testIt() throws SQLException
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select ").
			append(q(getPrimaryKeyColumnName(InstanceOfAItem.TYPE))).
			append(',').
			append(q(getTypeColumnName(InstanceOfAItem.TYPE))).
			append(',').
			append(q(getUpdateCounterColumnName(InstanceOfAItem.TYPE))).
			append(',').
			append(q(getColumnName(InstanceOfAItem.code))).
			append(" from ").
			append(q(getTableName(InstanceOfAItem.TYPE)));

		connection.execute(bf.toString());
	}

	@Test public void testTypeColumn() throws SQLException
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select ").
			append(q(getPrimaryKeyColumnName(InstanceOfRefItem.TYPE))).
			append(',').
			append(q(getUpdateCounterColumnName(InstanceOfRefItem.TYPE))).
			append(',').
			append(q(getColumnName(InstanceOfRefItem.ref))).
			append(',').
			append(q(getTypeColumnName(InstanceOfRefItem.ref))).
			append(" from ").
			append(q(getTableName(InstanceOfRefItem.TYPE)));

		connection.execute(bf.toString());
	}

	private String q(final String name)
	{
		return quoteName(model, name);
	}
}
