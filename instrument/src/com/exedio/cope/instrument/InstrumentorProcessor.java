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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
final class InstrumentorProcessor extends AbstractProcessor
{
	private DocTrees docTrees;

	private final boolean deinstrument;
	private final JavaRepository javaRepository;

	InstrumentorProcessor(boolean deinstrument, JavaRepository javaRepository)
	{
		this.deinstrument = deinstrument;
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
			final TypeElement typeElement=(TypeElement)e;
			final TreePath tp = docTrees.getPath(e);
			final CompilationUnitTree compilationUnit=tp.getCompilationUnit();
			JavaFile javaFile=files.get(compilationUnit);
			if ( javaFile==null )
			{
				files.put(compilationUnit, javaFile=new JavaFile(javaRepository, compilationUnit));
				final String qualifiedName=typeElement.getQualifiedName().toString();
				// TODO use class fqn
				javaFile.setPackage(qualifiedName.substring(0, qualifiedName.lastIndexOf('.')));
				for (ImportTree aImport: compilationUnit.getImports())
				{
					javaFile.addImport(aImport.getQualifiedIdentifier().toString());
				}
			}
			// new TreeDump().scan(tp, null);
			// TODO: check typeElement.getSuperclass()
			final InstrumentorVisitor visitor=new InstrumentorVisitor(compilationUnit, docTrees, javaFile);
			visitor.scan(tp, null);
			if ( deinstrument )
			{
				visitor.removeAllGeneratedFragments();
			}
		}
		return true;
	}
}
