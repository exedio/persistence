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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import com.exedio.cope.util.Properties;

public class InvalidatorMarshallTest extends TestCase
{
	private ConnectProperties properties;
	private InvalidationConfig ics;
	private InvalidationConfig icl;
	private InvalidationSender is;
	
	private static final int SECRET = 0x88776655;
	private static final int PACKET_SIZE = 40;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ConnectProperties defaultProperties = new ConnectProperties();
		final Properties.Source source = defaultProperties.getSourceObject();
		properties = new ConnectProperties(
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.packetSize"))
							return "43";
						else
							return source.get(key);
					}

					public String getDescription()
					{
						return source.getDescription();
					}

					public Collection<String> keySet()
					{
						return source.keySet();
					}
				},
				null
			);
		ics = new InvalidationConfig(SECRET, 0x11224433, properties);
		icl = new InvalidationConfig(SECRET, 0x11224434, properties);
		is = new InvalidationSender(ics, properties);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		is.close();
		super.tearDown();
	}
	
	public void testSet()
	{
		assertEquals(PACKET_SIZE, ics.packetSize);
		
		final byte[] buf = m(new int[][]{new int[]{0x456789ab, 0xaf896745}, null, new int[]{}, null});
		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11, // magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, // secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // node
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // sequence
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // id 0
					(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xaf, // pk2 (swapped by hash set)
					(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45, // pk1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, // NaPK for end
				(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, // id 2
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // NaPK for end
		
		final byte[] buf2 = m(new int[][]{new int[]{0x456789ac, 0xaf896746}, null, new int[]{}, null});
		assertEqualsBytes(buf2,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11, // magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, // secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // node
				(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, // sequence
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // id 0
					(byte)0x46, (byte)0x67, (byte)0x89, (byte)0xaf, // pk2 (swapped by hash set)
					(byte)0xac, (byte)0x89, (byte)0x67, (byte)0x45, // pk1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, // NaPK for end
				(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, // id 2
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // NaPK for end
		{
			final TIntHashSet[] is = um(buf, 4);
			assertContains(is[0], 0x456789ab, 0xaf896745);
			assertEquals(null, is[1]);
			assertTrue(is[2].isEmpty());
			assertEquals(null, is[3]);
			assertEquals(4, is.length);
		}
		buf[8] = 0x34;
		buf[9] = 0x44;
		buf[10] = 0x22;
		buf[11] = 0x11;
		assertEquals(null, um(buf, 4));
		
		buf[4] = 0x54;
		try
		{
			um(buf, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("wrong secret", e.getMessage());
		}
		buf[0] = 0x11;
		try
		{
			um(buf, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("missing magic", e.getMessage());
		}
	}
	
	public void testSplitBeforeTypeSingle()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5, 6}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 1
					(byte)6,    (byte)0,    (byte)0,    (byte)0);    // 40 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 28 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 1);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(1, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 1);
			assertContains(pks[0], 3);
			assertEquals(1, pks.length);
		}
	}
	
	public void testSplitBeforeType()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5, 6}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 1
					(byte)6,    (byte)0,    (byte)0,    (byte)0);    // 40 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 28 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 32 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 36 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 40 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertContains(pks[0], 3);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAtType()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0);    // 40 pk 3
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 24 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 28 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 5, 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterType()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 40 NaPK for end
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 24 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 28 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterType()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 2
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 36 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0);       // 40 type 1
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 24 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 28 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 2, 3, 1);
			assertTrue(pks[1].isEmpty());
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterAfterType()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2}, new int[]{11, 12}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 2
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 32 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 36 type 1
					(byte)11,    (byte)0,    (byte)0,    (byte)0);   // 40 pk 11
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 type 1
					(byte)12,   (byte)0,    (byte)0,    (byte)0,     // 24 pk 12
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 28 NaPK for end
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 12);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterAfterTypeCollapse()
	{
		assertEquals(40, ics.packetSize);
		
		final byte[][] bufs = mm(new int[][]{new int[]{1, 2}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 16 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 24 pk 2
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 32 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 36 type 1
					(byte)11,    (byte)0,    (byte)0,    (byte)0);   // 40 pk 11
		assertEqualsBytes(bufs[1], // TODO should not be there
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)1,    (byte)0,    (byte)0,    (byte)0);       // 16 sequence
		assertEquals(2, bufs.length);

		{
			final TIntHashSet[] pks = um(bufs[0], 2);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(bufs[1], 2);
			assertEquals(null, pks[0]);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
	}
	
	public void testPing()
	{
		final ArrayList<byte[]> sink = new ArrayList<byte[]>();
		is.testSink = sink;
		is.ping();
		is.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);
		
		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,     // 16 ping
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,       // 20 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,       // 24 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,      // 28 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,      // 32 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,       // 36 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126);     // 40 fillup
		
		assertEquals(
				new Integer(InvalidationConfig.PING_AT_SEQUENCE),
				umi(buf, 0));
		
		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 36", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 39", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 41", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 44", e.getMessage());
			}
		}
		buf[36] = (byte)35;
		try
		{
			um(buf, 0);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid ping, at position 36 expected 56, but was 35", e.getMessage());
		}
		buf[28] = (byte)29;
		try
		{
			um(buf, 0);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid ping, at position 28 expected 98, but was 29", e.getMessage());
		}
	}
	
	public void testPong()
	{
		final ArrayList<byte[]> sink = new ArrayList<byte[]>();
		is.testSink = sink;
		is.pong();
		is.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);
		
		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0xfe, (byte)0xff, (byte)0xff, (byte)0xff,     // 16 pong
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,       // 20 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,       // 24 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,      // 28 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,      // 32 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,       // 36 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126);     // 40 fillup
		
		assertEquals(
				new Integer(InvalidationConfig.PONG_AT_SEQUENCE),
				umi(buf, 0));
		
		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 36", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 39", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 41", e.getMessage());
			}
		}
		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2, 0);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 44", e.getMessage());
			}
		}
		buf[36] = (byte)35;
		try
		{
			um(buf, 0);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid pong, at position 36 expected 56, but was 35", e.getMessage());
		}
		buf[28] = (byte)29;
		try
		{
			um(buf, 0);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid pong, at position 28 expected 98, but was 29", e.getMessage());
		}
	}
	
	
	private static void assertContains(final TIntHashSet actual, final int... expected)
	{
		for(int i : expected)
			assertTrue(actual.contains(i));
		assertEquals(expected.length, actual.size());
	}
	
	private void assertEqualsBytes(final byte[] actualData, final byte... expectedData)
	{
		for(int i = 0; i<actualData.length; i++)
			assertEquals(String.valueOf(i), expectedData[i], actualData[i]);
		assertEquals(expectedData.length, actualData.length);
	}
	
	private static TIntHashSet[] convert(final int[][] invalidationNumbers)
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
		return invalidations;
	}
	
	private byte[] m(final int[][] invalidationNumbers)
	{
		final TIntHashSet[] invalidations = convert(invalidationNumbers);
		final ArrayList<byte[]> sink = new ArrayList<byte[]>();
		is.testSink = sink;
		is.invalidate(invalidations);
		is.testSink = null;
		assertEquals(1, sink.size());
		return sink.get(0);
	}
	
	private byte[][] mm(final int[][] invalidationNumbers)
	{
		final TIntHashSet[] invalidations = convert(invalidationNumbers);
		final ArrayList<byte[]> sink = new ArrayList<byte[]>();
		is.testSink = sink;
		is.invalidate(invalidations);
		is.testSink = null;
		final byte[][] result = new byte[sink.size()][];
		int i = 0;
		for(final byte[] b : sink)
			result[i++] = b;
		return result;
	}
	
	private TIntHashSet[] um(final byte[] buf, final int typeLength)
	{
		return (TIntHashSet[])umx(buf, typeLength);
	}
	
	private Integer umi(final byte[] buf, final int typeLength)
	{
		return (Integer)umx(buf, typeLength);
	}
	
	private Object umx(final byte[] buf, final int typeLength)
	{
		return InvalidationListener.unmarshal(0, buf, buf.length, icl, typeLength);
	}
}
