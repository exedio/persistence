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

	@Wrap(order=10, doc="This method can return null.", docReturn="may be null")
	@Nullable
	public Object allCanReturnNull()
	{
		throw new RuntimeException();
	}

	@Wrap(order=20)
	@Nonnull
	public Object allCannotReturnNull()
	{
		throw new RuntimeException();
	}

	@Wrap(order=30, nullability=IfOptional.class)
	public Object onlyOptionalsCanReturnNull()
	{
		throw new RuntimeException();
	}

	@Wrap(order=40)
	public void allCanTakeNull(@Nullable @SuppressWarnings("unused") final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=50)
	public void allCannotTakeNull(@Nonnull @SuppressWarnings("unused") final Object parameter)
	{
		throw new RuntimeException();
	}

	@Wrap(order=60)
	public void onlyOptionalsCanTakeNull(
			@SuppressWarnings("unused") final Item item,
			@SuppressWarnings("unused") @Parameter(nullability=IfOptional.class) final Object parameter)
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
