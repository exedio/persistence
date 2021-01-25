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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.HashTest.newRandomPassword;
import static com.exedio.cope.pattern.NestedHashMigrationItem.TYPE;
import static com.exedio.cope.pattern.NestedHashMigrationItem.migratePassword;
import static com.exedio.cope.pattern.NestedHashMigrationItem.password;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.security.SecureRandom;
import java.time.Duration;
import org.junit.jupiter.api.Test;

public class NestedHashMigrationTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public NestedHashMigrationTest()
	{
		super(MODEL);
	}

	@Test void testSetNull()
	{
		final NestedHashMigrationItem item = new NestedHashMigrationItem("111111", 1.1);

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

		assertEquals(false, password.isNull(null));
		//noinspection SerializableInnerClassWithNonSerializableOuterClass
		assertEquals("21i3v9", newRandomPassword(password, new SecureRandom(){
			private static final long serialVersionUID = 1l;
			@Override public long nextLong() { return 123456789l; }}));
	}

	@Test void testMigratePasswordOnChange()
	{
		final NestedHashMigrationItem item = new NestedHashMigrationItem("111111", 1.1);
		assertLegacy(item);

		assertTrue(item.checkPassword("111111"));
		assertLegacy(item);

		assertFalse(item.checkPassword("222222"));
		assertLegacy(item);

		item.setPassword("222222");
		assertTarget(item);
		assertTrue(item.checkPassword("222222"));
		assertFalse(item.checkPassword("333333"));
		assertFalse(item.checkPassword("111111"));
	}

	@Test void testMigratePasswordAutomatically()
	{
		final NestedHashMigrationItem itemA = new NestedHashMigrationItem("111111A", 1.1);
		final NestedHashMigrationItem itemB = new NestedHashMigrationItem("111111B", 1.1);
		final NestedHashMigrationItem itemX = new NestedHashMigrationItem("111111X");
		assertLegacy(itemA);
		assertLegacy(itemB);
		assertTarget(itemX);
		model.commit();

		{
			final MyJobContext ctx = new MyJobContext();
			migratePassword(ctx);
			assertEquals(2, ctx.stopIfRequested);
			assertEquals(2, ctx.progress);
		}

		model.startTransaction("test result");
		assertTarget(itemA);
		assertTarget(itemB);
		assertTarget(itemX);
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
		assertTarget(itemA);
		assertTarget(itemB);
		assertTarget(itemX);
		assertTrue(itemA.checkPassword("111111A"));
		assertTrue(itemB.checkPassword("111111B"));
		assertTrue(itemX.checkPassword("111111X"));
	}

	private static void assertLegacy(final NestedHashMigrationItem item)
	{
		assertNotNull(password.getLegacyHash().getHash(item));
		assertNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getLegacyHash().getHash(item));
	}

	private static void assertTarget(final NestedHashMigrationItem item)
	{
		assertNull(password.getLegacyHash().getHash(item));
		assertNotNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getTargetHash().getHash(item));
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
			assertFalse(MODEL.hasCurrentTransaction());
			stopIfRequested++;
		}

		@Override
		public Duration requestsDeferral()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			return Duration.ZERO;
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}
}
