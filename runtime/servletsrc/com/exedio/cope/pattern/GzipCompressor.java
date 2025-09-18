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

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;

final class GzipCompressor
{
	void getBody(
			@Nonnull final Media media,
			@Nonnull final Item item,
			@Nonnull final OutputStream body) throws IOException
	{
		if(media.isNull(item))
			return;

		final Path temp = readTemp(media, item);
		try(var inflater = new GZIPInputStream(Files.newInputStream(temp)))
		{
			inflater.transferTo(body);
		}
		Files.delete(temp);
	}

	void getBody(
			@Nonnull final Media media,
			@Nonnull final Item item,
			@Nonnull final Path body) throws IOException
	{
		if(media.isNull(item))
			return;

		final Path temp = readTemp(media, item);
		try(var inflater = new GZIPInputStream(Files.newInputStream(temp));
			 var out = Files.newOutputStream(body))
		{
			inflater.transferTo(out);
		}
		Files.delete(temp);
	}

	private static Path readTemp( // TODO avoid temporary file
			@Nonnull final Media media,
			@Nonnull final Item item) throws IOException
	{
		final Path temp = Files.createTempFile(
				GzipCompressor.class.getName() + '#' + media.getID() + '#' + item.getCopeID(), "");
		media.getBody().get(item, temp);
		return temp;
	}

	boolean acceptsEncoding(final String header)
	{
		return acceptsEncodingPattern.matcher(header).matches();
	}

	private static final Pattern acceptsEncodingPattern = Pattern.compile(
			".*\\b(?:x-)?gzip\\b.*",
			Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("FieldMayBeStatic") // OK: prepares other compressors
	final String contentEncoding = "gzip";

	byte[] uncompress(final byte[] compressed)
	{
		try(var baos = new ByteArrayInputStream(compressed);
			 var gz = new GZIPInputStream(baos))
		{
			return gz.readAllBytes();
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	DataField.Value compress(final DataField.Value value)
	{
		final var baos = new ByteArrayOutputStream();
		try(var gzip = new GZIPOutputStream(baos))
		{
			value.update(new MD(gzip));
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		return DataField.toValue(baos.toByteArray());
	}

	private static final class MD extends MessageDigest
	{
		private final GZIPOutputStream out;

		private MD(final GZIPOutputStream out)
		{
			super(null);
			this.out = out;
		}

		@Override
		public void update(final byte[] input, final int offset, final int len)
		{
			try
			{
				out.write(input, offset, len);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void engineUpdate(final byte input)
		{
			throw new RuntimeException();
		}

		@Override
		protected void engineUpdate(final byte[] input, final int offset, final int len)
		{
			throw new RuntimeException();
		}

		@Override
		public void update(final byte[] input)
		{
			throw new RuntimeException();
		}

		@Override
		protected byte[] engineDigest()
		{
			throw new RuntimeException();
		}

		@Override
		protected void engineReset()
		{
			throw new RuntimeException();
		}
	}

	public Condition bodyMatchesContentType(final DataField body)
	{
		return body.startsWithIfSupported(new byte[]{
				31, (byte)139, // Magic number
				8 // Compression method = Deflate
		});
	}

	static final GzipCompressor INSTANCE = new GzipCompressor();

	private GzipCompressor()
	{
	}
}
