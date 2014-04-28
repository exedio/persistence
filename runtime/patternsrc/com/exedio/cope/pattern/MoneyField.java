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

import com.exedio.cope.CheckingSettable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Set;

public final class MoneyField<C extends Money.Currency> extends Pattern implements Settable<Money<C>>, CheckingSettable<Money<C>> // TODO currency
{
	private static final long serialVersionUID = 1l;

	public static <C extends Money.Currency> MoneyField<C> shared(final FunctionField<C> currency)
	{
		return new MoneyField<>(new PriceField(), new SharedCurrencySource<>(currency));
	}

	public static <C extends Money.Currency> MoneyField<C> exclusive(final FunctionField<C> currency)
	{
		return new MoneyField<>(new PriceField(), new ExclusiveCurrencySource<>(currency));
	}

	private final PriceField amount;
	@SuppressFBWarnings("SE_BAD_FIELD")
	private final CurrencySource<C> currency;
	private final boolean isfinal;
	private final boolean mandatory;

	private MoneyField(final PriceField amount, final CurrencySource<C> currency)
	{
		this.amount = amount;
		addSource(amount, "amount", CustomAnnotatedElement.create(CopeSchemaNameEmpty.get()));
		this.isfinal = amount.isFinal();
		this.mandatory = amount.isMandatory();
		this.currency = currency;
		if(currency instanceof ExclusiveCurrencySource<?>)
			addSource(currency.getField(), "currency");
	}

	public MoneyField<C> toFinal()
	{
		return new MoneyField<>(amount.toFinal(), currency.toFinal());
	}

	public MoneyField<C> optional()
	{
		return new MoneyField<>(amount.optional(), currency.optional());
	}

	public MoneyField<C> minZero()
	{
		return new MoneyField<>(amount.min(Price.ZERO), currency.copy());
	}

	public PriceField getAmount()
	{
		return amount;
	}

	public FunctionField<C> getCurrency()
	{
		return currency.getField();
	}

	@Override
	public boolean isInitial()
	{
		return amount.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isMandatory()
	{
		return mandatory;
	}

	@Override
	@Deprecated
	public Class<?> getInitialType()
	{
		return Money.class;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return amount.getInitialExceptions();
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	public Money<C> get(final Item item)
	{
		final Price amountResult = amount.get(item);
		return
			amountResult!=null
			? Money.valueOf(amountResult, currency.get(item))
			: null;
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(final Item item, final Money<C> value)
	{
		if(isfinal)
			throw FinalViolationException.create(this, item);

		if(value==null)
		{
			if(mandatory)
				throw MandatoryViolationException.create(this, item);

			if(currency instanceof SharedCurrencySource<?>)
			{
				amount.set(item, null);
			}
			else
			{
				item.set(amount.map(null), currency.getField().map(null));
			}
		}
		else
		{
			if(currency instanceof SharedCurrencySource<?>)
			{
				{
					final C expectedCurrency = currency.get(item);
					if(!value.getCurrency().equals(expectedCurrency))
						throw new IllegalCurrencyException(this, item, value, expectedCurrency);
				}
				amount.set(item, value.amountWithoutCurrency());
			}
			else
			{
				item.set(
						amount.map(value.amountWithoutCurrency()),
						currency.getField().map(value.getCurrency()));
			}
		}
	}

	@Override
	public SetValue<Money<C>> map(final Money<C> value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final Money<C> value, final Item exceptionItem)
	{
		return execute(value, exceptionItem, new SetValue<?>[]{});
	}

	@Override
	public SetValue<?>[] execute(final Money<C> value, final Item exceptionItem, final SetValue<?>[] sources)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);


		if(currency instanceof SharedCurrencySource<?>)
		{
			if(value!=null)
			{
				final SetValue<C> c = getFirst(sources, currency.getField());
				final C expectedCurrency = c==null ? currency.get(exceptionItem) : c.value;
				if(!value.getCurrency().equals(expectedCurrency))
					throw new IllegalCurrencyException(this, exceptionItem, value, expectedCurrency);

				return new SetValue<?>[]{
					amountExecute( value.amountWithoutCurrency(), exceptionItem )
				};
			}
			else
			{
				return new SetValue<?>[]{
					amountExecute( null, exceptionItem )
				};
			}
		}
		else
		{
			return new SetValue<?>[]{
				amountExecute( value!=null ? value.amountWithoutCurrency() : null, exceptionItem ),
				currency.getField().map( value!=null ? value.getCurrency() : null )
			};
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <E> SetValue<E> getFirst(final SetValue<?>[] setValues, final Settable<E> settable)
	{
		for(final SetValue setValue : setValues)
			if(settable==setValue.settable)
				return setValue;
		return null;
	}

	private SetValue<?> amountExecute(final Price amount, final Item exceptionItem)
	{
		final SetValue<?>[] array = this.amount.execute(amount, exceptionItem);
		if(array.length!=1)
			throw new IllegalArgumentException(Arrays.toString(array));
		return array[0];
	}

	// convenience methods for conditions and views ---------------------------------

	public final IsNullCondition<?> isNull()
	{
		return amount.isNull();
	}

	public final IsNullCondition<?> isNotNull()
	{
		return amount.isNotNull();
	}
}
