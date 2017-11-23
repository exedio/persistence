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

import static com.exedio.cope.pattern.DrivebyHashMigrationItem.TYPE;
import static com.exedio.cope.pattern.DrivebyHashMigrationItem.password;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class DrivebyHashMigrationTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DrivebyHashMigrationTest()
	{
		super(MODEL);
	}

	@SuppressFBWarnings({"NP_NONNULL_PARAM_VIOLATION", "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"})
	@Test void testSetNull()
	{
		final DrivebyHashMigrationItem item = new DrivebyHashMigrationItem("111111", 1.1);

		try
		{
			item.setPassword(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for DrivebyHashMigrationItem.password", e.getMessage());
		}
		try
		{
			item.set(password.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + item + " for DrivebyHashMigrationItem.password", e.getMessage());
		}
		try
		{
			new DrivebyHashMigrationItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for DrivebyHashMigrationItem.password", e.getMessage());
		}
	}

	@Test void testMigratePasswordOnChange()
	{
		final DrivebyHashMigrationItem item = new DrivebyHashMigrationItem("111111", 1.1);
		assertLegacy(item);

		item.setPassword("222222");
		assertTarget(item);
		assertTrue(item.checkPassword("222222"));
		assertFalse(item.checkPassword("333333"));
		assertFalse(item.checkPassword("111111"));
	}

	@Test void testMigratePasswordOnCheck()
	{
		final DrivebyHashMigrationItem item = new DrivebyHashMigrationItem("111111A", 1.1);
		assertLegacy(item);

		assertFalse(item.checkPassword("111111Ax"));
		assertLegacy(item);

		assertTrue(item.checkPassword("111111A"));
		assertTarget(item);

		assertTrue(item.checkPassword("111111A"));
		assertTarget(item);

		assertFalse(item.checkPassword("111111Ax"));
		assertTarget(item);
	}

	private static void assertLegacy(final DrivebyHashMigrationItem item)
	{
		assertNotNull(password.getLegacyHash().getHash(item));
		assertNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getLegacyHash().getHash(item));
	}

	private static void assertTarget(final DrivebyHashMigrationItem item)
	{
		assertNull(password.getLegacyHash().getHash(item));
		assertNotNull(password.getTargetHash().getHash(item));
		assertEquals(password.getHash(item), password.getTargetHash().getHash(item));
	}
}
