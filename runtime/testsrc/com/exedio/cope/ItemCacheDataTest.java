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

import static com.exedio.cope.ItemCacheDataItem.TYPE;
import static com.exedio.cope.SchemaInfo.isUpdateCounterEnabled;
import static java.lang.Integer.MIN_VALUE;

import com.exedio.cope.util.Hex;

public class ItemCacheDataTest extends AbstractRuntimeModelTest
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

	public void testNull()
	{
		try
		{
			item.set((SetValue[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("setValues", e.getMessage());
		}
	}

	public void testCommitSingleString()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	public void testCommitSingleData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setData(Hex.decodeLower("aabbccdd"));
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testCommitMultiEmpty()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.set(new SetValue<?>[0]);
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testCommitMultiString()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	public void testCommitMultiData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		assertUpdateCount(0, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testCommitMultiBoth()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	public void testRollbackSingleString()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testRollbackSingleData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setData(Hex.decodeLower("aabbccdd"));
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testRollbackMultiEmpty()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.set(new SetValue<?>[0]);
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testRollbackMultiString()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testRollbackMultiData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		assertUpdateCount(0, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	public void testRollbackMultiBoth()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertUpdateCount(final int expected, final int global)
	{
		final ConnectProperties props = model.getConnectProperties();
		if(isUpdateCounterEnabled(model))
		{
			assertEquals("transaction", expected, item.getUpdateCountIfActive());
			if(props.getItemCacheLimit()>0)
				assertEquals("global", global, item.getUpdateCountGlobal());
			else
				assertEquals("global", Integer.MIN_VALUE, item.getUpdateCountGlobal());
		}
	}
}
