package com.exedio.cope.instrument;

final class NullabilityGetterDefault implements NullabilityGetter<Object>
{
	private NullabilityGetterDefault()
	{
		throw new RuntimeException();
	}

	@Override
	public Nullability getNullability(Object feature)
	{
		throw new RuntimeException();
	}
}
