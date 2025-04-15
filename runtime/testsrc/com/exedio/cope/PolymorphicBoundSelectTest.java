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

import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class PolymorphicBoundSelectTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(PolymorphicBoundSelectSuperItem.TYPE, PolymorphicBoundSelectSubItem.TYPE);

	public PolymorphicBoundSelectTest()
	{
		super(MODEL);
	}

	@Test void testItemField()
	{
		final PolymorphicBoundSelectSuperItem superItem = new PolymorphicBoundSelectSuperItem((PolymorphicBoundSelectSuperItem)null);
		final PolymorphicBoundSelectSubItem subItem = new PolymorphicBoundSelectSubItem(superItem);

		final Query<List<Object>> q = Query.newQuery(
			new Selectable<?>[]{PolymorphicBoundSelectSubItem.TYPE.getThis(), PolymorphicBoundSelectSuperItem.parent},
			PolymorphicBoundSelectSubItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectSuperItem.TYPE, PolymorphicBoundSelectSuperItem.parent::isTarget);
		q.setSelects(PolymorphicBoundSelectSubItem.TYPE.getThis(), PolymorphicBoundSelectSuperItem.parent.bind(j));
		q.addOrderBy(PolymorphicBoundSelectSuperItem.parent.bind(j));
		assertEquals(list(list(subItem, null)), q.search());
	}

	@Test void testThis()
	{
		final PolymorphicBoundSelectSuperItem superItem = new PolymorphicBoundSelectSuperItem((PolymorphicBoundSelectSuperItem)null);
		new PolymorphicBoundSelectSubItem(superItem);

		final Query<List<Object>> q = Query.newQuery(
			new Selectable<?>[]{PolymorphicBoundSelectSuperItem.TYPE.getThis(), PolymorphicBoundSelectSuperItem.parent},
			PolymorphicBoundSelectSuperItem.TYPE,
			null
		);
		final Join j = q.joinOuterLeft(PolymorphicBoundSelectSuperItem.TYPE, PolymorphicBoundSelectSuperItem.parent::isTarget);
		q.setSelects(PolymorphicBoundSelectSuperItem.TYPE.getThis().bind(j), PolymorphicBoundSelectSuperItem.parent);
		q.addOrderBy(PolymorphicBoundSelectSuperItem.parent);
		assertEquals(list(list(null, null), list(superItem, superItem)), q.search());
	}
}
