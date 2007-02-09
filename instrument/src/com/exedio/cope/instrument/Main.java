/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.exedio.cope.Cope;

public final class Main
{
	
	public static void main(final String[] args)
	{
		try
		{
			(new Main()).run(new File("."), args, true, true);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch(IllegalParameterException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	Main()
	{/* do not allow instantiation by public */}
	
	private void printUsage(PrintStream o)
	{
		o.println("usage:");
		o.print("java ");
		o.print(getClass().getName());
		o.println(" tobemodified1.java ...");
	}
	
	final void run(final File dir, final String[] args, final boolean longJavadoc, final boolean verbose) throws IllegalParameterException, InjectorParseException, IOException
	{
		final ArrayList<File> files = new ArrayList<File>();
		
		for(int i=0; i<args.length; i++)
			files.add(new File(dir, args[i]));
		
		run(files, longJavadoc, verbose);
	}
		
	final void run(final ArrayList<File> files, final boolean longJavadoc, final boolean verbose) throws IllegalParameterException, InjectorParseException, IOException
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

		this.verbose = verbose;
		instrumented = 0;
		skipped = 0;
		for(final File file : files)
		{
			if(!file.exists())
				throw new RuntimeException("error: input file " + file.getAbsolutePath() + " does not exist.");
			if(!file.isFile())
				throw new RuntimeException("error: input file " + file.getAbsolutePath() + " is not a regular file.");
				
			final Injector injector = new Injector(file, new Instrumentor(), repository);
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
						{
							if(!(feature instanceof CopeQualifier)) // TODO make it work for all
								feature.getInstance();
						}
					}
				}
			}
		}
		
		final Iterator<Injector> injectorsIter = injectors.iterator();
		for(final File file : files)
		{
			final Injector injector = injectorsIter.next();
			
			final ByteArrayOutputStream baos = new ByteArrayOutputStream((int)file.length() + 100);
			final Generator generator = new Generator(injector.javaFile, baos, longJavadoc);
			generator.write();
			generator.close();
			
			if(injector.inputCRC!=generator.getCRC())
			{
				logInstrumented(file);
				if(!file.delete())
					throw new RuntimeException("deleting "+file+" failed.");
				final FileOutputStream o = new FileOutputStream(file);
				try
				{
					baos.writeTo(o);
				}
				finally
				{
					baos.close();
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
