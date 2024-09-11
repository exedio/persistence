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
import static com.exedio.cope.PlusIntegerItem.numB;
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
	private String cope_total_distinct;

	@BeforeEach final void setUp()
	{
		item1 = new PlusIntegerItem(2, 4, 5);
		item2 = new PlusIntegerItem(1, 4, 5);
		item3 = new PlusIntegerItem(1, 4, 5);
		NULLS_FIRST = postgresql ? " NULLS FIRST" : "";
		cope_total_distinct = !hsqldb ? " AS cope_total_distinct" : "";
	}

	@Test void noDistinctOrOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.join(TYPE, join -> numC.equal(numC.bind(join)));

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
		assertEquals(
				"SELECT COUNT(*) " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC),
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

		assertContainsList(asList(item1, item1, item1, item2, item2, item2, item3, item3, item3), query.search());
		assertEquals(9, query.total());
		assertTrue(query.exists());
	}

	@Test void noOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.join(TYPE, join -> numC.equal(numC.bind(join)));
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
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) +
				" )" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

		assertContains(item1, item2, item3, query.search());
		assertEquals(3, query.total());
		assertTrue(query.exists());
	}

	@Test void noDistinct()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.join(TYPE, join -> numC.equal(numC.bind(join)));
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
		assertEquals(
				"SELECT COUNT(*) " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC),
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

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
				"SELECT DISTINCT " + SI.pk(TYPE) + withoutAny("," + SI.col(numA)) + " " +
				"FROM " + SI.tab(TYPE) + " " +
				"ORDER BY " + ANY_VALUE(SI.col(numA)) + NULLS_FIRST,
				SchemaInfo.search(query));
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE)),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		assertContains(item2, item3, item1, query.search());
	}

	@Test void problemWithJoin()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.join(TYPE, join -> numC.equal(numC.bind(join)));
		query.setDistinct(true);
		query.setOrderBy(numA, true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by numA",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + withoutAny("," + ALIAS + "0." + SI.col(numA)) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				"ORDER BY " + ANY_VALUE(ALIAS+"0." + SI.col(numA)) + NULLS_FIRST,
				SchemaInfo.search(query));
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		assertContains(item2, item3, item1, query.search());
	}

	@Test void problemWithJoinAndOtherOrder()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE, j -> numC.equal(numC.bind(j)));
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
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb ->
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 ON PlusIntegerItem0.\"numC\"=PlusIntegerItem1.\"numC\" " +
								"ORDER BY PlusIntegerItem1.\"numA\"]"));

			case mysql ->
				notAllowed(query,
						"Expression #1 of ORDER BY clause is not in SELECT list, " +
						"references column '" + dbCat() + ".PlusIntegerItem1.numA' which is not in SELECT list; " +
						"this is incompatible with DISTINCT");

			case postgresql ->
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(181));

			default ->
				throw new RuntimeException(dialect.name());
		}
	}

	@Test void testDistinctOrderByAnyAggregate()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.join(TYPE, join -> numC.equal(numC.bind(join)));
		query.setDistinct(true);
		query.setOrderBy(numA.any(), true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 on numC=p1.numC " +
				"order by any(numA)",
				query.toString());
		assertEquals(
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				"ORDER BY ANY_VALUE(" + ALIAS+"0." + SI.col(numA) + ")" + NULLS_FIRST,
				SchemaInfo.search(query));
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC) + " " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
				"SELECT " + ALIAS+"0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " + ALIAS+"0 " +
				"JOIN " + SI.tab(TYPE) + " " + ALIAS+"1 " +
				"ON " + ALIAS+"0." + SI.col(numC) + "=" + ALIAS+"1." + SI.col(numC)),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb ->
				notAllowed(query,
						"user lacks privilege or object not found: ANY_VALUE" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 ON PlusIntegerItem0.\"numC\"=PlusIntegerItem1.\"numC\" " +
								"ORDER BY ANY_VALUE(PlusIntegerItem0.\"numA\")]"));

			case mysql ->
				assertContains(item2, item3, item1, query.search());

			case postgresql ->
				notAllowedStartsWith(query,
						"ERROR: function any_value(integer) does not exist\n");

			default ->
				throw new RuntimeException(dialect.name());
		}
	}

	@Test void testDistinctOrderByPlus()
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		query.setDistinct(true);
		query.setOrderBy(numA.plus(numB), true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"order by plus(numA,numB)",
				query.toString());
		final boolean selectPlus = !mysql;
		assertEquals(
				"SELECT DISTINCT " + SI.pk(TYPE) + (selectPlus ? (",(" + SI.col(numA) + "+" + SI.col(numB) + ")") : "") + " " +
				"FROM " + SI.tab(TYPE) + " " +
				"ORDER BY " + ANY_VALUE("(" + SI.col(numA) + "+" + SI.col(numB) + ")") + NULLS_FIRST,
				SchemaInfo.search(query));
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT " + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
						"SELECT " + SI.pk(TYPE) + " " +
						"FROM " + SI.tab(TYPE)
				),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());
		assertContains(item2, item3, item1, query.search());
	}

	@Test void testDistinctOrderByJoinedPlus()
	{
		// must not add any() if the order-by-view includes a joined field
		final Query<PlusIntegerItem> query = TYPE.newQuery();
		final Join join = query.join(TYPE);
		query.setDistinct(true);
		query.setOrderBy(numA.plus(numB.bind(join)), true);

		assertEquals(
				"select distinct this from PlusIntegerItem " +
				"join PlusIntegerItem p1 " +
				"order by plus(numA,p1.numB)",
				query.toString());
		assertEquals(
				"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
				"CROSS JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 " +
				"ORDER BY (PlusIntegerItem0." + SI.col(numA) + "+PlusIntegerItem1." + SI.col(numB) + ")"+NULLS_FIRST,
				SchemaInfo.search(query));
		assertEquals(
				"SELECT COUNT(*) FROM ( " +
				"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
				"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
				"CROSS JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 " +
				")" + cope_total_distinct,
				SchemaInfo.total(query));
		assertEquals(
				SELECT_EXISTS(
						"SELECT PlusIntegerItem0." + SI.pk(TYPE) + " " +
						"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
						"CROSS JOIN " + SI.tab(TYPE) + " PlusIntegerItem1"
				),
				SchemaInfo.exists(query));

		assertEquals(3, query.total());
		assertTrue(query.exists());

		switch(dialect)
		{
			case hsqldb ->
				notAllowed(query,
						"invalid ORDER BY expression" +
						ifPrep(
								" in statement [" +
								"SELECT DISTINCT PlusIntegerItem0." + SI.pk(TYPE) + " " +
								"FROM " + SI.tab(TYPE) + " PlusIntegerItem0 " +
								"CROSS JOIN " + SI.tab(TYPE) + " PlusIntegerItem1 " +
								"ORDER BY (PlusIntegerItem0." + SI.col(numA) + "+PlusIntegerItem1." + SI.col(numB) + ")]"));

			case mysql ->
				notAllowed(query,
						"Expression #1 of ORDER BY clause is not in SELECT list, " +
						"references column '" + dbCat() + ".PlusIntegerItem0.numA' which is not in SELECT list; " +
						"this is incompatible with DISTINCT");

			case postgresql ->
				notAllowed(query,
						"ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list" +
						postgresqlPosition(137));

			default ->
				throw new RuntimeException(dialect.name());
		}
	}


	private String withoutAny(final String s)
	{
		return
				mysql
				? ""
				: s;
	}

	private String ANY_VALUE(final String s)
	{
		return
				mysql
				? ("ANY_VALUE(" + s + ")")
				: s;
	}

	private String SELECT_EXISTS(final String s)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(mysql ? "SELECT EXISTS (" : "SELECT COUNT(*) FROM (");
		bf.append(s);
		if(!mysql)
			bf.append(" LIMIT 1");
		bf.append(")");
		if(!hsqldb)
			bf.append(" AS cope_exists");
		return bf.toString();
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
