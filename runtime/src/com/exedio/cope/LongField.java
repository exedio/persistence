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

package com.exedio.cope;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LongField extends NumberField<Long>
{
	private static final long serialVersionUID = 1l;

	private final long minimum;
	private final long maximum;

	private LongField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Long> defaultS,
			final long minimum,
			final long maximum)
	{
		super(isfinal, optional, Long.class, unique, copyFrom, defaultS);
		this.minimum = minimum;
		this.maximum = maximum;

		if(minimum>maximum)
			throw new IllegalArgumentException("maximum must be at least minimum, but was " + maximum + " and " + minimum);

		mountDefault();
	}

	private static final class DefaultRandom extends DefaultSupplier<Long>
	{
		private final Random source;
		private Boolean absolute = null;

		DefaultRandom(final Random source)
		{
			this.source = requireNonNull(source, "source");
		}

		@Override
		Long generate(final Context ctx)
		{
			final long raw = source.nextLong();
			if(absolute && (raw<0))
				return raw+1+MAX_VALUE;
			else
				return raw;
		}

		@Override
		DefaultSupplier<Long> forNewField()
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
			assert absolute==null;

			absolute = (minimum==0l);
		}
	}

	public LongField()
	{
		this(false, false, false, null, null, MIN_VALUE, MAX_VALUE);
	}

	@Override
	public LongField copy()
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public LongField toFinal()
	{
		return new LongField(true, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public LongField optional()
	{
		return new LongField(isfinal, true, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public LongField unique()
	{
		return new LongField(isfinal, optional, true, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public LongField nonUnique()
	{
		return new LongField(isfinal, optional, false, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public LongField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target));
	}

	private LongField copyFrom(final CopyFrom copyFrom)
	{
		return new LongField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS, minimum, maximum);
	}

	@Override
	public LongField noCopyFrom()
	{
		return new LongField(isfinal, optional, unique, null, defaultS, minimum, maximum);
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

	public LongField rangeEvenIfRedundant(final long minimum, final long maximum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	public LongField range(final long minimum, final long maximum)
	{
		return rangeEvenIfRedundant(minimum, maximum).failOnRedundantRange();
	}

	public LongField min(final long minimum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum).failOnRedundantRange();
	}

	public LongField max(final long maximum)
	{
		return new LongField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum).failOnRedundantRange();
	}

	private LongField failOnRedundantRange()
	{
		if(minimum==maximum)
			throw new IllegalArgumentException(
					"Redundant field with minimum==maximum " +
					"(" + minimum + ") is probably a mistake. " +
					"You may call method rangeEvenIfRedundant if you are sure this is ok.");
		return this;
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
		if(minimum!=MIN_VALUE || maximum!=MAX_VALUE)
			result.add(LongRangeViolationException.class);
		return result;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Long> getValueType()
	{
		return SimpleSelectType.LONG;
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
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
	@Wrap(order=10, name="get{0}", doc=Wrap.GET_DOC, hide=OptionalGetter.class)
	public long getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final long value)
	{
		set(item, Long.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) final long value)
	{
		return searchUnique(typeClass, Long.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide={OptionalGetter.class, NonUniqueGetter.class},
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if there is no matching item."))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final long value)
			throws IllegalArgumentException
	{
		return searchUniqueStrict(typeClass, Long.valueOf(value));
	}

	@Override
	void checkNotNull(final Long value, final Item exceptionItem)
	{
		final long valuePrimitive = value;
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
