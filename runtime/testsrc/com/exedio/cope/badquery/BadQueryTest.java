/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.badquery;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;

public class BadQueryTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(SuperItem.TYPE, QueryItem.TYPE, SuperContainer.TYPE, SubContainer.TYPE);
	
	public BadQueryTest()
	{
		super(MODEL);
	}
	
	QueryItem left1, left2, leftX;
	SuperContainer middle1, middle2, middleX;
	SubContainer right1, right2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		leftX = deleteOnTearDown(new QueryItem("leftX"));
		left1 = deleteOnTearDown(new QueryItem("left1"));
		left2 = deleteOnTearDown(new QueryItem("left2"));
		middleX = deleteOnTearDown(new SuperContainer("middleX", leftX, false));
		middle1 = deleteOnTearDown(new SuperContainer("middle1", left1, false));
		middle2 = deleteOnTearDown(new SubContainer("middle2", left2, false, middleX));
		right1 = deleteOnTearDown(new SubContainer("right1", leftX, false, middle1));
		right2 = deleteOnTearDown(new SubContainer("right2", leftX, false, middle2));
	}
	
	public void testIt()
	{
		{
			// with specifying join
			final Query<QueryItem> query = QueryItem.TYPE.newQuery(null);
			final Join superJoin = query.join(SuperContainer.TYPE);
			superJoin.setCondition(SuperContainer.queryItem.bind(superJoin).equalTarget());
			query.join(SubContainer.TYPE, SubContainer.superContainer.equalTarget(superJoin));
			query.setCondition(SuperContainer.TYPE.getThis().bind(superJoin).notInstanceOf(SubContainer.TYPE));
			assertContains(leftX, left1, query.search());
		}
		{
			// with specifying join but without condition
			final Query<QueryItem> query = QueryItem.TYPE.newQuery(null);
			final Join superJoin = query.join(SuperContainer.TYPE);
			superJoin.setCondition(SuperContainer.queryItem.bind(superJoin).equalTarget());
			query.join(SubContainer.TYPE, SubContainer.superContainer.equalTarget(superJoin));
			assertContains(leftX, left1, left2, query.search());
		}
		
		{
			// without specifying join
			final Query<QueryItem> query = QueryItem.TYPE.newQuery(null);
			final Join superJoin = query.join(SuperContainer.TYPE);
			superJoin.setCondition(SuperContainer.queryItem.bind(superJoin).equalTarget());
			query.join(SubContainer.TYPE, SubContainer.superContainer.equalTarget(superJoin));
			query.setCondition(SuperContainer.TYPE.getThis().notInstanceOf(SubContainer.TYPE));
			try
			{
				query.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				// TODO
				// should not happen, since SuperContainer.this is not ambiguous
				// because feature "this" is not inherited.
				assertEquals("feature SuperContainer#class is ambiguous, use Function#bind (deprecated)", e.getMessage());
			}
		}
	}
	
}
