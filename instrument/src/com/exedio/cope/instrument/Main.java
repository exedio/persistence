/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.SafeFile.delete;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.exedio.cope.Cope;

public final class Main
{

	public static void main(final String[] args)
	{
		try
		{
			(new Main()).run(new File("."), args, new Params());
		}
		catch(final RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch(final IllegalParameterException e)
		{
			e.printStackTrace();
			throw new RuntimeException(Arrays.toString(args), e);
		}
		catch(final IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(Arrays.toString(args), e);
		}
	}

	Main()
	{/* do not allow instantiation by public */}

	final void run(final File dir, final String[] args, final Params params) throws IllegalParameterException, InjectorParseException, IOException
	{
		final ArrayList<File> files = new ArrayList<File>();

		for(final String arg : args)
			files.add(new File(dir, arg));

		run(files, params);
	}

	final void run(final ArrayList<File> files, final Params params) throws IllegalParameterException, InjectorParseException, IOException
	{
		{
			final Package runtimePackage = Cope.class.getPackage();
			final Package instrumentorPackage = Main.class.getPackage();
			final String runtimeVersion = runtimePackage.getImplementationVersion();
			final String instrumentorVersion = instrumentorPackage.getImplementationVersion();
			if(verbose)
			{
				System.out.println("Instrumentor version: "+instrumentorVersion);
				System.out.println("Runtime version: "+runtimeVersion);
			}
			if(runtimeVersion!=null && instrumentorVersion!=null && !runtimeVersion.equals(instrumentorVersion))
				throw new RuntimeException("version of cope runtime library ("+runtimeVersion+") does dot match version of cope instrumentor: "+instrumentorVersion);
		}

		if(files.isEmpty())
			throw new IllegalParameterException("nothing to do.");

		final JavaRepository repository = new JavaRepository();
		final ArrayList<Injector> injectors = new ArrayList<Injector>(files.size());

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
			final Injector injector = new Injector(file, new Instrumentor(), javaFile);
			injector.parseFile();
			injectors.add(injector);
		}

		repository.endBuildStage();

		for(final Injector injector : injectors)
		{
			final JavaFile javaFile = injector.javaFile;
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

		final Iterator<Injector> injectorsIter = injectors.iterator();
		for(final File file : files)
		{
			final Injector injector = injectorsIter.next();

			final StringWriter baos = new StringWriter((int)file.length() + 100);
			final Generator generator = new Generator(injector.javaFile, baos, params);
			generator.write();
			generator.close();

			if(!equal(injector.input, baos))
			{
				logInstrumented(file);
				delete(file);
				final Charset charset = Charset.defaultCharset(); // TODO make configurable
				final CharsetEncoder decoder = charset.newEncoder();
				final ByteBuffer out = decoder.encode(CharBuffer.wrap(toCharBuffer(baos.getBuffer())));
				final FileOutputStream o = new FileOutputStream(file);
				try
				{
					o.getChannel().write(out);
				}
				finally
				{
					o.close();
				}
			}
			else
			{
				logSkipped(file);
			}
		}

		if(verbose || instrumented>0)
			System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + files.iterator().next().getParentFile().getAbsolutePath());
	}

	private static boolean equal(final char[] a, final StringWriter b)
	{
		final StringBuffer bf = b.getBuffer();
		if(a.length!=bf.length())
			return false;

		for(int i = 0; i<a.length; i++)
			if(a[i]!=bf.charAt(i))
				return false;

		return true;
	}

	private static CharBuffer toCharBuffer(final StringBuffer bf)
	{
		final int length = bf.length();
		final char[] chars = new char[length];
		bf.getChars(0, length, chars, 0);
		return CharBuffer.wrap(chars);
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
