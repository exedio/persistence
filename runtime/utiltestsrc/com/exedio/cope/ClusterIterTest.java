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

import java.net.DatagramPacket;
import java.util.NoSuchElementException;
import junit.framework.TestCase;
import org.junit.Test;

public class ClusterIterTest extends TestCase
{
	@Test public void testInt()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0xff, (byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}, 1, 4));
		assertTrue(iter.hasNext());
		assertEquals(0x456789ab, iter.nextInt());
		assertFalse(iter.hasNext());
		try
		{
			iter.nextInt();
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals("4", e.getMessage());
		}
	}

	@Test public void testIntNegative()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab}, 4));
		assertTrue(iter.hasNext());
		assertEquals(0xab896745, iter.nextInt());
		assertFalse(iter.hasNext());
	}

	@Test public void testCheckInt()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0xff, (byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45, (byte)0xff}, 1, 4));
		assertTrue(iter.hasNext());
		assertTrue(iter.checkBytes(new byte[]{(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45}));
		assertFalse(iter.hasNext());
		try
		{
			assertTrue(iter.checkBytes(new byte[]{(byte)0xab}));
			fail();
		}
		catch(final NoSuchElementException e)
		{
			assertEquals("4", e.getMessage());
		}
	}

	@Test public void testCheckIntFalse()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45}, 4));
		assertTrue(iter.hasNext());
		assertFalse(iter.checkBytes(new byte[]{(byte)0xab, (byte)0x89, (byte)0x68, (byte)0x45}));
		assertTrue(iter.hasNext());
	}

	@Test public void testCheckIntNegative()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab}, 4));
		assertTrue(iter.hasNext());
		assertTrue(iter.checkBytes(new byte[]{(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab}));
		assertFalse(iter.hasNext());
	}
}
