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

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.ClusterListenerInfo;
import com.exedio.cope.util.Properties;

public class InvalidatorMarshallTest extends CopeAssert
{
	private ConnectProperties properties;
	private ClusterConfig ics;
	private ClusterConfig icl;
	private InvalidationSender is;
	private ClusterListener il;
	
	private static final int SECRET = 0x88776655;
	private static final int PACKET_SIZE = 40;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ConnectProperties defaultProperties = new ConnectProperties();
		final Properties.Source source = defaultProperties.getSourceObject();
		Properties.Source context = null;
		try
		{
			context = defaultProperties.getContext();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no context available", e.getMessage());
		}
		properties = new ConnectProperties(
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.packetSize"))
							return "43";
						else if(key.equals("cluster.log"))
							return "false";
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
				context
			);
		ics = new ClusterConfig(SECRET, 0x11224433, properties);
		icl = new ClusterConfig(SECRET, 0x11224434, properties);
		is = new InvalidationSender(ics, properties);
		il = new ClusterListener(icl, properties, is, 4, null, null);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		is.close();
		il.close();
		super.tearDown();
	}
	
	public void testSet()
	{
		assertEquals(PACKET_SIZE, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] is = um(buf);
			assertContains(is[0], 0x456789ab, 0xaf896745);
			assertEquals(null, is[1]);
			assertTrue(is[2].isEmpty());
			assertEquals(null, is[3]);
			assertEquals(4, is.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		buf[8] = 0x34;
		buf[9] = 0x44;
		buf[10] = 0x22;
		buf[11] = 0x11;
		ume(buf);
		assertStats(0, 0, 1, new long[0][]);
		
		buf[4] = 0x54;
		ume(buf);
		assertStats(0, 1, 1, new long[0][]);

		buf[0] = 0x11;
		ume(buf);
		assertStats(1, 1, 1, new long[0][]);
	}
	
	public void testSplitBeforeTypeSingle()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
	}
	
	public void testSplitBeforeType()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
	}
	
	public void testSplitAtType()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
	}
	
	public void testSplitAfterType()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
	}
	
	public void testSplitAfterAfterType()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 3, 1);
			assertContains(pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 12);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
	}
	
	public void testSplitAfterAfterAfterTypeCollapse()
	{
		assertEquals(40, ics.packetSize);
		assertStats(0, 0, 0, new long[0][]);
		
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
		assertStats(0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
		
		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertStats(0, 0, 0, new long[0][]);
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
				new Integer(ClusterConfig.PING_AT_SEQUENCE),
				umi(buf));
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 36", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 39", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 41", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid ping, expected length 40, but was 44", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		buf[36] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid ping, at position 36 expected 56, but was 35", e.getMessage());
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
		
		buf[28] = (byte)29;
		try
		{
			um(buf);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid ping, at position 28 expected 98, but was 29", e.getMessage());
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
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
				new Integer(ClusterConfig.PONG_AT_SEQUENCE),
				umi(buf));
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 36", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 39", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 41", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("invalid pong, expected length 40, but was 44", e.getMessage());
			}
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		buf[36] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("invalid pong, at position 36 expected 56, but was 35", e.getMessage());
		}
		assertStats(0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});
		
		buf[28] = (byte)29;
		try
		{
			um(buf);
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
	
	private TIntHashSet[] um(final byte[] buf)
	{
		return (TIntHashSet[])umx(buf);
	}
	
	private Integer umi(final byte[] buf)
	{
		return (Integer)umx(buf);
	}
	
	private Object umx(final byte[] buf)
	{
		final ArrayList<Object> sink = new ArrayList<Object>();
		il.testSink = sink;
		il.handle(new DatagramPacket(buf, buf.length));
		il.testSink = null;
		assertEquals(1, sink.size());
		return sink.get(0);
	}
	
	private void ume(final byte[] buf)
	{
		final ArrayList<Object> sink = new ArrayList<Object>();
		il.testSink = sink;
		il.handle(new DatagramPacket(buf, buf.length));
		il.testSink = null;
		assertEquals(list(), sink);
	}
	
	private void assertStats(
			final long listenerMissingMagic,
			final long listenerWrongSecret,
			final long listenerFromMyself,
			final long[][] listenerNodes)
	{
		final ClusterListenerInfo listenerInfo = il.getInfo();
		assertEquals(listenerMissingMagic, listenerInfo.getMissingMagic());
		assertEquals(listenerWrongSecret, listenerInfo.getWrongSecret());
		assertEquals(listenerFromMyself, listenerInfo.getFromMyself());
		final List<ClusterListenerInfo.Node> listenerInfoNodes = listenerInfo.getNodes();
		assertUnmodifiable(listenerInfoNodes);
		nodes: for(final long[] node : listenerNodes)
		{
			final long id = node[0];
			assertTrue(String.valueOf(id), id>=Integer.MIN_VALUE);
			assertTrue(String.valueOf(id), id<=Integer.MAX_VALUE);
			for(final ClusterListenerInfo.Node infoNode : listenerInfoNodes)
			{
				if(infoNode.getID()==id)
				{
					assertEquals("ping", node[1], infoNode.getPing());
					assertEquals("pong", node[2], infoNode.getPong());
					break nodes;
				}
			}
			fail("node not found: " + Long.toHexString(id));
		}
		assertEquals(listenerNodes.length, listenerInfoNodes.size());
	}
}
