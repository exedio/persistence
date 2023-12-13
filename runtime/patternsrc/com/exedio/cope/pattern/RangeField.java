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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ReflectionTypes;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class RangeField<E extends Comparable<E>> extends Pattern implements Settable<Range<E>>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<E> from;
	private final FunctionField<E> to;
	private final CheckConstraint unison;
	private final boolean isfinal;

	private RangeField(final FunctionField<E> from, final FunctionField<E> to)
	{
		this.from = addSourceFeature(from, "from");
		this.to   = addSourceFeature(to, "to");
		this.unison = addSourceFeature(new CheckConstraint(from.lessOrEqual(to)), "unison");
		this.isfinal = from.isFinal();
	}

	public static <E extends Comparable<E>> RangeField<E> create(final FunctionField<E> borderTemplate)
	{
		if(borderTemplate.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("unique borderTemplate is not supported");

		return new RangeField<>(borderTemplate.copy(), borderTemplate.copy());
	}

	@Override
	public RangeField<E> copy(final CopyMapper mapper)
	{
		return new RangeField<>(mapper.copy(from), mapper.copy(to));
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

	@Wrap(order=10)
	@Nonnull
	public Range<E> get(@Nonnull final Item item)
	{
		return Range.valueOf(from.get(item), to.get(item));
	}

	@Wrap(order=20,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Nonnull final Range<? extends E> value)
	{
		FinalViolationException.check(this, item);

		//noinspection unchecked
		item.set(
				SetValue.map(from, value.getFrom()),
				SetValue.map(to,   value.getTo  ()));
	}

	@Wrap(order=30, nullability=NullableIfOptionalFrom.class)
	public E getFrom(@Nonnull final Item item)
	{
		return from.get(item);
	}

	@Wrap(order=40, nullability=NullableIfOptionalFrom.class)
	public E getTo(@Nonnull final Item item)
	{
		return to.get(item);
	}

	@Wrap(order=50,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void setFrom(@Nonnull final Item item, @Parameter(nullability=NullableIfOptionalFrom.class) final E from)
	{
		FinalViolationException.check(this, item);

		this.from.set(item, from);
	}

	@Wrap(order=60,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void setTo(@Nonnull final Item item, @Parameter(nullability=NullableIfOptionalFrom.class) final E to)
	{
		FinalViolationException.check(this, item);

		this.to.set(item, to);
	}

	private static final class NullableIfOptionalFrom implements NullabilityGetter<RangeField<?>>
	{
		@Override
		public Nullability getNullability(final RangeField<?> feature)
		{
			return Nullability.forMandatory(feature.getFrom().isMandatory());
		}
	}

	@Wrap(order=70)
	@SuppressWarnings("RedundantIfStatement")
	public boolean doesContain(@Nonnull final Item item, @Nonnull final E value)
	{
		requireNonNull(value, "value");

		final E from = getFrom(item);
		if(from!=null && from.compareTo(value)>0)
			return false;

		final E to   = getTo  (item);
		if(to  !=null && to  .compareTo(value)<0)
			return false;

		return true;
	}

	public Condition contains(final E value)
	{
		return from.isMandatory()
			? from.lessOrEqual(value).and(to.greaterOrEqual(value))
			: from.isNull().or(from.lessOrEqual(value)).and(to.isNull().or(to.greaterOrEqual(value)));
	}

	@Override
	public SetValue<?>[] execute(final Range<E> value, final Item exceptionItem)
	{
		return new SetValue<?>[]{
				SetValue.map(from, value.getFrom()),
				SetValue.map(to,   value.getTo  ())};
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	/**
	 * Does always return {@code true}.
	 * A {@code RangeField} may store {@link Range#valueOf(Comparable, Comparable) Range.valueOf(null, null)},
	 * but never null.
	 */
	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(Range.class, from.getValueClass());
	}

	@Override
	public boolean isInitial()
	{
		return from.isInitial();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return from.getInitialExceptions();
	}
}
