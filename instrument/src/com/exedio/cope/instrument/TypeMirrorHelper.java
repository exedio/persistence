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

import java.lang.reflect.Array;
import java.util.function.Supplier;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class TypeMirrorHelper
{
	static Class<?> getClass(final TypeMirror typeMirror, final ClassLoader classLoader) throws ClassNotFoundException
	{
		if (typeMirror.getKind()==TypeKind.DECLARED)
		{
			final ElementKind enclosingKind = ((DeclaredType) typeMirror).asElement().getEnclosingElement().getKind();
			if (enclosingKind==ElementKind.CLASS || enclosingKind==ElementKind.ANNOTATION_TYPE)
			{
				final String enclosingName = ((DeclaredType) typeMirror).asElement().getEnclosingElement().toString();
				final Class<?> enclosing = classLoader.loadClass(enclosingName);
				for (final Class<?> inner: enclosing.getClasses())
				{
					if (inner.getCanonicalName().equals(typeMirror.toString()))
						return inner;
				}
			}
		}
		else if (typeMirror.getKind()==TypeKind.ARRAY)
		{
			final ArrayType arrayType = (ArrayType)typeMirror;
			final Class<?> componentClass = getClass(arrayType.getComponentType(), classLoader);
			return Array.newInstance(componentClass, 0).getClass();
		}
		else if (typeMirror.getKind().isPrimitive())
		{
			return ClassHelper.getClass(typeMirror.toString());
		}
		final String name = typeMirror.toString();
		return classLoader.loadClass(name);
	}

	@SuppressWarnings("unchecked")
	static <T> Class<T> get(final Supplier<Class<T>> classSupplier)
	{
		try
		{
			return classSupplier.get();
		}
		catch (final MirroredTypeException e)
		{
			try
			{
				return (Class<T>) Class.forName( e.getTypeMirror().toString() );
			}
			catch (final ClassNotFoundException cnf)
			{
				throw new RuntimeException(cnf);
			}
		}
	}


	private TypeMirrorHelper()
	{
		// prevent instantiation
	}
}
