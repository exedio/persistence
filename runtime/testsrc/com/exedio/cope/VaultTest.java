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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Hex;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("HardcodedLineSeparator")
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class VaultTest
{
	static final Model MODEL = new Model(VaultItem.TYPE);
	static final String HASH1 = "c665cb3dd08b32c85e6d50149ea3c46ac9f56878f4965f85c1e40d535d980842d591a25d5ad232eedfed6f1d32b2ae950efe2957cdd93ea2b9c5fe794b113608";
	static final String HASH2 = "52b8c77bebdef6f008784916e726b1da073cf5fc826f5f442d2cf7e868b1b0c9197dc2146b80faaf292f0898abb3f41687c270d68537cd6b2584651269869fde";
	static final String VALUE1 = "aabbcc";
	static final String VALUE2 = "ccddee";

	@Test public void connect()
	{
		assertNotNull(service);
		assertEquals("SHA-512", service.vaultProperties.getAlgorithm());
		assertEquals("mainExampleValue", service.serviceProperties.example);
		assertEquals(true, service.writable);
	}

	@Test public void getBytes()
	{
		service.assertIt("");

		new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes\n");

		new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes\n");

		new VaultItem(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes\n");
	}


	private VaultMockService service;

	@Before public void setUp()
	{
		MODEL.connect(ConnectProperties.factory().create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultMockService.class),
				single("dataField.vault.service.example", "mainExampleValue"),
				single("dataField.vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		service = (VaultMockService)MODEL.connect().vault;
		MODEL.tearDownSchema();
		MODEL.createSchema();
		MODEL.startTransaction("VaultTest");
	}

	@After public void tearDown()
	{
		MODEL.rollback();
		MODEL.disconnect();
	}

	static String hex(final byte[] bytes)
	{
		return Hex.encodeLower(bytes);
	}

	static byte[] unhex(final String hex)
	{
		return Hex.decodeLower(hex);
	}
}
