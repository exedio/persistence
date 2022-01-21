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

	@BeforeEach final void setUp()
	{
		item = new ItemCacheDataItem();
		restartTransaction();
	}

	@Test void testCommitSingleString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testCommitSingleData()
	{
		item.setData("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test void testCommitMultiEmpty()
	{
		item.set(SetValue.EMPTY_ARRAY);
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testCommitMultiString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testCommitMultiData()
	{
		item.setDataMulti("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test void testCommitMultiBoth()
	{
		item.setBothMulti("zick", "aabbccdd");
		assertEquals("zick", item.getString());
		assertEquals("aabbccdd", item.getData());

		model.commit();
		model.startTransaction("ItemCacheDataTest");
		assertEquals("zick", item.getString());
		assertEquals("aabbccdd", item.getData());
	}

	@Test void testRollbackSingleString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testRollbackSingleData()
	{
		item.setData("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testRollbackMultiEmpty()
	{
		item.set(SetValue.EMPTY_ARRAY);
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testRollbackMultiString()
	{
		item.setString("zick");
		assertEquals("zick", item.getString());
		assertEquals(null, item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testRollbackMultiData()
	{
		item.setDataMulti("aabbccdd");
		assertEquals(null, item.getString());
		assertEquals("aabbccdd", item.getData());

		model.rollback();
		model.startTransaction("ItemCacheDataTest");
		assertEquals(null, item.getString());
		assertEquals(null, item.getData());
	}

	@Test void testRollbackMultiBoth()
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
