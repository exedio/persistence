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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;

@SupportedAnnotationTypes("*")
final class FillRepositoryProcessor extends JavacProcessor
{
	private final JavaRepository javaRepository;
	private final InterimProcessor interimProcessor;

	FillRepositoryProcessor(final JavaRepository javaRepository, final InterimProcessor interimProcessor)
	{
		this.javaRepository = javaRepository;
		this.interimProcessor = interimProcessor;
	}

	@Override
	void processInternal(final RoundEnvironment roundEnv)
	{
		final ClassLoader interimClassLoader = interimProcessor.getInterimClassLoader();
		if (interimClassLoader==null)
		{
			// InterimProcessor failed
			return;
		}
		final UniqueGuard<CompilationUnitTree> compilationUnitGuard = new UniqueGuard<>("two root elements in same compilation unit should be prevented by "+InMemoryCompiler.class.getSimpleName()+"#addJavaFile");
		final DocTrees docTrees = DocTrees.instance(processingEnv);
		for (final Element e: roundEnv.getRootElements())
		{
			// We could check for ((TypeElement)e).getSuperclass() here to only visit "interesting" elements,
			// but in a test run that made hardly any runtime difference and caused quite some complications.
			final TreePath tp = docTrees.getPath(e);
			final CompilationUnitTree compilationUnit=tp.getCompilationUnit();
			compilationUnitGuard.check(compilationUnit);
			final JavaFile javaFile=new JavaFile(javaRepository, interimClassLoader, compilationUnit.getSourceFile(), getPackageName(compilationUnit));
			final TreeApiContext treeApiContext=new TreeApiContext(processingEnv, javaFile, compilationUnit);
			if (isFileIgnored(compilationUnit.getSourceFile()))
			{
				new WarnForGeneratedVisitor(treeApiContext).scan(tp, null);
			}
			else
			{
				final CompilationUnitVisitor visitor=new CompilationUnitVisitor(treeApiContext);
				visitor.scan(tp, null);
			}
		}
		javaRepository.endBuildStage();
	}

	private static String getPackageName(final CompilationUnitTree compilationUnit)
	{
		final ExpressionTree packageName = compilationUnit.getPackageName();
		return
				packageName!=null
				? packageName.toString()
				: null;
	}

	static class UniqueGuard<T>
	{
		private final Set<T> set = new HashSet<>();
		private final String violationMessage;

		UniqueGuard(final String violationMessage)
		{
			this.violationMessage = violationMessage;
		}

		void check(final T element)
		{
			if (!set.add(element))
			{
				throw new RuntimeException(violationMessage);
			}
		}
	}
}
