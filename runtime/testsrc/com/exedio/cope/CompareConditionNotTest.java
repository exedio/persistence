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

import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.doublex;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.CompareConditionItem.string;
import static com.exedio.cope.CompareConditionTest.MODEL;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CompareConditionItem.YEnum;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests various optimizations in overriding methods of {@link Condition#not()}.
 */
public class CompareConditionNotTest extends TestWithEnvironment
{
	public CompareConditionNotTest()
	{
		super(MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void testIt() throws SQLException
	{
		final long item1 = getPrimaryKeyColumnValueL(new CompareConditionItem("string1", 1, 11l, 2.1, null, null, YEnum.V1));
		final long item2 = getPrimaryKeyColumnValueL(new CompareConditionItem("string2", 2, 12l, 2.2, null, null, YEnum.V2));
		final long item3 = getPrimaryKeyColumnValueL(new CompareConditionItem("string3", 3, 13l, 2.3, null, null, YEnum.V3));
		final long item4 = getPrimaryKeyColumnValueL(new CompareConditionItem("string4", 4, 14l, 2.4, null, null, YEnum.V4));
		final long item5 = getPrimaryKeyColumnValueL(new CompareConditionItem("string5", 5, 15l, 2.5, null, null, YEnum.V5));
		final long itemX = getPrimaryKeyColumnValueL(new CompareConditionItem(null, null, null, null, null, null, null));
		MODEL.commit();

		assertIt(intx, "IS NULL", "IS NOT NULL", item1, item2, item3, item4, item5);
		assertIt(intx, "IS NOT NULL", "IS NULL", itemX);
		assertIt(intx,  "=3", "<>3", item1, item2,        item4, item5);
		assertIt(intx, "<>3",  "=3",               item3              );
		assertIt(intx,  "<3", ">=3",               item3, item4, item5);
		assertIt(intx, "<=3",  ">3",                      item4, item5);
		assertIt(intx,  ">3", "<=3", item1, item2, item3              );
		assertIt(intx, ">=3",  "<3", item1, item2                     );

		if(!hsqldb) // java.sql.SQLSyntaxErrorException: unexpected token: NOT
			assertEquals(List.of(item3), query("NOT NOT " + SI.col(intx) + "=3"));

		assertIt(string,  "<'string3'", ">='string3'", item3, item4, item5);
		assertIt(longx,   "<13",        ">=13",        item3, item4, item5);
		assertIt(doublex, "<2.3",       ">=2.3",       item3, item4, item5);
	}

	private void assertIt(
			final FunctionField<?> field,
			final String clause,
			final String invertedClause,
			final long... expected)
			throws SQLException
	{
		final ArrayList<Long> expectedList = new ArrayList<>();
		for(final long l : expected)
			expectedList.add(l);
		assertEquals(expectedList, query("NOT " + SI.col(field) + clause));
		assertEquals(expectedList, query(         SI.col(field) + invertedClause));
	}

	private List<Long> query(final String where) throws SQLException
	{
		final ArrayList<Long> result = new ArrayList<>();
		try(ResultSet rs = connection.executeQuery(
				"SELECT " + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " +
				"WHERE " + where + " " +
				"ORDER BY " + SI.pk(TYPE)))
		{
			while(rs.next())
				result.add(rs.getLong(1));
		}
		return List.copyOf(result);
	}
}
