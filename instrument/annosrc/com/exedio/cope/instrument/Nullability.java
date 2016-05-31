package com.exedio.cope.instrument;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Nullability
{
	NULLABLE, NONNULL, DEFAULT;

	public static Nullability fromAnnotations(Annotation[] annotations)
	{
		if (containsAnnotation(Nonnull.class, annotations))
		{
			return Nullability.NONNULL;
		}
		else if (containsAnnotation(Nullable.class, annotations))
		{
			return Nullability.NULLABLE;
		}
		else
		{
			return Nullability.DEFAULT;
		}
	}

	private static boolean containsAnnotation(final Class<? extends Annotation> annotationClass, final Annotation[] annotations)
	{
		for(final Annotation a : annotations)
		{
			if(a.annotationType().equals(annotationClass))
			{
				return true;
			}
		}
		return false;
	}
}
