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

public final class BindItemFunction<E extends Item> extends BindFunction<E>
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	final ItemFunction<E> itemFunction;

	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see ItemFunction#bind(Join)
	 */
	public BindItemFunction(final ItemFunction<E> function, final Join join)
	{
		super(function, join);
		this.itemFunction = function;
	}

	@Override
	public Type<E> getValueType()
	{
		return itemFunction.getValueType();
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		itemFunction.appendType(bf, this.join);
	}

	@Override
	public boolean needsCheckTypeColumn()
	{
		return itemFunction.needsCheckTypeColumn();
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
		return itemFunction.checkTypeColumnL();
	}

	// convenience methods for conditions and views ---------------------------------

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public BindItemFunction<E> bind(final Join join)
	{
		return this;
	}
}
