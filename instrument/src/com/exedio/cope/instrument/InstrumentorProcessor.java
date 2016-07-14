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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
import java.util.HashMap;
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

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
final class InstrumentorProcessor extends AbstractProcessor
{

	private final JavaRepository javaRepository;

	private DocTrees docTrees;

	InstrumentorProcessor(final JavaRepository javaRepository)
	{
		this.javaRepository = javaRepository;
	}

	@Override
	public void init(final ProcessingEnvironment pe)
	{
		super.init(pe);
		docTrees = DocTrees.instance(pe);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		final Map<CompilationUnitTree,JavaFile> files = new HashMap<>();
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
				for (ImportTree aImport: compilationUnit.getImports())
				{
					if (!aImport.isStatic())
					{
						javaFile.addImport(aImport.getQualifiedIdentifier().toString());
					}
				}
			}
			final CompilationUnitVisitor visitor=new CompilationUnitVisitor(new TreeApiContext(docTrees, javaFile,compilationUnit));
			visitor.scan(tp, null);
		}
		return true;
	}
}
