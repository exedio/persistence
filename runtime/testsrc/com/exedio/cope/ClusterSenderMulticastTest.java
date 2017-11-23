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

import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.SocketException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public final class ClusterSenderMulticastTest
{
	private ClusterSenderMulticast sender;

	private static final int SECRET = 0x88776655;

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@BeforeEach void setUp()
	{
		final ClusterProperties properties =
			ClusterProperties.factory().create(cascade(
				single("packetSize", 47),
				single("secret", SECRET)
			));
		sender = new ClusterSenderMulticast(properties);
	}

	@AfterEach void tearDown()
	{
		sender.close();
	}

	@Test void testIt() throws IOException
	{
		final byte[] b = {10, 10, 10, 10, 10, 10, 10};
		sender.send(b.length, b);
		sender.send(b.length, b);

		sender.close();
		try
		{
			sender.send(b.length, b);
			fail();
		}
		catch(final SocketException ignored)
		{
			// do not assert message of foreign library
		}
	}
}
