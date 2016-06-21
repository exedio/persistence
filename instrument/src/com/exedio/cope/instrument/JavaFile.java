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

import bsh.UtilEvalError;
import com.sun.source.tree.CompilationUnitTree;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a parsed java file.
 * Manages the mapping of type names and types.
 * This depends on the current package and all
 * imported packages/classes.
 *
 * @author Ralf Wiebicke
 */
final class JavaFile
{
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 */
	private final CopeNameSpace externalNameSpace;

	final CopeNameSpace nameSpace;

	private String packagename;

	/**
	 * Distiguishes two stages in life cycle of this object:
	 * getting imports via addImport and finding types via findType.
	 * @see #addImport
	 * @see #findTypeExternally(String)
	 */
	private boolean buildStageForImports = true;

	private final CompilationUnitTree compilationUnit;
	final JavaRepository repository;
	final ArrayList<JavaClass> classes = new ArrayList<>();

	final List<GeneratedFragment> generatedFragments = new ArrayList<>();

	public JavaFile(final JavaRepository repository, final CompilationUnitTree compilationUnit)
	{
		this.externalNameSpace = new CopeNameSpace(repository.externalNameSpace, compilationUnit.getSourceFile().getName() + " external");
		this.nameSpace = new CopeNameSpace(repository.nameSpace, compilationUnit.getSourceFile().getName());

		this.repository = repository;
		repository.add(this);

		this.compilationUnit = compilationUnit;
	}

	@Override
	public String toString()
	{
		return "JavaFile("+compilationUnit.getSourceFile().getName()+")";
	}

	void markFragmentAsGenerated(int start, int end)
	{
		generatedFragments.add( new GeneratedFragment(start, end) );
	}

	byte[] getSourceWithoutGeneratedFragments()
	{
		// TODO more efficient
		int start = 0;
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream())
		{
			int end;
			final byte[] allBytes;
			try (final InputStream inputStream=compilationUnit.getSourceFile().openInputStream())
			{
				allBytes=readFully(inputStream);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			for (final GeneratedFragment generatedFragment: generatedFragments)
			{
				end = generatedFragment.fromInclusive;
				os.write(allBytes, start, end-start);
				start = generatedFragment.endExclusive;
			}
			os.write(allBytes, start, allBytes.length-start);

			return os.toByteArray();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static byte[] readFully(InputStream fis) throws IOException
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			int b;
			while ( (b=fis.read())!=-1 )
			{
				baos.write(b);
			}
			return baos.toByteArray();
		}
	}

	String getSourceFileName()
	{
		return compilationUnit.getSourceFile().getName();
	}

	boolean inputEqual(final StringBuilder bf, final Charset charset)
	{
		try (
			final InputStream actualBytes = compilationUnit.getSourceFile().openInputStream();
			final InputStreamReader actualChars = new InputStreamReader(actualBytes, charset);
			)
		{
			for ( int i=0; i<bf.length(); i++ )
			{
				final char expectedChar = bf.charAt(i);
				final int actualChar = actualChars.read();
				if ( actualChar==-1 ) return false;
				if ( expectedChar!=(char)actualChar ) return false;
			}
			if ( actualChars.read()!=-1 )
			{
				return false;
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return true;
	}

	void add(final JavaClass javaClass)
	{
		assert repository.isBuildStage();
		classes.add(javaClass);
		repository.add(javaClass);
	}

	List<JavaClass> getClasses()
	{
		assert !repository.isBuildStage();
		return Collections.unmodifiableList(classes);
	}

	/**
	 * Sets the package of this file.
	 * Necessary, since the package is not known at construction time.
	 * @param packagename may be null for root package
	 * @throws ParserException if called more than once.
	 */
	public final void setPackage(final String packagename)
	throws ParserException
	{
		if(!buildStageForImports)
			throw new RuntimeException();
		if(this.packagename!=null)
			throw new ParserException("only one package statement allowed.");

		this.packagename=packagename;
		nameSpace.importPackage(packagename);
		externalNameSpace.importPackage(packagename);
	}

	/**
	 * Gets the value of the package statement encountered
	 * in this java file.
	 * Is null, if no package statement found.
	 */
	public final String getPackageName()
	{
		return packagename;
	}

	/**
	 * Adds the value of an import statement.
	 */
	public final void addImport(final String importname)
	throws ParserException
	{
		if(!buildStageForImports)
			throw new RuntimeException();

		if(importname.endsWith(".*"))
		{
			final String packageName = importname.substring(0,importname.length()-2);
			nameSpace.importPackage(packageName);
			externalNameSpace.importPackage(packageName);
		}
		else
		{
			nameSpace.importClass(importname);
			externalNameSpace.importClass(importname);
		}
	}

	public final Class<?> findTypeExternally(final String typename)
	{
		//System.out.println("findtype: >"+typename+"<");

		buildStageForImports=false;

		try
		{
			return externalNameSpace.getClass(Generics.remove(typename));
		}
		catch(final UtilEvalError e)
		{
			throw new RuntimeException(typename, e);
		}
	}

	void overwrite(byte[] bytes)
	{
		try(final OutputStream o = compilationUnit.getSourceFile().openOutputStream())
		{
			o.write(bytes);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	static class GeneratedFragment
	{
		final int fromInclusive;
		final int endExclusive;

		GeneratedFragment(int fromInclusive, int endExclusive)
		{
			this.fromInclusive=fromInclusive;
			this.endExclusive=endExclusive;
		}

		@Override
		public String toString()
		{
			return String.format("%s-%s", fromInclusive, endExclusive);
		}
	}
}
