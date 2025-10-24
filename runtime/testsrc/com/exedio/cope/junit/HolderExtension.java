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

package com.exedio.cope.junit;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.Holder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public abstract class HolderExtension<E> implements AfterEachCallback, ParameterResolver
{
	private final Holder<E> holder;

	protected HolderExtension(final Holder<E> holder)
	{
		this.holder = requireNonNull(holder, "holder");
	}

	public final void override(final E value)
	{
		holder.override(value);
	}

	@Override
	public final void afterEach(final ExtensionContext context)
	{
		holder.clearOverride();
	}

	@Override
	public final boolean supportsParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext)
			throws ParameterResolutionException
	{
		return getClass()==parameterContext.getParameter().getType();
	}

	@Override
	public final Object resolveParameter(
			final ParameterContext parameterContext,
			final ExtensionContext extensionContext) throws ParameterResolutionException
	{
		return this;
	}
}
