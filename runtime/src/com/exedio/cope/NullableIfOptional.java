package com.exedio.cope;

import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;

class NullableIfOptional implements NullabilityGetter<Settable<?>>
{
	@Override
	public Nullability getNullability(Settable<?> feature)
	{
		return feature.isMandatory() ? Nullability.NONNULL : Nullability.NULLABLE;
	}
}
