package com.exedio.cope.instrument;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import java.lang.annotation.Annotation;
import javax.annotation.Generated;
import javax.lang.model.element.Element;

abstract class GeneratedAwareScanner extends TreePathScanner<Void,Void>
{
	final TreeApiContext context;

	GeneratedAwareScanner(TreeApiContext context)
	{
		this.context=context;
	}

	@Override
	public Void visitMethod(final MethodTree mt, final Void ignore)
	{
		checkGenerated();
		return null;
	}

	@Override
	public Void visitVariable(final VariableTree mt, final Void ignore)
	{
		checkGenerated();
		return null;
	}

	abstract void visitGeneratedPath();

	private boolean checkGenerated() throws RuntimeException
	{
		if ( hasGeneratedAnnotation() )
		{
			visitGeneratedPath();
			return true;
		}
		else
		{
			return false;
		}
	}

	final boolean hasGeneratedAnnotation()
	{
		final Generated generated=getAnnotation(Generated.class);
		return generated!=null
			&& generated.value().length==1
			&& generated.value()[0].equals(Main.GENERATED_VALUE);
	}

	final <T extends Annotation> T getAnnotation(final Class<T> annotationType)
	{
		final Element element=context.getElement(getCurrentPath());
		return element.getAnnotation(annotationType);
	}

}
