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
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.misc.DataFieldVaultSummary;
import com.exedio.cope.tojunit.AssertionFailedErrorCounter;
import org.junit.jupiter.api.Test;

public class DataFieldVaultSummaryTest
{
	@Test void testIt()
	{
		final DataFieldVaultInfo i1 = newInfo(21, 31, 51, 61);
		final DataFieldVaultInfo i2 = newInfo(23, 33, 53, 63);

		final DataFieldVaultSummary ms = new DataFieldVaultSummary(new DataFieldVaultInfo[]{i1, i2});
		assertEquals(  0, ms.getGetLengthCount());
		assertEquals( 44, ms.getGetBytesCount());
		assertEquals( 64, ms.getGetStreamCount());
		assertEquals(108, ms.getGetCount());
		assertEquals(104, ms.getPutInitialCount());
		assertEquals(124, ms.getPutRedundantCount());
		assertEquals(228, ms.getPutCount());
	}

	@Test void testNull()
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

	@Test void testNullElement()
	{
		final DataFieldVaultInfo i1 = newInfo(21, 31, 41, 51);
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

	@Test void testEmpty()
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
			final long getBytes,
			final long getStream,
			final long putInitial,
			final long putRedundant)
	{
		return new DataFieldVaultInfo(
				null,
				null,
				new Service(),
				new ConstantCounter(getBytes),
				new ConstantCounter(getStream),
				new ConstantCounter(putInitial),
				new ConstantCounter(putRedundant));
	}

	private static final class Service extends AssertionErrorVaultService
	{
		@Override
		public String toString()
		{
			return Service.class.getName();
		}
	}

	private static final class ConstantCounter extends AssertionFailedErrorCounter
	{
		private final long count;

		ConstantCounter(final long count)
		{
			this.count = count;
		}

		@Override
		public double count()
		{
			return count;
		}
	}
}
