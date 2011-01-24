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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.CacheIsolationItem.name;

public class CacheTouchTest extends AbstractRuntimeTest
{
	public CacheTouchTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new CacheIsolationItem("itemName"));
	}

	public void testIt()
	{
		if(!mysql) return; // TODO

		model.commit();

		final Transaction loader = model.startTransaction("CacheTouchTest loader");
		assertEquals(item, TYPE.searchSingleton(name.equal("itemName")));
		assertSame(loader, model.leaveTransaction());

		model.startTransaction("CacheTouchTest changer");
		item.setName("itemName2");
		model.commit();

		model.joinTransaction(loader);
		assertEquals("itemName", item.getName());
		model.commit();

		model.startTransaction("CacheTouchTest failer");

		if(model.getConnectProperties().itemCacheConcurrentModificationDetection.booleanValue())
		{
			try
			{
				// TODO
				// The exception is a bug that needs to be fixed.
				item.setName("itemName3");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				// ok
			}
		}
		else
		{
			item.setName("itemName3");
		}
	}
}
