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

import static com.exedio.cope.ItemCacheDataTest.MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataRestartTest extends TestWithEnvironment
{
	public DataRestartTest()
	{
		super(MODEL);
	}

	ItemCacheDataItem item;

	@BeforeEach public final void setUp()
	{
		item = new ItemCacheDataItem();
		restartTransaction();
	}

	@Test public void testCommitSingleString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testCommitSingleData()
	{
		item.setData("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test public void testCommitMultiEmpty()
	{
		item.set(new SetValue<?>[0]);
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testCommitMultiString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testCommitMultiData()
	{
		item.setDataMulti("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test public void testCommitMultiBoth()
	{
		item.setBothMulti("zick", "aabbccdd");
		assertEquals("zick", item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test public void testRollbackSingleString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testRollbackSingleData()
	{
		item.setData("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testRollbackMultiEmpty()
	{
		item.set(new SetValue<?>[0]);
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testRollbackMultiString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testRollbackMultiData()
	{
		item.setDataMulti("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test public void testRollbackMultiBoth()
	{
		item.setBothMulti("zick", "aabbccdd");
		assertEquals("zick", item.getString());
		assertEquals("aabbccdd", item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}
}
