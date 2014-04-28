/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.Hash;
import java.nio.charset.Charset;

public class ByteAlgorithmTest extends CopeAssert
{
	public void testIt()
	{
		final Hash.Algorithm a = new Hash.Algorithm(){

			public boolean check(final byte[] plainText, final byte[] hash)
			{
				throw new RuntimeException();
			}

			public byte[] hash(final byte[] plainText)
			{
				throw new RuntimeException();
			}

			public int length()
			{
				return 1;
			}

			public String name()
			{
				return "name";
			}

			public boolean compatibleTo(final Hash.Algorithm other)
			{
				throw new RuntimeException();
			}
		};

		try
		{
			new Hash(a, (Charset)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("charset", e.getMessage());
		}
		try
		{
			new Hash(a, (String)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("encoding", e.getMessage());
		}
		try
		{
			new Hash(a, "nixus");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("nixus", e.getMessage());
			assertEquals(null, e.getCause());
		}
	}
}
