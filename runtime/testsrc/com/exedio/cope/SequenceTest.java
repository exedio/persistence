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

import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.SchemaInfo.getSequenceName;
import static com.exedio.cope.SequenceInfoAssert.assertInfo;
import static com.exedio.cope.SequenceItem.TYPE;
import static com.exedio.cope.SequenceItem.full;
import static com.exedio.cope.SequenceItem.limited;
import static com.exedio.cope.SequenceItem.nextFull;
import static com.exedio.cope.SequenceItem.nextLimited;
import static com.exedio.cope.SequenceModelTest.MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import io.micrometer.core.instrument.Timer;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class SequenceTest extends TestWithEnvironment
{
	public SequenceTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testIt()
	{
		// sequences are not part of a transaction
		assertFalse(MODEL.hasCurrentTransaction());

		assertEquals(filterTableName("SequenceItem_full"   ), getSequenceName(full));
		assertEquals(filterTableName("SequenceItem_limited"), getSequenceName(limited));

		assertTimer(0, full);
		assertTimer(0, limited);

		// full
		assertInfo(full, full.getInfo());

		assertEquals(0, nextFull());
		assertInfo(full, 1, 0, 0, full.getInfo());
		assertTimer(1, full);
		assertTimer(0, limited);

		assertEquals(1, nextFull());
		assertInfo(full, 2, 0, 1, full.getInfo());
		assertTimer(2, full);
		assertTimer(0, limited);

		assertEquals(2, nextFull());
		assertInfo(full, 3, 0, 2, full.getInfo());
		assertTimer(3, full);
		assertTimer(0, limited);

		assertEquals(3, nextFull());
		assertInfo(full, 4, 0, 3, full.getInfo());
		assertTimer(4, full);
		assertTimer(0, limited);


		// limited
		assertInfo(limited, limited.getInfo());

		assertEquals(5, nextLimited());
		assertInfo(limited, 1, 5, 5, limited.getInfo());
		assertTimer(4, full);
		assertTimer(1, limited);

		assertEquals(6, nextLimited());
		assertInfo(limited, 2, 5, 6, limited.getInfo());
		assertTimer(4, full);
		assertTimer(2, limited);

		assertEquals(7, nextLimited());
		assertInfo(limited, 3, 5, 7, limited.getInfo());
		assertTimer(4, full);
		assertTimer(3, limited);

		assertEquals(8, nextLimited());
		assertInfo(limited, 4, 5, 8, limited.getInfo());
		assertTimer(4, full);
		assertTimer(4, limited);

		assertEquals(9, nextLimited());
		assertInfo(limited, 5, 5, 9, limited.getInfo());
		assertTimer(4, full);
		assertTimer(5, limited);

		try
		{
			nextLimited();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 10 in SequenceItem.limited limited to 5,9", e.getMessage());
		}
		assertInfo(limited, 5, 5, 9, limited.getInfo());
		assertTimer(4, full);
		assertTimer(6, limited);

		try
		{
			nextLimited();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 11 in SequenceItem.limited limited to 5,9", e.getMessage());  // TODO should not increase further
		}
		assertInfo(limited, 5, 5, 9, limited.getInfo());
		assertTimer(4, full);
		assertTimer(7, limited);


		// Model#getSequenceInfo()
		assertInfo(MODEL.getSequenceInfo(), TYPE.getThis(), full, limited);
	}

	private static void assertTimer(
			final long expected,
			final Sequence feature)
	{
		assertEquals(
				expected,
				((Timer)meter(Sequence.class, "fetch",
						tag(feature).and(
						tag(feature.getType().getModel())))).count());
	}

	@Test void testParallelSequenceAccess() throws InterruptedException
	{
		final Thread[] threads = new Thread[ 10 ];
		final HashMap<Integer,Integer> fullIds  = new HashMap<>();
		final HashMap<Integer,Integer> expected = new HashMap<>();
		for ( int i=0; i<threads.length; i++ )
		{
			expected.put( i, 1 );
			threads[i] = new Thread(() ->
			{
				final int next = nextFull();
				synchronized(fullIds)
				{
					fullIds.put(next, fullIds.getOrDefault(next, 0) + 1);
				}
			});
		}
		for ( final Thread t: threads )
		{
			t.start();
		}
		for ( final Thread t: threads )
		{
			t.join();
		}
		assertEquals( expected, fullIds );
	}
}
