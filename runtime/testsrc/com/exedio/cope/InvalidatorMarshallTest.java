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
	private InvalidationSender is;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ConnectProperties defaultProperties = new ConnectProperties();
		final Properties.Source source = defaultProperties.getSourceObject();
		final ConnectProperties properties = new ConnectProperties(
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
		is = new InvalidationSender(0x88776655, 0x11224433, properties);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		is.close();
		super.tearDown();
	}
	
	public void testSet()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] is = um(0, buf, 0x88776655, 0x11224434, 4);
			assertContains(is[0], 0x456789ab, 0xaf896745);
			assertEquals(null, is[1]);
			assertTrue(is[2].isEmpty());
			assertEquals(null, is[3]);
			assertEquals(4, is.length);
		}
		assertEquals(null, um(0, buf, 0x88776655, 0x11224433, 4));
		
		try
		{
			um(0, buf, 0x88776654, 0x11224433, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("wrong secret", e.getMessage());
		}
		buf[0] = 0x11;
		try
		{
			um(0, buf, 0x88776654, 0x11224433, 4);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("missing magic", e.getMessage());
		}
	}
	
	public void testSplitBeforeTypeSingle()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 1);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(1, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 1);
			assertContains(pks[0], 3);
			assertEquals(1, pks.length);
		}
	}
	
	public void testSplitBeforeType()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 5, 2, 4, 1, 6);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 3);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAtType()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 5, 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterType()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 2, 4, 3, 1);
			assertEquals(null, pks[1]);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterType()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 2, 3, 1);
			assertTrue(pks[1].isEmpty());
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterAfterType()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
			assertEquals(null, pks[0]);
			assertContains(pks[1], 12);
			assertEquals(2, pks.length);
		}
	}
	
	public void testSplitAfterAfterAfterTypeCollapse()
	{
		assertEquals(40, is.packetSize);
		
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
			final TIntHashSet[] pks = um(0, bufs[0], 0x88776655, 0x11224434, 2);
			assertContains(pks[0], 2, 1);
			assertContains(pks[1], 11);
			assertEquals(2, pks.length);
		}
		{
			final TIntHashSet[] pks = um(0, bufs[1], 0x88776655, 0x11224434, 2);
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
		
		assertEqualsBytes(sink.get(0),
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff);    // 16 ping
		
		assertEquals(
				new Integer(InvalidationEndpoint.PING_AT_SEQUENCE),
				InvalidationListener.unmarshal(0, sink.get(0), sink.get(0).length, 0x88776655, 0x11224434, 0));
	}
	
	public void testPong()
	{
		final ArrayList<byte[]> sink = new ArrayList<byte[]>();
		is.testSink = sink;
		is.pong();
		is.testSink = null;
		assertEquals(1, sink.size());
		
		assertEqualsBytes(sink.get(0),
				(byte)0xc0, (byte)0xbe, (byte)0x11, (byte)0x11,     //  4 magic
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,     //  8 secret
				(byte)0x33, (byte)0x44, (byte)0x22, (byte)0x11,     // 12 node
				(byte)0xfe, (byte)0xff, (byte)0xff, (byte)0xff);    // 16 pong
		
		assertEquals(
				new Integer(InvalidationEndpoint.PONG_AT_SEQUENCE),
				InvalidationListener.unmarshal(0, sink.get(0), sink.get(0).length, 0x88776655, 0x11224434, 0));
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
	
	private TIntHashSet[] um(final int pos, final byte[] buf, final int secret, final int node, final int typeLength)
	{
		return (TIntHashSet[])InvalidationListener.unmarshal(pos, buf, buf.length, secret, node, typeLength);
	}
}
