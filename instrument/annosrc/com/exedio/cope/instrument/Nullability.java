package com.exedio.cope.instrument;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;

public enum Nullability
{
	NULLABLE, NONNULL, DEFAULT;

	public static Nullability fromAnnotations(Annotation[] annotations)
	{
		final Nonnull nonnullAnnotation = getAnnotation(Nonnull.class, annotations);
		if (nonnullAnnotation!=null)
		{
			if ( nonnullAnnotation.when().equals(When.ALWAYS) )
			{
				return Nullability.NONNULL;
			}
			else
			{
				throw new RuntimeException("non-default setting of Nonnull.when() not supported");
			}
		}
		else if (getAnnotation(Nullable.class, annotations)!=null)
		{
			return Nullability.NULLABLE;
		}
		else
		{
			return Nullability.DEFAULT;
		}
	}

	private static <A extends Annotation> A getAnnotation(final Class<A> annotationClass, final Annotation[] annotations)
	{
		for(final Annotation a : annotations)
		{
			if(a.annotationType().equals(annotationClass))
			{
				return annotationClass.cast(a);
			}
		}
		return null;
	}
}
