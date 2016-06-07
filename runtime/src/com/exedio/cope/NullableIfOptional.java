package com.exedio.cope;

import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;

public class NullableIfOptional implements NullabilityGetter<Settable<?>>
{
	@Override
	public Nullability getNullability(final Settable<?> feature)
	{
		return feature.isMandatory() ? Nullability.NONNULL : Nullability.NULLABLE;
	}
}
