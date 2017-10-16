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
import com.sun.source.tree.ImportTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
final class InstrumentorProcessor extends JavacProcessor
{
	private final JavaRepository javaRepository;
	private final ClassLoader interimClassLoader;

	InstrumentorProcessor(final JavaRepository javaRepository, final ClassLoader interimClassLoader)
	{
		this.javaRepository = javaRepository;
		this.interimClassLoader = interimClassLoader;
	}

	@Override
	boolean includeIgnoredFiles()
	{
		return false;
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		final Map<CompilationUnitTree,JavaFile> files = new HashMap<>();
		final DocTrees docTrees = DocTrees.instance(processingEnv);
		for (final Element e: roundEnv.getRootElements())
		{
			// We could check for ((TypeElement)e).getSuperclass() here to only visit "interesting" elements,
			// but in a test run that made hardly any runtime difference and caused quite some complications.
			final TreePath tp = docTrees.getPath(e);
			final CompilationUnitTree compilationUnit=tp.getCompilationUnit();
			JavaFile javaFile=files.get(compilationUnit);
			if ( javaFile==null )
			{
				files.put(compilationUnit, javaFile=new JavaFile(javaRepository, interimClassLoader, compilationUnit.getSourceFile(), getPackageName(compilationUnit)));
				for (final ImportTree aImport: compilationUnit.getImports())
				{
					if (!aImport.isStatic())
					{
						javaFile.addImport(aImport.getQualifiedIdentifier().toString());
					}
				}
			}
			final TreeApiContext treeApiContext=new TreeApiContext(processingEnv, javaFile, compilationUnit);
			final CompilationUnitVisitor visitor=new CompilationUnitVisitor(treeApiContext);
			visitor.scan(tp, null);
		}
		return true;
	}

	private static String getPackageName(final CompilationUnitTree compilationUnit)
	{
		final ExpressionTree packageName = compilationUnit.getPackageName();
		return
				packageName!=null
				? packageName.toString()
				: null;
	}
}
