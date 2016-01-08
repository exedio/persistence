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

import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.util.Hex;
import com.exedio.cope.util.Properties;
import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class ClusterTest
{
	private ClusterProperties csp;
	private ClusterProperties clp;
	private ClusterSenderMock cs;
	private ClusterListenerMock cl;

	private static final int SECRET = 0x88776655;
	private static final int PACKET_SIZE = 44;

	private static ClusterProperties getProperties(final int node)
	{
		return ClusterProperties.get(
			new ConnectProperties(
				ConnectSource.get(),
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.packetSize"))
							return "47";
						else if(key.equals("cluster.secret"))
							return String.valueOf(SECRET);
						else if(key.equals("cluster.nodeAuto"))
							return "false";
						else if(key.equals("cluster.node"))
							return String.valueOf(node);
						else if(key.equals("cluster.log"))
							return "false";
						else
							return null;
					}

					public String getDescription()
					{
						return "Cluster Properties";
					}

					public Collection<String> keySet()
					{
						return null;
					}
				}
			));
	}

	@Before public final void setUpClusterTest()
	{
		csp = getProperties(0x11224433);
		clp = getProperties(0x11224434);
		cs = new ClusterSenderMock(csp);
		cl = new ClusterListenerMock(clp, 4);
	}

	@After public final void tearDownClusterTest()
	{
		cs.close();
		cl.close();
	}

	@Test public void testSet()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[] buf = m(new int[][]{new int[]{0x456789ab, 0xaf896745}, null, new int[]{}, null});
		assertEqualsBytes(buf,
				"c0be1111" + // magic
				"55667788" + // secret
				"33442211" + // node
				"01001200" + // kind=invalidation
				"00000000" + // sequence
				"00000000" + // id 0
					"456789af" + // pk2 (swapped by hash set)
					"ab896745" + // pk1
					"00000080" + // NaPK for end
				"02000000" + // id 2
					"00000080" ); // NaPK for end
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[] buf2 = m(new int[][]{new int[]{0x456789ac, 0xaf896746}, null, new int[]{}, null});
		assertEqualsBytes(buf2,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11, // magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, // secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00, // kind=invalidation
				(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, // sequence
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // id 0
					(byte)0x46, (byte)0x67, (byte)0x89, (byte)0xaf, // pk2 (swapped by hash set)
					(byte)0xac, (byte)0x89, (byte)0x67, (byte)0x45, // pk1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, // NaPK for end
				(byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00, // id 2
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // NaPK for end
		assertInfo(0, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] is = um(buf);
			assertContains(is[0], 0x456789ab, 0xaf896745);
			assertEquals(null, is[1]);
			assertTrue(is[2].isEmpty());
			assertEquals(null, is[3]);
			assertEquals(4, is.length);
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			// duplicate
 			ume(buf);
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		buf[8] = 0x34;
		buf[9] = 0x44;
		buf[10] = 0x22;
		buf[11] = 0x11;
		ume(buf);
		assertInfo(0, 0, 0, 1, new long[][]{new long[]{0x11224433, 0, 0}});

		buf[4] = 0x54;
		ume(buf);
		assertInfo(0, 0, 1, 1, new long[][]{new long[]{0x11224433, 0, 0}});

		buf[0] = 0x11;
		ume(buf);
		assertInfo(0, 1, 1, 1, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitBeforeTypeSingle()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5, 6}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 40 pk 1
					(byte)6,    (byte)0,    (byte)0,    (byte)0);    // 44 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 32 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

	}

	@Test public void testSplitBeforeType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5, 6}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 40 pk 1
					(byte)6,    (byte)0,    (byte)0,    (byte)0);    // 44 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 32 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 36 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 40 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 44 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitAtType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4, 5}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)5,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 5
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 40 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0);    // 44 pk 3
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 24 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 28 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 32 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3, 4}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)4,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 4
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 40 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 44 NaPK for end
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 24 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 28 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 32 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitAfterAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2, 3}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 1
					(byte)3,    (byte)0,    (byte)0,    (byte)0,     // 36 pk 3
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 40 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0);       // 44 type 1
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 24 type 1
					(byte)11,   (byte)0,    (byte)0,    (byte)0,     // 28 pk 11
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 32 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 3, 1);
			assertContains(pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitAfterAfterAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2}, new int[]{11, 12}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 24 type 0
					(byte)2,    (byte)0,    (byte)0,    (byte)0,     // 28 pk 2
					(byte)1,    (byte)0,    (byte)0,    (byte)0,     // 32 pk 1
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80,  // 36 NaPK for end
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 40 type 1
					(byte)11,    (byte)0,    (byte)0,    (byte)0);   // 44 pk 11
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x12, (byte)0x00,     // 16 kind=invalidation
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)1,    (byte)0,    (byte)0,    (byte)0,        // 24 type 1
					(byte)12,   (byte)0,    (byte)0,    (byte)0,     // 28 pk 12
					(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80); // 32 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TIntHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 12);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testSplitAfterAfterAfterTypeCollapse()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new int[][]{new int[]{1, 2}, new int[]{11}});
		assertEqualsBytes(bufs[0],
				"c0be1111" +     //  4 magic
				"55667788" +     //  8 secret
				"33442211" +     // 12 node
				"01001200" +     // 16 kind=invalidation
				"00000000" +     // 20 sequence
				"00000000" +     // 24 type 0
					"02000000" +  // 28 pk 2
					"01000000" +  // 32 pk 1
					"00000080" +  // 36 NaPK for end
				"01000000" +     // 40 type 1
					"0b000000" ); // 44 pk 11
		assertEquals(1, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TIntHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test public void testPing()
	{
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.ping(1);
		cs.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);

		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x01, (byte)0x00, (byte)0x11, (byte)0x00,     // 16 kind=ping
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,       // 24 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,       // 28 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,      // 32 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,      // 36 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,       // 40 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126);     // 44 fillup

		assertEquals(
				"PONG",
				umi(buf));
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid ping, expected length 44, but was 40", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid ping, expected length 44, but was 43", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid ping, expected length 44, but was 45", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid ping, expected length 44, but was 48", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		buf[36] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid ping, at position 36 expected 47, but was 35", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		buf[28] = (byte)29;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid ping, at position 28 expected 40, but was 29", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
	}

	@Test public void testPingCount()
	{
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.ping(3);
		cs.testSink = null;
		assertEquals(3, sink.size());
		byte count = 0;
		for(final byte[] buf : sink)
		{
			assertEqualsBytes(buf,
					(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
					(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
					(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
					(byte)0x01, (byte)0x00, (byte)0x11, (byte)0x00,     // 16 kind=ping
					count++,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
					(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,       // 24 fillup
					(byte)-84,  (byte)-73,  (byte)23,   (byte)83,       // 28 fillup
					(byte)40,   (byte)-93,  (byte)75,   (byte)-62,      // 32 fillup
					(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,      // 36 fillup
					(byte)47,   (byte)-43,  (byte)103,  (byte)46,       // 40 fillup
					(byte)56,   (byte)-32,  (byte)-117, (byte)126);     // 44 fillup
		}

		sink.clear();
		cs.testSink = sink;
		try
		{
			cs.ping(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("count must be greater than zero, but was 0", e.getMessage());
		}
		cs.testSink = null;
		assertEquals(0, sink.size());
	}

	@Test public void testPong()
	{
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.pong();
		cs.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);

		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0x02, (byte)0x00, (byte)0x11, (byte)0x00,     // 16 kind=pong
				(byte)0,    (byte)0,    (byte)0,    (byte)0,        // 20 sequence
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,       // 24 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,       // 28 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,      // 32 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,      // 36 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,       // 40 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126);     // 44 fillup

		ume(buf);
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		{
			final byte[] buf2 = new byte[buf.length-4];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid pong, expected length 44, but was 40", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		{
			final byte[] buf2 = new byte[buf.length-1];
			System.arraycopy(buf, 0, buf2, 0, buf2.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid pong, expected length 44, but was 43", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		{
			final byte[] buf2 = new byte[buf.length+1];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid pong, expected length 44, but was 45", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		{
			final byte[] buf2 = new byte[buf.length+4];
			System.arraycopy(buf, 0, buf2, 0, buf.length);
			try
			{
				um(buf2);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("invalid pong, expected length 44, but was 48", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		buf[36] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid pong, at position 36 expected 47, but was 35", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		buf[28] = (byte)29;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid pong, at position 28 expected 40, but was 29", e.getMessage());
		}
	}


	private static void assertContains(final TIntHashSet actual, final int... expected)
	{
		for(final int i : expected)
			assertTrue(actual.contains(i));
		assertEquals(expected.length, actual.size());
	}

	private static void assertEqualsBytes(final byte[] actualData, final byte... expectedData)
	{
		for(int i = 0; i<actualData.length; i++)
			assertEquals(String.valueOf(i), expectedData[i], actualData[i]);
		assertEquals(expectedData.length, actualData.length);
	}

	private static void assertEqualsBytes(final byte[] actualData, final String expectedData)
	{
		assertEquals(expectedData, Hex.encodeLower(actualData));
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
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.invalidate(invalidations);
		cs.testSink = null;
		assertEquals(1, sink.size());
		return sink.get(0);
	}

	private byte[][] mm(final int[][] invalidationNumbers)
	{
		final TIntHashSet[] invalidations = convert(invalidationNumbers);
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.invalidate(invalidations);
		cs.testSink = null;
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

	private String umi(final byte[] buf)
	{
		return (String)umx(buf);
	}

	private Object umx(final byte[] buf)
	{
		final ArrayList<Object> sink = new ArrayList<>();
		cl.testSink = sink;
		cl.handle(toPacket(buf));
		cl.testSink = null;
		assertEquals(1, sink.size());
		return sink.get(0);
	}

	private void ume(final byte[] buf)
	{
		final ArrayList<Object> sink = new ArrayList<>();
		cl.testSink = sink;
		cl.handle(toPacket(buf));
		cl.testSink = null;
		assertEquals(list(), sink);
	}

	protected abstract DatagramPacket toPacket(final byte[] buf);

	private void assertInfo(
			final long invalidationSplit,
			final long listenerMissingMagic,
			final long listenerWrongSecret,
			final long listenerFromMyself,
			final long[][] listenerNodes)
	{
		final ClusterSenderInfo senderInfo = cs.getInfo();
		assertEquals(123456, senderInfo.getLocalPort());
		assertEquals(123457, senderInfo.getSendBufferSize());
		assertEquals(123458, senderInfo.getTrafficClass());
		assertEquals(invalidationSplit, senderInfo.getInvalidationSplit());

		final ClusterListenerInfo listenerInfo = cl.getInfo();
		assertEquals(234567, listenerInfo.getReceiveBufferSize());
		assertEquals(0, listenerInfo.getException());
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
					assertNotNull(infoNode.getFirstEncounter());
					assertEquals(null, infoNode.getAddress());
					assertEquals(967, infoNode.getPort());
					assertEquals("ping", node[1], infoNode.getPingInfo().getInOrder());
					assertEquals("pong", node[2], infoNode.getPongInfo().getInOrder());

					assertEquals(0, infoNode.getPingInfo().getOutOfOrder());
					assertEquals(0, infoNode.getPongInfo().getOutOfOrder());
					assertEquals(0, infoNode.getPingInfo().getDuplicate());
					assertEquals(0, infoNode.getPongInfo().getDuplicate());

					assertEquals(0, infoNode.getPingInfo().getLost());
					assertEquals(0, infoNode.getPongInfo().getLost());
					assertEquals(0, infoNode.getPingInfo().getLate());
					assertEquals(0, infoNode.getPongInfo().getLate());
					break nodes;
				}
			}
			fail("node not found: " + Long.toHexString(id));
		}
		assertEquals(listenerNodes.length, listenerInfoNodes.size());
	}

	static final String toString(final TIntHashSet[] invalidations)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('[');
		boolean first = true;
		for(final TIntHashSet invalidation : invalidations)
		{
			if(first)
				first = false;
			else
				bf.append(", ");

			if(invalidation!=null)
			{
				bf.append('{');
				boolean first2 = true;
				for(final TIntIterator i = invalidation.iterator(); i.hasNext(); )
				{
					if(first2)
						first2 = false;
					else
						bf.append(',');

					bf.append(i.next());
				}
				bf.append('}');
			}
			else
				bf.append("null");
		}
		bf.append(']');

		return bf.toString();
	}
}
