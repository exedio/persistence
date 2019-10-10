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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePathScanner;
import java.lang.annotation.Annotation;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

@SuppressWarnings("AbstractClassExtendsConcreteClass") // OK: abstract TreePathScanner subclasses have no choice
abstract class GeneratedAwareScanner extends TreePathScanner<Void,Void>
{
	final TreeApiContext context;

	GeneratedAwareScanner(final TreeApiContext context)
	{
		this.context=context;
	}

	@Override
	public final Void visitClass(final ClassTree ct, final Void ignore)
	{
		if ( hasGeneratedAnnotation() )
		{
			visitGeneratedPath();
			return null;
		}
		else
		{
			return visitClassInternal(ct, ignore);
		}
	}

	Void visitClassInternal(final ClassTree ct, final Void ignore)
	{
		return super.visitClass(ct, ignore);
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

	private void checkGenerated() throws RuntimeException
	{
		if ( hasGeneratedAnnotation() )
		{
			visitGeneratedPath();
		}
	}

	final boolean hasGeneratedAnnotation()
	{
		final javax.annotation.Generated generated=getAnnotation(javax.annotation.Generated.class);
		return generated!=null
			&& generated.value().length==1
			&& generated.value()[0].equals(Main.GENERATED_VALUE);
	}

	final <T extends Annotation> T getAnnotation(final Class<T> annotationType)
	{
		final Element element=context.getElement(getCurrentPath());
		return element.getAnnotation(annotationType);
	}

	final void printWarning(final String key, final String message)
	{
		final SuppressWarnings suppressWarnings=getAnnotation(SuppressWarnings.class);
		if (suppressWarnings!=null)
		{
			for (final String string: suppressWarnings.value())
			{
				if (key.equals(string))
					return;
			}
		}
		context.messager.printMessage(Diagnostic.Kind.WARNING, "[" + key + "] "+message, context.getElement(getCurrentPath()));
	}

}
