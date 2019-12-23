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

import static java.util.Arrays.asList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

final class InMemoryCompiler
{
	private final Map<Path,JavaFile> javaFiles = new LinkedHashMap<>();

	void addJavaFile(final Path path, final String content)
	{
		final JavaFile previous = javaFiles.putIfAbsent(path, new JavaFile(path, content));
		if (previous!=null)
			throw new IllegalArgumentException("there already is a java file for "+path+"; cope instrumentor does not support two top-level classes in one source file");
	}

	@SuppressWarnings("ClassLoaderInstantiation")
	@SuppressFBWarnings("DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED")
	ClassLoader compile(final JavaCompiler javaCompiler, final String classpath) throws CompileException
	{
		try
		{
			final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
			try (final StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(diagnostics, null, null))
			{
				final InMemoryClassFileManager classFilesInMemory = new InMemoryClassFileManager(fileManager);
				final JavaCompiler.CompilationTask task = javaCompiler.getTask(null, classFilesInMemory, diagnostics, asList("-cp", classpath), null, javaFiles.values());
				if (!task.call())
				{
					throw new CompileException(diagnostics.getDiagnostics());
				}
				final ClassLoader interimContext = new URLClassLoader(toURLs(classpath), getClass().getClassLoader());
				return classFilesInMemory.createInMemoryClassLoader(interimContext);
			}
		}
		catch (final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static URL[] toURLs(final String path) throws MalformedURLException
	{
		final String[] paths = path.split(File.pathSeparator);
		final URL[] urls = new URL[paths.length];
		for (int i = 0; i < paths.length; i++)
		{
			final String next = paths[i];
			urls[i] = new File(next).toURI().toURL();
		}
		return urls;
	}

	void dumpJavaFiles(final Path targetDirectory, final Charset charset) throws IOException
	{
		for (final JavaFile javaFile : javaFiles.values())
		{
			javaFile.dump(targetDirectory, charset);
		}
	}

	@SuppressWarnings("serial")
	@SuppressFBWarnings("SE_BAD_FIELD") // doesn't get serialized
	static class CompileException extends Exception
	{
		private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

		CompileException(final List<Diagnostic<? extends JavaFileObject>> diagnostics)
		{
			super(diagnostics.toString());
			this.diagnostics = new ArrayList<>(diagnostics);
		}

		List<Diagnostic<? extends JavaFileObject>> getDiagnostics()
		{
			return Collections.unmodifiableList(diagnostics);
		}
	}

	private static class JavaFile extends SimpleJavaFileObject
	{
		private final Path path;
		private final String content;

		JavaFile(final Path path, final String content)
		{
			super(toURI(path), Kind.SOURCE);
			checkPath(path);
			this.path = path;
			this.content = content;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
		{
			return content;
		}

		@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
		void dump(final Path targetDirectory, final Charset charset) throws IOException
		{
			final Path absolute = targetDirectory.resolve(path);
			Files.createDirectories(absolute.getParent());
			try (final Writer w = new OutputStreamWriter(Files.newOutputStream(absolute), charset))
			{
				w.write(content);
			}
		}

		private static URI toURI(final Path path)
		{
			try
			{
				return new URI("memory:/"+path.toString().replace(File.separatorChar, '/'));
			}
			catch (final URISyntaxException e)
			{
				throw new RuntimeException(e);
			}
		}

		private static void checkPath(final Path path)
		{
			if (path.isAbsolute())
				throw new IllegalArgumentException("path must be relative");
			if (path.normalize()!=path)
				throw new IllegalArgumentException("path contains redundant elements");
		}
	}
}
