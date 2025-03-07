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

import static com.exedio.cope.DateField.logWithStacktrace;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.DayPartView.Part;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.util.Day;
import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.slf4j.LoggerFactory;

public final class DayField extends FunctionField<Day>
{
	@Serial
	private static final long serialVersionUID = 1l;

	public static Day getDefaultMinimum()
	{
		return minimum;
	}

	public static Day getDefaultMaximum()
	{
		return maximum;
	}


	// TODO allow customization of minimum and maximum
	private static final Day minimum = new Day(1600,  1,  1); // TODO allow earlier days, but see DayConsistencyTest
	private static final Day maximum = new Day(9999, 12, 31);

	private DayField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Day> defaultS)
	{
		super(isfinal, optional, Day.class, unique, copyFrom, defaultS);
		mountDefault();
	}

	public DayField()
	{
		this(false, false, false, null, null);
	}

	@Override
	public DayField copy()
	{
		return new DayField(isfinal, optional, unique, copyFrom, defaultS);
	}

	@Override
	public DayField toFinal()
	{
		return new DayField(true, optional, unique, copyFrom, defaultS);
	}

	@Override
	public DayField optional()
	{
		return new DayField(isfinal, true, unique, copyFrom, defaultS);
	}

	@Override
	public DayField mandatory()
	{
		return new DayField(isfinal, false, unique, copyFrom, defaultS);
	}

	@Override
	public DayField unique()
	{
		return new DayField(isfinal, optional, true, copyFrom, defaultS);
	}

	@Override
	public DayField nonUnique()
	{
		return new DayField(isfinal, optional, false, copyFrom, defaultS);
	}

	@Override
	public DayField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public DayField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<Day>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public DayField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private DayField copyFrom(final CopyFrom copyFrom)
	{
		return new DayField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS);
	}

	@Override
	public DayField noCopyFrom()
	{
		return new DayField(isfinal, optional, unique, null, defaultS);
	}

	@Override
	public DayField noDefault()
	{
		return defaultTo(null);
	}

	@Override
	public DayField defaultTo(final Day defaultConstant)
	{
		return new DayField(isfinal, optional, unique, copyFrom, defaultConstantChecked(defaultConstant));
	}

	private static final class DefaultNow extends DefaultSupplier<Day>
	{
		private final Supplier<ZoneId> zoneSupplier;

		DefaultNow(final Supplier<ZoneId> zone)
		{
			this.zoneSupplier = requireNonNull(zone, "zone");
		}

		ZoneId zone()
		{
			return requireNonNull(zoneSupplier.get(), "zone supplier did return null");
		}

		@Override
		Day generate(final Context ctx)
		{
			return Day.from(ctx.currentInstant().atZone(zone()).toLocalDate());
		}

		@Override
		DefaultSupplier<Day> forNewField()
		{
			return this;
		}

		@Override
		void mount(final FunctionField<Day> field)
		{
			// nothing to be checked
		}
	}

	public DayField defaultToNow(final ZoneId zone)
	{
		requireNonNull(zone, "zone");
		return defaultToNow(() -> zone);
	}

	public DayField defaultToNow(final Supplier<ZoneId> zone)
	{
		return new DayField(isfinal, optional, unique, copyFrom, new DefaultNow(zone));
	}

	public boolean isDefaultNow()
	{
		return defaultS instanceof DefaultNow;
	}

	public ZoneId getDefaultNowZone()
	{
		return
			defaultS instanceof DefaultNow
			? ((DefaultNow)defaultS).zone()
			: null;
	}

	public Day getMinimum()
	{
		return minimum;
	}

	public Day getMaximum()
	{
		return maximum;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Day> getValueType()
	{
		return SimpleSelectType.DAY;
	}

	private DefaultSupplier<Day> defaultConstantChecked(final Day value)
	{
		if(suspiciousForWrongDefaultNow(value))
		{
			final String suspicion =
					"Very probably you called \"DayField.defaultTo(new Day())\". " +
					"This will not work as expected, use \"defaultToNow()\" instead.";

			logWithStacktrace(LoggerFactory.getLogger(getClass()), suspicion);
			return defaultConstantSuspicious(value, suspicion);
		}
		else
			return defaultConstant(value);
	}

	private static boolean suspiciousForWrongDefaultNow(final Day defaultConstant)
	{
		if(defaultConstant==null)
			return false;

		final LocalDate defaultDate = defaultConstant.toLocalDate();
		final LocalDate today = Instant.now().atZone(ZoneOffset.UTC).toLocalDate();
		return
				defaultDate.equals(today) ||
				defaultDate.equals(today.minusDays(1)) || // margin for time zone offsets
				defaultDate.equals(today.plusDays (1));   // margin for time zone offsets
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return new DayColumn(table, name, optional, minimum, maximum);
	}

	@Override
	Day get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : DayColumn.getDay((Integer)cell);
	}

	@Override
	void set(final Row row, final Day surface)
	{
		row.put(getColumn(), surface==null ? null : DayColumn.getTransientNumber(surface));
	}

	@Override
	void checkNotNull(final Day value, final Item exceptionItem)
	{
		if(value.compareTo(minimum)<0)
			throw new DayRangeViolationException(this, exceptionItem, value, minimum);
		if(value.compareTo(maximum)>0)
			throw new RuntimeException(value.toString());
	}

	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
	 */
	@Wrap(order=10,
			doc="Sets today for the date field {0}.", // TODO better text
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class})
	public void touch(@Nonnull final Item item, @Nonnull @Parameter("zone") final TimeZone zone)
	{
		set(item, new Day(zone)); // TODO: make a more efficient implementation
	}

	public DayPartView year()
	{
		return new DayPartView(this, Part.YEAR);
	}

	public DayPartView month()
	{
		return new DayPartView(this, Part.MONTH);
	}

	public DayPartView weekOfYear()
	{
		return new DayPartView(this, Part.WEEK_OF_YEAR);
	}

	public DayPartView dayOfMonth()
	{
		return new DayPartView(this, Part.DAY_OF_MONTH);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #defaultToNow(ZoneId)} instead.
	 */
	@Deprecated
	public DayField defaultToNow(final TimeZone zone)
	{
		return defaultToNow(toNew(zone));
	}

	/**
	 * @deprecated Use {@link #getDefaultNowTimeZone()} instead.
	 */
	@Deprecated
	public TimeZone getDefaultNowZimeZone()
	{
		return getDefaultNowTimeZone();
	}

	/**
	 * @deprecated Use {@link #getDefaultNowZone()} instead.
	 */
	@Deprecated
	public TimeZone getDefaultNowTimeZone()
	{
		return fromNew(getDefaultNowZone());
	}

	@Deprecated
	private static ZoneId toNew(final TimeZone zone)
	{
		return zone!=null ? zone.toZoneId() : null;
	}

	@Deprecated
	private static TimeZone fromNew(final ZoneId zone)
	{
		if(zone==null)
			return null;

		final TimeZone result = TimeZone.getTimeZone(zone);
		final ZoneId zoneCheck = result.toZoneId();
		if(!zone.equals(zoneCheck))
			throw new IllegalArgumentException(zone.toString() + '/' + zoneCheck + '/' + result);
		return result;
	}
}
