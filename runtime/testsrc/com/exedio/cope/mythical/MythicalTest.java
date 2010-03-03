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

package com.exedio.cope.mythical;

import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;

public class MythicalTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(MythicalSuper.TYPE, MythicalSub.TYPE);
	
	public MythicalTest()
	{
		super(MODEL);
	}
	
	public void test()
	{
		final MythicalSuper superItem = deleteOnTearDown(new MythicalSuper((MythicalSuper)null));
		deleteOnTearDown(new MythicalSub(superItem));

		final Query<List<Object>> q = Query.newQuery(
			new Selectable[]{MythicalSub.TYPE.getThis(), MythicalSuper.parent},
			MythicalSub.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(MythicalSuper.TYPE, null);
		j.setCondition(MythicalSub.parent.equalTarget(j));
		q.setSelects(MythicalSub.TYPE.getThis(), MythicalSuper.parent.bind(j));
		q.addOrderBy(MythicalSuper.parent.bind(j));
		if(noJoinParentheses)
			return;
		try
		{
			q.search();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(
					"inconsistent type column on field MythicalSuper.parent: MythicalSuper" +
					" --- row: {MythicalSuper#parentType=MythicalSuper}" +
					" --- query: " +
						"select this,m1.MythicalSuper.parent " +
						"from MythicalSub left join MythicalSuper m1 on MythicalSuper.parent=m1.MythicalSuper.this " +
						"order by m1.MythicalSuper.parent",
				e.getMessage());
		}
	}
}
