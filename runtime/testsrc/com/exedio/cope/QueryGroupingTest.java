/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.GroupItem.TYPE;
import static com.exedio.cope.GroupItem.day;
import static com.exedio.cope.GroupItem.number;
import static com.exedio.cope.GroupItem.optionalDouble;

import com.exedio.cope.util.Day;
import com.exedio.dsmf.SQLRuntimeException;

public class QueryGroupingTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model( TYPE );

	static final Day day1 = new Day(2006, 02, 19);
	static final Day day2 = new Day(2006, 02, 20);
	static final Day day3 = new Day(2006, 02, 21);

	public QueryGroupingTest()
	{
		super( MODEL );
	}

	public void testGroupBy()
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

		query.setSelects( day, number.min(), number.max() );
		assertEquals( "select day,min(number),max(number) from GroupItem group by day", query.toString() );
		assertContains(
			list(day1, 1, 1), list(day2, 2, 3), list(day3, 4, 4),
			query.search()
		);

		query.setCondition( day.greater(day1) );
		assertEquals( "select day,min(number),max(number) from GroupItem where day>'2006/2/19' group by day", query.toString() );
		assertContains(
			list(day2, 2, 3), list(day3, 4, 4),
			query.search()
		);

		query.setCondition( number.notEqual(3) );
		assertEquals( "select day,min(number),max(number) from GroupItem where number<>'3' group by day", query.toString() );
		assertContains(
			list(day1, 1, 1), list(day2, 2, 2), list(day3, 4, 4),
			query.search()
		);

		query.setSelects( day, optionalDouble.sum() );
		query.setCondition( day.equal(day2) );
		assertEquals( "select day,sum(optionalDouble) from GroupItem where day='2006/2/20' group by day", query.toString() );
		assertContains(
			list(day2, null),
			query.search()
		);

		item2a.setOptionalDouble( 3.5 );
		assertEquals( null, item2b.getOptionalDouble() );
		assertContains(
			list(day2, 3.5),
			query.search()
		);

		item2b.setOptionalDouble( 1.0 );
		assertContains(
			list(day2, 4.5),
			query.search()
		);

		query.setSelects( day, optionalDouble.sum(), new Count() );
		assertContains(
			list(day2, 4.5, 2),
			query.search()
		);
	}

	public void testUngroupedSelect()
	{
		if ( postgresql )
		{
			// TODO: this test fails on postgresql, with the error below:
			//
			//java.lang.RuntimeException: test completed successfully but didn't clean up database
			//	at com.exedio.cope.junit.CopeTest.tearDown(CopeTest.java:221)
			//	at com.exedio.cope.AbstractRuntimeTest.tearDown(AbstractRuntimeTest.java:182)
			//	at com.exedio.cope.junit.CopeTest.runBare(CopeTest.java:107)
			//Caused by: java.lang.RuntimeException: test completed successfully but failed to delete a 'deleteOnTearDown' item
			//	at com.exedio.cope.junit.CopeTest.tearDown(CopeTest.java:211)
			//Caused by: com.exedio.dsmf.SQLRuntimeException: delete from "GroupItem" where "this"=?1? and "catch"=?0?
			//	at com.exedio.cope.Executor.update(Executor.java:297)
			//	at com.exedio.cope.Executor.updateStrict(Executor.java:244)
			//	at com.exedio.cope.DeletedState.doDelete(DeletedState.java:83)
			//	at com.exedio.cope.DeletedState.write(DeletedState.java:50)
			//	at com.exedio.cope.Entity.write(Entity.java:62)
			//	at com.exedio.cope.Item.deleteCopeItem(Item.java:416)
			//	at com.exedio.cope.Item.deleteCopeItem(Item.java:343)
			//	at com.exedio.cope.junit.CopeTest.tearDown(CopeTest.java:198)
			//Caused by: org.postgresql.util.PSQLException: FEHLER: aktuelle Transaktion wurde abgebrochen, Befehle werden bis zum Ende der Transaktion ignoriert
			return;
		}
		new GroupItem(day1, 1);
		new GroupItem(day2, 2);
		final Query<?> query = Query.newQuery( new Selectable<?>[]{day, number}, TYPE, null );
		query.setGroupBy( number );
		assertEquals( "select day,number from GroupItem group by number", query.toString() );
		try
		{
			fail( query.search().toString() );
		}
		catch ( final SQLRuntimeException e )
		{
			// fine
		}
	}

	public void testCannotGroupSingleSelect()
	{
		final Query<GroupItem> query = TYPE.newQuery();
		try
		{
			query.setGroupBy( day );
			fail();
		}
		catch ( final IllegalStateException e )
		{
			assertEquals("grouping not supported for single-select queries", e.getMessage());
		}
	}

	public void testMultiGrouping()
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

		query.setSelects( day, number, optionalDouble.sum(), new Count() );
		assertContains(
			list(day1, 1, 10.0, 1), list(day2, 2, 20.0, 1), list(day2, 3, 52.0, 2),
			query.search()
		);
	}

	public void testGroupJoin()
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

		query.setGroupBy( day );
		query.setSelects( day, new Count() );
		assertContains(
			list(day1, 4), list(day2, 1),
			query.search()
		);
	}

	public void testSorting()
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

		query.setOrderBy( optionalDouble, false );
		assertEquals( "select optionalDouble,sum(number),count(*) from GroupItem group by optionalDouble order by optionalDouble desc", query.toString() );
		assertContains( list(null, 3, 1), list(2.0, 1, 1), list(1.0, 2, 1), query.search() );
	}

}
