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
import com.sun.source.tree.ImportTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
final class InstrumentorProcessor extends AbstractProcessor
{

	private final Params params;
	private final JavaRepository javaRepository;
	private final Set<JavaFileObject> ignoreFiles;

	boolean processHasBeenCalled = false;
	boolean foundJavadocControlTags = false;

	InstrumentorProcessor(final Params params, final JavaRepository javaRepository, final Iterable<? extends JavaFileObject> ignoreFiles)
	{
		this.params = params;
		this.javaRepository = javaRepository;
		this.ignoreFiles = new HashSet<>();
		for (final JavaFileObject ignoreFile: ignoreFiles)
		{
			this.ignoreFiles.add(ignoreFile);
		}
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pe)
	{
		super.init(pe);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		processHasBeenCalled=true;
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
				files.put(compilationUnit, javaFile=new JavaFile(javaRepository, compilationUnit.getSourceFile(), compilationUnit.getPackageName().toString()));
				for (final ImportTree aImport: compilationUnit.getImports())
				{
					if (!aImport.isStatic())
					{
						javaFile.addImport(aImport.getQualifiedIdentifier().toString());
					}
				}
			}
			if (!ignoreFiles.contains(compilationUnit.getSourceFile()))
			{
				final TreeApiContext treeApiContext=new TreeApiContext(params.configByTags, params.hintFormat==HintFormat.forAnnotations, processingEnv, javaFile, compilationUnit);
				final CompilationUnitVisitor visitor=new CompilationUnitVisitor(treeApiContext);
				visitor.scan(tp, null);
				if (treeApiContext.foundJavadocControlTags)
				{
					foundJavadocControlTags=true;
				}
			}
		}
		return true;
	}
}
