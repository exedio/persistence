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

package com.exedio.filevault;

import com.exedio.cope.util.CharSet;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

@SuppressWarnings("AbstractClassExtendsConcreteClass")
abstract class VaultFileServiceVisitor extends SimpleFileVisitor<Path>
{
	private final ArrayList<String> previousHashes = new ArrayList<>();
	private final PrintStream err;
	private String directoryHashes = null;

	VaultFileServiceVisitor(final PrintStream err)
	{
		this.err = err;
	}

	@Override
	public final FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
	{
		final String filename = dir.getFileName().toString();
		if(directoryHashes==null)
		{
			directoryHashes = "";
			return FileVisitResult.CONTINUE;
		}
		if(CharSet.HEX_LOWER.indexOfNotContains(filename)>=0)
		{
			err.println("Skipping non-hex directory " + dir.toAbsolutePath());
			return FileVisitResult.SKIP_SUBTREE;
		}
		final String newHash = directoryHashes + filename;
		previousHashes.add(directoryHashes);
		directoryHashes = newHash;
		return FileVisitResult.CONTINUE;
	}

	@Override
	public final FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
	{
		super.postVisitDirectory(dir, exc); // fails if exc!=null
		directoryHashes = previousHashes.isEmpty() ? null : previousHashes.remove(previousHashes.size()-1);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public final FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
			throws IOException
	{
		if(!Files.isRegularFile(file)) // TODO junit test
		{
			err.println("Skipping non-regular file " + file.toAbsolutePath());
			return FileVisitResult.CONTINUE;
		}

		final String filename = file.getFileName().toString();
		if(CharSet.HEX_LOWER.indexOfNotContains(filename)>=0)
		{
			err.println("Skipping non-hex file " + file.toAbsolutePath());
			return FileVisitResult.CONTINUE;
		}

		final long length = Files.size(file);
		if(length==0)
		{
			err.println("Skipping empty file " + file.toAbsolutePath());
			return FileVisitResult.CONTINUE;
		}

		onFile(directoryHashes + filename, file, length);
		return FileVisitResult.CONTINUE;
	}

	abstract void onFile(final String hash, final Path file, final long length) throws IOException;
}
