
package com.exedio.cope.instrument;

import java.io.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

public class Main
{
	
	public static void inject(File inputfile, File outputfile)
	throws IOException, InjectorParseException
	{
		//System.out.println("injecting from "+inputfile+" to "+outputfile);
		
		if(outputfile.exists())
		{
			if(inputfile.getCanonicalPath().equals(outputfile.getCanonicalPath()))
				throw new RuntimeException("error: input file and output file are the same.");
			if(!outputfile.isFile())
				throw new RuntimeException("error: output file is not a regular file.");
		}
		
		Reader input=null;
		Writer output=null;
		try
		{
			input =new InputStreamReader(new FileInputStream(inputfile));
			output=new OutputStreamWriter(new FileOutputStream(outputfile));
			(new Injector(input, output, new Instrumentor())).parseFile();
			input.close();
			output.close();
		}
		catch(InjectorParseException e)
		{
			e.printStackTrace();
			input.close();
			output.close();
			outputfile.delete();
			throw new InjectorParseException(inputfile+": "+e.getMessage());
		}
		catch(IOException e)
		{
			if(input!=null)  input.close();
			if(output!=null) output.close();
			outputfile.delete();
			throw e;
		}
	}
	
	private static final String TEMPFILE_SUFFIX=".temp_oclinjection";
	
	public static void inject(File tobemodifiedfile)
	throws IOException, InjectorParseException
	{
		File outputfile=new File(tobemodifiedfile.getPath()+TEMPFILE_SUFFIX);
		inject(tobemodifiedfile, outputfile);
		if(!tobemodifiedfile.delete())
			System.out.println("warning: deleting "+tobemodifiedfile+" failed.");
		if(!outputfile.renameTo(tobemodifiedfile))
			System.out.println("warning: renaming "+outputfile+" to "+tobemodifiedfile+" failed.");
	}
	
	public static void expand(Collection files, String pattern)
	throws IOException
	{
		if(pattern.endsWith("*.java"))
		{
			//System.out.println("expanding "+pattern);
			String directoryName = pattern.substring(0,pattern.length()-"*.java".length());
			File directory = new File(directoryName);
			if(!directory.isDirectory())
				throw new IOException(directoryName+" should be a directory");
			File[] expandedFiles = directory.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return
					file.isFile() &&
					file.getName().endsWith(".java");
				}
			});
			//for(int i=0; i<expandedFiles.length; i++) System.out.println("  into "+expandedFiles[i].getPath());
			for(int i=0; i<expandedFiles.length; i++)
				files.add(expandedFiles[i].getPath());
		}
		else
			files.add(pattern);
	}
	
	public static void main(final String[] args)
	{
		try
		{
			(new Main()).run(args);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	protected Main()
	{}
	
	protected void printUsage(PrintStream o)
	{
		o.println("usage:");
		o.print("java ");
		o.print(getClass().getName());
		o.println("[options] tobemodified1.java ...");
		o.println("  -m  --modify");
		o.println("      modify files");
		o.println("  -c  --clean");
		o.println("      clean files");
		o.println("  -vm --violation-macro macro");
		o.println("      what to to, if a constraint fails.");
		o.println("  -tt --trace-types");
		o.println("      trace types of collection elements.");
	}
	
	protected int i;
	protected String[] args;
	protected final ArrayList taskConfigs = new ArrayList();
	
	private boolean modify=false;
	private ArrayList sourcefiles=new ArrayList();
	
	protected void processParameter() throws IOException, IllegalParameterException
	{
		if("--modify".equals(args[i])||"-m".equals(args[i]))
			modify=true;
		else if(args[i].startsWith("-"))
			throw new IllegalParameterException("unknown option: "+args[i]);
		else
		{
			for(; i<args.length; i++)
				expand(sourcefiles, args[i]);
		}
	}
	
	protected final void run(final String[] args)
	{
		this.args = args;
		
		try
		{
			for(i=0; i<args.length; i++)
				processParameter();
			
			if(sourcefiles.isEmpty())
				throw new IllegalParameterException("nothing to do.");
			
			for(Iterator i=sourcefiles.iterator(); i.hasNext(); )
			{
				String s=(String)i.next();
				if(modify)
					inject(new File(s));
				else
					inject(new File(s), new File(s+".injected"));
			}
		}
		catch(IllegalParameterException e)
		{
			System.out.println(e.getMessage());
			printUsage(System.out);
			throw new RuntimeException(e.getMessage());
		}
		catch(InjectorParseException e)
		{
			System.out.println(e);
			throw new RuntimeException(e.getMessage());
		}
		catch(IOException e)
		{
			System.out.println(e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
}
