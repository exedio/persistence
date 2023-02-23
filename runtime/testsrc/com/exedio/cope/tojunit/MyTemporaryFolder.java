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

package com.exedio.cope.tojunit;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.util.StrictFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.rules.TemporaryFolder;

public final class MyTemporaryFolder extends TemporaryFolder
{
	public Path newPath(final Class<?> clazz, final String name) throws IOException
	{
		final Path result = newFile().toPath();
		try(InputStream resource = clazz.getResourceAsStream(name))
		{
			Files.copy(resource, result, REPLACE_EXISTING);
		}
		return result;
	}

	public Path newPath(final byte[] bytes) throws IOException
	{
		return newFile(bytes).toPath();
	}

	public File newFile(final byte[] bytes) throws IOException
	{
		final File result = newFile();
		try(FileOutputStream stream = new FileOutputStream(result))
		{
			stream.write(bytes);
		}
		return result;
	}

	public Path newPathNotExists() throws IOException
	{
		return newFileNotExists().toPath();
	}

	public File newFileNotExists() throws IOException
	{
		final File result = newFile();
		StrictFile.delete(result);
		assertFalse(result.exists());
		return result;
	}
}
