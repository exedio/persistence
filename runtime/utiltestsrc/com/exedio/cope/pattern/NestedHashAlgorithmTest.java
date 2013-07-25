package com.exedio.cope.pattern;

import java.security.SecureRandom;

import junit.framework.TestCase;

public class NestedHashAlgorithmTest extends TestCase
{
	public void testNormal()
	{
		final HashAlgorithm a = NestedHashAlgorithm.create(
				MessageDigestHashAlgorithm.create("utf8", "SHA-512", 8, new SecureRandom(), 200),
				MessageDigestHashAlgorithm.create("utf8", "MD5",     0, (SecureRandom)null, 100));

		assertEquals("SHA512s8i200-MD5i100", a.getID());
		assertEquals("SHA512s8i200-MD5i100", a.getDescription());

		final String hash = a.hash("1234");
		assertEquals(true,  a.check("1234", hash));
		assertEquals(false, a.check("12345", hash));
	}

	public void testMigration()
	{
		final HashAlgorithm old = MessageDigestHashAlgorithm.create("utf8", "MD5",     0, (SecureRandom)null, 100);

		final String oldHash = old.hash("1234");
		assertEquals(true,  old.check("1234", oldHash));
		assertEquals(false, old.check("12345", oldHash));

		final HashAlgorithm neu = MessageDigestHashAlgorithm.create("utf8", "SHA-512", 8, new SecureRandom(), 200);
		final String newHash = neu.hash(oldHash);
		final HashAlgorithm a = NestedHashAlgorithm.create(neu, old);
		assertEquals(true,  a.check("1234", newHash));
		assertEquals(false, a.check("12345", newHash));
	}

	public void testFail()
	{
		try
		{
			NestedHashAlgorithm.create(
					MessageDigestHashAlgorithm.create("utf8", "MD5",     0, (SecureRandom)null, 100),
					MessageDigestHashAlgorithm.create("utf8", "SHA-512", 8, new SecureRandom(), 200));
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inner algorithm must be deterministic (i.e. unsalted), but was SHA512s8i200", e.getMessage());
		}
		// TODO test nulls etc
	}
}
