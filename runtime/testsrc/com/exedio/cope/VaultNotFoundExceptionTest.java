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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.vault.VaultNotFoundException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class VaultNotFoundExceptionTest
{
	@Test void notAnonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("0123456789abcdef");
		assertEquals("0123456789abcdef", e.getHashComplete());
		assertEquals("0123456789abcdef", e.getHashAnonymous());
		assertEquals("hash not found in vault: 0123456789abcdef", e.getMessage());
	}

	@Test void anonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("0123456789abcdef0");
		assertEquals("0123456789abcdef0", e.getHashComplete());
		assertEquals("0123456789abcdefxx17", e.getHashAnonymous());
		assertEquals("hash not found in vault: 0123456789abcdefxx17", e.getMessage());
	}

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test void testAnonymiseHash()
	{
		assertEquals("0123456789abcdefxx17", anonymiseHash("0123456789abcdef0"));
		assertSame("0123456789abcdef", anonymiseHash("0123456789abcdef"));
		assertSame("", anonymiseHash(""));
		assertSame(null, anonymiseHash(null));
	}

	@SuppressWarnings("ThrowableNotThrown")
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	@Test void constructor1HashNull()
	{
		try
		{
			new VaultNotFoundException(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@SuppressWarnings("ThrowableNotThrown")
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	@Test void constructor2HashNull()
	{
		final IOException cause = new IOException();
		try
		{
			new VaultNotFoundException(null, cause);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void constructor2CauseNull()
	{
		final VaultNotFoundException e = new VaultNotFoundException("myHash", null);
		assertEquals("myHash", e.getHashComplete());
	}

	@SuppressWarnings("ThrowableNotThrown")
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	@Test void constructor2BothNull()
	{
		try
		{
			new VaultNotFoundException(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}