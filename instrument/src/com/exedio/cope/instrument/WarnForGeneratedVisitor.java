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

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import java.lang.annotation.Annotation;
import javax.annotation.Generated;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

final class WarnForGeneratedVisitor extends TreePathScanner<Void,Void>
{
	private final TreeApiContext context;

	WarnForGeneratedVisitor(final TreeApiContext context)
	{
		this.context=context;
	}

	private boolean hasGeneratedAnnotation()
	{
		final Generated generated=getAnnotation(Generated.class);
		return generated!=null
			&& generated.value().length==1
			&& generated.value()[0].equals(Main.GENERATED_VALUE);
	}

	private <T extends Annotation> T getAnnotation(final Class<T> annotationType)
	{
		final Element element=context.getElement(getCurrentPath());
		return element.getAnnotation(annotationType);
	}

	@Override
	public Void visitVariable(final VariableTree node, final Void p)
	{
		checkGenerated();
		return null;
	}

	private void checkGenerated()
	{
		if (hasGeneratedAnnotation())
		{
			context.messager.printMessage(Diagnostic.Kind.WARNING, "@Generated annotation in non-generated code", context.getElement(getCurrentPath()));
		}
	}

	@Override
	public Void visitMethod(final MethodTree node, final Void p)
	{
		checkGenerated();
		return null;
	}
}
