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

import static com.exedio.cope.CheckConstraintConditionItem.TYPE;

import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class CheckConstraintConditionTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(
			TYPE,
			CheckConstraintConditionItemTarget.TYPE,
			CheckConstraintConditionItemSub.TYPE,
			CheckConstraintConditionItemBottom.TYPE);

	static
	{
		MODEL.enableSerialization(CheckConstraintConditionTest.class, "MODEL");
	}

	public CheckConstraintConditionTest()
	{
		super(MODEL);
	}

	@Override
	protected boolean doesManageTransactions()
	{
		return false;
	}

	public void testIt()
	{
		if(oracle) // TODO
			return;

		// TODO use assertSchema();

		final Schema schema = model.getVerifiedSchema();

		for(final Table table : schema.getTables())
		{
			for(final Column column : table.getColumns())
				assertOk(table.getName() + '#' + column.getName() + '#' + column.getType(), column);

			if(hsqldb) // TODO
				continue;

			for(final Constraint constraint : table.getConstraints())
			{
				final String message = table.getName() + '#' + constraint.getName();
				if(constraint instanceof com.exedio.dsmf.CheckConstraint &&
					!SchemaInfo.supportsCheckConstraints(model))
				{
					assertEquals(message, "not supported", constraint.getError());
					assertEquals(message, Schema.Color.OK, constraint.getParticularColor());
					assertEquals(message, Schema.Color.OK, constraint.getCumulativeColor());
				}
				else
				{
					assertOk(message, constraint);
				}
			}

			assertOk(table.getName(), table);
		}

		for(final com.exedio.dsmf.Sequence sequence : schema.getSequences())
			assertOk(sequence.getName(), sequence);

		if(hsqldb) // TODO
			return;

		assertOk("schema", schema);
	}

	private static final void assertOk(final String message, final com.exedio.dsmf.Node node)
	{
		assertEquals(message, null, node.getError());
		assertEquals(message, Schema.Color.OK, node.getParticularColor());
		assertEquals(message, Schema.Color.OK, node.getCumulativeColor());
	}
}
