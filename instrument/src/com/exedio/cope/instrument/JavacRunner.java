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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

final class JavacRunner
{
	private final JavacProcessor[] processors;

	JavacRunner(final JavacProcessor... processors)
	{
		this.processors = processors.clone();
	}

	void run(final Params params) throws IOException, HumanReadableException
	{
		final JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
		try (final StandardJavaFileManager fileManager=compiler.getStandardFileManager(null, null, params.charset))
		{
			final List<File> sortedSourceFiles=params.getAllJavaSourceFiles();
			// We have to sort files to have a deterministic order - otherwise, resolving classes by
			// simple name is not deterministic.
			Collections.sort(sortedSourceFiles);
			final Iterable<? extends JavaFileObject> sources=fileManager.getJavaFileObjectsFromFiles(sortedSourceFiles);
			final List<String> optionList = new ArrayList<>();
			optionList.addAll(asList("-classpath", combineClasspath(getCurrentClasspath(), toClasspathString(params.classpath))));
			optionList.addAll(asList("-sourcepath", toClasspathString(params.getSourceDirectories())));
			optionList.add("-proc:only");
			optionList.add("-encoding");
			optionList.add(params.charset.name());
			optionList.add("-Xmaxwarns");
			optionList.add(params.getMaxwarns());
			optionList.add("-implicit:none");
			final JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, optionList, null, sources);
			for (final JavacProcessor processor : processors)
			{
				processor.prepare(params, fileManager);
			}
			task.setProcessors(asList(processors));
			if (!task.call())
				throw new HumanReadableException("cope instrumentor failed");
		}
	}

	static String combineClasspath(final String classpathA, final String classpathB)
	{
		if (classpathA.isEmpty())
		{
			return classpathB;
		}
		else if (classpathB.isEmpty())
		{
			return classpathA;
		}
		else
		{
			return classpathA+File.pathSeparatorChar+classpathB;
		}
	}

	static String toClasspathString(final List<File> classpathFiles)
	{
		return classpathFiles.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
	}

	static String getCurrentClasspath()
	{
		// This is a hack:
		// We want to use the current classpath also in the javac task that's being started, so we
		// have to reconstruct a file-based classpath from a class loader.
		return toClasspath(com.exedio.cope.Item.class.getClassLoader());
	}

	@SuppressWarnings("ExtractMethodRecommender")
	private static String toClasspath(final ClassLoader cl)
	{
		if(cl instanceof URLClassLoader)
		{
			// this works for Gradle with JDK 1.8 and 11, and unit tests with JDK <= 1.8
			// (unit tests would also work with System.getProperty("java.class.path") - so this if-branch is
			// required only for Gradle)
			final URLClassLoader urlClassLoader = (URLClassLoader) cl;
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < urlClassLoader.getURLs().length; i++)
			{
				if(i != 0)
				{
					result.append(File.pathSeparatorChar);
				}
				final URL url = urlClassLoader.getURLs()[i];
				result.append(url);
			}
			return result.toString();
		}
		else
		{
			// this works for Ant
			final Pattern pattern = Pattern.compile("AntClassLoader\\[(.*)]");
			final String classLoaderString = cl.toString();
			final Matcher matcher = pattern.matcher(classLoaderString);
			if(matcher.matches())
			{
				return matcher.group(1);
			}
			else
			{
				// this works for unit tests
				return System.getProperty("java.class.path");
			}
		}
	}
}
