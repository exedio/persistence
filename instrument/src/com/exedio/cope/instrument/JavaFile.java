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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	/**
	 * Defines a name space, that does not depend on
	 * information gathered by the instrumentor,
	 * thus can be used in build stage.
	 */
	private final CopeNameSpace externalNameSpace;

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
	final ClassLoader interimClassLoader;
	final ArrayList<JavaClass> classes = new ArrayList<>();

	private final ByteReplacements generatedFragments = new ByteReplacements();

	JavaFile(final JavaRepository repository, final ClassLoader interimClassLoader, final JavaFileObject sourceFile, final String packagename)
	{
		this.externalNameSpace = new CopeNameSpace(repository.externalNameSpace, sourceFile.getName() + " external");
		this.sourceFile = sourceFile;
		this.packagename = packagename;
		if(packagename!=null)
		{
			externalNameSpace.importPackage(packagename);
		}

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
		generatedFragments.addReplacement(startInclusive, endExclusive, EMPTY_BYTES);
	}

	private static final byte[] EMPTY_BYTES = {};

	int translateToPositionInSourceWithoutGeneratedFragments(final int positionInRawSource)
	{
		return generatedFragments.translateToPositionInOutput(positionInRawSource);
	}

	byte[] getSourceWithoutGeneratedFragments()
	{
		try (final InputStream inputStream=new BufferedInputStream(sourceFile.openInputStream()))
		{
			return generatedFragments.applyReplacements(inputStream);
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
	public String getPackageName()
	{
		return packagename;
	}

	/**
	 * Adds the value of an import statement.
	 */
	public void addImport(final String importname)
	{
		if(!buildStageForImports)
			throw new RuntimeException();

		if(importname.endsWith(".*"))
		{
			final String packageName = importname.substring(0,importname.length()-2);
			externalNameSpace.importPackage(packageName);
		}
		else
		{
			externalNameSpace.importClass(importname);
		}
	}

	public Class<?> findTypeExternally(final String typename)
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
