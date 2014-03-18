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

import static com.exedio.cope.pattern.NestedHashMigrationItem.TYPE;
import static com.exedio.cope.pattern.NestedHashMigrationItem.migratePassword;
import static com.exedio.cope.pattern.NestedHashMigrationItem.password;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.util.AssertionErrorJobContext;

public class NestedHashMigrationTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE);

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
			assertEquals("mandatory violation on " + item + " for NestedHashMigrationItem.password", e.getMessage());
		}
		try
		{
			item.set(password.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for NestedHashMigrationItem.password", e.getMessage());
		}
		try
		{
			new NestedHashMigrationItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for NestedHashMigrationItem.password", e.getMessage());
		}

		try
		{
			migratePassword(null);
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
		assertNotNull(password.getLegacyHash().getHash(item));
		assertNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getLegacyHash().getHash(item));

		assertTrue(item.checkPassword("111111"));
		assertFalse(item.checkPassword("222222"));

		item.setPassword("222222");
		assertNull(password.getLegacyHash().getHash(item));
		assertNotNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getTargetHash().getHash(item));
		assertTrue(item.checkPassword("222222"));
		assertFalse(item.checkPassword("333333"));
		assertFalse(item.checkPassword("111111"));
	}

	public void testMigratePasswordAutomatically()
	{
		final NestedHashMigrationItem itemA = deleteOnTearDown(new NestedHashMigrationItem("111111A", 1.1));
		final NestedHashMigrationItem itemB = deleteOnTearDown(new NestedHashMigrationItem("111111B", 1.1));
		final NestedHashMigrationItem itemX = deleteOnTearDown(new NestedHashMigrationItem("111111X"));
		assertNotNull(password.getLegacyHash().getHash(itemA));
		assertNotNull(password.getLegacyHash().getHash(itemB));
		assertNull(password.getLegacyHash().getHash(itemX));
		assertNull(password.getTargetHash().getHash(itemA));
		assertNull(password.getTargetHash().getHash(itemB));
		assertNotNull(password.getTargetHash().getHash(itemX));
		assertEquals(password.getHash(itemA), password.getLegacyHash().getHash(itemA));
		assertEquals(password.getHash(itemB), password.getLegacyHash().getHash(itemB));
		assertEquals(password.getHash(itemX), password.getTargetHash().getHash(itemX));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			migratePassword(ctx);
			assertEquals(2, ctx.stopIfRequested);
			assertEquals(2, ctx.progress);
		}

		model.startTransaction("test result");
		assertNull(password.getLegacyHash().getHash(itemA));
		assertNull(password.getLegacyHash().getHash(itemB));
		assertNull(password.getLegacyHash().getHash(itemX));
		assertNotNull(password.getTargetHash().getHash(itemA));
		assertNotNull(password.getTargetHash().getHash(itemB));
		assertNotNull(password.getTargetHash().getHash(itemX));
		assertEquals(password.getHash(itemA), password.getTargetHash().getHash(itemA));
		assertEquals(password.getHash(itemB), password.getTargetHash().getHash(itemB));
		assertEquals(password.getHash(itemX), password.getTargetHash().getHash(itemX));
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			migratePassword(ctx);
			assertEquals(0, ctx.stopIfRequested);
			assertEquals(0, ctx.progress);
		}

		model.startTransaction("test result");
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
	}

	private static class MyJobContext extends AssertionErrorJobContext
	{
		int stopIfRequested = 0;
		int progress = 0;

		MyJobContext()
		{
			// make package private
		}

		@Override
		public void stopIfRequested()
		{
			stopIfRequested++;
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}
}
