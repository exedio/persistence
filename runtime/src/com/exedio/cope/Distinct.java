package com.exedio.cope;

import java.io.Serial;

public final class Distinct<E> extends Aggregate<E,E>
{
	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * @see Function#distinct()
	 */
	Distinct(final Function<E> source)
	{
		super(source, "distinct", "DISTINCT", source.getValueType());
	}

	@Override
	public Function<E> bind(final Join join)
	{
		return new Distinct<>(source.bind(join));
	}
}
