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
import static com.exedio.cope.GroupItem.TYPE;
import static com.exedio.cope.GroupItem.day;
import static com.exedio.cope.GroupItem.number;
import static com.exedio.cope.GroupItem.optionalDouble;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.Day;
import org.junit.jupiter.api.Test;

public class QueryGroupingTest extends TestWithEnvironment
{
	static final Model MODEL = new Model( TYPE );

	static final Day day1 = new Day(2006, 2, 19);
	static final Day day2 = new Day(2006, 2, 20);
	static final Day day3 = new Day(2006, 2, 21);

	public QueryGroupingTest()
	{
		super( MODEL );
	}

	@Test void testGroupBy()
	{
		final GroupItem item1  = new GroupItem(day1, 1);
		final GroupItem item2a = new GroupItem(day2, 2);
		final GroupItem item2b = new GroupItem(day2, 3);
		final GroupItem item3  = new GroupItem(day3, 4);

		assertContains(
			item1, item2a, item2b, item3,
			TYPE.search()
		);
		final Query<?> query = Query.newQuery( new Selectable<?>[]{day, number.sum()}, TYPE, null );
		assertEquals( "select day,sum(number) from GroupItem", query.toString() );
		query.setGroupBy( day );
		assertEquals( "select day,sum(number) from GroupItem group by day", query.toString() );

		assertContains(
			list(day1, 1), list(day2, 5), list(day3, 4),
			query.search()
		);
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setSelects( day, number.min(), number.max() );
		assertEquals( "select day,min(number),max(number) from GroupItem group by day", query.toString() );
		assertContains(
			list(day1, 1, 1), list(day2, 2, 3), list(day3, 4, 4),
			query.search()
		);
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setCondition( day.greater(day1) );
		assertEquals( "select day,min(number),max(number) from GroupItem where day>'2006/2/19' group by day", query.toString() );
		assertContains(
			list(day2, 2, 3), list(day3, 4, 4),
			query.search()
		);
		assertEquals(2, query.total());
		assertEquals(true, query.exists());

		query.setCondition( number.notEqual(3) );
		assertEquals( "select day,min(number),max(number) from GroupItem where number<>'3' group by day", query.toString() );
		assertContains(
			list(day1, 1, 1), list(day2, 2, 2), list(day3, 4, 4),
			query.search()
		);
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setSelects( day, optionalDouble.sum() );
		query.setCondition( day.equal(day2) );
		assertEquals( "select day,sum(optionalDouble) from GroupItem where day='2006/2/20' group by day", query.toString() );
		assertContains(
			list(day2, null),
			query.search()
		);
		assertEquals(1, query.total());
		assertEquals(true, query.exists());

		item2a.setOptionalDouble( 3.5 );
		assertEquals( null, item2b.getOptionalDouble() );
		assertContains(
			list(day2, 3.5),
			query.search()
		);
		assertEquals(1, query.total());
		assertEquals(true, query.exists());

		item2b.setOptionalDouble( 1.0 );
		assertContains(
			list(day2, 4.5),
			query.search()
		);
		assertEquals(1, query.total());
		assertEquals(true, query.exists());

		query.setSelects( day, optionalDouble.sum(), new Count() );
		assertContains(
			list(day2, 4.5, 2),
			query.search()
		);
		assertEquals(1, query.total());
		assertEquals(true, query.exists());
	}

