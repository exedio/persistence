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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.misc.DataFieldVaultSummary;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

public class DataFieldVaultSummaryTest
{
	@Test public void testIt()
	{
		final DataFieldVaultInfo i1 = newInfo(11, 21, 31, 51, 61);
		final DataFieldVaultInfo i2 = newInfo(13, 23, 33, 53, 63);

		final DataFieldVaultSummary ms = new DataFieldVaultSummary(new DataFieldVaultInfo[]{i1, i2});
		assertEquals( 24, ms.getGetLengthCount());
		assertEquals( 44, ms.getGetBytesCount());
		assertEquals( 64, ms.getGetStreamCount());
		assertEquals(132, ms.getGetCount());
		assertEquals(104, ms.getPutInitialCount());
		assertEquals(124, ms.getPutRedundantCount());
		assertEquals(228, ms.getPutCount());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test public void testNull()
	{
		try
		{
			new DataFieldVaultSummary(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test public void testNullElement()
	{
		final DataFieldVaultInfo i1 = newInfo(11, 21, 31, 41, 51);
		try
		{
			new DataFieldVaultSummary(new DataFieldVaultInfo[]{i1, null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testEmpty()
	{
		final DataFieldVaultSummary ms = new DataFieldVaultSummary(new DataFieldVaultInfo[]{});
		assertEquals(0, ms.getGetLengthCount());
		assertEquals(0, ms.getGetBytesCount());
		assertEquals(0, ms.getGetStreamCount());
		assertEquals(0, ms.getGetCount());
		assertEquals(0, ms.getPutInitialCount());
		assertEquals(0, ms.getPutRedundantCount());
		assertEquals(0, ms.getPutCount());
	}

	private static DataFieldVaultInfo newInfo(
			final long getLength,
			final long getBytes,
			final long getStream,
			final long putInitial,
			final long putRedundant)
	{
		return new DataFieldVaultInfo(
				null,
				new Service(),
				new AtomicLong(getLength),
				new AtomicLong(getBytes),
				new AtomicLong(getStream),
				new AtomicLong(putInitial),
				new AtomicLong(putRedundant));
	}

	private static final class Service extends AssertionErrorVaultService
	{
		@Override
		public String toString()
		{
			return Service.class.getName();
		}
	}
}
