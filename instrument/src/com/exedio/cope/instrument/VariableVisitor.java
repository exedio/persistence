package com.exedio.cope.instrument;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.util.TreeScanner;

class VariableVisitor extends TreeScanner<Void,Void>
{
	boolean currentVariableHasGeneratedAnnotation = false;

	@Override
	public Void visitAnnotation(AnnotationTree node, Void p)
	{
		if ( node.getAnnotationType().toString().contains("javax.annotation.Generated")
			&& node.getArguments().size()==1
			&& node.getArguments().get(0).toString().equals("value = \"com.exedio.cope.instrument\"")
			)
		{
			currentVariableHasGeneratedAnnotation = true;
		}
		return super.visitAnnotation(node, p);
	}



}
