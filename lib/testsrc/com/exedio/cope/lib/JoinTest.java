/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
package com.exedio.cope.lib;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerItem2;

public class JoinTest extends DatabaseLibTest
{
	PointerItem item1a;
	PointerItem item1b;
	PointerItem2 item2a;
	PointerItem2 item2b;

	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item2a = new PointerItem2("hallo"));
		deleteOnTearDown(item2b = new PointerItem2("bello"));
		deleteOnTearDown(item1a = new PointerItem("bello", item2a));
		deleteOnTearDown(item1b = new PointerItem("collo", item2b));
	}

	public void testJoin()
	{
		{
			final Query query = new Query(PointerItem2.TYPE, null);
			query.join(PointerItem.TYPE, Cope.isNotNull(PointerItem.code));
			assertContains(item2b, item2a, item2b, item2a, query.search());
		}
		{
			final Query query = new Query(PointerItem2.TYPE, null);
			query.join(PointerItem.TYPE, Cope.join(PointerItem.pointer));
			assertContains(item2b, item2a, query.search());
		}
		{
			final Query query = new Query(PointerItem.TYPE, PointerItem2.TYPE, null);
			query.join(PointerItem.TYPE, Cope.join(PointerItem.pointer));
			assertContains(item1b, item1a, query.search());
		}
		{
			final Query query = new Query(PointerItem2.TYPE, null);
			query.join(PointerItem.TYPE, Cope.equal(PointerItem.code, PointerItem2.code));
			assertContains(item2b, query.search());
		}
		{
			final Query query = new Query(PointerItem.TYPE, Cope.equal(PointerItem2.code, "hallo"));
			query.join(PointerItem2.TYPE, Cope.join(PointerItem.pointer));
			assertContains(item1a, query.search());
		}
		{
			final Query query = new Query(new Selectable[]{PointerItem2.code, PointerItem.TYPE, PointerItem.code}, PointerItem2.TYPE, null);
			query.join(PointerItem.TYPE, Cope.join(PointerItem.pointer));
			assertContains(
					list("bello", item1b, "collo"),
					list("hallo", item1a, "bello"),
					query.search());
		}
	}

}
