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

package com.exedio.cope.pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.misc.Arrays;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.IllegalAlgorithmException;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

public class MessageDigestAlgorithmTest
{
	@Test void testIt()
	{
		try
		{
			new MessageDigestAlgorithm("NIXUS", -1, 0);
			fail();
		}
		catch(final IllegalAlgorithmException e)
		{
			assertEquals("NIXUS", e.getAlgorithm());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", -1, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("saltLength must be at least zero, but was -1", e.getMessage());
		}
		final MessageDigestAlgorithm a = new MessageDigestAlgorithm("SHA-512", 0, 1);
		try
		{
			a.salt(0, new SecureRandom());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("saltLength inconsistent to saltSource", e.getMessage());
		}
		try
		{
			a.salt(1, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("saltLength inconsistent to saltSource", e.getMessage());
		}
		try
		{
			new MessageDigestAlgorithm("SHA-512", 0, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("iterations must be at least one, but was 0", e.getMessage());
		}
	}

	@Test void testCompatibleTo()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom());
		assertTrue(a.compatibleTo(a));
		assertTrue( a.compatibleTo(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom())));
		assertFalse(a.compatibleTo(new MessageDigestAlgorithm("MD5",     0, 5).salt(8, new MockSecureRandom())));
		assertFalse(a.compatibleTo(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(7, new MockSecureRandom())));
		assertFalse(a.compatibleTo(new MessageDigestAlgorithm("SHA-512", 0, 4).salt(8, new MockSecureRandom())));
		try
		{
			a.compatibleTo(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test void testSalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom());
		assertEquals("SHA512s8i5", a.name());
		assertEquals(72, a.length());
		assertEquals(8, a.getSaltLength());
		assertEquals(5, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"aeab417a9b5a7cf314339787d765de2fa913946ad6786572c9a4f22d16339411057e7a27c94421f5e6471998cc5a6301029f5272243a8dee889dd23fcd45410658556608a18f0d91");
		assertDigest(a,
			"knollo",
			"aeab417a9b5a7cf39a91d054d056ff387266c789c26ccff7677c01cca150b1575db3db8bca64f7d027a606f692cb3f6e6ff3cfac5c4c458007a9fac9db7b877707f300f0a904ec4a");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"aeab417a9b5a7cf3868ff1153f6d0807b9e4e859112e559cb1c0ae0de8e00c9046e0722338d820408267487d618d5c5edbdeedf53d6fbd9949896dd92e38bcd386c2f651886b79db");
	}

	@Test void testSaltedMinimal()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 2).salt(1, new MockSecureRandom());
		assertEquals("SHA512s1i2", a.name());
		assertEquals(65, a.length());
		assertEquals(1, a.getSaltLength());
		assertEquals(2, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"ae22459d30b999761147500ba024599289ffcd989eedd67b0cb8f6aca5c6f145738515e4d611fc2498c73c2dc73227de4dbb810203e3dcff4393d4b1cd9f6a962d");
		assertDigest(a,
			"knollo",
			"aebbcf91ef48b0956eac8fe9eccc650dd9e74e345ed995e156af7023d14443ff08953e3f97c147bc17200fc2c63f2f986917ab755340c735b522114a11aa940af4");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"ae38e71ccfe981521397d4392ba5931148d99718a127495cdd96ebe2091756ef16511e8ef4a6ea907813e44ff8535fce2c710d82cb55e1d4ea15a0757a80409177");
	}

	@Test void testUnsalted()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 5);
		assertEquals("SHA512i5", a.name());
		assertEquals(64, a.length());
		assertEquals(0, a.getSaltLength());
		assertEquals(5, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"c73b8a5d581bdcf828e2ab92dfb119cfc439b81a956de76dd02b5664cd5bb6f8b861e7ee1a4edac44295e1b9d28828e83efb620cb2e21aae575e3286424ea92f");
		assertDigest(a,
			"knollo",
			"0af3af1b4414ebb1e3a1b394d5304e54572c07b3ad15b937fde7612b31c22f661048c28470a14b3a78ad0bf43113c5831ce20f08b12cad710beca273894ab01c");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"33fc82fac82a7c602149d0888b807d94d00a1c44d60dc092a2357ded829e37b9c5440c2ef07d72431ab37f2eaa26b2fee1144d9a9b501e3d4b25875e47689e6b");
	}

	@Test void testNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 1).salt(8, new MockSecureRandom());
		assertEquals("SHA512s8", a.name());
		assertEquals(72, a.length());
		assertEquals(8, a.getSaltLength());
		assertEquals(1, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"aeab417a9b5a7cf379c224f53a48f3ba32de8c9f5e12a2d78e281665c88b4addfe9c5357e1edd5f74ce7b0a2822dbb4a4274627d5e87bc8f24db5999b18dfe812bb037e1196bb4bc");
		assertDigest(a,
			"knollo",
			"aeab417a9b5a7cf385decb666a4d572c9962e9c042e0fc33718b2cbabc28a866c35594f6c17596dedb0437dedb652eb2854d9645e7aa80926538923763b733f8ad00747248df9ade");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"aeab417a9b5a7cf39904017f7a5e22767e17c88ec0b1442490df10531c7806f803b07dac383380623df954bef6ce5da18fdc82d1baf7146fbd3e95be7c00acf08c4062f624510b20");
	}

	@Test void testUnsaltedNoniterated()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("SHA-512", 0, 1);
		assertEquals("SHA512", a.name());
		assertEquals(64, a.length());
		assertEquals(0, a.getSaltLength());
		assertEquals(1, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
		assertDigest(a,
			"knollo",
			"1835861e09d3f89ee0f3f0e875366cad0d1877615ad322be2fff1135eb8e6f1ee1f55ce00edd17ae1c2ad89a96e676dfb106a0a8e78a7ea71e3ac373a5426af6");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"cb7e2023fc2372d06a876501b2f8f6c5347ac23fd8e92c02b5efd8ce60e03240a60ca82d760849103455dfd26cd1c28695e5f51b41001f71496e8126d168eb84");

		// reference example from https://de.wikipedia.org/wiki/SHA-512
		assertDigest(a,
			"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c");
		assertDigest(a,
			"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"90b30ef9902ae4c4c691d2d78c2f8fa0aa785afbc5545286b310f68e91dd2299c84a2484f0419fc5eaa7de598940799e1091c4948926ae1c9488dddae180bb80");
	}

	@Test void testUnsaltedNoniteratedMD5()
	{
		final MessageDigestAlgorithm a =
			new MessageDigestAlgorithm("MD5", 0, 1);
		assertEquals("MD5", a.name());
		assertEquals(16, a.length());
		assertEquals(0, a.getSaltLength());
		assertEquals(1, a.getIterations());

		assertDigestNull(a);
		assertDigest(a,
			"",
			"d41d8cd98f00b204e9800998ecf8427e");
		assertDigest(a,
			"knollo",
			"ad373a47d81949f466552edf29499b32");
		assertDigest(a,
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
			"knolloknolloknolloknolloknollo",
			"6ce62d0dbd8e8b3f453ba742c102cd0b");

		// reference example from https://de.wikipedia.org/wiki/MD5
		assertDigest(a,
			"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"a3cca2b2aa1e3b5b3b5aad99a8529074");
		assertDigest(a,
			"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern",
			"7e716d0e702df0505fc72e2b89467910");
	}

	private static void assertDigestNull(final MessageDigestAlgorithm algorithm)
	{
		try
		{
			algorithm.hash(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			algorithm.check(null, new byte[]{1, 2, 3});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			algorithm.check(new byte[]{1, 2, 3}, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			algorithm.check(new byte[]{1, 2, 3}, new byte[]{1, 2, 3});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("3", e.getMessage());
		}
	}

	private static void assertDigest(
			final MessageDigestAlgorithm algorithm,
			final String plainText,
			final String expectedHash)
	{
		final byte[] plainTextBytes = plainText.getBytes(UTF_8);
		final byte[] plainTextBytesCopy = Arrays.copyOf(plainTextBytes);

		final MockSecureRandom saltSource = (MockSecureRandom)algorithm.getSaltSource();
		if(saltSource!=null)
			saltSource.expectNextBytes(Hex.decodeLower("aeab417a9b5a7cf3".substring(0, algorithm.getSaltLength()*2)));
		assertEquals(expectedHash, Hex.encodeLower(algorithm.hash(plainTextBytes)));
		assertTrue(java.util.Arrays.equals(plainTextBytes, plainTextBytesCopy));

		assertTrue(algorithm.check(plainTextBytes, Hex.decodeLower(expectedHash)));
		assertTrue(java.util.Arrays.equals(plainTextBytes, plainTextBytesCopy));
		assertFalse(algorithm.check((plainText+"x").getBytes(UTF_8), Hex.decodeLower(expectedHash)));
	}
}
