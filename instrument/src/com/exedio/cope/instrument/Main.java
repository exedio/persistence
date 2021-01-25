/*
 * Copyright (C) 2000  Ralf Wiebicke
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

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.List;

final class Main
{
	void run(final Params params) throws HumanReadableException, IOException
	{
		final List<File> files = params.getJavaSourceFilesExcludingIgnored();
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");
		if ( noFilesModifiedAfter(files, params.getTimestampFile(), params.verbose)
			&& noFilesModifiedAfter(params.resources, params.getTimestampFile(), params.verbose)
			&& noFilesModifiedAfter(params.classpath, params.getTimestampFile(), params.verbose) )
		{
			System.out.println("No files or resources modified.");
			return;
		}

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");

		runJavac(params);
	}

	private static boolean noFilesModifiedAfter(final Iterable<File> checkFiles, final File referenceFile, final boolean verbose)
	{
		if ( !referenceFile.exists() )
		{
			if ( verbose )
			{
				System.out.println("No timestamp file, instrumentation required.");
			}
			return false;
		}
		else
		{
			final long referenceLastModified = referenceFile.lastModified();
			for (final File file: checkFiles)
			{
				if ( file.lastModified()>=referenceLastModified )
				{
					if ( verbose )
					{
						System.out.println("File "+file+" changed after timestamp file, instrumentation required.");
					}
					return false;
				}
				if ( file.isDirectory() )
				{
					//noinspection ConstantConditions OK: checks isDirectory before calling listFiles
					if ( !noFilesModifiedAfter(asList(file.listFiles()), referenceFile, verbose) )
					{
						return false;
					}
				}
			}
			return true;
		}
	}

	private static void runJavac(final Params params) throws IOException, HumanReadableException
	{
		final JavaRepository repository = new JavaRepository();
		final InterimProcessor interimProcessor = new InterimProcessor(params);
		final FillRepositoryProcessor fillRepositoryProcessor = new FillRepositoryProcessor(repository, interimProcessor);
		final InstrumentorWriteProcessor writeProcessor = new InstrumentorWriteProcessor(params, repository, interimProcessor);
		new JavacRunner(interimProcessor, fillRepositoryProcessor, writeProcessor).run(params);
	}
}
