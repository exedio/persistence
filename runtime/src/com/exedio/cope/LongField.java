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

package com.exedio.cope;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.util.Random;
import java.util.Set;

public final class LongField extends NumberField<Long>
{
	private static final long serialVersionUID = 1l;

	private final long minimum;
	private final long maximum;

	private LongField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Long> defaultSource,
			final long minimum,
			final long maximum)
	{
		super(isfinal, optional, unique, copyFrom, Long.class, defaultSource);
		this.minimum = minimum;
		this.maximum = maximum;

		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');

		mountDefaultSource();
	}

	private static final class DefaultRandom extends DefaultSource<Long>
	{
		private final Random source;
		private Boolean absolute = null;

		DefaultRandom(final Random source)
		{
			this.source = requireNonNull(source, "source");
		}

		@Override
		Long generate(final long now)
		{
			final long raw = source.nextLong();
			if(absolute && (raw<0))
				return raw+1+MAX_VALUE;
			else
				return raw;
		}

		@Override
		DefaultSource<Long> forNewField()
		{
			return new DefaultRandom(source);
		}

		@Override
		void mount(final FunctionField<Long> field)
		{
			final LongField f = (LongField)field;
			final long minimum = f.getMinimum();
			final long maximum = f.getMaximum();

			if(minimum!=MIN_VALUE && minimum!=0l)
				throw new IllegalArgumentException("defaultToRandom supports minimum of " + MIN_VALUE + " or 0 only, but was " + minimum);
			if(maximum!=MAX_VALUE)
				throw new IllegalArgumentException("defaultToRandom supports maximum of " + MAX_VALUE + " only, but was " + maximum);
			assert this.absolute==null;

			this.absolute = (minimum==0l);
		}
	}

	public LongField()
	{
		this(false, false, false, null, null, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public LongField copy()
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public LongField toFinal()
	{
		return new LongField(true, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public LongField optional()
	{
		return new LongField(isfinal, true, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public LongField unique()
	{
		return new LongField(isfinal, optional, true, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public LongField nonUnique()
	{
		return new LongField(isfinal, optional, false, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public LongField copyFrom(final ItemField<?> copyFrom)
	{
		return new LongField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource, minimum, maximum);
	}

	@Override
	public LongField noDefault()
	{
		return new LongField(isfinal, optional, unique, copyFrom, null, minimum, maximum);
	}

	@Override
	public LongField defaultTo(final Long defaultConstant)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimum, maximum);
	}

	public LongField defaultToRandom(final Random source)
	{
		return new LongField(isfinal, optional, unique, copyFrom, new DefaultRandom(source), minimum, maximum);
	}

	public LongField range(final long minimum, final long maximum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public LongField min(final long minimum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public LongField max(final long maximum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public long getMinimum()
	{
		return minimum;
	}

	public long getMaximum()
	{
		return maximum;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(minimum!=Long.MIN_VALUE || maximum!=Long.MAX_VALUE)
			result.add(LongRangeViolationException.class);
		return result;
	}

	@Deprecated
	@Override
	public Class<?> getInitialType()
	{
		return optional ? Long.class : long.class;
	}

	public SelectType<Long> getValueType()
	{
		return SimpleSelectType.LONG;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, false, optional, minimum, maximum, true);
	}

	@Override
	Long get(final Row row)
	{
		return (Long)row.get(getColumn());
	}

	@Override
	void set(final Row row, final Long surface)
	{
		row.put(getColumn(), surface);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc="Returns the value of {0}.", hide=OptionalGetter.class)
	public final long getMandatory(final Item item)
	{
		return getMandatoryObject(item).longValue();
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide={FinalSettableGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public final void set(final Item item, final long value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, Long.valueOf(value));
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			hide={OptionalGetter.class, NonUniqueGetter.class})
	public final <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final long value)
	{
		return super.searchUnique(typeClass, Long.valueOf(value));
	}

	@Override
	void checkNotNull(final Long value, final Item exceptionItem) throws LongRangeViolationException
	{
		final long valuePrimitive = value.longValue();
		if(valuePrimitive<minimum)
			throw new LongRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new LongRangeViolationException(this, exceptionItem, value, false, maximum);
	}

	public LongField rangeDigits(final int digits)
	{
		return LongFieldRangeDigits.rangeDigits(this, digits);
	}

	public LongField rangeDigits(final int minimumDigits, final int maximumDigits)
	{
		return LongFieldRangeDigits.rangeDigits(this, minimumDigits, maximumDigits);
	}
}
