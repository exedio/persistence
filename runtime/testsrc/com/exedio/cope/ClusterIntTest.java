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

import static org.junit.Assert.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class ClusterIntTest
{
	private static final byte FILL = (byte)0xee;
	private byte[] buf;

	@Before public final void setUp()
	{
		buf = new byte[100];
		Arrays.fill(buf, (byte)0xee);
	}

	@After public final void tearDown()
	{
		buf = null;
	}


	@Test public void testInt()
	{
		assertEquals(7, m(3, 0x456789ab));
		assertBuf(FILL, FILL, FILL, (byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45);
	}

	@Test public void testIntNegative()
	{
		assertEquals(7, m(3, 0xab896745));
		assertBuf(FILL, FILL, FILL, (byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab);
	}


	private void assertBuf(final byte... expectedData)
	{
		int i = 0;
		for(; i<expectedData.length; i++)
			assertEquals(String.valueOf(i), expectedData[i], buf[i]);
		for(; i<buf.length; i++)
			assertEquals(String.valueOf(i), FILL, buf[i]);
	}

	private int m(final int pos, final int i)
	{
		return ClusterSender.marshal(pos, buf, i);
	}
}