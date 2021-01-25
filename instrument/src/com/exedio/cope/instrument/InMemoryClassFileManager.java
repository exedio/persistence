/*
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

final class InMemoryClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager>
{
	private List<ClassFile> classFiles = new ArrayList<>();

	InMemoryClassFileManager(final StandardJavaFileManager nested)
	{
		super(nested);
	}

	@SuppressWarnings("ClassLoaderInstantiation")
	InMemoryClassLoader createInMemoryClassLoader(final ClassLoader parent)
	{
		final InMemoryClassLoader classLoader = new InMemoryClassLoader(parent);
		for (final ClassFile classFile : classFiles)
		{
			classLoader.addClass(classFile.getClassName(), classFile.getByteCode());
		}
		// make sure no further files are added:
		classFiles = null;
		return classLoader;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling)
	{
		if (kind!=JavaFileObject.Kind.CLASS) throw new RuntimeException();
		try
		{
			final ClassFile classFile = new ClassFile(className);
			classFiles.add(classFile);
			return classFile;
		}
		catch (final URISyntaxException e)
		{
			throw new RuntimeException(className, e);
		}
	}

	static final class ClassFile extends SimpleJavaFileObject
	{
		private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		private final String className;

		ClassFile(final String className) throws URISyntaxException
		{
			super(new URI(className), Kind.CLASS);
			this.className = className;
		}

		String getClassName()
		{
			return className;
		}

		@Override
		public OutputStream openOutputStream()
		{
			return baos;
		}

		public byte[] getByteCode()
		{
			return baos.toByteArray();
		}
	}

}
