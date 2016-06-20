package com.exedio.cope.instrument;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;

class MethodVisitor extends TreeScanner<Void,Void>
{
	boolean currentMethodHasGeneratedAnnotation = false;

	@Override
	public Void visitAnnotation(AnnotationTree node, Void p)
	{
		if ( node.getAnnotationType().toString().contains("javax.annotation.Generated")
			&& node.getArguments().size()==1
			&& node.getArguments().get(0).toString().equals("value = \"com.exedio.cope.instrument\"")
			)
		{
			currentMethodHasGeneratedAnnotation = true;
		}
		return super.visitAnnotation(node, p);
	}

	@Override
	public Void visitVariable(VariableTree node, Void p)
	{
		// this is a method parameter
		// nobody cares -> don't traverse
		return null;
	}

	@Override
	public Void visitBlock(BlockTree node, Void p)
	{
		// nobody cares -> don't traverse
		return null;
	}


}