	@Test void testUngroupedSelect()
	{
		new GroupItem(day1, 1);
		new GroupItem(day2, 2);
		final Query<?> query = Query.newQuery( new Selectable<?>[]{day, number}, TYPE, null );
		query.setGroupBy( number );
		assertEquals( "select day,number from GroupItem group by number", query.toString() );

		final String table = getTableName(TYPE);
		final String column = getColumnName(day);
		switch(dialect)
		{
			case hsqldb:
			{
				final String message =
						"expression not in aggregate or GROUP BY columns: " +
						"PUBLIC.\"" + table + "\".\"" + column + "\"" + ifPrep(" in statement ");
				notAllowed(query, message + ifPrep("[SELECT \"day\",\"number\" FROM " + SI.tab(TYPE) + " GROUP BY \"number\"]"));
				notAllowedTotal(query, message + ifPrep("[SELECT COUNT(*) FROM ( SELECT \"day\",\"number\" FROM " + SI.tab(TYPE) + " GROUP BY \"number\" )]"));
				break;
			}
			case mysql:
			{
				final String message;
				if(atLeastMysql57())
					message =
							"Expression #1 of SELECT list is not in GROUP BY clause and " +
							"contains nonaggregated column '" + dbCat() + ".GroupItem.day' " +
							"which is not functionally dependent on columns in GROUP BY clause; " +
							"this is incompatible with sql_mode=only_full_group_by";
				else
					message =
							"'" + dbCat() + "." + table + "." + column + "' isn't in GROUP BY";

				notAllowed(query, message);
				notAllowedTotal(query, message);
				break;
			}
			case postgresql:
			{
				final String message =
						"ERROR: column \"" + table + "." + column + "\" must appear " +
						"in the GROUP BY clause or be used in an aggregate function";
				restartTransaction();
				notAllowed(query, message + postgresqlPosition(8));
				restartTransaction();
				notAllowedTotal(query, message + postgresqlPosition(31));
				break;
			}
			default:
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testSingleSelect()
	{
		new GroupItem(day1, 1);
		new GroupItem(day2, 2);
		new GroupItem(day2, 3);
		new GroupItem(day3, 4);
		final Query<Integer> query = new Query<>(number.max());
		query.setGroupBy( day );
		query.setOrderBy(number.max(), true);
		assertEquals(asList(1, 3, 4), query.search());
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setOrderBy(number.max(), false);
		assertEquals(asList(4, 3, 1), query.search());
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setCondition(number.lessOrEqual(2));
		assertEquals(asList(2, 1), query.search());
		assertEquals(2, query.total());
		assertEquals(true, query.exists());
	}

	@Test void testMultiGrouping()
	{
		final GroupItem item1  = new GroupItem(day1, 1);
		item1.setOptionalDouble( 10.0 );
		final GroupItem item2a = new GroupItem(day2, 2);
		item2a.setOptionalDouble( 20.0 );
		final GroupItem item2b = new GroupItem(day2, 3);
		item2b.setOptionalDouble( 25.0 );
		final GroupItem item2c = new GroupItem(day2, 3);
		item2c.setOptionalDouble( 27.0 );

		final Query<?> query = Query.newQuery(
			new Selectable<?>[]{day, number, optionalDouble.sum()}, TYPE, null
		);
		query.setGroupBy( day, number );
		assertEquals( "select day,number,sum(optionalDouble) from GroupItem group by day,number", query.toString() );
		assertContains(
			list(day1, 1, 10.0), list(day2, 2, 20.0), list(day2, 3, 52.0),
			query.search()
		);
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setSelects( day, number, optionalDouble.sum(), new Count() );
		assertContains(
			list(day1, 1, 10.0, 1), list(day2, 2, 20.0, 1), list(day2, 3, 52.0, 2),
			query.search()
		);
		assertEquals(3, query.total());
		assertEquals(true, query.exists());
	}

	@Test void testGroupJoin()
	{
		new GroupItem(day1, 1);
		new GroupItem(day1, 2);
		new GroupItem(day2, 3);

		final Query<?> query = Query.newQuery(
			new Selectable<?>[]{day, day}, TYPE, null
		);
		final Join join = query.join( TYPE );
		join.setCondition( day.bind(join).equal(day) );
		query.setSelects( day, number, number.bind(join) );
		assertContains(
			list(day1, 1, 2), list(day1, 2, 1), list(day1, 1, 1), list(day1, 2, 2), list(day2, 3, 3),
			query.search()
		);
		assertEquals(5, query.total());
		assertEquals(true, query.exists());

		query.setGroupBy( day );
		query.setSelects( day, new Count() );
		assertContains(
			list(day1, 4), list(day2, 1),
			query.search()
		);
		assertEquals(2, query.total());
		assertEquals(true, query.exists());
	}

	@Test void testSorting()
	{
		final GroupItem item2 = new GroupItem(day1, 1);
		item2.setOptionalDouble( 2.0 );
		final GroupItem item1 = new GroupItem(day1, 2);
		item1.setOptionalDouble( 1.0 );
		new GroupItem(day2, 3);

		final Query<?> query = Query.newQuery(
			new Selectable<?>[]{optionalDouble, number.sum(), new Count()}, TYPE, null
		);
		query.setGroupBy( optionalDouble );
		query.setOrderBy( optionalDouble, true );
		assertEquals( "select optionalDouble,sum(number),count(*) from GroupItem group by optionalDouble order by optionalDouble", query.toString() );
		assertContains( list(null, 3, 1), list(1.0, 2, 1), list(2.0, 1, 1), query.search() );
		assertEquals(3, query.total());
		assertEquals(true, query.exists());

		query.setOrderBy( optionalDouble, false );
		assertEquals( "select optionalDouble,sum(number),count(*) from GroupItem group by optionalDouble order by optionalDouble desc", query.toString() );
		assertContains( list(null, 3, 1), list(2.0, 1, 1), list(1.0, 2, 1), query.search() );
		assertEquals(3, query.total());
		assertEquals(true, query.exists());
	}

	@Test void testHaving()
	{
		new GroupItem(day1, 1);
		new GroupItem(day1, 2);
		new GroupItem(day2, 1);
		new GroupItem(day2, 2);
		new GroupItem(day2, 3);

		final Query<?> query = Query.newQuery(new Selectable<?>[]{day, number.max()}, TYPE, null);
		query.setGroupBy(day);
		query.setOrderBy(day, true);
		assertEquals(
				"select day,max(number) from GroupItem " +
				"group by day " +
				"order by day",
				query.toString() );
		assertEquals(
				asList(asList(day1, 2), asList(day2, 3)),
				query.search());
		assertEquals(2, query.total());
		assertEquals(true, query.exists());

		query.setHaving(number.max().greaterOrEqual(3));
		assertEquals(
				"select day,max(number) from GroupItem " +
				"group by day " +
				"having max(number)>='3' " +
				"order by day",
				query.toString() );
		assertEquals(
				asList(asList(day2, 3)),
				query.search());
		assertEquals(1, query.total());
		assertEquals(true, query.exists());
	}
}
