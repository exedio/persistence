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

import java.io.UnsupportedEncodingException;

import com.exedio.cope.junit.CopeAssert;

public class RevisionInfoTest extends CopeAssert
{
	public void testParse() throws UnsupportedEncodingException
	{
		assertEquals(map("key1", "value1", "key2", "value2"), RevisionInfo.parse("#migrationlogv01\nkey1=value1\nkey2=value2".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("#migrationlogv0".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("x#migrationlogv01".getBytes("latin1")));
		assertEquals(null, RevisionInfo.parse("".getBytes("latin1")));
		try
		{
			RevisionInfo.parse(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
