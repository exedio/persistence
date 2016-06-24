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
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.tools.JavaFileObject;

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

	private final String packagename;

	/**
	 * Distiguishes two stages in life cycle of this object:
	 * getting imports via addImport and finding types via findType.
	 * @see #addImport
	 * @see #findTypeExternally(String)
	 */
	private boolean buildStageForImports = true;

	private final JavaFileObject sourceFile;
	final JavaRepository repository;
	final ArrayList<JavaClass> classes = new ArrayList<>();

	private final List<GeneratedFragment> generatedFragments = new ArrayList<>();

	public JavaFile(final JavaRepository repository, final JavaFileObject sourceFile, final String packagename)
	{
		this.externalNameSpace = new CopeNameSpace(repository.externalNameSpace, sourceFile.getName() + " external");
		this.nameSpace = new CopeNameSpace(repository.nameSpace, sourceFile.getName());
		this.sourceFile = sourceFile;
		this.packagename = packagename;
		nameSpace.importPackage(packagename);
		externalNameSpace.importPackage(packagename);

		this.repository = repository;
		repository.add(this);
	}

	@Override
	public String toString()
	{
		return "JavaFile("+sourceFile.getName()+")";
	}

	void markFragmentAsGenerated(final int startInclusive, final int endExclusive)
	{
		if (!generatedFragments.isEmpty())
		{
			final GeneratedFragment last=generatedFragments.get(generatedFragments.size()-1);
			if (last.endExclusive>startInclusive) throw new RuntimeException("fragments must be marked from start to end");
		}
		generatedFragments.add( new GeneratedFragment(startInclusive, endExclusive) );
	}

	int translateToPositionInSourceWithoutGeneratedFragments(final int positionInRawSource)
	{
		int generatedBytesBeforeClassEnd = 0;
		for (final JavaFile.GeneratedFragment generatedFragment: generatedFragments)
		{
			if (generatedFragment.startInclusive<=positionInRawSource)
			{
				if (generatedFragment.endExclusive>positionInRawSource)
				{
					throw new RuntimeException("in generated fragment");
				}
				generatedBytesBeforeClassEnd += (generatedFragment.endExclusive-generatedFragment.startInclusive);
			}
			else
			{
				break;
			}
		}

		return positionInRawSource-generatedBytesBeforeClassEnd;
	}

	byte[] getSourceWithoutGeneratedFragments()
	{
		final Iterator<GeneratedFragment> generatedFragmentIter=generatedFragments.iterator();
		try (final InputStream inputStream=new BufferedInputStream(sourceFile.openInputStream()); final ByteArrayOutputStream os = new ByteArrayOutputStream(Main.INITIAL_BUFFER_SIZE))
		{
			int indexInSource = 0;
			int nextSourceByte;
			GeneratedFragment currentGeneratedFragment=generatedFragmentIter.hasNext()?generatedFragmentIter.next():null;
			while ( (nextSourceByte=inputStream.read())!=-1 )
			{
				if (currentGeneratedFragment==null || currentGeneratedFragment.startInclusive>indexInSource)
				{
					os.write(nextSourceByte);
				}
				else if (currentGeneratedFragment.endExclusive-1>indexInSource)
				{
					// generated, skip
				}
				else if (currentGeneratedFragment.endExclusive-1==indexInSource)
				{
					currentGeneratedFragment=generatedFragmentIter.hasNext()?generatedFragmentIter.next():null;
				}
				else
				{
					// currentGeneratedFragment should have already been discarded
					throw new RuntimeException();
				}
				indexInSource++;
			}
			if (currentGeneratedFragment!=null)
			{
				throw new RuntimeException("unconsumed GeneratedFragment at end of file");
			}
			return os.toByteArray();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	String getSourceFileName()
	{
		return sourceFile.getName();
	}

	boolean inputEqual(final CharSequence bf, final Charset charset)
	{
		try (
			final InputStream actualBytes = sourceFile.openInputStream();
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

	void overwrite(CharSequence content, Charset charset)
	{
		try(final OutputStreamWriter w = new OutputStreamWriter(sourceFile.openOutputStream(), charset))
		{
			for (int i=0; i<content.length(); i++)
			{
				w.write(content.charAt(i));
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final static class GeneratedFragment
	{
		final int startInclusive;
		final int endExclusive;

		GeneratedFragment(int startInclusive, int endExclusive)
		{
			if (startInclusive<0) throw new RuntimeException();
			if (startInclusive>=endExclusive) throw new RuntimeException();
			this.startInclusive=startInclusive;
			this.endExclusive=endExclusive;
		}

		@Override
		public String toString()
		{
			return String.format("%s-%s", startInclusive, endExclusive);
		}
	}
}
