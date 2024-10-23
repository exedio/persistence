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

import static com.exedio.cope.CheckConstraintHierarchyItemBottom.bottom1;
import static com.exedio.cope.CheckConstraintHierarchyItemBottom.cross1;
import static com.exedio.cope.CheckConstraintHierarchyItemTop.top1;
import static com.exedio.cope.CheckConstraintHierarchyItemTop.up1;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CheckConstraintHierarchyViolationTest extends TestWithEnvironment
{
	@Test void testIsSupportedBySchema()
	{
		assertEquals(true,  CheckConstraintHierarchyItemTop   .top   .isSupportedBySchemaIfSupportedByDialect());
		assertEquals(true,  CheckConstraintHierarchyItemBottom.up    .isSupportedBySchemaIfSupportedByDialect());
		assertEquals(true,  CheckConstraintHierarchyItemBottom.bottom.isSupportedBySchemaIfSupportedByDialect());
		assertEquals(false, CheckConstraintHierarchyItemBottom.cross .isSupportedBySchemaIfSupportedByDialect());
	}

	/**
	 * @see CheckConstraintHierarchyTest#testTop()
	 */
	@Test void testTop() throws SQLException
	{
		final CheckConstraint constraint = CheckConstraintHierarchyItemTop.top;
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertEquals(0, constraint.check());
		commit();

		final String sql = sql(top1, 101, item);
		if(supportsCheckConstraint(model))
		{
			assertFailsSql(
					() -> connection.execute(sql),
					checkViolationMessage("ItemTop", "ItemTop_top"));
		}
		else
		{
			assertEquals(1, connection.executeUpdate(sql));
		}
		startTransaction();
		assertEquals(supportsCheckConstraint(model)?0:1, constraint.check());
	}

	/**
	 * @see CheckConstraintHierarchyTest#testUp()
	 */
	@Test void testUp() throws SQLException
	{
		final CheckConstraint constraint = CheckConstraintHierarchyItemBottom.up;
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertEquals(0, constraint.check());
		commit();

		final String sql = sql(up1, 201, item);
		if(supportsCheckConstraint(model))
		{
			assertFailsSql(
					() -> connection.execute(sql),
					checkViolationMessage("ItemTop", "ItemBottom_up")); // NOTE: Divergent name prefix, see notes in CheckConstraint#makeSchema
		}
		else
		{
			assertEquals(1, connection.executeUpdate(sql));
		}
		startTransaction();
		assertEquals(supportsCheckConstraint(model)?0:1, constraint.check());
	}

	/**
	 * @see CheckConstraintHierarchyTest#testBottom()
	 */
	@Test void testBottom() throws SQLException
	{
		final CheckConstraint constraint = CheckConstraintHierarchyItemBottom.bottom;
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertEquals(0, constraint.check());
		commit();

		final String sql = sql(bottom1, 301, item);
		if(supportsCheckConstraint(model))
		{
			assertFailsSql(
					() -> connection.execute(sql),
					checkViolationMessage("ItemBottom", "ItemBottom_bottom"));
		}
		else
		{
			assertEquals(1, connection.executeUpdate(sql));
		}
		startTransaction();
		assertEquals(supportsCheckConstraint(model)?0:1, constraint.check());
	}

	/**
	 * @see CheckConstraintHierarchyTest#testCross()
	 */
	@Test void testCross() throws SQLException
	{
		final CheckConstraint constraint = CheckConstraintHierarchyItemBottom.cross;
		final CheckConstraintHierarchyItemBottom item = new CheckConstraintHierarchyItemBottom();
		assertEquals(0, constraint.check());
		commit();

		final String sql = sql(cross1, 401, item);
		assertEquals(1, connection.executeUpdate(sql));
		startTransaction();
		assertEquals(1, constraint.check());
	}


	private static String sql(final IntegerField field, final int value, final CheckConstraintHierarchyItemBottom item)
	{
		return
				"UPDATE " + SI.tab(field.getType()) + " " +
				"SET " + SI.col(field) + "=" + value + " " +
				"WHERE " + SI.pk(field.getType()) + "=" + getPrimaryKeyColumnValueL(item);
	}

	private void assertFailsSql(
			final Executable executable,
			final String message)
	{
		assertEquals(message, dropMariaConnectionId(assertThrows(SQLException.class, executable).getMessage()));
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	public CheckConstraintHierarchyViolationTest()
	{
		super(CheckConstraintHierarchyTest.MODEL);
	}
}
