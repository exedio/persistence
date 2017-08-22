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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.vault.VaultNotFoundException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.junit.Test;

public class VaultNotFoundExceptionTest
{
	@Test public void notAnonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("0123456789abcdef");
		assertEquals("0123456789abcdef", e.getHash());
		assertEquals("hash not found in vault: 0123456789abcdef", e.getMessage());
	}

	@Test public void anonymous()
	{
		final VaultNotFoundException e = new VaultNotFoundException("0123456789abcdef0");
		assertEquals("0123456789abcdef0", e.getHash());
		assertEquals("hash not found in vault: 0123456789abcdef0", e.getMessage());
	}

	@SuppressWarnings("ThrowableNotThrown")
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	@Test public void constructor1HashNull()
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
	@Test public void constructor2HashNull()
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

	@Test public void constructor2CauseNull()
	{
		final VaultNotFoundException e = new VaultNotFoundException("myHash", null);
		assertEquals("myHash", e.getHash());
	}

	@SuppressWarnings("ThrowableNotThrown")
	@SuppressFBWarnings("RV_EXCEPTION_NOT_THROWN")
	@Test public void constructor2BothNull()
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
