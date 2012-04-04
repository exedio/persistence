/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.Day;
import com.exedio.dsmf.SQLRuntimeException;

public class QueryGroupingTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model( GroupItem.TYPE );

	static final Day day1 = new Day(2006, 02, 19);
	static final Day day2 = new Day(2006, 02, 20);
	static final Day day3 = new Day(2006, 02, 21);

	public QueryGroupingTest()
	{
		super( MODEL );
	}

	public void testGroupBy()
	{
		final GroupItem item1 = deleteOnTearDown( new GroupItem(day1, 1) );
		final GroupItem item2a = deleteOnTearDown( new GroupItem(day2, 2) );
		final GroupItem item2b = deleteOnTearDown( new GroupItem(day2, 3) );
		final GroupItem item3 = deleteOnTearDown( new GroupItem(day3, 4) );

		assertContains(
			item1, item2a, item2b, item3,
			GroupItem.TYPE.search()
		);
		final Query<?> query = Query.newQuery( new Selectable[]{GroupItem.day, GroupItem.number.sum()}, GroupItem.TYPE, Condition.TRUE );
		assertEquals( "select day,sum(number) from GroupItem", query.toString() );
		query.setGroupBy( GroupItem.day );
		assertEquals( "select day,sum(number) from GroupItem group by day", query.toString() );

		assertContains(
			list(day1, i1), list(day2, i5), list(day3, i4),
			query.search()
		);

		query.setSelects( GroupItem.day, GroupItem.number.min(), GroupItem.number.max() );
		assertEquals( "select day,min(number),max(number) from GroupItem group by day", query.toString() );
		assertContains(
			list(day1, i1, i1), list(day2, i2, i3), list(day3, i4, i4),
			query.search()
		);

		query.setCondition( GroupItem.day.greater(day1) );
		assertEquals( "select day,min(number),max(number) from GroupItem where day>'2006/2/19' group by day", query.toString() );
		assertContains(
			list(day2, i2, i3), list(day3, i4, i4),
			query.search()
		);

		query.setCondition( GroupItem.number.notEqual(3) );
		assertEquals( "select day,min(number),max(number) from GroupItem where number<>'3' group by day", query.toString() );
		assertContains(
			list(day1, i1, i1), list(day2, i2, i2), list(day3, i4, i4),
			query.search()
		);

		query.setSelects( GroupItem.day, GroupItem.optionalDouble.sum() );
		query.setCondition( GroupItem.day.equal(day2) );
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

		query.setSelects( GroupItem.day, GroupItem.optionalDouble.sum(), new CountSelectable() );
		assertContains(
			list(day2, 4.5, 2),
			query.search()
		);
	}

	public void testUngroupedSelect()
	{
		deleteOnTearDown( new GroupItem(day1, 1) );
		deleteOnTearDown( new GroupItem(day2, 2) );
		final Query<?> query = Query.newQuery( new Selectable[]{GroupItem.day, GroupItem.number}, GroupItem.TYPE, Condition.TRUE );
		query.setGroupBy( GroupItem.number );
		assertEquals( "select day,number from GroupItem group by number", query.toString() );
		if ( model.supportsSelectingUngrouped() )
		{
			assertContains(
				list(day1, 1), list(day2, 2),
				query.search()
			);
		}
		else
		{
			try
			{
				fail( query.search().toString() );
			}
			catch ( SQLRuntimeException e )
			{
				// fine
			}
		}
	}

	public void testCannotGroupSingleSelect()
	{
		final Query<GroupItem> query = GroupItem.TYPE.newQuery();
		try
		{
			query.setGroupBy( GroupItem.day );
			fail();
		}
		catch ( IllegalStateException e )
		{
			// fine
		}
	}

	public void testMultiGrouping()
	{
		final GroupItem item1 = deleteOnTearDown( new GroupItem(day1, 1) );
		item1.setOptionalDouble( 10.0 );
		final GroupItem item2a = deleteOnTearDown( new GroupItem(day2, 2) );
		item2a.setOptionalDouble( 20.0 );
		final GroupItem item2b = deleteOnTearDown( new GroupItem(day2, 3) );
		item2b.setOptionalDouble( 25.0 );
		final GroupItem item2c = deleteOnTearDown( new GroupItem(day2, 3) );
		item2c.setOptionalDouble( 27.0 );

		final Query<?> query = Query.newQuery(
			new Selectable[]{GroupItem.day, GroupItem.number, GroupItem.optionalDouble.sum()}, GroupItem.TYPE, Condition.TRUE
		);
		query.setGroupBy( GroupItem.day, GroupItem.number );
		assertEquals( "select day,number,sum(optionalDouble) from GroupItem group by day,number", query.toString() );
		assertContains(
			list(day1, 1, 10.0), list(day2, 2, 20.0), list(day2, 3, 52.0),
			query.search()
		);

		query.setSelects( GroupItem.day, GroupItem.number, GroupItem.optionalDouble.sum(), new CountSelectable() );
		assertContains(
			list(day1, 1, 10.0, 1), list(day2, 2, 20.0, 1), list(day2, 3, 52.0, 2),
			query.search()
		);
	}

	public void testGroupJoin()
	{
		deleteOnTearDown( new GroupItem(day1, 1) );
		deleteOnTearDown( new GroupItem(day1, 2) );
		deleteOnTearDown( new GroupItem(day2, 3) );

		final Query<?> query = Query.newQuery(
			new Selectable[]{GroupItem.day, GroupItem.day}, GroupItem.TYPE, Condition.TRUE
		);
		final Join join = query.join( GroupItem.TYPE );
		join.setCondition( GroupItem.day.bind(join).equal(GroupItem.day) );
		query.setSelects( GroupItem.day, GroupItem.number, GroupItem.number.bind(join) );
		assertContains(
			list(day1, 1, 2), list(day1, 2, 1), list(day1, 1, 1), list(day1, 2, 2), list(day2, 3, 3),
			query.search()
		);

		query.setGroupBy( GroupItem.day );
		query.setSelects( GroupItem.day, new CountSelectable() );
		assertContains(
			list(day1, 4), list(day2, 1),
			query.search()
		);
	}

}
