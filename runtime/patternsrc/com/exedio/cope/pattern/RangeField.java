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

import com.exedio.cope.Condition;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperSuppressor;

public final class RangeField<E> extends Pattern implements Settable<Range<E>>
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<E> from;
	private final FunctionField<E> to;

	private RangeField(final FunctionField<E> borderTemplate)
	{
		addSource(from = borderTemplate.copy(), "from");
		addSource(to   = borderTemplate.copy(), "to");
	}

	public static final <E> RangeField<E> newRange(final FunctionField<E> borderTemplate)
	{
		if(!borderTemplate.isMandatory())
			throw new IllegalArgumentException("optional borderTemplate not yet implemented");
		if(borderTemplate.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("unique borderTemplate is not supported");

		return new RangeField<E>(borderTemplate);
	}

	public FunctionField<E> getFrom()
	{
		return from;
	}

	public FunctionField<E> getTo()
	{
		return to;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(RangeField.class, this, super.getWrappers());
	}

	@Wrap(order=10)
	public Range<E> get(final Item item)
	{
		return new Range<E>(from.get(item), to.get(item));
	}

	@Wrap(order=20, hide=FinalSuppressor.class)
	public void set(final Item item, final Range<? extends E> value)
	{
		item.set(
				this.from.map(value.from),
				this.to  .map(value.to  ));
	}

	@Wrap(order=30)
	public E getFrom(final Item item)
	{
		return from.get(item);
	}

	@Wrap(order=40)
	public E getTo(final Item item)
	{
		return to.get(item);
	}

	@Wrap(order=50, hide=FinalSuppressor.class)
	public void setFrom(final Item item, final E from)
	{
		this.from.set(item, from);
	}

	@Wrap(order=60, hide=FinalSuppressor.class)
	public void setTo(final Item item, final E to)
	{
		this.to.set(item, to);
	}

	private static final class FinalSuppressor implements WrapperSuppressor<RangeField>
	{
		public boolean get(final RangeField feature)
		{
			return feature.isFinal();
		}
	}

	public Condition contains(final E value)
	{
		return from.isMandatory()
			? from.lessOrEqual(value).and(to.greaterOrEqual(value))
			: from.isNull().or(from.lessOrEqual(value)).and(to.isNull().or(to.greaterOrEqual(value)));
	}

	public SetValue<Range<E>> map(final Range<E> value)
	{
		return new SetValue<Range<E>>(this, value);
	}

	public SetValue[] execute(final Range<E> value, final Item exceptionItem)
	{
		return new SetValue[]{
				from.map(value.from),
				to  .map(value.to  )};
	}

	public boolean isFinal()
	{
		return from.isFinal();
	}

	public boolean isInitial()
	{
		return from.isInitial();
	}

	public java.lang.reflect.Type getInitialType()
	{
		return Wrapper.generic(Range.class, from.getValueClass());
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return from.getInitialExceptions();
	}
}
