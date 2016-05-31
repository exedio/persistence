package com.exedio.cope.instrument;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Nullability
{
	NULLABLE, NONNULL, DEFAULT;

	private static boolean initialized = false;

	private static Class<? extends Annotation> nonNullClass;
	private static Class<? extends Annotation> nullableClass;

	private static void checkInitialized()
	{
		if ( !initialized )
		{
			try
			{
				nonNullClass=Nonnull.class;
				nullableClass=Nullable.class;
			}
			catch(NoClassDefFoundError e)
			{
				System.out.println("WARNING: classes javax.annotation.Nonnull/Nullable not available");
			}
			initialized = true;
		}
	}

	public static Nullability fromAnnotations(Annotation[] annotations)
	{
		checkInitialized();
		if ( nonNullClass==null || nullableClass==null )
		{
			return Nullability.DEFAULT;
		}
		if (containsAnnotation(nonNullClass, annotations))
		{
			return Nullability.NONNULL;
		}
		else if (containsAnnotation(nullableClass, annotations))
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
