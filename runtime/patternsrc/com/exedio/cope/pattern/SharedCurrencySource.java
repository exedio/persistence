/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;

final class SharedCurrencySource<C extends Money.Currency> extends CurrencySource<C>
{
	private final FunctionField<C> currency;

	SharedCurrencySource(final FunctionField<C> currency)
	{
		this.currency = requireNonNull(currency, "currency");
		if(!currency.isMandatory())
			throw new IllegalArgumentException("currency must be mandatory");
	}

	@Override
	CurrencySource<C> copy()
	{
		return this;
	}

	@Override
	CurrencySource<C> toFinal()
	{
		return this;
	}

	@Override
	CurrencySource<C> optional()
	{
		return this;
	}

	@Override
	FunctionField<C> getField()
	{
		return currency;
	}

	@Override
	C get(final Item item)
	{
		return currency.get(item);
	}
}
