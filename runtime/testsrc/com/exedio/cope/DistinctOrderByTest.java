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

import static com.exedio.cope.GroupByTest.postgresqlPosition;
import static com.exedio.cope.PlusIntegerItem.TYPE;
import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numC;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertContainsList;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.SQLRuntimeException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator") // OK: newline in sql error
public class DistinctOrderByTest extends TestWithEnvironment
{
	public DistinctOrderByTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	private PlusIntegerItem item1;
	private PlusIntegerItem item2;
	private PlusIntegerItem item3;

	private static final String ALIAS = "PlusIntegerItem";
	private String NULLS_FIRST;

	@BeforeEach final void setUp()
	{
		item1 = new PlusIntegerItem(2, 4, 5);
		item2 = new PlusIntegerItem(1, 4, 5);
		item3 = new PlusIntegerItem(1, 4, 5);
		NULLS_FIRST = postgresql ? " NULLS FIRST" : "";
	}

	@Test void noDistinctOrOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));

		assertEquals(
				"select this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC",
				query.toString());
		assertEquals(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC),
				SchemaInfo.search(query));

		assertContainsList(asList(item1, item1, item1, item2, item2, item2, item3, item3, item3), query.search());
		assertEquals(9, query.total());
		assertTrue(query.exists());
	}

	@Test void noOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
		query.setDistinct(true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC),
				SchemaInfo.search(query));

		assertContains(item1, item2, item3, query.search());
		assertEquals(3, query.total());
		assertTrue(query.exists());
	}

	@Test void noDistinct()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
		query.setOrderBy(numA, true);

		assertEquals(
				"select this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by numA",
				query.toString());
		assertEquals(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				"ORDER BY " + ALIAS+"0." + SI.col(numA) + NULLS_FIRST,
				SchemaInfo.search(query));

		assertContainsList(asList(item1, item1, item1, item2, item2, item2, item3, item3, item3), query.search());
		assertEquals(9, query.total());
		assertTrue(query.exists());
	}

	@Test void problemWithoutJoin()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.setOrderBy(numA, true);
		query.setDistinct(true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"order by numA",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " +
				"ORDER BY " + ANY_VALUE(SI.col(numA)) + NULLS_FIRST,
				SchemaInfo.search(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT " + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " " +
								"ORDER BY \"numA\"]"));
				break;
			case mysql:
				assertContains(item2, item3, item1, query.search());
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(56));
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}

	@Test void problemWithJoin()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
		query.setDistinct(true);
		query.setOrderBy(numA, true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by numA",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				"ORDER BY " + ANY_VALUE(ALIAS+"0." + SI.col(numA)) + NULLS_FIRST,
				SchemaInfo.search(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 ON PlusIntegerItem0.\"numC\"=PlusIntegerItem1.\"numC\" " +
								"ORDER BY PlusIntegerItem0.\"numA\"]"));
				break;
			case mysql:
				assertContains(item2, item3, item1, query.search());
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(181));
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}

	@Test void problemWithJoinAndOtherOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
		query.setDistinct(true);
		query.setOrderBy(numA.bind(join), true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by p1.numA",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				"ORDER BY " + ALIAS+"1." + SI.col(numA) + NULLS_FIRST,
				SchemaInfo.search(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		final EnvironmentInfo env = model.getEnvironmentInfo();
		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 ON PlusIntegerItem0.\"numC\"=PlusIntegerItem1.\"numC\" " +
								"ORDER BY PlusIntegerItem1.\"numA\"]"));
				break;
			case mysql:
				if(env.isDatabaseVersionAtLeast(5, 7))
					notAllowed(query,
							"Expression #1 of ORDER BY clause is not in SELECT list, " +
							"references column '" + env.getCatalog() + ".PlusIntegerItem1.numA' which is not in SELECT list; " +
							"this is incompatible with DISTINCT");
				else
					assertContains(item2, item3, item1, query.search());
				break;
			case postgresql:
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(181));
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}

	@Test void testDistinctOrderByAnyAggregate()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		join.setCondition(numC.equal(numC.bind(join)));
		query.setDistinct(true);
		query.setOrderBy(numA.any(), true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by any(numA)",
				query.toString());

		assertEquals(3, query.total());
		assertTrue(query.exists());

		final EnvironmentInfo env = model.getEnvironmentInfo();
		switch(dialect)
		{
			case hsqldb:
				notAllowed(query,
						"user lacks privilege or object not found: ANY_VALUE" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 ON PlusIntegerItem0.\"numC\"=PlusIntegerItem1.\"numC\" " +
								"ORDER BY ANY_VALUE(PlusIntegerItem0.\"numA\")]"));
				break;
			case mysql:
				if(env.isDatabaseVersionAtLeast(5, 7))
					assertContains(item2, item3, item1, query.search());
				else
					notAllowed(query, msg ->
							("FUNCTION " + env.getCatalog() + ".ANY_VALUE does not exist").equals(msg) ||
							(
									// happens without EXECUTE privilege
									msg.startsWith("execute command denied to user ") &&
									msg.endsWith(" for routine '" + env.getCatalog() + ".ANY_VALUE'")
							));
				break;
			case postgresql:
				notAllowedStartsWith(query,
						"ERROR: function any_value(integer) does not exist\n");
				break;
			default:
				throw new RuntimeException(dialect.name());
		}
	}


	private String ANY_VALUE(final String s)
	{
		return
				(mysql && model.getEnvironmentInfo().isDatabaseVersionAtLeast(5, 7))
				? ("ANY_VALUE(" + s + ")")
				: s;
	}

	static void notAllowedStartsWith(final Query<?> query, final String message)
	{
		try
		{
			final List<?> result = query.search();
			fail("search is expected to fail, but returned " + result);
		}
		catch(final SQLRuntimeException e)
		{
			assertTrue(e.getCause().getMessage().startsWith(message), e.getCause().getMessage());
		}
	}
}
