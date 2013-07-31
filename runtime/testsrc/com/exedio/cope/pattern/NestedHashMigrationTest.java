/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;

public class NestedHashMigrationTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(NestedHashMigrationItem.TYPE);

	public NestedHashMigrationTest()
	{
		super(MODEL);
	}

	public void testSetNull()
	{
		final NestedHashMigrationItem item = deleteOnTearDown(new NestedHashMigrationItem("111111", 1.1));

		try
		{
			item.setPassword(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for Customer.password", e.getMessage());
		}
		try
		{
			item.set(NestedHashMigrationItem.password.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for Customer.password", e.getMessage());
		}
		try
		{
			new NestedHashMigrationItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for Customer.password", e.getMessage());
		}

		try
		{
			NestedHashMigrationItem.migratePassword(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	public void testMigratePasswordOnChange()
	{
		final NestedHashMigrationItem item = deleteOnTearDown(new NestedHashMigrationItem("111111", 1.1));
		assertNotNull(NestedHashMigrationItem.password.getOldHash().getHash(item));
		assertNull(NestedHashMigrationItem.password.getNewHash().getHash(item));

		assertTrue(item.checkPassword("111111"));
		assertFalse(item.checkPassword("222222"));

		item.setPassword("222222");
		assertNull(NestedHashMigrationItem.password.getOldHash().getHash(item));
		assertNotNull(NestedHashMigrationItem.password.getNewHash().getHash(item));
		assertTrue(item.checkPassword("222222"));
		assertFalse(item.checkPassword("333333"));
		assertFalse(item.checkPassword("111111"));
	}

	public void testMigratePasswordAutomatically()
	{
		final NestedHashMigrationItem itemA = deleteOnTearDown(new NestedHashMigrationItem("111111A", 1.1));
		final NestedHashMigrationItem itemB = deleteOnTearDown(new NestedHashMigrationItem("111111B", 1.1));
		final NestedHashMigrationItem itemX = deleteOnTearDown(new NestedHashMigrationItem("111111X"));
		assertNotNull(NestedHashMigrationItem.password.getOldHash().getHash(itemA));
		assertNotNull(NestedHashMigrationItem.password.getOldHash().getHash(itemB));
		assertNull(NestedHashMigrationItem.password.getOldHash().getHash(itemX));
		assertNull(NestedHashMigrationItem.password.getNewHash().getHash(itemA));
		assertNull(NestedHashMigrationItem.password.getNewHash().getHash(itemB));
		assertNotNull(NestedHashMigrationItem.password.getNewHash().getHash(itemX));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			NestedHashMigrationItem.migratePassword(ctx);
			assertEquals(2, ctx.progress);
		}

		model.startTransaction("test result");
		assertNull(NestedHashMigrationItem.password.getOldHash().getHash(itemA));
		assertNull(NestedHashMigrationItem.password.getOldHash().getHash(itemB));
		assertNull(NestedHashMigrationItem.password.getOldHash().getHash(itemX));
		assertNotNull(NestedHashMigrationItem.password.getNewHash().getHash(itemA));
		assertNotNull(NestedHashMigrationItem.password.getNewHash().getHash(itemB));
		assertNotNull(NestedHashMigrationItem.password.getNewHash().getHash(itemX));
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			NestedHashMigrationItem.migratePassword(ctx);
			assertEquals(0, ctx.progress);
		}

		model.startTransaction("test result");
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
	}

	private static class MyJobContext extends AssertionErrorJobContext
	{
		int progress = 0;

		MyJobContext()
		{
			// make package private
		}

		@Override
		public void stopIfRequested()
		{
			// nop
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}
}
