package com.exedio.cope;

public final class Distinct<E> extends Aggregate<E>
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see Function#distinct()
	 */
	Distinct(final Function<E> source)
	{
		super(source, "distinct", "DISTINCT", source.getValueType());
	}
}
