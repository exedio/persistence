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
import static java.util.Collections.singleton;

import com.sun.tools.javac.api.JavacTool;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

final class JavacRunner
{
	private final JavacProcessor processor;

	JavacRunner(final JavacProcessor processor)
	{
		this.processor = processor;
	}

	void run(final Params params) throws IOException, HumanReadableException
	{
		// "JavacTool.create()" is not part of the "exported" API
		// (not annotated with https://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/jdk/Exported.html).
		// The more stable alternative would be calling "ToolProvider.getSystemJavaCompiler()", but that causes
		// class path issues with when run as an ant task.
		final JavaCompiler compiler=JavacTool.create();
		try (final StandardJavaFileManager fileManager=compiler.getStandardFileManager(null, null, null))
		{
			final List<File> sortedSourceFiles=params.getJavaSourceFilesExcludingIgnored();
			// We have to sort files to have a deterministic order - otherwise, resolving classes by
			// simple name is not deterministic.
			Collections.sort(sortedSourceFiles);
			final Iterable<? extends JavaFileObject> sources=fileManager.getJavaFileObjectsFromFiles(sortedSourceFiles);
			final List<String> optionList = new ArrayList<>();
			optionList.addAll(asList("-classpath", combineClasspath(getCurrentClasspath(), toClasspathString(params.classpath))));
			optionList.addAll(asList("-sourcepath", toClasspathString(params.sourceDirectories)));
			optionList.add("-proc:only");
			optionList.add("-encoding");
			optionList.add(params.charset.name());
			optionList.add("-Xmaxwarns");
			optionList.add(params.getMaxwarns());
			optionList.add("-implicit:none");
			final JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, optionList, null, sources);
			processor.prepare(params, fileManager);
			task.setProcessors(singleton(processor));
			task.call();
			processor.validate();
		}
	}

	private static String combineClasspath(final String classpathA, final String classpathB)
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

	private static String toClasspathString(final List<File> classpathFiles)
	{
		return classpathFiles.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
	}

	private static String getCurrentClasspath()
	{
		// This is a hack:
		// We want to use the current classpath also in the javac task that's being started, so we
		// have to reconstruct a file-based classpath from a class loader.
		return toClasspath(com.exedio.cope.Item.class.getClassLoader());
	}

	private static String toClasspath(final ClassLoader cl)
	{
		// this works for Ant
		final Pattern pattern = Pattern.compile("AntClassLoader\\[(.*)]");
		final String classLoaderString=cl.toString();
		final Matcher matcher = pattern.matcher(classLoaderString);
		if ( !matcher.matches() ) throw new RuntimeException("failed to construct file-based classpath from class loader; see Main.java getJavacClasspath(); class loader: "+classLoaderString);
		return matcher.group(1);
	}
}
