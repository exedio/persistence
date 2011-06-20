/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.SequenceItem.TYPE;
import static com.exedio.cope.SequenceItem.full;
import static com.exedio.cope.SequenceItem.limited;
import static com.exedio.cope.SequenceModelTest.MODEL;

public class SequenceTest extends AbstractRuntimeTest
{
	public SequenceTest()
	{
		super(MODEL);
		skipTransactionManagement();
	}

	public void testIt()
	{
		// sequences are not part of a transaction
		assertFalse(MODEL.hasCurrentTransaction());


		// full
		assertInfo(full, full.getInfo());

		assertEquals(0, full.next());
		assertInfo(full, 1, 0, 0, full.getInfo());

		assertEquals(1, full.next());
		assertInfo(full, 2, 0, 1, full.getInfo());

		assertEquals(2, full.next());
		assertInfo(full, 3, 0, 2, full.getInfo());

		assertEquals(3, full.next());
		assertInfo(full, 4, 0, 3, full.getInfo());


		// limited
		assertInfo(limited, limited.getInfo());

		assertEquals(5, limited.next());
		assertInfo(limited, 1, 5, 5, limited.getInfo());

		assertEquals(6, limited.next());
		assertInfo(limited, 2, 5, 6, limited.getInfo());

		assertEquals(7, limited.next());
		assertInfo(limited, 3, 5, 7, limited.getInfo());

		assertEquals(8, limited.next());
		assertInfo(limited, 4, 5, 8, limited.getInfo());

		assertEquals(9, limited.next());
		assertInfo(limited, 5, 5, 9, limited.getInfo());

		try
		{
			limited.next();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 10 in SequenceItem.limited limited to 5,9", e.getMessage());
		}
		assertInfo(limited, 5, 5, 9, limited.getInfo());

		try
		{
			limited.next();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("sequence overflow to 11 in SequenceItem.limited limited to 5,9", e.getMessage());  // TODO should not increase further
		}
		assertInfo(limited, 5, 5, 9, limited.getInfo());



		assertInfo(MODEL.getSequenceInfo(), TYPE.getThis(), full, limited);
	}
}
