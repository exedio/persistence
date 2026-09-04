package com.exedio.cope.instrument;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
enum NullabilityAnnotation
{
	none(() -> true, null, null),
	javax(JavaxNullability::classesAreAvailable, javax.annotation.Nullable.class, javax.annotation.Nonnull.class);

	private final BooleanSupplier classesAreAvailable;
	private final @Nullable Class<? extends Annotation> nullableClass;
	private final @Nullable Class<? extends Annotation> nonnullClass;

	NullabilityAnnotation(
			final BooleanSupplier classesAreAvailable,
			final @Nullable Class<? extends Annotation> nullableClass,
			final @Nullable Class<? extends Annotation> nonnullClass)
	{
		this.classesAreAvailable = classesAreAvailable;
		this.nullableClass = nullableClass;
		this.nonnullClass = nonnullClass;
	}

	@Nullable Class<? extends Annotation> forMethodNullability(final Nullability nullability)
	{
		return forNullability(nullability);
	}

	@Nullable Class<? extends Annotation> forParameterNullability(final Nullability nullability)
	{
		return forNullability(nullability);
	}

	private @Nullable Class<? extends Annotation> forNullability(final Nullability nullability)
	{
		return switch(nullability)
		{
			case NONNULL -> nonnullClass;
			case NULLABLE -> nullableClass;
			case DEFAULT -> null;
		};
	}

	String nullableClassName()
	{
		return Objects.requireNonNull(nullableClass).getCanonicalName();
	}

	boolean classesAreAvailable()
	{
		return classesAreAvailable.getAsBoolean();
	}
}
