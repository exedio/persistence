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
import static java.nio.file.attribute.FileTime.fromMillis;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.util.Arrays.asList;

import com.exedio.cope.misc.Holder;
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
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.Clock;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceProperties(VaultFileService.Props.class)
public final class VaultFileService implements VaultService
{
	private final Path rootDir;
	private final Path contentDir;
	private final Set<PosixFilePermission> filePermissions;
	private final FileAttribute<?>[] fileAttributes;
	final Set<PosixFilePermission> filePermissionsAfterwards;
	final String fileGroup;
	final VaultDirectory directory;
	private final FileAttribute<?>[] directoryAttributes;
	final Set<PosixFilePermission> directoryPermissionsAfterwards;
	final String directoryGroup;
	final Path tempDir;
	final BooleanSupplier requiresToMarkPut;

	VaultFileService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		final boolean writable = parameters.isWritable();
		this.rootDir = properties.root;
		this.contentDir = properties.content;
		this.filePermissions = writable&&!isWindows() ? properties.filePosixPermissions : null;
		this.fileAttributes = writable ? asFileAttributes(properties.filePosixPermissions) : null;
		this.filePermissionsAfterwards = writable ? properties.filePosixPermissionsAfterwards : null;
		this.fileGroup = writable ? properties.filePosixGroup : null;
		this.directory = VaultDirectory.instance(properties.directory, parameters);
		this.directoryAttributes = properties.directory!=null&&writable ? asFileAttributes(properties.directory.posixPermissions) : null;
		this.directoryPermissionsAfterwards = properties.directory!=null&&writable ? properties.directory.posixPermissionsAfterwards : null;
		this.directoryGroup = properties.directory!=null&&writable ? properties.directory.posixGroup : null;
		this.tempDir = writable ? properties.tempDir() : null;
		this.requiresToMarkPut = writable ? parameters.requiresToMarkPut() : null;
	}

	@SuppressWarnings("ZeroLengthArrayAllocation") // OK: just for Windows
	private static FileAttribute<?>[] asFileAttributes(final Set<PosixFilePermission> posixFilePermissions)
	{
		if(posixFilePermissions==null)
			return null;

		return
				isWindows()
				? new FileAttribute<?>[] { }
				: new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixFilePermissions) };
	}

	private static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	// TODO implement purgeSchema:
	// delete old files in tempDir
	// * only if VaultServiceParameters#isWritable()==false
	// * preserve files such as README, NOTICE etc.
	// * preserve anything, that is not a regular file
	// * do not purge subdirectories


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
			// The following copy code is almost equivalent to
			//    Files.copy(value, out, REPLACE_EXISTING);
			// But it does not delete the temporary file "out" before writing to it,
			// because that drops the posixPermissions applied to the temporary file
			// at creation time.
			try(OutputStream s = Files.newOutputStream(out, TRUNCATE_EXISTING))
			{
				value.transferTo(s);
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
				setPermissions(out, filePermissions, "");
		});
	}

	private boolean put(final String hash, final Consumer value) throws IOException
	{
		final Path file = file(hash);
		if(Files.exists(file))
		{
			markRedundantPut(file);
			return false;
		}

		final Path temp = createTempFile(hash);

		value.accept(temp);

		setPermissions(temp, filePermissionsAfterwards, fileGroup);

		final String dir = directory.directoryToBeCreated(hash);
		if(dir!=null)
			createDirectoryIfNotExists(contentDir.resolve(dir));

		return moveIfDestDoesNotExist(temp, file);
	}

	private void markRedundantPut(final Path file) throws IOException
	{
		if(requiresToMarkPut.getAsBoolean())
			Files.setLastModifiedTime(file, fromMillis(markRedundantPutClock.get().millis()));
	}

	static final Holder<Clock> markRedundantPutClock = new Holder<>(Clock.systemUTC());

	private void createDirectoryIfNotExists(final Path dir) throws IOException
	{
		try
		{
			Files.createDirectory(dir, directoryAttributes);
		}
		catch(final FileAlreadyExistsException ignored)
		{
			return; // ok
		}
		// NOTE:
		// It is crucial for data consistency, that directoryPermissionsAfterwards
		// and directoryGroup are applied, even if the directory does exist already.
		// Otherwise, an interruption immediately after directory creation may
		// leave a directory without correct properties indefinitely.
		setPermissions(dir, directoryPermissionsAfterwards, directoryGroup);
	}

	private static void setPermissions(
			final Path file,
			final Set<PosixFilePermission> permissions,
			final String group)
			throws IOException
	{
		if(permissions!=null || !group.isEmpty())
		{
			final PosixFileAttributeView posixView =
					Files.getFileAttributeView(file, PosixFileAttributeView.class);
			if(permissions!=null)
				posixView.setPermissions(permissions);
			if(!group.isEmpty())
				posixView.setGroup(lookupGroup(file, group));
		}
	}

	private boolean moveIfDestDoesNotExist(final Path file, final Path dest) throws IOException
	{
		try
		{
			Files.move(file, dest, ATOMIC_MOVE);
		}
		catch(final FileAlreadyExistsException e)
		{
			logger.error("concurrent upload (should happen rarely)", e); // may be just warn
			markRedundantPut(file);
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
		return contentDir.resolve(directory.path(hash));
	}

	private Path createTempFile(final String hash) throws IOException
	{
		return Files.createTempFile(tempDir, anonymiseHash(hash), ".tmp", fileAttributes);
	}

	private RuntimeException wrap(final String hash, final IOException exception)
	{
		throw new RuntimeException(contentDir.toAbsolutePath() + ":" + anonymiseHash(hash), exception);
	}


	@Override
	// Method signature shall NOT narrow down specification from VaultService to
	//   Path probeGenuineServiceKey(String bucket) throws IOException
	// so we are free to change signature in the future without breaking API compatibility.
	public Object probeGenuineServiceKey(final String bucket) throws Exception
	{
		@SuppressWarnings("deprecation") // OK: support deprecated directory
		final Path oldDir = contentDir.resolve(VAULT_GENUINE_SERVICE_KEY);
		final Path newDir = contentDir.resolve(VAULT_BUCKET_TAG);
		return
				!Files.exists(newDir) && Files.exists(oldDir)
				? "deprecated: " +
				  probeBucketTag(bucket, oldDir)
				: probeBucketTag(bucket, newDir);
	}

	private static Path probeBucketTag(final String bucket, final Path tagDirectory) throws Exception
	{
		final Path file = tagDirectory.resolve(bucket);
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
		final Path content = valueSP("content", root, true, "");
		final boolean writable = value("writable", true);

		/**
		 * New files added to the vault will be created with the permissions
		 * specified by this property.
		 * <p>
		 * Note, that actual results are affected by {@code umask},
		 * see <a href="https://en.wikipedia.org/wiki/Umask#Mask_effect">Mask effect</a>.
		 */
		final Set<PosixFilePermission> filePosixPermissions = writable ? valuePP("posixPermissions", OWNER_READ, OWNER_WRITE) : null;

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
		final Set<PosixFilePermission> filePosixPermissionsAfterwards = writable ? valuePP("posixPermissionsAfterwards") : null;

		final String filePosixGroup = writable ? value("posixGroup", "") : null;

		final DirProps directory = value("directory", true, s -> new DirProps(s, writable, filePosixPermissions, filePosixGroup));
		private final Path temp = writable ? valueSP("temp", root, false, ".tempVaultFileService") : null;

		Props(final Source source)
		{
			super(source);
		}

		Path tempDir()
		{
			if(temp==null)
				throw new IllegalArgumentException(
						"non-writable properties cannot be used in writable service");

			return temp;
		}

		@Probe(name="root.Exists", order=110)
		private Path probeRootExists()
		{
			return probeDirectoryExists(root);
		}

		@Probe(name="root.Free", order=120)
		private String probeRootFree() throws IOException
		{
			final FileStore store = Files.getFileStore(root);
			final long total = store.getTotalSpace();
			return
					(store.getUsableSpace()*100/total) + "% of " +
					(total/(1024*1024*1024)) + "GiB";
		}

		@Probe(name="content.Exists", order=210)
		private Path probeContentExists()
		{
			return probeDirectoryExists(content);
		}

		@Probe(name="PosixGroup", order=310)
		private GroupPrincipal probeGroup() throws ProbeAbortedException, IOException
		{
			return probeGroup(filePosixGroup);
		}

		@Probe(name="directory.PosixGroup", order=420)
		private GroupPrincipal probeDirectoryGroup() throws ProbeAbortedException, IOException
		{
			return probeGroup(directoryForProbe().posixGroup);
		}

		private GroupPrincipal probeGroup(final String group) throws ProbeAbortedException, IOException
		{
			if(group==null)
				throw newProbeAbortedException("not writable");
			if(group.isEmpty())
				throw newProbeAbortedException("group disabled");

			return lookupGroup(root, group);
		}

		@Probe(name="temp.Exists", order=510)
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
				throw new IllegalArgumentException("is not writable: " + absolute);
			return absolute;
		}

		@Probe(name="temp.Store", order=520)
		private FileStore probeTempStore() throws ProbeAbortedException, IOException
		{
			final Path tempDir = tempDirForProbe();
			final FileStore contStore = Files.getFileStore(content);
			final FileStore tempStore = Files.getFileStore(tempDir);
			if(!contStore.equals(tempStore))
				throw new IllegalArgumentException( // TODO test
						"not the same file store: " +
						"root " + content.toAbsolutePath() + " on " + contStore + ", but " +
						"temp " + tempDir.toAbsolutePath() + " on " + tempStore);
			return contStore;
		}

		@Probe(name="directory.Premised", order=410)
		private Object probeDirectoryPremised() throws ProbeAbortedException
		{
			int missingTotal = 0;
			final int MISSING_LIMIT = 15;
			final StringBuilder missing = new StringBuilder();
			int ok = 0;
			for(final Iterator<String> i = directoryForProbe().iterator(); i.hasNext(); )
			{
				final String dirName = i.next();
				if(Files.isDirectory(content.resolve(dirName)))
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

		private DirProps directoryForProbe() throws ProbeAbortedException
		{
			if(directory==null)
				throw newProbeAbortedException("directories disabled");

			return directory;
		}
	}

	static GroupPrincipal lookupGroup(final Path path, final String name) throws IOException
	{
		final UserPrincipalLookupService service =
				path.getFileSystem().getUserPrincipalLookupService();
		try
		{
			return service.lookupPrincipalByGroupName(name);
		}
		catch(final UserPrincipalNotFoundException e)
		{
			// Puts group name into exception message. Otherwise, the exception
			// stacktrace gives no hint about the name of the group that was
			// looked up.
			throw new RuntimeException(e.getName(), e);
		}
	}

	static final class DirProps extends VaultDirectory.Properties
	{
		/**
		 * New directories added to the vault will be created with the permissions
		 * specified by this property.
		 * <p>
		 * Note, that actual results are affected by {@code umask},
		 * see <a href="https://en.wikipedia.org/wiki/Umask#Mask_effect">Mask effect</a>.
		 */
		final Set<PosixFilePermission> posixPermissions;

		/**
		 * If set, new directories added to the vault will be {@code chmod}ed
		 * to the permissions specified by this property immediately after
		 * creation.
		 * <p>
		 * In contrast to {@link #posixPermissions} permissions set here
		 * are not affected by {@code umask}.
		 * <p>
		 * If this property is not set, permissions won't be changed at all
		 * after creation of the directory and effects of {@link #posixPermissions}
		 * are not overwritten.
		 */
		final Set<PosixFilePermission> posixPermissionsAfterwards;

		final String posixGroup;


		@SuppressWarnings("AssignmentToSuperclassField") // OK: bug in idea, premised is not assigned but just read here
		DirProps(
				final Source source,
				final boolean writable,
				final Set<PosixFilePermission> filePosixPermissions,
				final String posixGroupDefault)
		{
			super(source, writable);
			final boolean writableReally = writable && !premised;
			posixPermissions = writableReally ? valuePP("posixPermissions", amend(filePosixPermissions, OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)) : null;
			posixPermissionsAfterwards = writableReally ? valuePP("posixPermissionsAfterwards") : null;
			posixGroup = writableReally ? value("posixGroup", posixGroupDefault) : null;
		}

		private static PosixFilePermission[] amend(
				final Set<PosixFilePermission> filePerms,
				final PosixFilePermission... own)
		{
			if(filePerms==null)
				return own;

			final EnumSet<PosixFilePermission> result = EnumSet.copyOf(asList(own));
			if(filePerms.contains( GROUP_READ)) result.add( GROUP_EXECUTE);
			if(filePerms.contains(OTHERS_READ)) result.add(OTHERS_EXECUTE);
			return result.toArray(EMPTY_FILE_PERMS);
		}

		private static final PosixFilePermission[] EMPTY_FILE_PERMS = new PosixFilePermission[0];
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
