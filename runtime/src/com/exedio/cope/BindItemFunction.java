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

package com.exedio.cope;

import static com.exedio.cope.CastUtils.toIntCapped;

final class BindItemFunction<E extends Item> extends BindFunction<E>
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	private final ItemFunction<E> function;

	private BindItemFunction(final ItemFunction<E> function, final Join join)
	{
		super(function, join);
		this.function = function;
	}

	static <E extends Item> BindItemFunction<E> create(final This<E> function, final Join join)
	{
		return new BindItemFunction<>(function, join);
	}

	static <E extends Item> BindItemFunction<E> create(final ItemField<E> function, final Join join)
	{
		return new BindItemFunction<>(function, join);
	}

	@Override
	public Type<E> getValueType()
	{
		return function.getValueType();
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		function.appendType(bf, this.join);
	}

	@Override
	public boolean needsCheckTypeColumn()
	{
		return function.needsCheckTypeColumn();
	}

	@Override
	@Deprecated
	public int checkTypeColumn()
	{
		return toIntCapped(checkTypeColumnL());
	}

	@Override
	public long checkTypeColumnL()
	{
		return function.checkTypeColumnL();
	}

	@Override
	public Statement checkTypeColumnStatement(final Statement.Mode mode)
	{
		return function.checkTypeColumnStatement(mode);
	}

	// convenience methods for conditions and views ---------------------------------

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public ItemFunction<E> bind(final Join join)
	{
		return this;
	}
}
