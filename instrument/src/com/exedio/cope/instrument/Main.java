/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import com.exedio.cope.Cope;

public final class Main
{

	/**
	 * @return whether the output file is different to the input file
	 */
	static boolean inject(final File inputfile, final File outputfile, final JavaRepository repository)
	throws IOException, InjectorParseException
	{
		//System.out.println("injecting from "+inputfile+" to "+outputfile);
		
		if(!inputfile.exists())
			throw new RuntimeException("error: input file " + inputfile.getAbsolutePath() + " does not exist.");
		if(!inputfile.isFile())
			throw new RuntimeException("error: input file " + inputfile.getAbsolutePath() + " is not a regular file.");
			
		if(outputfile.exists())
		{
			if(inputfile.getCanonicalPath().equals(outputfile.getCanonicalPath()))
				throw new RuntimeException("error: input file and output file are the same.");
			if(!outputfile.isFile())
				throw new RuntimeException("error: output file is not a regular file.");
		}
		
		final CRC32 outputCRC = new CRC32();
		Injector injector = null;
		Writer output=null;
		try
		{
			output=new OutputStreamWriter(new CheckedOutputStream(new FileOutputStream(outputfile), outputCRC));
			injector = new Injector(inputfile, output, new Instrumentor(), repository);
			injector.parseFile();
		}
		catch(InjectorParseException e)
		{
			e.printStackTrace();
			throw new InjectorParseException(inputfile.getAbsolutePath() + " does not exist");
		}
		finally
		{
			if(injector!=null) injector.close();
			if(output!=null) output.close();
		}
		return injector.inputCRC.getValue() != outputCRC.getValue();
	}
	
	private static final String TEMPFILE_SUFFIX=".temp_cope_injection";
	
	private void inject(final File tobemodifiedfile, final JavaRepository repository)
	throws IOException, InjectorParseException
	{
		final File outputfile=new File(tobemodifiedfile.getAbsolutePath()+TEMPFILE_SUFFIX);
		if(inject(tobemodifiedfile, outputfile, repository))
		{
			logInstrumented(tobemodifiedfile);
			if(!outputfile.exists())
				throw new RuntimeException("not exists "+outputfile+".");
			if(!tobemodifiedfile.delete())
				throw new RuntimeException("deleting "+tobemodifiedfile+" failed.");
			if(!outputfile.renameTo(tobemodifiedfile))
				throw new RuntimeException("renaming "+outputfile+" to "+tobemodifiedfile+" failed.");
		}
		else
		{
			logSkipped(tobemodifiedfile);
			if(!outputfile.exists())
				throw new RuntimeException("not exists "+outputfile+".");
			if(!outputfile.delete())
				throw new RuntimeException("deleting "+tobemodifiedfile+" failed.");
		}
	}
	
	public static void main(final String[] args)
	{
		try
		{
			(new Main()).run(new File("."), args, true);
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
		catch(InjectorParseException e)
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
	{}
	
	private void printUsage(PrintStream o)
	{
		o.println("usage:");
		o.print("java ");
		o.print(getClass().getName());
		o.println(" tobemodified1.java ...");
	}
	
	final void run(final File dir, final String[] args, final boolean verbose) throws IllegalParameterException, InjectorParseException, IOException
	{
		final ArrayList sourcefiles = new ArrayList();
		
		for(int i=0; i<args.length; i++)
			sourcefiles.add(new File(dir, args[i]));
		
		run(sourcefiles, verbose);
	}
		
	final void run(final ArrayList sourcefiles, final boolean verbose) throws IllegalParameterException, InjectorParseException, IOException
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
		
		if(sourcefiles.isEmpty())
			throw new IllegalParameterException("nothing to do.");
		
		final JavaRepository repository = new JavaRepository();

		this.verbose = verbose;
		instrumented = 0;
		skipped = 0;
		for(Iterator i=sourcefiles.iterator(); i.hasNext(); )
			inject((File)i.next(), repository);

		if(verbose || instrumented>0)
			System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + ((File)sourcefiles.iterator().next()).getParentFile().getAbsolutePath());
	}

	boolean verbose;
	int skipped;
	int instrumented;
	
	private void logSkipped(final File file)
	{
		if(verbose)
			System.out.println("Instrumented " + file);
		
		skipped++;
	}
	
	private void logInstrumented(final File file)
	{
		if(verbose)
			System.out.println("Skipped " + file);
		
		instrumented++;
	}
	
}
