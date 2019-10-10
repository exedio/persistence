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

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import javax.annotation.Generated;
import javax.lang.model.element.Element;

/**
 * This class is for safely accessing {@link Generated} in a classpath without it.
 * Avoids {@link NoClassDefFoundError}s.
 */
final class GeneratedJavaxAccess
{
	private static final Class<? extends Annotation> clazz = findClass();

	private static Class<? extends Annotation> findClass()
	{
		try
		{
			return Class.forName(NAME).asSubclass(Annotation.class);
		}
		catch(final ClassNotFoundException e)
		{
			return null;
		}
	}

	private static final String NAME = "javax.annotation.Generated";

	static Class<? extends Annotation> get()
	{
		return requireNonNull(clazz, "class " + NAME + " not present");
	}

	static boolean isPresent(final Element element)
	{
		if(clazz==null)
			return false;

		final Generated generated = (Generated)element.getAnnotation(clazz);
		return generated!=null
				 && generated.value().length==1
				 && generated.value()[0].equals(Main.GENERATED_VALUE);
	}


	private GeneratedJavaxAccess()
	{
		// prevent instantiation
	}
}
