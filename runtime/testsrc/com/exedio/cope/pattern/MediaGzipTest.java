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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.vaulttest.VaultServiceTest.AssertionErrorOutputStream;
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import org.junit.jupiter.api.Test;

public final class MediaGzipTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(AnItem.TYPE);

	public MediaGzipTest()
	{
		super(MODEL);
	}

	@Test void test() throws IOException
	{
		assertEquals(true,  AnItem.gzipped .isCompressed());
		assertEquals(false, AnItem.identity.isCompressed());
		assertEquals(true,  AnItem.gzipped .isGzip());
		assertEquals(false, AnItem.identity.isGzip());

		final AnItem item = new AnItem();
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
		assertEquals(-1, item.getGzippedLength());
		assertEquals(-1, item.getIdentityLength());

		assertEquals(null, item.getGzippedBody());
		assertEquals(null, item.getIdentityBody());

		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink);
			assertEquals(false, Files.exists(sink));
			item.getIdentityBody(sink);
			assertEquals(false, Files.exists(sink));
			item.getGzippedBody(sink.toFile());
			assertEquals(false, Files.exists(sink));
			item.getIdentityBody(sink.toFile());
			assertEquals(false, Files.exists(sink));
		}
		item.getGzippedBody (new AssertionErrorOutputStream());
		item.getIdentityBody(new AssertionErrorOutputStream());

		item.setGzipped (decodeLower(PLAIN), CONTENT_TYPE);
		item.setIdentity(decodeLower(PLAIN), CONTENT_TYPE);

		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());
		assertEquals(COMPRESSED.length()/2, item.getGzippedLength()); // TODO would be nicer, if this was PLAIN length, but I have no good idea how to do this
		assertEquals(PLAIN     .length()/2, item.getIdentityLength());

		assertEquals(PLAIN, encodeLower(item.getGzippedBody()));
		assertEquals(PLAIN, encodeLower(item.getIdentityBody()));

		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink);
			assertEquals(PLAIN, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final Path sink = files.newPathNotExists();
			item.getIdentityBody(sink);
			assertEquals(PLAIN, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink.toFile());
			assertEquals(PLAIN, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final Path sink = files.newPathNotExists();
			item.getIdentityBody(sink.toFile());
			assertEquals(PLAIN, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final var sink = new NonCloseableOrFlushableOutputStream();
			item.getGzippedBody(sink);
			assertEquals(PLAIN, encodeLower(sink.toByteArray()));
		}
		{
			final var sink = new NonCloseableOrFlushableOutputStream();
			item.getIdentityBody(sink);
			assertEquals(PLAIN, encodeLower(sink.toByteArray()));
		}

		item.setGzipped ((byte[])null, null);
		item.setIdentity((byte[])null, null);
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
	}

	@Test void testSetInputStream() throws IOException
	{
		final AnItem item = new AnItem();
		item.setGzipped (stream(decodeLower(PLAIN)), CONTENT_TYPE);
		assertStreamClosed();
		item.setIdentity(stream(decodeLower(PLAIN)), CONTENT_TYPE);
		assertStreamClosed();
		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());

		item.setGzipped ((InputStream)null, null);
		item.setIdentity((InputStream)null, null);
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test void testSetPath() throws IOException
	{
		final AnItem item = new AnItem();
		item.setGzipped (files.newPath(decodeLower(PLAIN)), CONTENT_TYPE);
		item.setIdentity(files.newPath(decodeLower(PLAIN)), CONTENT_TYPE);
		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());

		item.setGzipped ((Path)null, null);
		item.setIdentity((Path)null, null);
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
	}

	@Test void testSetFile() throws IOException
	{
		final AnItem item = new AnItem();
		item.setGzipped (files.newFile(decodeLower(PLAIN)), CONTENT_TYPE);
		item.setIdentity(files.newFile(decodeLower(PLAIN)), CONTENT_TYPE);
		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());

		item.setGzipped ((File)null, null);
		item.setIdentity((File)null, null);
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
	}

	@Test void testSetValue() throws IOException
	{
		final AnItem item = new AnItem();
		item.setGzipped (Media.toValue(decodeLower(PLAIN), CONTENT_TYPE));
		item.setIdentity(Media.toValue(decodeLower(PLAIN), CONTENT_TYPE));
		assertEquals(COMPRESSED, item.getGzippedRaw());
		assertEquals(PLAIN,      item.getIdentityRaw());

		item.setGzipped ((Media.Value)null);
		item.setIdentity((Media.Value)null);
		assertEquals(null, item.getGzippedRaw());
		assertEquals(null, item.getIdentityRaw());
	}

	static final String PLAIN = "0102030405060708090a0b0c0d0e0f";

	// https://en.wikipedia.org/wiki/Gzip#File_structure
	static final String COMPRESSED =
			"1f8b"     + // 0: Magic number
			"08"       + // 2: Compression method = Deflate
			"00"       + // 3: Flags
			"00000000" + // 4: Unix time (0 = no timestamp is available)
			"00"       + // 8: Extra flags = None (default)
			"ff"       + // 9: Filesystem = Unknown (default)
			"63646266616563e7e0e4e2e6e1e5e30700" + // The compressed data
			"3aaaa6f5" + // CRC-32
			"0f000000";  // Size of the uncompressed data = 15 bytes

	@Test void testLarge() throws IOException
	{
		final AnItem item = new AnItem();
		final int dataBigLength = (50*1024) + 77; // must be larger than InputStream#DEFAULT_BUFFER_SIZE = 8192
		final byte[] dataBig = new byte[dataBigLength];
		final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
		final int data8Length = bytes8.length;
		for(int i = 0; i<dataBigLength; i++)
			dataBig[i] = bytes8[i % data8Length];
		item.setGzipped(dataBig, CONTENT_TYPE);

		assertArrayEquals(dataBig, item.getGzippedBody());
		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink);
			assertArrayEquals(dataBig, Files.readAllBytes(sink));
		}
		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink.toFile());
			assertArrayEquals(dataBig, Files.readAllBytes(sink));
		}
		{
			final var sink = new NonCloseableOrFlushableOutputStream();
			item.getGzippedBody(sink);
			assertArrayEquals(dataBig, sink.toByteArray());
		}
	}

	@Test void testNative() throws IOException
	{
		final AnItem item = new AnItem();
		item.set(
				// resource created by
				//   echo -n "abcd0123" | gzip > runtime/testsrc/com/exedio/cope/pattern/MediaGzipTestNative.gz
				AnItem.gzipped.getBody().map(getClass().getResourceAsStream("MediaGzipTestNative.gz")),
				Cope.mapAndCast(AnItem.gzipped.getContentType(), CONTENT_TYPE),
				SetValue.map(AnItem.gzipped.getLastModified(), new Date()));

		final String plain = "61626364"+"30313233";
		assertEquals(plain, encodeLower(item.getGzippedBody()));
		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink);
			assertEquals(plain, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final Path sink = files.newPathNotExists();
			item.getGzippedBody(sink.toFile());
			assertEquals(plain, encodeLower(Files.readAllBytes(sink)));
		}
		{
			final var sink = new NonCloseableOrFlushableOutputStream();
			item.getGzippedBody(sink);
			assertEquals(plain, encodeLower(sink.toByteArray()));
		}
	}

	static final String CONTENT_TYPE = "major/minor";

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap={"getBody","getLength","set"}, visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media gzipped = new Media().optional().gzip();

		@Wrapper(wrap={"getBody","getLength","set"}, visibility=PACKAGE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media identity = new Media().optional();

		String getGzippedRaw()
		{
			return encodeLower(gzipped.getBody().getArray(this));
		}

		String getIdentityRaw()
		{
			return encodeLower(identity.getBody().getArray(this));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getGzippedLength()
		{
			return AnItem.gzipped.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		byte[] getGzippedBody()
		{
			return AnItem.gzipped.getBody(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getGzippedBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			AnItem.gzipped.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getGzippedBody(@javax.annotation.Nonnull final java.nio.file.Path body)
				throws
					java.io.IOException
		{
			AnItem.gzipped.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		void getGzippedBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			AnItem.gzipped.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGzipped(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value gzipped)
				throws
					java.io.IOException
		{
			AnItem.gzipped.set(this,gzipped);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGzipped(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			AnItem.gzipped.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGzipped(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.gzipped.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setGzipped(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.gzipped.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		void setGzipped(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.gzipped.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getIdentityLength()
		{
			return AnItem.identity.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		byte[] getIdentityBody()
		{
			return AnItem.identity.getBody(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getIdentityBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			AnItem.identity.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void getIdentityBody(@javax.annotation.Nonnull final java.nio.file.Path body)
				throws
					java.io.IOException
		{
			AnItem.identity.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		void getIdentityBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			AnItem.identity.getBody(this,body);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIdentity(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value identity)
				throws
					java.io.IOException
		{
			AnItem.identity.set(this,identity);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIdentity(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			AnItem.identity.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIdentity(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.identity.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIdentity(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.identity.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@java.lang.Deprecated
		void setIdentity(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			AnItem.identity.set(this,body,contentType);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
