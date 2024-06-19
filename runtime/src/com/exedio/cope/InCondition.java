package com.exedio.cope;

import static com.exedio.cope.CompareCondition.isComparable;
import static com.exedio.cope.CompareCondition.toStringForValue;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public final class InCondition<E> extends Condition
{
	private static final long serialVersionUID = 5405405990794416218L;
	private final Function<E> function;
	private final boolean not;
	private final List<E> allowedValues;

	InCondition(
			@Nonnull
			final Function<E> function,
			final boolean not,
			@Nonnull
			final List<E> allowedValues)
	{
		this.function = requireNonNull(function, "function");
		this.not = not;
		this.allowedValues = requireNonNull(allowedValues, "allowedValues");
		if(allowedValues.size()<2)
			throw new IllegalArgumentException(
					"allowedValues must contain more than one element");


		for (final E element : allowedValues)
		{
			if(! isComparable(function.getValueClass(), element.getClass(), element))
			{
				final StringBuilder bf = new StringBuilder();
				bf.append(function).
						append(" not comparable to '");
				toStringForValue(bf, element, false);
				bf.append("' (").
						append(element.getClass().getName()).
						append(')');
				throw new IllegalArgumentException(bf.toString());
			}
		}
	}

	@Override
	void append(final Statement bf)
	{
		bf.append(function);
		if (not)
		{
			bf.append(" NOT");
		}
		bf.append(" IN (");
		bf.appendParameterAny(allowedValues.get(0));
		for (int i= 1; i<allowedValues.size(); i++)
		{
			bf.append(",");
			bf.appendParameterAny(allowedValues.get(i));
		}
		bf.append(")");
	}

	@Override
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		function.requireSupportForGet();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		final E value = function.get(item);
		//"in" and "not in" both return false for null values
		if(value==null)
			return Trilean.False;

		if (allowedValues.contains(value))
		{
			return Trilean.valueOf(!not);
		}
		return Trilean.valueOf(not);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public void forEachFieldCovered(final Consumer<Field<?>> action)
	{
		function.forEachFieldCovered(action);
	}

	@Override
	InCondition<E> copy(final CopyMapper mapper)
	{
		return new InCondition<>(mapper.getS(function), not, allowedValues);
	}

	@Override
	public Condition bind(final Join join)
	{
		return new InCondition<>(function.bind(join), not, allowedValues);
	}

	@Override
	public Condition not()
	{
		return new InCondition<>(function, !not, allowedValues);
	}

	@Override
	public boolean equals(final Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		final InCondition<?> that = (InCondition<?>) o;

		if(! function.equals(that.function)) return false;
		if( not != that.not) return false;
		return allowedValues.equals(that.allowedValues);
	}

	@Override
	public int hashCode()
	{
		int result = function.hashCode();
		result = 31 * result + allowedValues.hashCode();
		result = not ? ~result : result;
		return result;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);
		if (not)
		{
			bf.append(" not");
		}
		bf.append(" in (");
		bf.append('\'');
		toStringForValue(bf, allowedValues.get(0), key);
		bf.append('\'');

		for (int i= 1; i<allowedValues.size(); i++)
		{
			bf.append(",");
			bf.append('\'');
			toStringForValue(bf, allowedValues.get(i), key);
			bf.append('\'');
		}

		bf.append(")");
	}
}
