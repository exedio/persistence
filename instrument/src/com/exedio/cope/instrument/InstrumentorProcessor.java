package com.exedio.cope.instrument;

import com.sun.source.util.DocTrees;
import com.sun.source.util.TreePath;
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
	private DocTrees docTrees;

	private final boolean deinstrument;

	InstrumentorProcessor(boolean deinstrument)
	{
		this.deinstrument = deinstrument;
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
		for (final Element e: roundEnv.getRootElements())
		{
			final TreePath tp = docTrees.getPath(e);
			// new TreeDump().scan(tp, null);
			final InstrumentorVisitor visitor=new InstrumentorVisitor(tp.getCompilationUnit(), docTrees);
			visitor.scan(tp, null);
			if ( deinstrument )
			{
				visitor.removeAllGeneratedFragments();
			}
		}
		return true;
	}
}
