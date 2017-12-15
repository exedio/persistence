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

package com.exedio.cope.instrument;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.tools.SimpleJavaFileObject;

final class InterimFile extends SimpleJavaFileObject
{
	private static final URI toURI(final Path path)
	{
		try
		{
			return new URI("memory:/"+path.toString().replace(File.separatorChar, '/'));
		}
		catch (final URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final Path sourcePath;
	private final String sourceChars;

	InterimFile(final Path sourcePath, final String sourceChars)
	{
		super(toURI(sourcePath), Kind.SOURCE);
		this.sourcePath = sourcePath;
		this.sourceChars = sourceChars;
	}

	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException
	{
		return sourceChars;
	}

	void dump(final Params params, final Path targetDirectory) throws IOException
	{
		final Path absolute = targetDirectory.resolve(sourcePath);
		Files.createDirectories(absolute.getParent());
		try (final Writer w = new OutputStreamWriter(Files.newOutputStream(absolute), params.charset))
		{
			w.write(sourceChars);
		}
	}
}
