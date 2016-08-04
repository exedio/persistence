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

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
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
			append(SI.columnPk(InstanceOfAItem.TYPE)).
			append(',').
			append(SI.columnType(InstanceOfAItem.TYPE)).
			append(',').
			append(SI.columnUpdate(InstanceOfAItem.TYPE)).
			append(',').
			append(SI.column(InstanceOfAItem.code)).
			append(" from ").
			append(SI.table(InstanceOfAItem.TYPE));

		connection.execute(bf.toString());
	}

	@Test public void testTypeColumn() throws SQLException
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select ").
			append(SI.columnPk(InstanceOfRefItem.TYPE)).
			append(',').
			append(SI.columnUpdate(InstanceOfRefItem.TYPE)).
			append(',').
			append(SI.column(InstanceOfRefItem.ref)).
			append(',').
			append(SI.columnType(InstanceOfRefItem.ref)).
			append(" from ").
			append(SI.table(InstanceOfRefItem.TYPE));

		connection.execute(bf.toString());
	}
}
