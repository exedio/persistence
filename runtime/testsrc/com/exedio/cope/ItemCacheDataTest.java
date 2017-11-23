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
import static java.lang.Integer.MIN_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemCacheDataTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public ItemCacheDataTest()
	{
		super(MODEL);
	}

	boolean v;
	ItemCacheDataItem item;

	@BeforeEach public final void setUp()
	{
		v = ItemCacheDataItem.data.getVaultInfo()!=null;
		item = new ItemCacheDataItem();
	}

	@Test public void testNull()
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

	@Test public void testCommitSingleString()
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

	@Test public void testCommitSingleData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setData("aabbccdd");
		assertUpdateCount(v ? 1 : MIN_VALUE, v ? 0 : MIN_VALUE);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(v?2:1, v?1:0);
	}

	@Test public void testCommitMultiEmpty()
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

	@Test public void testCommitMultiString()
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

	@Test public void testCommitMultiData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setDataMulti("aabbccdd");
		assertUpdateCount(v?1:0, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, v ? MIN_VALUE : 0);

		item.setString("zack");
		assertUpdateCount(v?2:1, v?1:0);
	}

	@Test public void testCommitMultiBoth()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setBothMulti("zick", "aabbccdd");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	@Test public void testRollbackSingleString()
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

	@Test public void testRollbackSingleData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setData("aabbccdd");
		assertUpdateCount(v ? 1 : MIN_VALUE, v ? 0 : MIN_VALUE);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, v ? 0 : MIN_VALUE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test public void testRollbackMultiEmpty()
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

	@Test public void testRollbackMultiString()
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

	@Test public void testRollbackMultiData()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setDataMulti("aabbccdd");
		assertUpdateCount(v?1:0, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(MIN_VALUE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test public void testRollbackMultiBoth()
	{
		assertUpdateCount(0, MIN_VALUE);

		restartTransaction();
		assertUpdateCount(MIN_VALUE, MIN_VALUE);

		item.setBothMulti("zick", "aabbccdd");
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
		assertEquals(expected, item.getUpdateCountIfActive(), "transaction");
		if(props.getItemCacheLimit()>0)
			assertEquals(global, item.getUpdateCountGlobal(), "global");
		else
			assertEquals(MIN_VALUE, item.getUpdateCountGlobal(), "global");
	}
}
