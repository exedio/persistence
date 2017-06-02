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

import static java.lang.Math.toIntExact;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.StrictFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO:
 * For production use with large numbers of files we need a cache dir hierarchy similar to Apache mod_cache_disk:
 * http://httpd.apache.org/docs/2.4/mod/mod_cache_disk.html#cachedirlength
 */
@VaultServiceProperties(VaultFileService.Factory.class)
public final class VaultFileService implements VaultService
{
	private final File rootDir;
	final File tempDir;
	final int bufferSize;

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	public VaultFileService(final VaultServiceParameters parameters)
	{
		final Props sp = (Props)parameters.getServiceProperties();
		this.rootDir = sp.root.get();
		this.tempDir = new File(rootDir, sp.temp);
		this.bufferSize = sp.bufferSize;
	}


	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		final long result = file(hash).length();
		if(result==0)
			throw new VaultNotFoundException(hash);
		return result;
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		final File file = file(hash);
		final long length = file.length();
		if(length==0)
			throw new VaultNotFoundException(hash);

		final byte[] result = new byte[toIntExact(length)];

		try(FileInputStream in = new FileInputStream(file))
		{
			int offset = 0;
			for(int len = in.read(result); len>=0; len = in.read(result, offset, result.length-offset))
			{
				offset += len;
				if(offset==result.length)
					break;

				if(offset>result.length)
					throw new RuntimeException("overflow " + offset + '/' + result.length + '/' + rootDir.getAbsolutePath() + '/' + hash);
			}
			if(offset!=result.length)
				throw new RuntimeException("mismatch " + offset + '/' + result.length + '/' + rootDir.getAbsolutePath() + '/' + hash);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(rootDir.getAbsolutePath() + ':' + hash, e);
		}
		return result;
	}

	@Override
	public void get(final String hash, final OutputStream value) throws VaultNotFoundException, IOException
	{
		final File file = file(hash);

		try(FileInputStream in = new FileInputStream(file))
		{
			final byte[] buf = new byte[bufferSize];
			for(int len = in.read(buf); len>=0; len = in.read(buf))
				value.write(buf, 0, len);
		}
		catch(final FileNotFoundException e)
		{
			throw new VaultNotFoundException(hash, e);
		}
	}


	@Override
	public boolean put(final String hash, final byte[] value)
	{
		try
		{
			return put(hash, (out) -> out.write(value));
		}
		catch(final IOException e)
		{
			throw new RuntimeException(rootDir.getAbsolutePath() + ':' + hash, e);
		}
	}

	@Override
	public boolean put(final String hash, final InputStream value) throws IOException
	{
		return put(hash, (out) ->
		{
			final byte[] buf = new byte[bufferSize];
			for(int len = value.read(buf); len>=0; len = value.read(buf))
				out.write(buf, 0, len);
		});
	}

	@Override
	public boolean put(final String hash, final File value) throws IOException
	{
		return put(hash, (out) ->
		{
			try(FileInputStream in = new FileInputStream(value))
			{
				final byte[] buf = new byte[bufferSize];
				for(int len = in.read(buf); len>=0; len = in.read(buf))
					out.write(buf, 0, len);
			}
		});
	}

	private boolean put(final String hash, final Consumer value) throws IOException
	{
		final File file = file(hash);
		if(file.exists())
			return false;

		final File temp = createTempFile(hash);

		try(FileOutputStream out = new FileOutputStream(temp))
		{
			value.accept(out);
		}

		StrictFile.renameTo(temp, file);
		return true;
	}

	@FunctionalInterface
	private interface Consumer
	{
		void accept(FileOutputStream t) throws IOException;
	}


	private File file(final String hash)
	{
		if(hash==null)
			throw new NullPointerException();
		if(hash.isEmpty())
			throw new IllegalArgumentException();

		return new File(rootDir, hash);
	}

	private File createTempFile(final String hash) throws IOException
	{
		return File.createTempFile(hash, ".tmp", tempDir);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + rootDir.getAbsolutePath();
	}


	static final class Props extends Properties
	{
		final FileField root = fieldFile("root");
		final String temp = value("temp", ".tempVaultFileService");
		final int bufferSize = value("bufferSize", 50*1024, 1); // 50K

		Props(final Source source)
		{
			super(source);
		}
	}

	public static final class Factory implements Properties.Factory<Props>
	{
		@Override
		public Props create(final Properties.Source source)
		{
			return new Props(source);
		}
	}
}
