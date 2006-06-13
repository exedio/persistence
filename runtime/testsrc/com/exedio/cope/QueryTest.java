/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Day;

public class QueryTest extends CopeAssert
{
	static final Day d1 = new Day(2006, 02, 19);
	
	public void testIt()
	{
		final Query q = DayItem.TYPE.newQuery(null);
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(null, q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
		
		q.narrow(DayItem.day.less(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(DayItem.day.less(d1), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
		
		q.narrow(DayItem.day.greater(d1));
		assertEquals(DayItem.TYPE, q.getType());
		assertEquals(DayItem.day.less(d1).and(DayItem.day.greater(d1)), q.getCondition());
		assertEqualsUnmodifiable(list(), q.getJoins());
	}
	
}
