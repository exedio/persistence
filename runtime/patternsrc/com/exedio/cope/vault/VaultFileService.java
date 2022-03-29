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
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

import com.exedio.cope.util.ServiceProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProperties(VaultFileService.Props.class)
public final class VaultFileService implements VaultService
{
	private final Path rootDir;
	private final Set<PosixFilePermission> filePermissions;
	private final FileAttribute<?>[] fileAttributes;
	final Set<PosixFilePermission> filePermissionsAfterwards;
	final VaultDirectory directory;
	private final FileAttribute<?>[] directoryAttributes;
	final Set<PosixFilePermission> directoryPermissionsAfterwards;
	final Path tempDir;

	VaultFileService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		final boolean writable = parameters.isWritable();
		this.rootDir = properties.root;
		this.filePermissions = writable ? properties.filePosixPermissions : null;
		this.fileAttributes = writable ? asFileAttributes(properties.filePosixPermissions) : null;
		this.filePermissionsAfterwards = writable ? properties.filePosixPermissionsAfterwards : null;
		this.directory = VaultDirectory.instance(properties.directory, parameters);
		this.directoryAttributes = properties.directory!=null&&writable ? asFileAttributes(properties.directory.posixPermissions) : null;
		this.directoryPermissionsAfterwards = properties.directory!=null&&writable ? properties.directory.posixPermissionsAfterwards : null;
		this.tempDir = writable ? properties.tempDir() : null;
	}

	@SuppressWarnings("ZeroLengthArrayAllocation") // OK: just for Windows
	private static FileAttribute<?>[] asFileAttributes(final Set<PosixFilePermission> posixFilePermissions)
	{
		if(posixFilePermissions==null)
			return null;

		return
				System.getProperty("os.name").toLowerCase().contains("windows")
				? new FileAttribute<?>[] { }
				: new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixFilePermissions) };
	}

	// TODO implement purgeSchema, delete old files in tempDir if VaultServiceParameters#isWritable()==false


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
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		final Path file = file(hash);

		try
		{
			Files.copy(file, sink);
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
		return put(hash, (out) ->
		{
			// The following copy loop is almost equivalent to
			//    Files.copy(value, out, REPLACE_EXISTING);
			// But it does not delete the temporary file "out" before writing to it,
			// because that drops the posixPermissions applied to the temporary file
			// at creation time.
			try(OutputStream s = Files.newOutputStream(out, TRUNCATE_EXISTING))
			{
				final byte[] buffer = new byte[8192];
				int bytesRead;
				while((bytesRead = value.read(buffer)) > 0)
				{
					s.write(buffer, 0, bytesRead);
				}
			}
		});
	}

	@Override
	public boolean put(final String hash, final Path value, final VaultPutInfo info) throws IOException
	{
		return put(hash, (out) ->
		{
			Files.copy(value, out, REPLACE_EXISTING);

			// Permissions must be set afterwards, because REPLACE_EXISTING deletes
			// the temporary file "out" before writing to it, which drops the
			// posixPermissions applied to the temporary file at creation time.
			if(filePermissionsAfterwards==null) // setting filePermissions can be omitted when filePermissionsAfterwards would obliterate it
				setPermissions(out, filePermissions);
		});
	}

	private boolean put(final String hash, final Consumer value) throws IOException
	{
		final Path file = file(hash);
		if(Files.exists(file))
			return false;

		final Path temp = createTempFile(hash);

		value.accept(temp);

		setPermissions(temp, filePermissionsAfterwards);

		final String dir = directory.directoryToBeCreated(hash);
		if(dir!=null)
			createDirectoryIfNotExists(rootDir.resolve(dir));

		return moveIfDestDoesNotExist(temp, file);
	}

	private void createDirectoryIfNotExists(final Path file) throws IOException
	{
		try
		{
			Files.createDirectory(file, directoryAttributes);
		}
		catch(final FileAlreadyExistsException ignored)
		{
			return; // ok
		}
		setPermissions(file, directoryPermissionsAfterwards);
	}

	private static void setPermissions(
			final Path file,
			final Set<PosixFilePermission> permissions)
			throws IOException
	{
		if(permissions!=null)
			Files.getFileAttributeView(file, PosixFileAttributeView.class).
					setPermissions(permissions);
	}

	private static boolean moveIfDestDoesNotExist(final Path file, final Path dest) throws IOException
	{
		try
		{
			Files.move(file, dest, ATOMIC_MOVE);
		}
		catch(final FileAlreadyExistsException e)
		{
			logger.error("concurrent upload (should happen rarely)", e); // may be just warn
			return false;
		}
		return true;
	}

	@FunctionalInterface
	private interface Consumer
	{
		void accept(Path t) throws IOException;
	}


	private Path file(final String hash)
	{
		return rootDir.resolve(directory.path(hash));
	}

	private Path createTempFile(final String hash) throws IOException
	{
		return Files.createTempFile(tempDir, anonymiseHash(hash), ".tmp", fileAttributes);
	}

	private RuntimeException wrap(final String hash, final IOException exception)
	{
		throw new RuntimeException("" + rootDir.toAbsolutePath() + ':' + anonymiseHash(hash), exception);
	}


	@Override
	// Method signature shall NOT narrow down specification from VaultService to
	//   Path probeGenuineServiceKey(String serviceKey) throws IOException
	// so we are free to change signature in the future without breaking API compatibility.
	public Object probeGenuineServiceKey(final String serviceKey) throws Exception
	{
		final Path file = rootDir.resolve(VAULT_GENUINE_SERVICE_KEY).resolve(serviceKey);
		final BasicFileAttributes attributes =
				Files.readAttributes(file, BasicFileAttributes.class); // throw NoSuchFileException is file does not exist
		final Path fileAbsolute = file.toAbsolutePath();

		if(!attributes.isRegularFile())
			throw new IllegalStateException(
					"is not a regular file: " + fileAbsolute);

		final long size = attributes.size();
		if(size!=0) // file must not have any content, because it is likely exposed to public
			throw new IllegalStateException(
					"is not empty, but has size " + size + ": " + fileAbsolute);

		return fileAbsolute;
	}


	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + rootDir.toAbsolutePath();
	}


	static final class Props extends PosixProperties
	{
		final Path root = valuePath("root");
		final boolean writable = value("writable", true);

		/**
		 * New files added to the vault will be created with the permissions
		 * specified by this property.
		 * <p>
		 * Note, that actual results are affected by {@code umask},
		 * see https://en.wikipedia.org/wiki/Umask#Mask_effect .
		 */
		final Set<PosixFilePermission> filePosixPermissions = writable ? value("posixPermissions", EnumSet.of(OWNER_READ, OWNER_WRITE)) : null;

		/**
		 * If set, new files added to the vault will be {@code chmod}ed
		 * to the permissions specified by this property immediately after
		 * creation.
		 * <p>
		 * In contrast to {@link #filePosixPermissions} permissions set here
		 * <ul>
		 * <li>are not affected by {@code umask} and
		 * <li>may strip the {@link PosixFilePermission#OWNER_WRITE user=write} permission from files.
		 * </ul>
		 * If this property is not set, permissions won't be changed at all
		 * after creation of the file and effects of {@link #filePosixPermissions}
		 * are not overwritten.
		 */
		final Set<PosixFilePermission> filePosixPermissionsAfterwards = writable ? value("posixPermissionsAfterwards", (Set<PosixFilePermission>)null) : null;

		final VaultDirectory.Properties directory = value("directory", true, s -> new VaultDirectory.Properties(s, writable));
		private final String temp = writable ? value("temp", ".tempVaultFileService") : null;

		Props(final Source source)
		{
			super(source);

			if(temp!=null)
			{
				if(temp.isEmpty())
					throw newException("temp", "must not be empty");
				if(!temp.equals(temp.trim()))
					throw newException("temp", "must be trimmed, but was >" + temp + '<');
			}
		}

		Path tempDir()
		{
			if(temp==null)
				throw new IllegalArgumentException(
						"non-writable properties cannot be used in writable service");

			return root.resolve(temp);
		}

		@Probe(name="root.Exists")
		private Path probeRootExists()
		{
			return probeDirectoryExists(root);
		}

		@Probe(name="root.Free")
		private String probeRootFree() throws IOException
		{
			final FileStore store = Files.getFileStore(root);
			final long total = store.getTotalSpace();
			return
					(store.getUsableSpace()*100/total) + "% of " +
					(total/(1024*1024*1024)) + "GiB";
		}

		@Probe(name="temp.Exists")
		private Path probeTempExists() throws ProbeAbortedException
		{
			return probeDirectoryExists(tempDirForProbe());
		}

		private Path tempDirForProbe() throws ProbeAbortedException
		{
			if(temp==null)
				throw newProbeAbortedException("not writable");

			return tempDir();
		}

		private static Path probeDirectoryExists(final Path directory)
		{
			final Path absolute = directory.toAbsolutePath();
			if(!Files.exists(directory))
				throw new IllegalArgumentException("does not exist: " + absolute);
			if(!Files.isDirectory(directory))
				throw new IllegalArgumentException("is not a directory: " + absolute);
			if(!Files.isReadable(directory))
				throw new IllegalArgumentException("is not readable: " + absolute); // TODO test
			if(!Files.isWritable(directory))
				throw new IllegalArgumentException("is not writable: " + absolute); // TODO test
			return absolute;
		}

		@Probe(name="temp.Store")
		private FileStore probeTempStore() throws ProbeAbortedException, IOException
		{
			final Path tempDir = tempDirForProbe();
			final FileStore rootStore = Files.getFileStore(root);
			final FileStore tempStore = Files.getFileStore(tempDir);
			if(!rootStore.equals(tempStore))
				throw new IllegalArgumentException( // TODO test
						"not the same file store: " +
						"root " + root   .toAbsolutePath() + " on " + rootStore + ", but " +
						"temp " + tempDir.toAbsolutePath() + " on " + tempStore);
			return rootStore;
		}

		@Probe(name="directory.Premised")
		private Object probeDirectoryPremised() throws ProbeAbortedException
		{
			if(directory==null)
				throw newProbeAbortedException("directories disabled");

			int missingTotal = 0;
			final int MISSING_LIMIT = 15;
			final StringBuilder missing = new StringBuilder();
			int ok = 0;
			for(final Iterator<String> i = directory.iterator(); i.hasNext(); )
			{
				final String dirName = i.next();
				if(Files.isDirectory(root.resolve(dirName)))
					ok++;
				else
				{
					if((missingTotal++)<MISSING_LIMIT)
					{
						if(missingTotal==1)
							missing.append("missing ");
						else
							missing.append(',');

						missing.append(dirName);
					}
				}
			}
			if(missingTotal==0)
				return ok;

			if(missingTotal>MISSING_LIMIT)
				missing.
						append("... (total ").
						append(missingTotal).
						append(')');

			if(directory.premised)
				throw new IllegalStateException(missing.toString());
			else
				return missing.toString();
		}
	}


	// the methods below are just for junit tests
	Stream<FileAttribute<?>> fileAttributes()
	{
		return attributes(fileAttributes);
	}
	Stream<FileAttribute<?>> directoryAttributes()
	{
		return attributes(directoryAttributes);
	}
	private static Stream<FileAttribute<?>> attributes(final FileAttribute<?>[] attributes)
	{
		return attributes!=null ? Stream.of(attributes) : null;
	}


	private static final Logger logger = LoggerFactory.getLogger(VaultFileService.class);
}
