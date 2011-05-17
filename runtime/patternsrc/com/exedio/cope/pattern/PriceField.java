/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.List;
import java.util.Set;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.WrapperThrown;
import com.exedio.cope.misc.ComputedElement;

public final class PriceField extends Pattern implements Settable<Price>
{
	private static final long serialVersionUID = 1l;

	private final IntegerField integer;
	private final boolean isfinal;
	private final boolean optional;

	public PriceField()
	{
		this(new IntegerField());
	}

	private PriceField(final IntegerField integer)
	{
		this.integer = integer;
		addSource(integer, "int", ComputedElement.get());
		this.isfinal = integer.isFinal();
		this.optional = !integer.isMandatory();
	}

	public PriceField toFinal()
	{
		return new PriceField(integer.toFinal());
	}

	public PriceField optional()
	{
		return new PriceField(integer.optional());
	}

	public PriceField min(final int minimum)
	{
		return new PriceField(integer.min(minimum));
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

	public Class getInitialType()
	{
		return Price.class;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return integer.getInitialExceptions();
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(PriceField.class, this, super.getWrappers());
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	public Price get(final Item item)
	{
		return Price.storeOf(integer.get(item));
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=Thrown.class,
			hide=FinalGetter.class)
	public void set(final Item item, final Price value)
	{
		if(isfinal)
			throw new FinalViolationException(this, this, item);
		if(value==null && !optional)
			throw new MandatoryViolationException(this, this, item);

		integer.set(item, value!=null ? value.store : null);
	}

	private static final class FinalGetter implements BooleanGetter<PriceField>
	{
		public boolean get(final PriceField feature)
		{
			return feature.isFinal();
		}
	}

	private static final class Thrown implements WrapperThrown<PriceField>
	{
		public Set<Class<? extends Throwable>> get(final PriceField feature)
		{
			return feature.getInitialExceptions();
		}
	}

	public SetValue<Price> map(final Price value)
	{
		return new SetValue<Price>(this, value);
	}

	public SetValue[] execute(final Price value, final Item exceptionItem)
	{
		if(value==null && !optional)
			throw new MandatoryViolationException(this, this, exceptionItem);

		return new SetValue[]{ integer.map(value!=null ? value.store : null) };
	}
}
