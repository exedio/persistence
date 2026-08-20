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

import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;

public enum Nullability
{
	NULLABLE, NONNULL, DEFAULT;

	static final String NONNULL_CLASS_NAME = "javax.annotation.Nonnull";

	@SuppressWarnings("NonFinalFieldInEnum")
	private static Boolean nullabilityClassesAvailable;

	static boolean nullabilityClassesAreAvailable()
	{
		if (nullabilityClassesAvailable==null)
		{
			try
			{
				Class.forName(NONNULL_CLASS_NAME);
				nullabilityClassesAvailable = true;
			}
			catch (final ClassNotFoundException e)
			{
				nullabilityClassesAvailable = false;
			}
		}
		return nullabilityClassesAvailable;
	}

	public static Nullability forOptional(final boolean optional)
	{
		return optional?NULLABLE:NONNULL;
	}

	public static Nullability forMandatory(final boolean mandatory)
	{
		return forOptional(!mandatory);
	}

	public static Nullability fromAnnotations(final AnnotatedElement element)
	{
		if (!nullabilityClassesAreAvailable())
		{
			return DEFAULT;
		}
		final Nonnull nonnullAnnotation = element.getAnnotation(Nonnull.class);
		if (nonnullAnnotation!=null)
		{
			if ( nonnullAnnotation.when()==When.ALWAYS )
			{
				return NONNULL;
			}
			else
			{
				throw new RuntimeException("non-default setting of Nonnull.when() not supported");
			}
		}
		else if (element.isAnnotationPresent(Nullable.class))
		{
			return NULLABLE;
		}
		else
		{
			return DEFAULT;
		}
	}
}
