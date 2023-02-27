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

package com.exedio.cope.pattern;

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.misc.Conditions.unisonNull;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.FieldValues;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;

final class ExclusiveCurrencySource<C extends Money.Currency> extends CurrencySource<C>
{
	private final FunctionField<C> currency;

	ExclusiveCurrencySource(final FunctionField<C> currency)
	{
		this.currency = requireNonNull(currency, "currency");
	}

	@Override
	FunctionField<C> sourceToBeAdded()
	{
		return currency;
	}

	@Override
	Condition unison(final PriceField amount)
	{
		if(amount.isMandatory())
			return null;

		return unisonNull(asList(amount.getInt(), currency));
	}

	@Override CurrencySource<C> copy()     { return new ExclusiveCurrencySource<>(currency.copy()); }
	@Override CurrencySource<C> toFinal()  { return new ExclusiveCurrencySource<>(currency.toFinal()); }
	@Override CurrencySource<C> optional() { return new ExclusiveCurrencySource<>(currency.optional()); }

	@Override
	FunctionField<C> getField()
	{
		return currency;
	}

	@Override
	Class<C> getInitialType()
	{
		return currency.getValueClass();
	}

	@Override
	C get(final Item item)
	{
		return currency.get(item);
	}

	@Override
	SetValue<?>[] execute(final SetValue<?> amount, final Money<C> value)
	{
		return new SetValue<?>[]{
			amount,
			map(currency, value!=null ? value.getCurrency() : null)
		};
	}

	@Override
	void check(final MoneyField<C> field, final Money<C> value, final FieldValues fieldValues)
	{
		// empty
	}
}
