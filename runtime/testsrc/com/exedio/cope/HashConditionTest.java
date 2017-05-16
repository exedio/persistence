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

import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.junit.CopeAssert.assertUnmodifiable;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.SortedSet;
import javax.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;

public class HashConditionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE);

	public HashConditionTest()
	{
		super(MODEL);
	}

	enum Algorithm
	{
		// https://de.wikipedia.org/wiki/Message-Digest_Algorithm_5#MD5-Hashes
		md5("MD5",
				"d41d8cd98f00b204e9800998ecf8427e",
				"a3cca2b2aa1e3b5b3b5aad99a8529074"),
		// https://de.wikipedia.org/wiki/Secure_Hash_Algorithm#Beispiel-Hashes
		sha("SHA",
				"da39a3ee5e6b4b0d3255bfef95601890afd80709",
				"68ac906495480a3404beee4874ed853a037a7a8f"),
		// https://de.wikipedia.org/wiki/SHA-2#Beispiel-Hashes
		sha224("SHA-224",
				"d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
				"49b08defa65e644cbf8a2dd9270bdededabc741997d1dadd42026d7b"),
		sha256("SHA-256",
				"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				"d32b568cd1b96d459e7291ebf4b25d007f275c9f13149beeb782fac0716613f8"),
		sha384("SHA-384",
				"38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b",
				"71e8383a4cea32d6fd6877495db2ee353542f46fa44bc23100bca48f3366b84e809f0708e81041f427c6d5219a286677"),
		sha512("SHA-512",
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
				"af9ed2de700433b803240a552b41b5a472a6ef3fe1431a722b2063c75e9f07451f67a28e37d09cde769424c96aea6f8971389db9e1993d6c565c3c71b855723c");

		final String code;
		final String empty;
		final String franz;

		Algorithm(final String code, final String empty, final String franz)
		{
			this.code = code;
			this.empty = empty;
			this.franz = franz;
			assertEquals(empty.length(), franz.length());
			assertFalse(empty.equals(franz));
		}

		static Algorithm forCode(final String code)
		{
			for(final Algorithm a : Algorithm.values())
				if(a.code.equals(code))
					return a;
			throw new AssertionError();
		}
	}

	@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
	SortedSet<String> supported;
	MyItem item;

	@Before public final void setUp()
	{
		supported = model.getSupportedDataHashAlgorithms();
		item = new MyItem();
	}

	@Test public void test()
	{
		assertUnmodifiable(supported);

		for(final String algorithm : supported)
		{
			assertTrue(algorithm, !algorithm.isEmpty());
			assertTrue(algorithm, algorithm.equals(algorithm.trim()));

			final Algorithm a = Algorithm.forCode(algorithm);

			item.setData(null);
			assertIt(false, false, null, a);
			assertIt(false, false, "", a);
			assertIt(false, false, a.empty, a);
			assertIt(false, false, a.franz, a);

			item.setData("");
			assertIt(false, false, null, a);
			assertIt(false, true,  "", a);
			assertIt(true,  false, a.empty, a);
			assertIt(false, true,  a.franz, a);

			item.setData("Franz jagt im komplett verwahrlosten Taxi quer durch Bayern");
			assertIt(false, false, null, a);
			assertIt(false, true,  "", a);
			assertIt(false, true,  a.empty, a);
			assertIt(true,  false, a.franz, a);

			item.setData("Frank jagt im komplett verwahrlosten Taxi quer durch Bayern");
			assertIt(false, false, null, a);
			assertIt(false, true,  "", a);
			assertIt(false, true,  a.empty, a);
			assertIt(false, true,  a.franz, a);
		}

		final Condition unsupported = MyItem.hash.hashMatches("NIXUS", MyItem.data);
		final Condition unsupportedNot = MyItem.hash.hashDoesNotMatch("NIXUS", MyItem.data);
		assertEquals(  "MyItem.hash=NIXUS(MyItem.data)", unsupported.toString());
		assertEquals("!(MyItem.hash=NIXUS(MyItem.data))", unsupportedNot.toString());
		try
		{
			MyItem.TYPE.search(unsupported);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("hash >NIXUS< not supported", e.getMessage());
		}
		try
		{
			MyItem.TYPE.search(unsupportedNot);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("hash >NIXUS< not supported", e.getMessage());
		}
	}

	private void assertIt(
			final boolean matches, final boolean matchesNot,
			final String hash, final Algorithm algorithm)
	{
		item.setHash(hash);
		assertEquals(
				algorithm.code,
				matches ? asList(item) : asList(),
				MyItem.TYPE.search(MyItem.hash.hashMatches(algorithm.code, MyItem.data)));
		assertEquals(
				algorithm.code + " NOT",
				matchesNot ? asList(item) : asList(),
				MyItem.TYPE.search(MyItem.hash.hashDoesNotMatch(algorithm.code, MyItem.data)));
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		@Wrapper(wrap="set", visibility=PACKAGE, internal=true)
		static final DataField data = new DataField().optional();
		static final StringField hash = new StringField().optional().lengthRange(0, 128);

		void setData(@Nullable final String source)
		{
			setDataInternal(source!=null ? source.getBytes(US_ASCII) : null);
		}


		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final boolean isDataNull()
		{
			return MyItem.data.isNull(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final long getDataLength()
		{
			return MyItem.data.getLength(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		final byte[] getDataArray()
		{
			return MyItem.data.getArray(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
				throws
					java.io.IOException
		{
			MyItem.data.get(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void getData(@javax.annotation.Nonnull final java.io.File data)
				throws
					java.io.IOException
		{
			MyItem.data.get(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setDataInternal(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data)
		{
			MyItem.data.set(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setDataInternal(@javax.annotation.Nullable final byte[] data)
		{
			MyItem.data.set(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setDataInternal(@javax.annotation.Nullable final java.io.InputStream data)
				throws
					java.io.IOException
		{
			MyItem.data.set(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setDataInternal(@javax.annotation.Nullable final java.io.File data)
				throws
					java.io.IOException
		{
			MyItem.data.set(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		final java.lang.String getHash()
		{
			return MyItem.hash.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setHash(@javax.annotation.Nullable final java.lang.String hash)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.hash.set(this,hash);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
