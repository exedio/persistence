package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;

@WrapFeature
public class NullabilityFeature
{
	final boolean optional;

	public NullabilityFeature(boolean optional)
	{
		this.optional=optional;
	}

	@SuppressWarnings("static-method")
	@Wrap(order=10)
	@Nullable()
	public Object allCanReturnNull()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=20)
	@Nonnull
	public Object allCannotReturnNull()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=30, nullability=NullabilityFeature.IfOptional.class)
	public Object onlyOptionalsCanReturnNull()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=40)
	public void allCanTakeNull(@Nullable final Object parameter)
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=50)
	public void allCannotTakeNull(@Nonnull final Object parameter)
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=60)
	public void onlyOptionalsCanTakeNull(final Item item, @Parameter(nullability=NullabilityFeature.IfOptional.class) final Object parameter)
	{
		throw new RuntimeException();
	}

	static class IfOptional implements NullabilityGetter<NullabilityFeature>
	{
		@Override
		public Nullability getNullability(NullabilityFeature feature)
		{
			return feature.optional ? Nullability.NULLABLE : Nullability.NONNULL;
		}
	}

}
