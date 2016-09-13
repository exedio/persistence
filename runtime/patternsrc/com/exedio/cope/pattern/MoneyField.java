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

import com.exedio.cope.CheckingSettable;
import com.exedio.cope.Field;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.CopeSchemaNameElement;
import com.exedio.cope.misc.ReflectionTypes;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class MoneyField<C extends Money.Currency> extends Pattern implements Settable<Money<C>>, CheckingSettable<Money<C>> // TODO currency
{
	private static final long serialVersionUID = 1l;

	public static <C extends Money.Currency> MoneyField<C> fixed(final C currency)
	{
		return create(new FixedCurrencySource<>(currency));
	}

	public static <C extends Money.Currency> MoneyField<C> shared(final FunctionField<C> currency)
	{
		return create(new SharedCurrencySource<>(currency));
	}

	public static <C extends Money.Currency> MoneyField<C> exclusive(final FunctionField<C> currency)
	{
		return create(new ExclusiveCurrencySource<>(currency));
	}

	private static <C extends Money.Currency> MoneyField<C> create(final CurrencySource<C> currency)
	{
		return new MoneyField<>(new PriceField(), currency);
	}


	private final PriceField amount;
	@SuppressFBWarnings("SE_BAD_FIELD")
	private final CurrencySource<C> currency;
	private final boolean isfinal;
	private final boolean mandatory;

	private MoneyField(final PriceField amount, final CurrencySource<C> currency)
	{
		this.amount = amount;
		addSource(amount, "amount", CustomAnnotatedElement.create(CopeSchemaNameElement.getEmpty()));
		this.isfinal = amount.isFinal();
		this.mandatory = amount.isMandatory();
		this.currency = currency;
		// TODO polymorhism of CurrencySource
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

	/**
	 * BEWARE:
	 * This method returns null, if the currency is not stored
	 * in a field.
	 * @see #getCurrencyValue()
	 */
	public FunctionField<C> getCurrencyField()
	{
		return currency.getField();
	}

	/**
	 * BEWARE:
	 * This method returns null, if the currency is stored
	 * in a field.
	 * @see #getCurrencyField()
	 */
	public C getCurrencyValue()
	{
		return currency.getValue();
	}

	public Class<C> getCurrencyClass()
	{
		return currency.getInitialType();
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
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(Money.class, currency.getInitialType());
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return amount.getInitialExceptions();
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	@Nullable
	public Money<C> get(@Nonnull final Item item)
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
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Money<C> value)
	{
		FinalViolationException.check(this, item);

		if(value==null)
		{
			if(mandatory)
				throw MandatoryViolationException.create(this, item);

			// TODO polymorhism of CurrencySource
			if(currency instanceof FixedCurrencySource<?> ||
				currency instanceof SharedCurrencySource<?>)
			{
				amount.set(item, null);
			}
			else if(currency instanceof ExclusiveCurrencySource<?>)
			{
				item.set(amount.map(null), currency.getField().map(null));
			}
			else
			{
				throw new RuntimeException("" + currency);
			}
		}
		else
		{
			// TODO polymorhism of CurrencySource
			if(currency instanceof FixedCurrencySource<?> ||
				currency instanceof SharedCurrencySource<?>)
			{
				IllegalCurrencyException.check(this, item, value, currency.get(item));
				amount.set(item, value.amountWithoutCurrency());
			}
			else if(currency instanceof ExclusiveCurrencySource<?>)
			{
				item.set(
						amount.map(value.amountWithoutCurrency()),
						currency.getField().map(value.getCurrency()));
			}
			else
			{
				throw new RuntimeException("" + currency);
			}
		}
	}

	@Override
	public SetValue<?>[] execute(final Money<C> value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);


		// TODO polymorhism of CurrencySource
		if(currency instanceof SharedCurrencySource<?>)
		{
			if(value!=null)
			{
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
		else if(currency instanceof FixedCurrencySource<?>)
		{
			if(value!=null)
			{
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
		else if(currency instanceof ExclusiveCurrencySource<?>)
		{
			return new SetValue<?>[]{
				amountExecute( value!=null ? value.amountWithoutCurrency() : null, exceptionItem ),
				currency.getField().map( value!=null ? value.getCurrency() : null )
			};
		}
		else
		{
			throw new RuntimeException("" + currency);
		}
	}

	private SetValue<?> amountExecute(final Price amount, final Item exceptionItem)
	{
		final SetValue<?>[] array = this.amount.execute(amount, exceptionItem);
		if(array.length!=1)
			throw new IllegalArgumentException(Arrays.toString(array));
		return array[0];
	}

	@Override
	public void check(final Money<C> value, final Item item, final Map<Field<?>, Object> sources)
	{
		// TODO polymorhism of CurrencySource
		if(currency instanceof SharedCurrencySource<?>)
		{
			if(value!=null)
			{
				final Field<C> f = currency.getField();
				IllegalCurrencyException.check(this, item, value,
						sources.containsKey(f) ? get(sources, f) : currency.get(item));
			}
		}
		else if(currency instanceof FixedCurrencySource<?>)
		{
			if(value!=null)
			{
				IllegalCurrencyException.check(this, item, value, currency.get(null));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <E> E get(final Map<Field<?>, Object> sources, final Field<E> field)
	{
		return (E)sources.get(field);
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getCurrencyField()} instead
	 */
	@Deprecated
	public FunctionField<C> getCurrency()
	{
		return getCurrencyField();
	}
}
