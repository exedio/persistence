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
		restartTransaction();

		item.setString("zick");
		model.commit();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testCommitSingleData()
	{
		restartTransaction();

		item.setData(Hex.decodeLower("aabbccdd"));
		model.commit();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testCommitMultiString()
	{
		restartTransaction();

		item.setString("zick");
		model.commit();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testCommitMultiData()
	{
		restartTransaction();

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		model.commit();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testCommitMultiBoth()
	{
		restartTransaction();

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		model.commit();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testRollbackSingleString()
	{
		restartTransaction();

		item.setString("zick");
		model.rollback();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testRollbackSingleData()
	{
		restartTransaction();

		item.setData(Hex.decodeLower("aabbccdd"));
		model.rollback();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testRollbackMultiString()
	{
		restartTransaction();

		item.setString("zick");
		model.rollback();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}

	public void testRollbackMultiData()
	{
		restartTransaction();

		item.setDataMulti(Hex.decodeLower("aabbccdd"));
		model.rollback();
		model.startTransaction("ItemCacheDataTest");

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

		// allow teardown to delete item
		restartTransaction();
	}

	public void testRollbackMultiBoth()
	{
		restartTransaction();

		item.setBothMulti("zick", Hex.decodeLower("aabbccdd"));
		model.rollback();
		model.startTransaction("ItemCacheDataTest");

		item.setString("zack");
	}
}
