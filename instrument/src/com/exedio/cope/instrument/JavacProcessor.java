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

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

abstract class JavacProcessor extends AbstractProcessor
{
	private Set<JavaFileObject> ignoreFiles;

	@Override
	public final boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		if (roundEnv.processingOver())
			return false;
		processInternal(roundEnv);
		return false;
	}

	abstract void processInternal(final RoundEnvironment roundEnv);

	final void prepare(final Params params, final StandardJavaFileManager fileManager)
	{
		ignoreFiles = new HashSet<>();
		for (final JavaFileObject ignoreFile : fileManager.getJavaFileObjectsFromFiles(params.ignoreFiles))
		{
			ignoreFiles.add(ignoreFile);
		}
	}

	public boolean isFileIgnored(final JavaFileObject e)
	{
		if (ignoreFiles==null)
			throw new IllegalStateException();
		return ignoreFiles.contains(requireNonNull(e));
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Collections.singleton("*");
	}
}
