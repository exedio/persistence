/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public class NullabilityFeature
{
	final boolean optional;

	public NullabilityFeature(final boolean optional)
	{
		this.optional = optional;
	}

	@Wrap(order=10)
	public void defaultReturnAndParameter(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=100, nullability=IfOptional.class)
	public Object conditionalNullableReturn()
	{
		throw new RuntimeException();
	}

	@Wrap(order=110)
	public void conditionalNullableParameter(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") @Parameter(nullability=IfOptional.class) final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=200, doc="This method can return {nullResult}.", docReturn="may be {nullResult}")
	@Nullable
	public Object javaxNullableReturn()
	{
		throw new RuntimeException();
	}

	@Wrap(order=210)
	@Nonnull
	public Object javaxNonnullReturn()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("NullableProblems")
	@Wrap(order=220)
	@Nullable
	@Nonnull
	public Object javaxNullableNonnullReturn()
	{
		throw new RuntimeException();
	}

	@Wrap(order=300)
	public void javaxNullableParameter(@Nullable @SuppressWarnings("unused") final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=310)
	public void javaxNonnullParameter(@Nonnull @SuppressWarnings("unused") final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=400)
	public @org.jspecify.annotations.Nullable Object jspecifyNullableReturn()
	{
		throw new RuntimeException();
	}

	@Wrap(order=410)
	public @org.jspecify.annotations.NonNull Object jspecifyNonnullReturn()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("NullableProblems")
	@Wrap(order=420)
	public @org.jspecify.annotations.Nullable @org.jspecify.annotations.NonNull Object jspecifyNullableNonnullReturn()
	{
		throw new RuntimeException();
	}

	@Wrap(order=500)
	public void jspecifyNullableParameter(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final @org.jspecify.annotations.Nullable Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=510)
	public void jspecifyNonnullParameter(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") final @org.jspecify.annotations.NonNull Object parameter)
	{
		throw new RuntimeException();
	}

	private static final class IfOptional implements NullabilityGetter<NullabilityFeature>
	{
		@Override
		public Nullability getNullability(final NullabilityFeature feature)
		{
			return Nullability.forOptional(feature.optional);
		}
	}
}
