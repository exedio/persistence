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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;

public final class RangeField<E extends Comparable<E>> extends Pattern implements Settable<Range<E>>
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<E> from;
	private final FunctionField<E> to;
	private final CheckConstraint unison;

	private RangeField(final FunctionField<E> borderTemplate)
	{
		addSource(from = borderTemplate.copy(), "from");
		addSource(to   = borderTemplate.copy(), "to");
		addSource(unison = new CheckConstraint(Cope.or(isNull(from), isNull(to), from.lessOrEqual(to))), "unison");
	}

	private static Condition isNull(final FunctionField field)
	{
		return field.isMandatory() ? Condition.FALSE : field.isNull();
	}

	public static final <E extends Comparable<E>> RangeField<E> create(final FunctionField<E> borderTemplate)
	{
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

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(RangeField.class, this, super.getWrappers());
	}

	@Wrap(order=10)
	public Range<E> get(final Item item)
	{
		return Range.newRange(from.get(item), to.get(item));
	}

	@Wrap(order=20, hide=FinalGetter.class)
	public void set(final Item item, final Range<? extends E> value)
	{
		item.set(
				SetValue.map(this.from, (E)value.getFrom()),
				SetValue.map(this.to  , (E)value.getTo  ()));
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

	@Wrap(order=50, hide=FinalGetter.class)
	public void setFrom(final Item item, final E from)
	{
		this.from.set(item, from);
	}

	@Wrap(order=60, hide=FinalGetter.class)
	public void setTo(final Item item, final E to)
	{
		this.to.set(item, to);
	}

	@Wrap(order=70)
	public boolean doesContain(final Item item, final E value)
	{
		if(value==null)
			throw new NullPointerException("value");

		final E from = getFrom(item);
		if(from!=null && from.compareTo(value)>0)
			return false;

		final E to   = getTo  (item);
		if(to  !=null && to  .compareTo(value)<0)
			return false;

		return true;
	}

	private static final class FinalGetter implements BooleanGetter<RangeField>
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

	@Deprecated
	public SetValue<Range<E>> map(final Range<E> value)
	{
		return SetValue.map(this, value);
	}

	public SetValue[] execute(final Range<E> value, final Item exceptionItem)
	{
		return new SetValue[]{
				SetValue.map(from, value.getFrom()),
				SetValue.map(to  , value.getTo  ())};
	}

	public boolean isFinal()
	{
		return from.isFinal();
	}

	public boolean isMandatory()
	{
		return from.isMandatory();
	}

	public boolean isInitial()
	{
		return from.isInitial();
	}

	@Deprecated
	public java.lang.reflect.Type getInitialType()
	{
		return Wrapper.generic(Range.class, from.getValueClass());
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return from.getInitialExceptions();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField)} instead
	 */
	@Deprecated
	public static final <E extends Comparable<E>> RangeField<E> newRange(final FunctionField<E> borderTemplate)
	{
		return create(borderTemplate);
	}
}
