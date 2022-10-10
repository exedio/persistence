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
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.util.Hex;
import gnu.trove.TLongHashSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class ClusterTest
{
	private ClusterProperties csp;
	@SuppressWarnings("FieldCanBeLocal")
	private ClusterProperties clp;
	private ClusterSenderMock cs;
	private ClusterListenerMock cl;

	private static final int SECRET = 0x88776655;
	private static final int PACKET_SIZE = 64;

	private static ClusterProperties getProperties(final int node)
	{
		return
			ClusterProperties.factory().create(cascade(
				single("packetSize", 67),
				single("secret", SECRET),
				single("nodeAuto", false),
				single("node", node)
			));
	}

	private double invalidationSplitBefore;
	private double missingMagicBefore;
	private double wrongSecretBefore;
	private double fromMyselfBefore;
	private double pingInOrderBefore;
	private double pongInOrderBefore;

	@BeforeEach final void setUpClusterTest()
	{
		csp = getProperties(0x11224433);
		clp = getProperties(0x11224434);
		cs = new ClusterSenderMock(csp);
		cl = new ClusterListenerMock(clp, 4);
		invalidationSplitBefore = count("invalidationSplit");
		missingMagicBefore = count("missingMagic");
		wrongSecretBefore = count("wrongSecret");
		fromMyselfBefore = count("fromMyself");
		pingInOrderBefore = countSequenceBefore("ping", "inOrder");
		pongInOrderBefore = countSequenceBefore("pong", "inOrder");
	}

	@AfterEach final void tearDownClusterTest()
	{
		cl.close();
	}

	@Test void testSet()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[] buf = m(new long[][]{new long[]{0x456789abcb320021L, 0xaf896745ff735907L}, null, new long[]{}, null});
		assertEqualsBytes(buf,
				"c0be1112" + // magic
				"55667788" + // secret
				"33442211" + // node
				"01001200" + // kind=invalidation
				"00000000" + // sequence
				"00000000" + // id 0
					"210032cbab896745" + // pk1
					"075973ff456789af" + // pk2
					"0000000000000080" + // NaPK for end
				"02000000" + // id 2
					"0000000000000080" ); // NaPK for end
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[] buf2 = m(new long[][]{new long[]{0x456789ac34582998L, 0xaf896746aaab2341L}, null, new long[]{}, null});
		assertEqualsBytes(buf2,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, // magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, // secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // node
				(byte)0x01, b0,         (byte)0x12, b0,         // kind=invalidation
				(byte)1,    b0,         b0,         b0,         // sequence
				(byte)0,    b0,         b0,         b0,         // id 0
					(byte)0x41, (byte)0x23, (byte)0xab, (byte)0xaa, (byte)0x46, (byte)0x67, (byte)0x89, (byte)0xaf, // pk2 (swapped by hash set)
					(byte)0x98, (byte)0x29, (byte)0x58, (byte)0x34, (byte)0xac, (byte)0x89, (byte)0x67, (byte)0x45, // pk1
					b0,         b0,         b0,         b0,         b0,         b0,         b0,         bNaPK,      // NaPK for end
				(byte)2,    b0,         b0,         b0,         // id 2
					b0,         b0,         b0,         b0,         b0,         b0,         b0,         bNaPK);     // NaPK for end
		assertInfo(0, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] is = um(buf);
			assertContains(is[0], 0x456789abcb320021L, 0xaf896745ff735907L);
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

	@Test void testSplitBeforeTypeSingle()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2, 3, 4, 5, 6}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)5, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 5
					(byte)2, b0,b0,b0,   b0,b0,b0,   b0,         // 40 pk 2
					(byte)4, b0,b0,b0,   b0,b0,b0,   b0,         // 48 pk 4
					(byte)1, b0,b0,b0,   b0,b0,b0,   b0,         // 56 pk 1
					(byte)6, b0,b0,b0,   b0,b0,b0,   b0);        // 64 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)3, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 3
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 40 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

	}

	@Test void testSplitBeforeType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2, 3, 4, 5, 6}, new long[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)5, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 5
					(byte)2, b0,b0,b0,   b0,b0,b0,   b0,         // 40 pk 2
					(byte)4, b0,b0,b0,   b0,b0,b0,   b0,         // 48 pk 4
					(byte)1, b0,b0,b0,   b0,b0,b0,   b0,         // 56 pk 1
					(byte)6, b0,b0,b0,   b0,b0,b0,   b0);        // 64 pk 6
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)3, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 3
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK,      // 40 NaPK for end
				(byte)1,    b0,         b0,         b0,         // 44 type 1
					(byte)11,b0,b0,b0,   b0,b0,b0,   b0,         // 52 pk 11
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 60 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertContains(pks[0], 3);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testSplitAtType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2, 3, 4, 5}, new long[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)5, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 5
					(byte)2, b0,b0,b0,   b0,b0,b0,   b0,         // 40 pk 2
					(byte)4, b0,b0,b0,   b0,b0,b0,   b0,         // 48 pk 4
					(byte)1, b0,b0,b0,   b0,b0,b0,   b0,         // 56 pk 1
					(byte)3, b0,b0,b0,   b0,b0,b0,   b0);        // 64 pk 3
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)1,    b0,         b0,         b0,         // 24 type 1
					(byte)11,b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 11
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 40 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 5, 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testSplitAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2, 3, 4}, new long[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)2, b0,b0,b0,    b0,b0,b0,  b0,         // 32 pk 2
					(byte)4, b0,b0,b0,    b0,b0,b0,  b0,         // 40 pk 4
					(byte)1, b0,b0,b0,    b0,b0,b0,  b0,         // 48 pk 1
					(byte)3, b0,b0,b0,    b0,b0,b0,  b0,         // 56 pk 3
					b0,      b0,b0,b0,    b0,b0,b0,  bNaPK);     // 64 NaPK for end
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)1,    b0,         b0,         b0,         // 24 type 1
					(byte)11,b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 11
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 40 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testSplitAfterAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2, 3}, new long[]{11}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)2, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 2
					(byte)1, b0,b0,b0,   b0,b0,b0,   b0,         // 40 pk 1
					(byte)3, b0,b0,b0,   b0,b0,b0,   b0,         // 48 pk 3
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK,      // 56 NaPK for end
				(byte)1,    b0,         b0,         b0);        // 60 type 1
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)1,    b0,         b0,         b0,         // 24 type 1
					(byte)11,b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 11
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 40 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 3, 1);
			assertContains(pks[1]);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testSplitAfterAfterAfterType()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2}, new long[]{11, 12}});
		assertEqualsBytes(bufs[0],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0,    b0,         b0,         b0,         // 24 type 0
					(byte)2, b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 2
					(byte)1, b0,b0,b0,   b0,b0,b0,   b0,         // 40 pk 1
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK,      // 48 NaPK for end
				(byte)1,    b0,         b0,         b0,         // 52 type 1
					(byte)11,b0,b0,b0,   b0,b0,b0,   b0);        // 60 pk 11
		assertEqualsBytes(bufs[1],
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x12, b0,         // 16 kind=invalidation
				(byte)1,    b0,         b0,         b0,         // 20 sequence
				(byte)1,    b0,         b0,         b0,         // 24 type 1
					(byte)12,b0,b0,b0,   b0,b0,b0,   b0,         // 32 pk 12
					b0,      b0,b0,b0,   b0,b0,b0,   bNaPK);     // 40 NaPK for end
		assertEquals(2, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});

		{
			final TLongHashSet[] pks = um(bufs[1]);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 12);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testSplitAfterAfterAfterTypeCollapse()
	{
		assertEquals(PACKET_SIZE, csp.packetSize);
		assertInfo(0, 0, 0, 0, new long[0][]);

		final byte[][] bufs = mm(new long[][]{new long[]{1, 2}, new long[]{11}});
		assertEqualsBytes(bufs[0],
				"c0be1112" +     //  4 magic
				"55667788" +     //  8 secret
				"33442211" +     // 12 node
				"01001200" +     // 16 kind=invalidation
				"00000000" +     // 20 sequence
				"00000000" +     // 24 type 0
					"0200000000000000" +  // 32 pk 2
					"0100000000000000" +  // 40 pk 1
					"0000000000000080" +  // 48 NaPK for end
				"01000000" +     // 52 type 1
					"0b00000000000000" ); // 60 pk 11
		assertEquals(1, bufs.length);
		assertInfo(1, 0, 0, 0, new long[0][]);

		{
			final TLongHashSet[] pks = um(bufs[0]);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(null, pks[2]);
			assertEquals(null, pks[3]);
			assertEquals(4, pks.length);
		}
		assertInfo(1, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 0}});
	}

	@Test void testPing()
	{
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.ping(1);
		cs.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);

		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x01, b0,         (byte)0x11, b0,         // 16 kind=ping
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0x99, (byte)0x88, (byte)0x77, (byte)0x66, // 24 pingNanos
				(byte)0x55, (byte)0x44, (byte)0x33, (byte)0x22, // 28 pingNanos
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 32 pingNode
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,   // 36 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,   // 40 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,  // 44 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,  // 48 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,   // 52 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126,  // 56 fillup
				(byte)12,   (byte)-64,  (byte)-63,  (byte)68,   // 60 fillup
				(byte)99,   (byte)-45,  (byte)-99,  (byte)-6);  // 64 fillup

		assertEquals(
				"PONG(2233445566778899,11224433)",
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
				assertEquals("invalid ping, expected length 64, but was 60", e.getMessage());
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
				assertEquals("invalid ping, expected length 64, but was 63", e.getMessage());
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
				assertEquals("invalid ping, expected length 64, but was 65", e.getMessage());
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
				assertEquals("invalid ping, expected length 64, but was 68", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		buf[40] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid ping, at position 40 expected 40, but was 35", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});

		buf[32] = (byte)29;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid ping, at position 32 expected 89, but was 29", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 1, 0}});
	}

	@Test void testPingCount()
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
					(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
					(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
					(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
					(byte)0x01, b0,         (byte)0x11, b0,         // 16 kind=ping
					count++,    b0,         b0,         b0,         // 20 sequence
					(byte)0x99, (byte)0x88, (byte)0x77, (byte)0x66, // 24 pingNanos TODO should increment with count
					(byte)0x55, (byte)0x44, (byte)0x33, (byte)0x22, // 28 pingNanos
					(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 32 pingNode
					(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,   // 36 fillup
					(byte)-84,  (byte)-73,  (byte)23,   (byte)83,   // 40 fillup
					(byte)40,   (byte)-93,  (byte)75,   (byte)-62,  // 44 fillup
					(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,  // 48 fillup
					(byte)47,   (byte)-43,  (byte)103,  (byte)46,   // 52 fillup
					(byte)56,   (byte)-32,  (byte)-117, (byte)126,  // 56 fillup
					(byte)12,   (byte)-64,  (byte)-63,  (byte)68,   // 60 fillup
					(byte)99,   (byte)-45,  (byte)-99,  (byte)-6);  // 64 fillup
		}
	}

	@Test void testPong()
	{
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.pong(0xaa334455667788bbl, 0x88aa99cc);
		cs.testSink = null;
		assertEquals(1, sink.size());
		final byte[] buf = sink.get(0);

		assertEqualsBytes(buf,
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x12, //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88, //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11, // 12 node
				(byte)0x02, b0,         (byte)0x11, b0,         // 16 kind=pong
				(byte)0,    b0,         b0,         b0,         // 20 sequence
				(byte)0xbb, (byte)0x88, (byte)0x77, (byte)0x66, // 24 pingNanos
				(byte)0x55, (byte)0x44, (byte)0x33, (byte)0xaa, // 28 pingNanos
				(byte)0xcc, (byte)0x99, (byte)0xaa, (byte)0x88, // 32 pingNode
				(byte)89,   (byte)-95,  (byte)-8,   (byte)-6,   // 36 fillup
				(byte)-84,  (byte)-73,  (byte)23,   (byte)83,   // 40 fillup
				(byte)40,   (byte)-93,  (byte)75,   (byte)-62,  // 44 fillup
				(byte)98,   (byte)-74,  (byte)-68,  (byte)-97,  // 48 fillup
				(byte)47,   (byte)-43,  (byte)103,  (byte)46,   // 52 fillup
				(byte)56,   (byte)-32,  (byte)-117, (byte)126,  // 56 fillup
				(byte)12,   (byte)-64,  (byte)-63,  (byte)68,   // 60 fillup
				(byte)99,   (byte)-45,  (byte)-99,  (byte)-6);  // 64 fillup

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
				assertEquals("invalid pong, expected length 64, but was 60", e.getMessage());
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
				assertEquals("invalid pong, expected length 64, but was 63", e.getMessage());
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
				assertEquals("invalid pong, expected length 64, but was 65", e.getMessage());
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
				assertEquals("invalid pong, expected length 64, but was 68", e.getMessage());
			}
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		buf[40] = (byte)35;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid pong, at position 40 expected 40, but was 35", e.getMessage());
		}
		assertInfo(0, 0, 0, 0, new long[][]{new long[]{0x11224433, 0, 1}});

		buf[32] = (byte)29;
		try
		{
			um(buf);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("invalid pong, at position 32 expected 89, but was 29", e.getMessage());
		}
	}


	private static void assertContains(final TLongHashSet actual, final long... expected)
	{
		for(final long i : expected)
			assertTrue(actual.contains(i));
		assertEquals(expected.length, actual.size());
	}

	private static void assertEqualsBytes(final byte[] actualData, final byte... expectedData)
	{
		for(int i = 0; i<actualData.length; i++)
			assertEquals(expectedData[i], actualData[i], String.valueOf(i));
		assertEquals(expectedData.length, actualData.length);
	}

	private static void assertEqualsBytes(final byte[] actualData, final String expectedData)
	{
		assertEquals(expectedData, Hex.encodeLower(actualData));
	}

	private static TLongHashSet[] convert(final long[][] invalidationNumbers)
	{
		final TLongHashSet[] invalidations = new TLongHashSet[invalidationNumbers.length];
		for(int i = 0; i<invalidationNumbers.length; i++)
		{
			final long[] invalidationNumber = invalidationNumbers[i];
			if(invalidationNumber!=null)
			{
				invalidations[i] = new TLongHashSet();
				for(final long b : invalidationNumber)
					invalidations[i].add(b);
			}
		}
		return invalidations;
	}

	private byte[] m(final long[][] invalidationNumbers)
	{
		final TLongHashSet[] invalidations = convert(invalidationNumbers);
		final ArrayList<byte[]> sink = new ArrayList<>();
		cs.testSink = sink;
		cs.invalidate(invalidations);
		cs.testSink = null;
		assertEquals(1, sink.size());
		return sink.get(0);
	}

	private byte[][] mm(final long[][] invalidationNumbers)
	{
		final TLongHashSet[] invalidations = convert(invalidationNumbers);
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

	private TLongHashSet[] um(final byte[] buf)
	{
		return (TLongHashSet[])umx(buf);
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
		assertEquals(invalidationSplit, senderInfo.getInvalidationSplit() - invalidationSplitBefore);
		assertEquals(invalidationSplit, count("invalidationSplit")        - invalidationSplitBefore);

		final ClusterListenerInfo listenerInfo = cl.getInfo();
		assertEquals(234567, listenerInfo.getReceiveBufferSize());
		assertEquals(0, listenerInfo.getException());
		assertEquals(0, count("fail"));
		assertEquals(listenerMissingMagic, listenerInfo.getMissingMagic() - missingMagicBefore);
		assertEquals(listenerWrongSecret,  listenerInfo.getWrongSecret()  - wrongSecretBefore);
		assertEquals(listenerFromMyself,   listenerInfo.getFromMyself()   - fromMyselfBefore);
		assertEquals(listenerMissingMagic, count("missingMagic") - missingMagicBefore);
		assertEquals(listenerWrongSecret,  count("wrongSecret")  - wrongSecretBefore);
		assertEquals(listenerFromMyself,   count("fromMyself")   - fromMyselfBefore);
		final List<ClusterListenerInfo.Node> listenerInfoNodes = listenerInfo.getNodes();
		assertUnmodifiable(listenerInfoNodes);
		nodes: for(final long[] node : listenerNodes)
		{
			assertEquals(3, node.length);
			final long id = node[0];
			assertTrue(id>=Integer.MIN_VALUE, String.valueOf(id));
			assertTrue(id<=Integer.MAX_VALUE, String.valueOf(id));
			for(final ClusterListenerInfo.Node infoNode : listenerInfoNodes)
			{
				if(infoNode.getID()==id)
				{
					assertNotNull(infoNode.getFirstEncounter());
					assertEquals(null, infoNode.getAddress());
					assertEquals(967, infoNode.getPort());
					assertEquals(node[1], infoNode.getPingInfo().getInOrder() - pingInOrderBefore, "ping");
					assertEquals(node[2], infoNode.getPongInfo().getInOrder() - pongInOrderBefore, "pong");

					assertEquals(0, infoNode.getPingInfo().getOutOfOrder());
					assertEquals(0, infoNode.getPongInfo().getOutOfOrder());
					assertEquals(0, infoNode.getPingInfo().getDuplicate());
					assertEquals(0, infoNode.getPongInfo().getDuplicate());

					assertEquals(0, infoNode.getPingInfo().getLost());
					assertEquals(0, infoNode.getPongInfo().getLost());
					assertEquals(0, infoNode.getPingInfo().getLate());
					assertEquals(0, infoNode.getPongInfo().getLate());

					assertEquals(node[1], countSequence("ping", "inOrder") - pingInOrderBefore, "ping");
					assertEquals(node[2], countSequence("pong", "inOrder") - pongInOrderBefore, "pong");
					assertEquals(0, countSequence("ping", "early"));
					assertEquals(0, countSequence("pong", "early"));
					assertEquals(0, countSequence("ping", "outOfOrder"));
					assertEquals(0, countSequence("pong", "outOfOrder"));
					assertEquals(0, countSequence("ping", "duplicate"));
					assertEquals(0, countSequence("pong", "duplicate"));
					assertEquals(0, countSequence("ping", "late"));
					assertEquals(0, countSequence("pong", "late"));
					assertEquals(0, countSequence("ping", "lost"));
					assertEquals(0, countSequence("pong", "lost"));

					break nodes;
				}
			}
			fail("node not found: " + Long.toHexString(id));
		}
		assertEquals(listenerNodes.length, listenerInfoNodes.size());
	}

	private static double count(final String nameSuffix)
	{
		return ((Counter)PrometheusMeterRegistrar.meter(
				Cluster.class, nameSuffix,
				Tags.of("model", "MOCK_MODEL_NAME"))).count();
	}

	private static double countSequence(
			final String kind,
			final String result)
	{
		return ((Counter)PrometheusMeterRegistrar.meter(
				Cluster.class, "sequence",
				Tags.of(
						"model", "MOCK_MODEL_NAME",
						"id", "11224433",
						"address", "null",
						"port", "967",
						"kind", kind,
						"result", result))).count();
	}

	private static double countSequenceBefore(
			final String kind,
			final String result)
	{
		try
		{
			return countSequence(kind, result);
		}
		catch(final PrometheusMeterRegistrar.NotFound e)
		{
			return 0;
		}
	}

	private static final byte b0 = 0;
	private static final byte bNaPK = (byte)0x80; // NaPK
}
