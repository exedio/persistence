/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.CompareConditionItem.string;
import static com.exedio.cope.CompareConditionItem.intx;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SelectBindTest extends AbstractRuntimeTest
{
	public SelectBindTest()
	{
		super(CompareConditionTest.MODEL);
	}
	
	protected CompareConditionItem item1, item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new CompareConditionItem("string1", 1, 11l, 2.1, null, null, null));
		item2 = deleteOnTearDown(new CompareConditionItem("string2", 2, 12l, 2.2, null, null, null));
	}

	public void testIt()
	{
		{
			final Query<List<?>> q = new Query<List<?>>(new Function[]{string, intx}, TYPE, null);
			final Join j = q.join(TYPE);
			j.setCondition(string.bind(j).greater(string));
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"order by string desc",
					q.toString());
			
			final Collection<List<?>> result = q.search();
			final Iterator<List<?>> i = result.iterator();
			
			assertEqualsUnmodifiable(list("string1", 1), i.next());
		}
		{
			final Query<List<?>> q = new Query<List<?>>(new Function[]{string, intx}, TYPE, null);
			final Join j = q.join(TYPE); j.setCondition(string.bind(j).greater(string));
			q.setSelects(new Function[]{string, intx, string.bind(j), intx.bind(j)});
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx,c1.string,c1.intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"order by string desc",
					q.toString());
			
			final Collection<List<?>> result = q.search();
			final Iterator<List<?>> i = result.iterator();
			
			assertEqualsUnmodifiable(list("string1", 1, "string2", 2), i.next());
		}
		{
			final Query<List<?>> q = new Query<List<?>>(new Function[]{string, intx}, TYPE, null);
			final Join j1 = q.join(TYPE); j1.setCondition(string.bind(j1).greater(string));
			final Join j2 = q.join(TYPE); j2.setCondition(string.bind(j2).greater(string));
			q.setSelects(new Function[]{string, intx, string.bind(j1), intx.bind(j1), string.bind(j2), intx.bind(j2)});
			q.setOrderBy(string, false);
			assertEquals(
					"select string,intx,c1.string,c1.intx,c2.string,c2.intx from CompareConditionItem " +
					"join CompareConditionItem c1 on c1.string>string " +
					"join CompareConditionItem c2 on c2.string>string " +
					"order by string desc",
					q.toString());
			
			final Collection<List<?>> result = q.search();
			final Iterator<List<?>> i = result.iterator();
			
			assertEqualsUnmodifiable(list("string1", 1, "string2", 2, "string2", 2), i.next());
		}
		{
			final Query<List<?>> q = new Query<List<?>>(new Function[]{string, intx}, TYPE, null);
			try
			{
				q.setSelects((Selectable[])null);
				fail();
			}
			catch(NullPointerException e)
			{
				assertEquals(null, e.getMessage());
			}
			try
			{
				q.setSelects(new Selectable[]{});
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals("must not be empty", e.getMessage());
			}
			try
			{
				q.setSelects(new Selectable[]{null});
				fail();
			}
			catch(NullPointerException e)
			{
				assertEquals("must not be null on position 0", e.getMessage());
			}
		}
	}
}
