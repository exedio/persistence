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
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultServiceParametersTest
{
	@Test void testWithWritable()
	{
		final VaultProperties props = VaultProperties.factory().create(cascade(
				single("default.service", VaultMockService.class)));
		final VaultServiceParameters w = new VaultServiceParameters(props, "serviceKeyW", true,  BSW);
		final VaultServiceParameters r = new VaultServiceParameters(props, "serviceKeyR", false, BSR);
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
		assertEquals(false, rw.isWritable());
		assertEquals(false, rr.isWritable());

		assertSame(ww, w);
		assertNotSame(wr, w);
		assertSame(rw, r);
		assertSame(rr, r);

		assertSame(BSW, ww.requiresToMarkPut());
		assertSame(BSW, wr.requiresToMarkPut());
		assertSame(BSR, rw.requiresToMarkPut());
		assertSame(BSR, rr.requiresToMarkPut());
	}

	private static final BooleanSupplier BSW = () -> { throw new AssertionFailedError(); };
	private static final BooleanSupplier BSR = () -> { throw new AssertionFailedError(); };
}
