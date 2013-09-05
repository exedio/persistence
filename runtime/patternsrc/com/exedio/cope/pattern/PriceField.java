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

import java.util.Set;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;

public final class PriceField extends Pattern implements Settable<Price>
{
	private static final long serialVersionUID = 1l;

	private final IntegerField integer;
	private final boolean isfinal;
	private final boolean mandatory;

	public PriceField()
	{
		this(new IntegerField());
	}

	private PriceField(final IntegerField integer)
	{
		this.integer = integer;
		addSource(integer, "int", ComputedElement.get());
		this.isfinal = integer.isFinal();
		this.mandatory = integer.isMandatory();
	}

	public PriceField toFinal()
	{
		return new PriceField(integer.toFinal());
	}

	public PriceField optional()
	{
		return new PriceField(integer.optional());
	}

	public PriceField defaultTo(final Price defaultConstant)
	{
		return new PriceField(integer.defaultTo(defaultConstant.store));
	}

	public PriceField range(final Price minimum, final Price maximum)
	{
		return new PriceField(integer.range(minimum.store, maximum.store));
	}

	public PriceField min(final Price minimum)
	{
		return new PriceField(integer.min(minimum.store));
	}

	public PriceField max(final Price maximum)
	{
		return new PriceField(integer.max(maximum.store));
	}

	public IntegerField getInt()
	{
		return integer;
	}

	public boolean isInitial()
	{
		return integer.isInitial();
	}

	public boolean isFinal()
	{
		return isfinal;
	}

	public boolean isMandatory()
	{
		return mandatory;
	}

	public Price getDefaultConstant()
	{
		return Price.storeOf(integer.getDefaultConstant());
	}

	public Price getMinimum()
	{
		return Price.storeOf(integer.getMinimum());
	}

	public Price getMaximum()
	{
		return Price.storeOf(integer.getMaximum());
	}

	@Deprecated
	public Class<?> getInitialType()
	{
		return Price.class;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return integer.getInitialExceptions();
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	public Price get(final Item item)
	{
		return Price.storeOf(integer.get(item));
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(final Item item, final Price value)
	{
		if(isfinal)
			throw FinalViolationException.create(this, item);
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, item);

		integer.set(item, value!=null ? value.store : null);
	}

	public SetValue<Price> map(final Price value)
	{
		return SetValue.map(this, value);
	}

	public SetValue<?>[] execute(final Price value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{ integer.map(value!=null ? value.store : null) };
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #min(Price)} instead.
	 */
	@Deprecated
	public PriceField min(final int minimum)
	{
		return new PriceField(integer.min(minimum));
	}
}
