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

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;

final class CompilationUnitVisitor extends TreePathScanner<Void, Void>
{
	private final TreeApiContext context;

	CompilationUnitVisitor(final TreeApiContext context)
	{
		this.context=context;
	}

	@Override
	public Void visitClass(final ClassTree ct, final Void ignore)
	{
		final ClassVisitor classVisitor = new ClassVisitor(context, null);
		classVisitor.scan(getCurrentPath(), ignore);
		return null;
	}

	@Override
	public Void visitVariable(final VariableTree node, final Void p)
	{
		throw new RuntimeException("unexpected - visiting classes is delegated");
	}

	@Override
	public Void visitMethod(final MethodTree node, final Void p)
	{
		throw new RuntimeException("unexpected - visiting classes is delegated");
	}

	@Override
	public Void visitBlock(final BlockTree node, final Void p)
	{
		throw new RuntimeException("unexpected - visiting classes is delegated");
	}
}
