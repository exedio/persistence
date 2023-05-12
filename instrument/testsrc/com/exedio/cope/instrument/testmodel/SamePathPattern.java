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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.instrument.WrapInterim;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class SamePathPattern<T extends Item> extends Pattern
{
	@WrapInterim
	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private final ItemField<T> field;

	@WrapInterim
	private SamePathPattern(final ItemField<T> field)
	{
		this.field = field;
	}

	@WrapInterim
	static <T extends Item> SamePathPattern<T> create(final ItemField<T> field)
	{
		return new SamePathPattern<>(field);
	}

	@Wrap(order=10)
	@WrapInterim
	@Nullable
	@SuppressWarnings("unused")
	public String wrappedNullable(
			@Nullable final String parameter)
	{
		return null;
	}

	@Wrap(order=20)
	@WrapInterim
	@Nonnull
	@SuppressWarnings("unused")
	public String wrappedNonnull(
			@Nonnull final String parameter)
	{
		return "NOTHING";
	}

	private static final long serialVersionUID = 1l;
}
