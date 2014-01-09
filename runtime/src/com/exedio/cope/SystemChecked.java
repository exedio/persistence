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

package com.exedio.cope;

final class SystemChecked
{
	private static volatile long previousResult = Long.MIN_VALUE;

	/**
	 * Observation:
	 *
	 * SystemNanoSource PROBLEM: 13339811644305134 13339811644305134
	 * SystemNanoSource PROBLEM: 13339811673189452 13339811673189452
	 * ------------- ---------------- ---------------
	 * Testcase: testOverlappingTwice(com.exedio.cope.ItemCacheInvalidateLastPurgeTest):	FAILED
	 * invalidateLastSize expected:<0> but was:<2>
	 * junit.framework.AssertionFailedError: invalidateLastSize expected:<0> but was:<2>
	 *    at com.exedio.cope.ItemCacheInvalidateLastPurgeTest.assertCache(ItemCacheInvalidateLastPurgeTest.java:225)
	 *    at com.exedio.cope.ItemCacheInvalidateLastPurgeTest.testOverlappingTwice(ItemCacheInvalidateLastPurgeTest.java:177)
	 *    at com.exedio.cope.junit.CopeTest.runBare(CopeTest.java:99)
	 */
	static long nanoTime()
	{
		final long result = System.nanoTime();
		final boolean complain = (previousResult>=result);
		previousResult = result;
		if(complain)
			System.out.println("SystemNanoSource PROBLEM: " + previousResult + ' ' + result);
		return result;
	}

	private SystemChecked()
	{
		// prevent instantiation
	}
}
