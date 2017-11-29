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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceProperties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProperties(VaultFileService.Props.class)
public final class VaultFileService implements VaultService
{
	private final Path rootDir;
	final int directoryLength;
	final Path tempDir;

	VaultFileService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		this.rootDir = properties.root;
		this.directoryLength = properties.directory!=null ? properties.directory.length : 0;
		this.tempDir = rootDir.resolve(properties.temp);

		{
			final int algorithmLength = parameters.getVaultProperties().getAlgorithmLength();
			if(directoryLength>=algorithmLength)
				throw new IllegalArgumentException(
						"directory.length must be less the length of algorithm, " +
						"but was " + directoryLength + ">=" + algorithmLength);
		}
	}


	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		final Path file = file(hash);
		try
		{
			return Files.size(file);
		}
		catch(final NoSuchFileException e)
		{
			throw new VaultNotFoundException(hash, e);
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		final Path file = file(hash);
		try
		{
			return Files.readAllBytes(file);
		}
		catch(final NoSuchFileException e)
		{
			throw new VaultNotFoundException(hash, e);
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	@Override
	public void get(final String hash, final OutputStream value) throws VaultNotFoundException, IOException
	{
		final Path file = file(hash);

		try
		{
			Files.copy(file, value);
		}
		catch(final NoSuchFileException e)
		{
			throw new VaultNotFoundException(hash, e);
		}
	}


	@Override
	public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
	{
		try
		{
			return put(hash, (out) -> Files.write(out, value, TRUNCATE_EXISTING));
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	@Override
	public boolean put(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		return put(hash, (out) -> Files.copy(value, out, REPLACE_EXISTING));
	}

	@Override
	public boolean put(final String hash, final File value, final VaultPutInfo info) throws IOException
	{
		return put(hash, (out) -> Files.copy(value.toPath(), out, REPLACE_EXISTING));
	}

	private boolean put(final String hash, final Consumer value) throws IOException
	{
		final Path file = file(hash);
		if(Files.exists(file))
			return false;

		final Path temp = createTempFile(hash);

		value.accept(temp);

		if(directoryLength>0)
			mkdirIfNotExists(rootDir.resolve(hash.substring(0, directoryLength)));

		renameToIfDestFileDoesNotExist(temp, file);
		return true;
	}

	private static void mkdirIfNotExists(final Path file) throws IOException
	{
		try
		{
			Files.createDirectory(file);
		}
		catch(final FileAlreadyExistsException ignored)
		{
			// ok
		}
	}

	private static void renameToIfDestFileDoesNotExist(final Path file, final Path dest) throws IOException
	{
		try
		{
			Files.move(file, dest, ATOMIC_MOVE);
		}
		catch(final FileAlreadyExistsException e)
		{
			logger.error("concurrent upload (should happen rarely)", e); // may be just warn
		}
	}

	@FunctionalInterface
	private interface Consumer
	{
		void accept(Path t) throws IOException;
	}


	private Path file(final String hash)
	{
		if(hash==null)
			throw new NullPointerException();
		if(hash.isEmpty())
			throw new IllegalArgumentException();

		if(directoryLength==0)
			return rootDir.resolve(hash);

		return rootDir.resolve(
				hash.substring(0, directoryLength) + '/' +
				hash.substring(directoryLength));
	}

	private Path createTempFile(final String hash) throws IOException
	{
		return Files.createTempFile(tempDir, anonymiseHash(hash), ".tmp");
	}

	private RuntimeException wrap(final String hash, final IOException exception)
	{
		throw new RuntimeException("" + rootDir.toAbsolutePath() + ':' + anonymiseHash(hash), exception);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + rootDir.toAbsolutePath();
	}


	static final class Props extends Properties
	{
		final Path root = valueFile("root").toPath();
		final DirectoryProps directory = value("directory", true, DirectoryProps::new);
		final String temp = value("temp", ".tempVaultFileService");

		Props(final Source source)
		{
			super(source);

			if(temp.isEmpty())
				throw newException("temp", "must not be empty");
			if(!temp.equals(temp.trim()))
				throw newException("temp", "must be trimmed, but was >" + temp + '<');
		}
	}


	static final class DirectoryProps extends Properties
	{
		/**
		 * This field is similar to directive {@code CacheDirLength} of Apache mod_cache_disk,
		 * however a value of {@code 2} in mod_cache_disk is equivalent to a value of {@code 3} here,
		 * as mod_cache_disk uses Base64 for encoding hashes and we use hexadecimal representation.
		 *
		 * See http://httpd.apache.org/docs/2.4/mod/mod_cache_disk.html#cachedirlength
		 */
		final int length = value("length", 3, 1);

		// TODO implement levels equivalent to CacheDirLevels, default to 1

		DirectoryProps(final Source source)
		{
			super(source);
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(VaultFileService.class);
}
