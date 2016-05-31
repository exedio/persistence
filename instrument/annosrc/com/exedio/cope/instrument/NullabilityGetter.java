package com.exedio.cope.instrument;

public interface NullabilityGetter<F>
{
	public Nullability getNullability(F feature);
}
