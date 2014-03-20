/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.StrictFile.delete;
import static java.io.File.createTempFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;

public final class FileFixture
{
	private final ArrayList<File> files = new ArrayList<>();

	public void setUp()
	{
		files.clear();
	}

	protected void tearDown()
	{
		for(final File file : files)
			delete(file);
		files.clear();
	}

	protected final File file(final byte[] data)
	{
		final File result;
		try
		{
			result = createTempFile(AbstractRuntimeTest.class.getName(), ".tmp");
			try(FileOutputStream s = new FileOutputStream(result))
			{
				s.write(data);
			}
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		files.add(result);
		return result;
	}

	protected final File deleteOnTearDown(final File file)
	{
		Assert.assertNotNull(file);
		files.add(file);
		return file;
	}
}
