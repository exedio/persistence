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

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.util.CharsetName.UTF8;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class DataDigestTest extends CopeAssert
{
	public void testUpdate() throws IOException
	{
		assertUpdate("904ac396ac3d50faa666e57146fe7862", bytes4);

		// reference example from http://de.wikipedia.org/wiki/MD5
		assertUpdate(
				"d41d8cd98f00b204e9800998ecf8427e",
				bytes0);
		assertUpdate(
				"a3cca2b2aa1e3b5b3b5aad99a8529074",
				"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern");
		assertUpdate(
				"7e716d0e702df0505fc72e2b89467910",
				"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern");
	}

	private static final void assertUpdate(final String hash, final String input) throws IOException
	{
		assertUpdate(hash, input.getBytes(UTF8));
	}

	private static final void assertUpdate(final String hash, final byte[] input) throws IOException
	{
		messageDigest.reset();
		toValue(input).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));

		messageDigest.reset();
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
		toValue(inputStream).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));

		messageDigest.reset();
		final File inputFile = File.createTempFile(DataDigestTest.class.getName(), ".dat");
		{
			final FileOutputStream out = new FileOutputStream(inputFile);
			try
			{
				out.write(input);
			}
			finally
			{
				out.close();
			}
		}
		toValue(inputFile).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));
	}

	private static final MessageDigest messageDigest = MessageDigestUtil.getInstance("MD5");

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
}
