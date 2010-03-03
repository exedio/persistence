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

package com.exedio.cope;

import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;

public class PolymorphicBoundSelectTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PolymorphicBoundSelectTestSuperItem.TYPE, PolymorphicBoundSelectTestSubItem.TYPE);
	
	public PolymorphicBoundSelectTest()
	{
		super(MODEL);
	}
	
	public void testItemField()
	{
		final PolymorphicBoundSelectTestSuperItem superItem = deleteOnTearDown(new PolymorphicBoundSelectTestSuperItem((PolymorphicBoundSelectTestSuperItem)null));
		final PolymorphicBoundSelectTestSubItem subItem = deleteOnTearDown(new PolymorphicBoundSelectTestSubItem(superItem));

		final Query<List<Object>> q = Query.newQuery(
			new Selectable[]{PolymorphicBoundSelectTestSubItem.TYPE.getThis(), PolymorphicBoundSelectTestSuperItem.parent},
			PolymorphicBoundSelectTestSubItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectTestSuperItem.TYPE, null);
		j.setCondition(PolymorphicBoundSelectTestSubItem.parent.equalTarget(j));
		q.setSelects(PolymorphicBoundSelectTestSubItem.TYPE.getThis(), PolymorphicBoundSelectTestSuperItem.parent.bind(j));
		q.addOrderBy(PolymorphicBoundSelectTestSuperItem.parent.bind(j));
		if(noJoinParentheses)
			return;
		assertEquals(list(list(subItem, null)), q.search());
	}
	
	public void testThis()
	{
		final PolymorphicBoundSelectTestSuperItem superItem = deleteOnTearDown(new PolymorphicBoundSelectTestSuperItem((PolymorphicBoundSelectTestSuperItem)null));
		deleteOnTearDown(new PolymorphicBoundSelectTestSubItem(superItem));

		final Query<List<Object>> q = Query.newQuery(
			new Selectable[]{PolymorphicBoundSelectTestSuperItem.TYPE.getThis(), PolymorphicBoundSelectTestSubItem.parent},
			PolymorphicBoundSelectTestSuperItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectTestSuperItem.TYPE, null);
		j.setCondition(PolymorphicBoundSelectTestSubItem.parent.equalTarget(j));
		q.setSelects(PolymorphicBoundSelectTestSuperItem.TYPE.getThis().bind(j), PolymorphicBoundSelectTestSubItem.parent);
		q.addOrderBy(PolymorphicBoundSelectTestSuperItem.parent.bind(j));
		if(noJoinParentheses)
			return;
		assertEquals(list(list(null, null), list(superItem, superItem)), q.search());
	}
}
