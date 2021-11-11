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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CheckingSettable;
import com.exedio.cope.Condition;
import com.exedio.cope.FieldValues;
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
import java.util.Arrays;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
@SuppressWarnings("RedundantInterfaceDeclaration") // TODO required by deficiency of instrumentor
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
	private final CurrencySource<C> currency;
	private final boolean isfinal;
	private final boolean mandatory;
	private final CheckConstraint unison;

	private MoneyField(final PriceField amount, final CurrencySource<C> currency)
	{
		this.amount = addSourceFeature(amount, "amount", CustomAnnotatedElement.create(CopeSchemaNameElement.getEmpty()));
		this.isfinal = amount.isFinal();
		this.mandatory = amount.isMandatory();
		this.currency = currency;

		final FunctionField<?> currencySourceToBeAdded = currency.sourceToBeAdded();
		if(currencySourceToBeAdded!=null)
			addSourceFeature(currencySourceToBeAdded, "currency");

		final Condition unison = currency.unison(amount);
		this.unison = unison!=null ? addSourceFeature(new CheckConstraint(unison), "unison") : null;
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

	public CheckConstraint getUnison()
	{
		return unison;
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

	@Override
	protected void onMount()
	{
		super.onMount();
		currency.onMount(this);
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public Money<C> get(@Nonnull final Item item)
	{
		final Price amountResult = amount.get(item);
		return
			amountResult!=null
			? Money.valueOf(amountResult, currency.get(item))
			: null;
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Money<C> value)
	{
		item.set(SetValue.map(this, value));
	}

	@Override
	public SetValue<?>[] execute(final Money<C> value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);

		final Price amount = value!=null ? value.amountWithoutCurrency() : null;
		final SetValue<?>[] array = this.amount.execute(amount, exceptionItem);
		if(array.length!=1)
			throw new IllegalArgumentException(Arrays.toString(array));

		return currency.execute(array[0], value);
	}

	@Override
	public void check(final Money<C> value, final FieldValues fieldValues)
	{
		if(value!=null)
			currency.check(this, value, fieldValues);
	}

	// convenience methods for conditions and views ---------------------------------

	public IsNullCondition<?> isNull()
	{
		return amount.isNull();
	}

	public IsNullCondition<?> isNotNull()
	{
		return amount.isNotNull();
	}
}
