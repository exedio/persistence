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

package com.exedio.cope.vault;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;

import com.exedio.cope.util.JobContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultNonWritableServiceTest
{
	@Test void mustNotBeWritable()
	{
		final VaultServiceParameters params = new VaultServiceParameters(VaultProperties.factory().create(cascade(
				single("default.service", MyService.class))),
				"testServiceKey",
				true,  // writable
				() -> { throw new AssertionFailedError(); }); // markPut
		//noinspection resource OK: does not allocate resources
		assertFails(
				() -> new MyService(params),
				IllegalArgumentException.class,
				CLASS_NAME + " does not support isWritable");
	}

	@Test void isNotWritable()
	{
		final VaultServiceParameters params = new VaultServiceParameters(VaultProperties.factory().create(cascade(
				single("default.service", MyService.class))),
				"testServiceKey",
				false,  // writable
				() -> { throw new AssertionFailedError(); }); // markPut
		final VaultPutInfo info = new AssertionFailedVaultPutInfo()
		{
			@Override public String toString()
			{
				return "myInfo";
			}
		};
		//noinspection resource OK: does not allocate resources
		final MyService s = new MyService(params);
		assertFails(
				() -> s.put("abcdefghijklmnopq", (byte[])null, info),
				IllegalStateException.class,
				"not writable: abcdefghijklmnopxx17 myInfo " + CLASS_NAME);
		assertFails(
				() -> s.put("abcdefghijklmnopq", (InputStream)null, info),
				IllegalStateException.class,
				"not writable: abcdefghijklmnopxx17 myInfo " + CLASS_NAME);
		assertFails(
				() -> s.put("abcdefghijklmnopq", (Path)null, info),
				IllegalStateException.class,
				"not writable: abcdefghijklmnopxx17 myInfo " + CLASS_NAME);
	}

	private static final class MyService extends VaultNonWritableService
	{
		MyService(final VaultServiceParameters parameters)
		{
			super(parameters);
		}
		@Override public void purgeSchema(final JobContext ctx)
		{
			throw new AssertionFailedError();
		}
		@Override public void close()
		{
			throw new AssertionFailedError();
		}
		@Override public long getLength(final String hash)
		{
			throw new AssertionFailedError();
		}
		@Override public byte[] get(final String hash)
		{
			throw new AssertionFailedError();
		}
		@Override public void get(final String hash, final OutputStream sink)
		{
			throw new AssertionFailedError();
		}
	}
	private static final String CLASS_NAME = MyService.class.getName();
}
