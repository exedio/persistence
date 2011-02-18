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

import java.util.List;

public class PolymorphicBoundSelectTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PolymorphicBoundSelectSuperItem.TYPE, PolymorphicBoundSelectSubItem.TYPE);

	public PolymorphicBoundSelectTest()
	{
		super(MODEL);
	}

	public void testItemField()
	{
		final PolymorphicBoundSelectSuperItem superItem = deleteOnTearDown(new PolymorphicBoundSelectSuperItem((PolymorphicBoundSelectSuperItem)null));
		final PolymorphicBoundSelectSubItem subItem = deleteOnTearDown(new PolymorphicBoundSelectSubItem(superItem));

		final Query<List<Object>> q = Query.newQuery(
			new Selectable[]{PolymorphicBoundSelectSubItem.TYPE.getThis(), PolymorphicBoundSelectSuperItem.parent},
			PolymorphicBoundSelectSubItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectSuperItem.TYPE, null);
		j.setCondition(PolymorphicBoundSelectSubItem.parent.equalTarget(j));
		q.setSelects(PolymorphicBoundSelectSubItem.TYPE.getThis(), PolymorphicBoundSelectSuperItem.parent.bind(j));
		q.addOrderBy(PolymorphicBoundSelectSuperItem.parent.bind(j));
		assertEquals(list(list(subItem, null)), q.search());
	}

	public void testThis()
	{
		final PolymorphicBoundSelectSuperItem superItem = deleteOnTearDown(new PolymorphicBoundSelectSuperItem((PolymorphicBoundSelectSuperItem)null));
		deleteOnTearDown(new PolymorphicBoundSelectSubItem(superItem));

		final Query<List<Object>> q = Query.newQuery(
			new Selectable[]{PolymorphicBoundSelectSuperItem.TYPE.getThis(), PolymorphicBoundSelectSubItem.parent},
			PolymorphicBoundSelectSuperItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectSuperItem.TYPE, null);
		j.setCondition(PolymorphicBoundSelectSubItem.parent.equalTarget(j));
		q.setSelects(PolymorphicBoundSelectSuperItem.TYPE.getThis().bind(j), PolymorphicBoundSelectSubItem.parent);
		q.addOrderBy(PolymorphicBoundSelectSuperItem.parent);
		if(!model.nullsAreSortedLow())
			assertEquals(list(list(superItem, superItem), list(null, null)), q.search());
		else
			assertEquals(list(list(null, null), list(superItem, superItem)), q.search());
	}
}
