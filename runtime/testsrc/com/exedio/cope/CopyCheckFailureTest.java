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

import static com.exedio.cope.CopySimpleModelTest.templateStringCopyFromTarget;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CopyCheckFailureTest extends TestWithEnvironment
{
	public CopyCheckFailureTest()
	{
		super(CopySimpleModelTest.MODEL);
	}

	private CopySimpleTarget target;
	private CopySimpleSource source;

	@BeforeEach void beforeEach()
	{
		target = new CopySimpleTarget("match", "otherString2", null, new CopyValue());
		source = new CopySimpleSource(target, "match", null);
		assertCheck(0);
	}

	@Test void testSourceDifferent() throws SQLException
	{
		setSource("'nonmatch'");
		assertCheck(1);

		setTarget("'nonmatch'");
		assertCheck(0);
	}

	@Test void testSourceNull() throws SQLException
	{
		setSource("NULL");
		assertCheck(1);

		setTarget("NULL");
		assertCheck(0);
	}

	@Test void testTargetDifferent() throws SQLException
	{
		setTarget("'nonmatch'");
		assertCheck(1);

		setSource("'nonmatch'");
		assertCheck(0);
	}

	@Test void testTargetNull() throws SQLException
	{
		setTarget("NULL");
		assertCheck(1);

		setSource("NULL");
		assertCheck(0);
	}


	private void setSource(final String value) throws SQLException
	{
		set(source, CopySimpleSource.templateString, value);
	}

	private void setTarget(final String value) throws SQLException
	{
		set(target, CopySimpleTarget.templateString, value);
	}

	private void set(
			final Item item,
			final StringField field,
			final String value)
			throws SQLException
	{
		final Type<?> type = item.getCopeType();
		assertSame(type, field.getType());
		model.commit();
		connection.execute(
				"UPDATE " + SI.tab(type) + " " +
				"SET " + SI.col(field) + "=" + value + " " +
				"WHERE " + SI.pk(type) + "=" + getPrimaryKeyColumnValueL(item));
		model.clearCache(); // because we changed the data bypassing cope
		model.startTransaction(CopyCheckFailureTest.class.getName());
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	private static void assertCheck(final int expected)
	{
		assertEquals(expected, templateStringCopyFromTarget.check());
	}
}
