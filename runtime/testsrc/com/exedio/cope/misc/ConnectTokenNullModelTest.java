/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.ConnectToken.issue;
import static com.exedio.cope.misc.ConnectToken.issueIfConnected;
import static com.exedio.cope.util.Properties.getSystemPropertySource;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.junit.CopeAssert;

public class ConnectTokenNullModelTest extends CopeAssert
{
	public void testIt()
	{
		final ConnectProperties props = new ConnectProperties(getSystemPropertySource());

		try
		{
			issue(null, props, "tokenNameNullModel");
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}

		assertNull(issueIfConnected(null, "tokenNameNullModelConditionally")); // TODO should fail
	}
}
