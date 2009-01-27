/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;

import java.util.Arrays;

import junit.framework.TestCase;

public class InvalidatorMarshallTest extends TestCase
{
	private static final byte FILL = (byte)0xee;
	private byte[] buf;
	
	@Override
	protected void setUp()
	{
		buf = new byte[100];
		Arrays.fill(buf, (byte)0xee);
	}
	
	@Override
	protected void tearDown()
	{
		buf = null;
	}
	
	
	public void testInt()
	{
		assertEquals(7, m(3, 0x456789ab));
		assertBuf(FILL, FILL, FILL, (byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45);
		assertEquals(Integer.toHexString(um(3)), 0x456789ab, um(3));
	}
	
	public void testIntNegative()
	{
		assertEquals(7, m(3, 0xab896745));
		assertBuf(FILL, FILL, FILL, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab);
		assertEquals(0xab896745, um(3));
	}
	
	public void testSet()
	{
		buf = m(0x88776655, 0x11224433, new int[][]{new int[]{0x456789ab, 0xaf896745}, null, new int[]{}, null});
		assertEqualsBytes(
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11, // magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, // secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // cluster id
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // id 0
					(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, // length
					(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xaf, // pk2 (swapped by hash set)
					(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45, // pk1
				(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, // id 2
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00); // length
		
		{
			final TIntHashSet[] is = um(0, 32, 0x88776655, 0x11224434, 4);
			
			assertTrue(is[0].contains(0x456789ab));
			assertTrue(is[0].contains(0xaf896745));
			assertEquals(2, is[0].size());
			
			assertEquals(null, is[1]);
			assertEquals(null, is[2]);
			assertEquals(null, is[3]);
			assertEquals(4, is.length);
		}
		assertEquals(null, um(0, 32, 0x88776655, 0x11224433, 4));
		
		try
		{
			um(0, 32, 0x88776654, 0x11224433, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("wrong secret", e.getMessage());
		}
		buf[0] = 0x11;
		try
		{
			um(0, 32, 0x88776654, 0x11224433, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("missing magic", e.getMessage());
		}
	}
	
	
	private void assertBuf(final byte... expectedData)
	{
		int i = 0;
		for(; i<expectedData.length; i++)
			assertEquals(String.valueOf(i), expectedData[i], buf[i]);
		for(; i<buf.length; i++)
			assertEquals(String.valueOf(i), FILL, buf[i]);
	}
	
	private void assertEqualsBytes(final byte... expectedData)
	{
		for(int i = 0; i<buf.length; i++)
			assertEquals(String.valueOf(i), expectedData[i], buf[i]);
		assertEquals(expectedData.length, buf.length);
	}
	
	private int m(final int pos, final int i)
	{
		return InvalidationSender.marshal(pos, buf, i);
	}
	
	private int um(final int pos)
	{
		return InvalidationListener.unmarshal(pos, buf);
	}
	
	private byte[] m(final int secret, final int id, final int[][] invalidationNumbers)
	{
		final TIntHashSet[] invalidations = new TIntHashSet[invalidationNumbers.length];
		for(int i = 0; i<invalidationNumbers.length; i++)
		{
			final int[] invalidationNumber = invalidationNumbers[i];
			if(invalidationNumber!=null)
			{
				invalidations[i] = new TIntHashSet();
				for(final int b : invalidationNumber)
					invalidations[i].add(b);
			}
		}
		
		return InvalidationSender.marshal(secret, id, invalidations);
	}
	
	private TIntHashSet[] um(final int pos, final int length, final int secret, final int id, final int typeLength)
	{
		return InvalidationListener.unmarshal(pos, buf, length, secret, id, typeLength);
	}
}
