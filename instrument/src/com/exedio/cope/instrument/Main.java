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

import static com.exedio.cope.util.StrictFile.delete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;

final class Main
{
	final void run(final ArrayList<File> files, final Params params) throws HumanReadableException, ParserException, IOException
	{
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");
		try
		{
			InstrumentContext.enter();

			final Charset charset = params.charset;
			final JavaRepository repository = new JavaRepository();
			final ArrayList<Parser> parsers = new ArrayList<>(files.size());

			this.verbose = params.verbose;
			instrumented = 0;
			skipped = 0;
			for(final File file : files)
			{
				if(!file.exists())
					throw new RuntimeException("error: input file " + file.getAbsolutePath() + " does not exist.");
				if(!file.isFile())
					throw new RuntimeException("error: input file " + file.getAbsolutePath() + " is not a regular file.");

				final JavaFile javaFile = new JavaFile(repository);
				final Parser parser = new Parser(new Lexer(file, charset, javaFile), new Instrumentor(), javaFile);
				parser.parseFile();
				parsers.add(parser);
			}

			repository.endBuildStage();

			for(final Parser parser : parsers)
			{
				final JavaFile javaFile = parser.javaFile;
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final CopeType type = CopeType.getCopeType(javaClass);
					if(type!=null)
					{
						if(!type.isInterface())
						{
							//System.out.println("onClassEnd("+jc.getName()+") writing");
							for(final CopeFeature feature : type.getFeatures())
								feature.getInstance();
						}
					}
				}
			}

			final Iterator<Parser> parsersIter = parsers.iterator();
			for(final File file : files)
			{
				final Parser parser = parsersIter.next();

				final StringBuilder baos = new StringBuilder((int)file.length() + 100);
				final Generator generator = new Generator(parser.javaFile, baos, params);
				generator.write();

				if(!parser.lexer.inputEqual(baos))
				{
					if(params.verify)
						throw new HumanReadableException("Not yet instrumented " + file.getAbsolutePath());
					logInstrumented(file);
					delete(file);
					final CharsetEncoder decoder = charset.newEncoder();
					final ByteBuffer out = decoder.encode(CharBuffer.wrap(baos));
					try(FileOutputStream o = new FileOutputStream(file))
					{
						o.getChannel().write(out);
					}
				}
				else
				{
					logSkipped(file);
				}
			}
		}
		finally
		{
			InstrumentContext.leave();
		}

		if(verbose || instrumented>0)
			System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + files.iterator().next().getParentFile().getAbsolutePath());
	}

	boolean verbose;
	int skipped;
	int instrumented;

	private void logSkipped(final File file)
	{
		if(verbose)
			System.out.println("Skipped " + file);

		skipped++;
	}

	private void logInstrumented(final File file)
	{
		if(verbose)
			System.out.println("Instrumented " + file);

		instrumented++;
	}

}
