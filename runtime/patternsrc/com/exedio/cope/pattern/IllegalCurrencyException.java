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

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.FieldValues;
import com.exedio.cope.instrument.ConstructorComment;
import com.exedio.cope.pattern.Money.Currency;
import java.io.Serial;

@ConstructorComment("if {0} violates its currency constraint.")
public final class IllegalCurrencyException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	static void check(
			final MoneyField<?> feature,
			final FieldValues item,
			final Money<?> value,
			final Currency allowed)
	{
		if(!value.getCurrency().equals(allowed))
			throw new IllegalCurrencyException(feature, item, value, allowed);
	}


	private final MoneyField<?> feature;
	private final Money<?> value;
	private final Currency allowed;

	private IllegalCurrencyException(
			final MoneyField<?> feature,
			final FieldValues item,
			final Money<?> value,
			final Currency allowed)
	{
		super(item.getBackingItem(), null);
		this.feature = feature;
		this.value = value;
		this.allowed = allowed;
	}

	@Override
	public MoneyField<?> getFeature()
	{
		return feature;
	}

	public Money<?> getValue()
	{
		return value;
	}

	public Currency getAllowed()
	{
		return allowed;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return
			"illegal currency at '" + value +
			'\'' + getItemPhrase() +
			(withFeature ? (" for " + feature) : "") +
			", allowed is '" + allowed + '\'';
	}
}
