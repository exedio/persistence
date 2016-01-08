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

import static com.exedio.cope.tojunit.Assert.assertContains;

import org.junit.Before;
import org.junit.Test;

public class PolymorphQueryCacheInvalidationTest extends TestWithEnvironment
{
	public PolymorphQueryCacheInvalidationTest()
	{
		super(InstanceOfModelTest.MODEL);
	}

	InstanceOfAItem itema;

	@Before public final void setUp()
	{
		itema = new InstanceOfAItem("itema");
	}

	@Test public void testIt()
	{
		final Query<InstanceOfAItem> q = InstanceOfAItem.TYPE.newQuery(null);
		assertContains(itema, q.search());

		final InstanceOfB1Item itemb1a = new InstanceOfB1Item("itemb1a");

		restartTransaction();

		assertContains(itema, itemb1a, q.search());

		final InstanceOfB1Item itemb1b = new InstanceOfB1Item("itemb1b");

		restartTransaction();

		assertContains(itema, itemb1a, itemb1b, q.search());
	}

}
