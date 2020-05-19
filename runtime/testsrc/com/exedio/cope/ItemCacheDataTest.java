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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@BeforeEach final void setUp()
	{
		v = ItemCacheDataItem.data.getVaultInfo()!=null;
		item = new ItemCacheDataItem();
	}

	@Test void testNull()
	{
		try
		{
			item.set((SetValue<?>[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("setValues", e.getMessage());
		}
	}

	@Test void testCommitSingleString()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, NONE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	@Test void testCommitSingleData()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setData("aabbccdd");
		assertUpdateCount(v ? 1 : 0, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, v ? NONE : 0);

		item.setString("zack");
		assertUpdateCount(v?2:1, v?1:0);
	}

	@Test void testCommitMultiEmpty()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.set(new SetValue<?>[0]);
		assertUpdateCount(NONE, NONE);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, NONE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testCommitMultiString()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, NONE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	@Test void testCommitMultiData() // same as testCommitSingleData
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setDataMulti("aabbccdd");
		assertUpdateCount(v ? 1 : 0, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, v ? NONE : 0);

		item.setString("zack");
		assertUpdateCount(v?2:1, v?1:0);
	}

	@Test void testCommitMultiBoth()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setBothMulti("zick", "aabbccdd");
		assertUpdateCount(1, 0);

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, NONE);

		item.setString("zack");
		assertUpdateCount(2, 1);
	}

	@Test void testRollbackSingleString()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testRollbackSingleData()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setData("aabbccdd");
		assertUpdateCount(v ? 1 : 0, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testRollbackMultiEmpty()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.set(new SetValue<?>[0]);
		assertUpdateCount(NONE, NONE);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, NONE);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testRollbackMultiString()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setString("zick");
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testRollbackMultiData() // same as testRollbackSingleData
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setDataMulti("aabbccdd");
		assertUpdateCount(v ? 1 : 0, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@Test void testRollbackMultiBoth()
	{
		assertUpdateCount(0, NONE);

		restartTransaction();
		assertUpdateCount(NONE, NONE);

		item.setBothMulti("zick", "aabbccdd");
		assertUpdateCount(1, 0);

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertUpdateCount(NONE, 0);

		item.setString("zack");
		assertUpdateCount(1, 0);
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private void assertUpdateCount(final int expected, final int global)
	{
		final ConnectProperties props = model.getConnectProperties();
		assertAll(
				() -> assertEquals(expected, item.getUpdateCountIfActive(), "transaction"),
				() -> assertEquals(props.getItemCacheLimit()>0 ? global : NONE, item.getUpdateCountGlobal(), "global")
		);
	}

	private static final int NONE = Integer.MIN_VALUE;
}
