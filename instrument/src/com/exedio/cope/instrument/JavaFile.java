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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
	private final String packagename;

	private final JavaFileObject sourceFile;
	final JavaRepository repository;
	final ClassLoader interimClassLoader;
	final ArrayList<JavaClass> classes = new ArrayList<>();

	private final CharReplacements generatedFragments = new CharReplacements();

	JavaFile(final JavaRepository repository, final ClassLoader interimClassLoader, final JavaFileObject sourceFile, final String packagename)
	{
		this.sourceFile = sourceFile;
		this.packagename = packagename;

		this.repository = repository;
		this.interimClassLoader = interimClassLoader;
		//noinspection ThisEscapedInObjectConstruction
		repository.add(this);
	}

	@Override
	public String toString()
	{
		return "JavaFile("+sourceFile.getName()+")";
	}

	void markFragmentAsGenerated(final int startInclusive, final int endExclusive)
	{
		generatedFragments.addReplacement(startInclusive, endExclusive, "");
	}

	int translateToPositionInSourceWithoutGeneratedFragments(final int positionInRawSource)
	{
		return generatedFragments.translateToPositionInOutput(positionInRawSource);
	}

	String getSourceWithoutGeneratedFragments()
	{
		try (final Reader reader = new BufferedReader(sourceFile.openReader(false)))
		{
			return generatedFragments.applyReplacements(reader);
		}
		catch (final IOException e)
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
			final InputStreamReader actualChars = new InputStreamReader(actualBytes, charset))
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
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
		return true;
	}

	void add(final JavaClass javaClass)
	{
		repository.assertBuildStage();
		classes.add(javaClass);
		repository.add(javaClass);
	}

	List<JavaClass> getClasses()
	{
		repository.assertNotBuildStage();
		return Collections.unmodifiableList(classes);
	}

	JavaClass getRootClass()
	{
		final List<JavaClass> result = new ArrayList<>(classes.size());
		for(final JavaClass javaClass : classes)
		{
			if (javaClass.parent==null)
				result.add(javaClass);
		}
		if (result.isEmpty())
			throw new RuntimeException("found no root classes in "+this);
		if (result.size()!=1)
			throw new RuntimeException("found multiple root classes in "+this);
		return result.get(0);
	}

	/**
	 * Gets the value of the package statement encountered
	 * in this java file.
	 * Is null, if no package statement found.
	 */
	public String getPackageName()
	{
		return packagename;
	}

	public Class<?> findTypeExternally(final String typename)
	{
		try
		{
			return interimClassLoader.loadClass(typename);
		}
		catch (final ClassNotFoundException ignored)
		{
			return null;
		}
	}

	public Class<?> findTypeExternallyOrFail(final String typename) throws ClassNotFoundException
	{
		return interimClassLoader.loadClass(typename);
	}

	void overwrite(final CharSequence content, final Charset charset)
	{
		try(final OutputStreamWriter w = new OutputStreamWriter(sourceFile.openOutputStream(), charset))
		{
			for (int i=0; i<content.length(); i++)
			{
				w.write(content.charAt(i));
			}
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
