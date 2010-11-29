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

import static com.exedio.cope.ItemCacheDataItem.TYPE;
import static java.lang.Integer.MIN_VALUE;

import com.exedio.cope.util.Hex;

public class ItemCacheDataTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);

	public ItemCacheDataTest()
	{
		super(MODEL);
	}

	ItemCacheDataItem item;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new ItemCacheDataItem());
	}

	public void testCommitSingleString()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setString("zick");
		assertModificationCount(1);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(2);
	}

	public void testCommitSingleData()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setData(Hex.decodeLower("aabbccdd"));
		assertModificationCount(MIN_VALUE);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(1);
	}

	public void testCommitMultiString()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setString("zick");
		assertModificationCount(1);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(2);
	}

	public void testCommitMultiData()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		assertModificationCount(1); // TODO should be 0

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(2); // TODO should be 1
	}

	public void testCommitMultiBoth()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		assertModificationCount(1);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(2);
	}

	public void testRollbackSingleString()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setString("zick");
		assertModificationCount(1);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(1);
	}

	public void testRollbackSingleData()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setData(Hex.decodeLower("aabbccdd"));
		assertModificationCount(MIN_VALUE);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(1);
	}

	public void testRollbackMultiString()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setString("zick");
		assertModificationCount(1);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(1);
	}

	public void testRollbackMultiData()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		assertModificationCount(1); // TODO should be 1

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		final ConnectProperties props = model.getConnectProperties();
		if(props.itemCacheConcurrentModificationDetection.booleanValue() &&
			props.getItemCacheLimit()>0)
		{
			try
			{
				item.setString("zack");
				fail();
			}
			catch(final TemporaryTransactionException e) // TODO this is a bug
			{
				assertTrue(e.getMessage(), e.getMessage().startsWith("expected one row, but got 0 on statement: "));
			}
		}
		else
			item.setString("zack");

		assertModificationCount(MIN_VALUE);

		// allow teardown to delete item
		restartTransaction();
		assertModificationCount(MIN_VALUE);
	}

	public void testRollbackMultiBoth()
	{
		assertModificationCount(0);

		restartTransaction();
		assertModificationCount(MIN_VALUE);

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		assertModificationCount(1);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertModificationCount(MIN_VALUE);

		item.setString("zack");
		assertModificationCount(1);
	}

	private void assertModificationCount(final int expected)
	{
		if(model.getConnectProperties().itemCacheConcurrentModificationDetection.booleanValue())
			assertEquals(expected, item.getModificationCountIfActive());
	}
}
