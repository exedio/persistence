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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LimitedQueueTest
{
	@Test void testIt() throws InterruptedException
	{
		final LimitedQueue<String> q = new LimitedQueue<>(2);

		assertEquals(true, q.offer("alpha"));
		assertEquals("alpha", q.take());

		assertEquals(true, q.offer("alpha"));
		assertEquals(true, q.offer("beta"));
		assertEquals("alpha", q.take());
		assertEquals("beta", q.take());

		assertEquals(true, q.offer("alpha"));
		assertEquals(true, q.offer("beta"));
		assertEquals(false, q.offer("gamma"));
		assertEquals("alpha", q.take());
		assertEquals("beta", q.take());
	}
}
