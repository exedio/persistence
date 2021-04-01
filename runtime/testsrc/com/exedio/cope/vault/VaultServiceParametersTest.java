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

package com.exedio.cope.vault;

import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.Test;

public class VaultServiceParametersTest
{
	@Test void testWithWritable()
	{
		final VaultProperties props = VaultProperties.factory().create(cascade(
				single("service", VaultMockService.class)));
		final VaultServiceParameters w = new VaultServiceParameters(props, "serviceKeyW", true);
		final VaultServiceParameters r = new VaultServiceParameters(props, "serviceKeyR", false);
		final VaultServiceParameters ww = w.withWritable(true);
		final VaultServiceParameters wr = w.withWritable(false);
		final VaultServiceParameters rw = r.withWritable(true);
		final VaultServiceParameters rr = r.withWritable(false);

		assertSame(props, ww.getVaultProperties());
		assertSame(props, wr.getVaultProperties());
		assertSame(props, rw.getVaultProperties());
		assertSame(props, rr.getVaultProperties());

		assertEquals("serviceKeyW", ww.getServiceKey());
		assertEquals("serviceKeyW", wr.getServiceKey());
		assertEquals("serviceKeyR", rw.getServiceKey());
		assertEquals("serviceKeyR", rr.getServiceKey());

		assertEquals(true,  ww.isWritable());
		assertEquals(false, wr.isWritable());
		assertEquals(true,  rw.isWritable()); // TODO this is a bug
		assertEquals(false, rr.isWritable());

		assertNotSame(ww, w);
		assertNotSame(wr, w);
		assertNotSame(rw, r);
		assertNotSame(rr, r);
	}
}
