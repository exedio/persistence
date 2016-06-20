package com.exedio.cope.instrument;

import com.sun.source.util.*;
import java.net.URLClassLoader;
import java.util.Arrays;
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
public final class InstrumentorProcessor extends AbstractProcessor
{
	private Trees trees;
	private DocTrees docTrees;

	@Override
	public void init(final ProcessingEnvironment pe)
	{
		super.init(pe);
		trees = Trees.instance(pe);
		docTrees = DocTrees.instance(pe);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		for (final Element e: roundEnv.getRootElements())
		{
			final TreePath tp = trees.getPath(e);
			// new TreeDump().scan(tp, null);
			final InstrumentorVisitor visitor=new InstrumentorVisitor(tp.getCompilationUnit(), docTrees);
			visitor.scan(tp, null);
			visitor.removeAllGeneratedFragments();
		}
		return true;
	}
}
