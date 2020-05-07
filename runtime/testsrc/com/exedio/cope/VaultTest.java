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

import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class VaultTest
{
	static final Model MODEL = new Model(VaultItem.TYPE);
	static final String HASH1 = "c665cb3dd08b32c85e6d50149ea3c46ac9f56878f4965f85c1e40d535d980842d591a25d5ad232eedfed6f1d32b2ae950efe2957cdd93ea2b9c5fe794b113608";
	static final String HASH1A= "c665cb3dd08b32c8xx128";
	static final String HASH2 = "52b8c77bebdef6f008784916e726b1da073cf5fc826f5f442d2cf7e868b1b0c9197dc2146b80faaf292f0898abb3f41687c270d68537cd6b2584651269869fde";
	static final String VALUE1 = "aabbcc";
	static final String VALUE2 = "ccddee";

	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("SHA-512", service.vaultProperties.getAlgorithm());
		assertEquals("mainExampleValue", service.serviceProperties.example);
		assertEquals(true, service.writable);
	}

	@Test void testClose()
	{
		MODEL.rollback();
		service.assertIt("");

		MODEL.disconnect();
		service.assertIt("close\n");
	}

	@Test void testGetLength()
	{
		service.assertIt("");

		final VaultItem i = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + i + "\n");

		assertEquals(VALUE1.length(), i.getFieldLength());
		service.assertIt(HASH1, VALUE1, "getLength\n");
	}

	@Test void testGetBytes()
	{
		service.assertIt("");

		final VaultItem i = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + i + "\n");

		assertEquals(VALUE1, i.getFieldBytes());
		service.assertIt(HASH1, VALUE1, "getBytes\n");
	}

	@Test void testGetStream() throws IOException
	{
		service.assertIt("");

		final VaultItem i = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + i + "\n");

		assertEquals(VALUE1, i.getFieldStream());
		service.assertIt(HASH1, VALUE1, "getStream\n");
	}

	@Test void getPutBytes()
	{
		service.assertIt("");

		final VaultItem i1 = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + i1 + "\n");

		new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field VaultItem-1\n");

		final VaultItem i3 = new VaultItem(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i3 + "\n");

		i1.setField(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i1 + "\n");

		i3.setField(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i3 + "\n");
	}

	@Test void getPutStream() throws IOException
	{
		service.assertIt("");

		final VaultItem i1 = VaultItem.byStream(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + i1 + "\n"); // TODO putStream

		VaultItem.byStream(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field VaultItem-1\n"); // TODO putStream

		final VaultItem i3 = VaultItem.byStream(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i3 + "\n"); // TODO putStream

		i1.setFieldByStream(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i1 + "\n"); // TODO putStream

		i3.setFieldByStream(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putBytes VaultItem.field " + i3 + "\n"); // TODO putStream
	}

	@Test void getPutPath() throws IOException
	{
		service.assertIt("");

		final VaultItem i1 = VaultItem.byPath(VALUE1);
		service.assertIt(HASH1, VALUE1, "putFile VaultItem.field " + i1 + "\n");

		VaultItem.byPath(VALUE1);
		service.assertIt(HASH1, VALUE1, "putFile VaultItem.field VaultItem-1\n");

		final VaultItem i3 = VaultItem.byPath(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i3 + "\n");

		i1.setFieldByPath(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i1 + "\n");

		i3.setFieldByPath(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i3 + "\n");
	}

	@Test void getPutFile() throws IOException
	{
		service.assertIt("");

		final VaultItem i1 = VaultItem.byFile(VALUE1);
		service.assertIt(HASH1, VALUE1, "putFile VaultItem.field " + i1 + "\n");

		VaultItem.byFile(VALUE1);
		service.assertIt(HASH1, VALUE1, "putFile VaultItem.field VaultItem-1\n");

		final VaultItem i3 = VaultItem.byFile(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i3 + "\n");

		i1.setFieldByFile(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i1 + "\n");

		i3.setFieldByFile(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putFile VaultItem.field " + i3 + "\n");
	}

	@Test void getPutZip() throws IOException, URISyntaxException
	{
		service.assertIt("");

		final VaultItem i1 = VaultItem.byZip(VALUE1);
		service.assertIt(HASH1, VALUE1, "putStream VaultItem.field " + i1 + "\n");

		VaultItem.byZip(VALUE1);
		service.assertIt(HASH1, VALUE1, "putStream VaultItem.field VaultItem-1\n");

		final VaultItem i3 = VaultItem.byZip(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putStream VaultItem.field " + i3 + "\n");

		i1.setFieldByZip(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putStream VaultItem.field " + i1 + "\n");

		i3.setFieldByZip(VALUE2);
		service.assertIt(HASH1, VALUE1, HASH2, VALUE2, "putStream VaultItem.field " + i3 + "\n");
	}


	private VaultMockService service;

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultMockService.class),
				single("dataField.vault.service.example", "mainExampleValue"),
				single("dataField.vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		service = (VaultMockService)MODEL.connect().vault;
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
	}

	@AfterEach void tearDown()
	{
		if(MODEL.isConnected())
		{
			MODEL.rollback();
			MODEL.disconnect();
		}
	}
}
