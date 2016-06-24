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
