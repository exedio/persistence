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

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import com.exedio.cope.util.Clock;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateField extends FunctionField<Date>
{
	@Serial
	private static final long serialVersionUID = 1l;

	public static Date getDefaultMinimum()
	{
		return new Date(minimum);
	}

	public static Date getDefaultMaximum()
	{
		return new Date(maximum);
	}


	// TODO allow customization of minimum and maximum
	private static final long minimum = LocalDateTime.of( 1600, 1, 1, 0, 0).toInstant(UTC).toEpochMilli(); // TODO allow earlier dates, but see DateConsistencyTest
	private static final long maximum = LocalDateTime.of(10000, 1, 1, 0, 0).toInstant(UTC).toEpochMilli() - 1;
	private final Precision precision;
	private final RoundingMode roundingMode;

	private DateField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Date> defaultS,
			final Precision precision,
			final RoundingMode roundingMode)
	{
		super(isfinal, optional, Date.class, unique, copyFrom, defaultS);
		this.precision = requireNonNull(precision, "precision");
		this.roundingMode = requireNonNull(roundingMode, "roundingMode");

		mountDefault();
	}

	public DateField()
	{
		this(false, false, false, null, null, Precision.MILLI, RoundingMode.UNNECESSARY);
	}

	@Override
	public DateField copy()
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, unique, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField optional()
	{
		return new DateField(isfinal, true, unique, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField mandatory()
	{
		return new DateField(isfinal, false, unique, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField nonUnique()
	{
		return new DateField(isfinal, optional, false, copyFrom, defaultS, precision, roundingMode);
	}

	@Override
	public DateField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public DateField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<Date>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public DateField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private DateField copyFrom(final CopyFrom copyFrom)
	{
		return new DateField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS, precision, roundingMode);
	}

	@Override
	public DateField noCopyFrom()
	{
		return new DateField(isfinal, optional, unique, null, defaultS, precision, roundingMode);
	}

	@Override
	public DateField noDefault()
	{
		return defaultTo(null);
	}

	@Override
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstantChecked(defaultConstant), precision, roundingMode);
	}

	private static final class DefaultNow extends DefaultSupplier<Date>
	{
		private Precision precision;
		private RoundingMode roundingMode;

		DefaultNow()
		{
			// just make package private
		}

		@Override
		Date generate(final Context ctx)
		{
			return precision.round(new Date(ctx.currentTimeMillis()), roundingMode, null, null);
		}

		@Override
		DefaultSupplier<Date> forNewField()
		{
			return new DefaultNow();
		}

		@Override
		void mount(final FunctionField<Date> field)
		{
			if(precision!=null)
				throw new RuntimeException();
			if(roundingMode!=null)
				throw new RuntimeException();

			precision = ((DateField)field).getPrecision();
			roundingMode = ((DateField)field).getRoundingMode();

			if(precision.constrains() &&
				roundingMode==RoundingMode.UNNECESSARY)
				throw new IllegalArgumentException(
						"precision constraint and " + RoundingMode.class.getSimpleName() + '.' + roundingMode +
						" do make no sense with defaultToNow");
		}
	}

	public DateField defaultToNow()
	{
		return new DateField(isfinal, optional, unique, copyFrom, new DefaultNow(), precision, roundingMode);
	}

	public boolean isDefaultNow()
	{
		return defaultS instanceof DefaultNow;
	}

	public Date getMinimum()
	{
		return new Date(minimum);
	}

	public Date getMaximum()
	{
		return new Date(maximum);
	}


	/**
	 * Returns a new DateField,
	 * that differs from this DateField
	 * by having a {@link #getPrecision() precision} of
	 * {@link Precision#SECOND seconds}.
	 * This means, dates stored into the field are
	 * constrained to whole seconds.
	 */
	public DateField precisionSecond()
	{
		return precision(Precision.SECOND);
	}

	/**
	 * Returns a new DateField,
	 * that differs from this DateField
	 * by having a {@link #getPrecision() precision} of
	 * {@link Precision#MINUTE minutes}.
	 * This means, dates stored into the field are
	 * constrained to whole minutes.
	 */
	public DateField precisionMinute()
	{
		return precision(Precision.MINUTE);
	}

	/**
	 * Returns a new DateField,
	 * that differs from this DateField
	 * by having a {@link #getPrecision() precision} of
	 * {@link Precision#HOUR hours}.
	 * This means, dates stored into the field are
	 * constrained to whole hours
	 * as represented in GMT (Greenwich Mean Time).
	 * <p>
	 * <b>NOTE:</b>
	 * Some time zones do have an offset to GMT, that is not a whole hour -
	 * for instance Indian Standard Time with an offset of 5 hours and 30 minutes.
	 * All dates allowed for the field will have a minute part of 30
	 * when being represented in Indian Standard Time.
	 */
	public DateField precisionHour()
	{
		return precision(Precision.HOUR);
	}

	private DateField precision(final Precision precision)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultS, precision, roundingMode);
	}

	public Precision getPrecision()
	{
		return precision;
	}

	public enum Precision
	{
		MILLI (null    , false, 0         , Calendar.FIELD_COUNT, (int[])null),
		SECOND("SECOND", true , 1000      , Calendar.SECOND,      Calendar.MILLISECOND),
		MINUTE("MINUTE", true , 1000*60   , Calendar.MINUTE,      Calendar.MILLISECOND, Calendar.SECOND),
		HOUR  (null    , true , 1000*60*60, Calendar.HOUR_OF_DAY, Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE);

		private final String sql;
		private final boolean constrains;
		private final int divisor;
		private final int field;
		private final int[] fields;

		Precision(
				final String sql,
				final boolean constrains, final int divisor,
				final int field, final int... fields)
		{
			this.sql = sql;
			this.constrains = constrains;
			this.divisor = divisor;
			this.field = field;
			this.fields = fields;
		}

		String sql()
		{
			if(sql==null)
				throw new IllegalArgumentException(String.valueOf(this));

			return sql;
		}

		public boolean constrains()
		{
			return constrains;
		}

		int divisor()
		{
			if(!constrains)
				throw new IllegalArgumentException();

			return divisor;
		}

		void check(final DateField feature, final Date value, final Item exceptionItem)
		{
			if(!constrains)
				return;

			final GregorianCalendar cal = newGregorianCalendar(value);
			for(final int field : fields)
			{
				//noinspection MagicConstant OK: generically using constants
				final int violation = cal.get(field);
				if(violation!=0)
					throw new DatePrecisionViolationException(feature, this, exceptionItem, value, violation);
			}
		}

		Date round(
				final Date value,
				final RoundingMode roundingMode,
				final DateField exceptionFeature,
				final Item exceptionItem)
		{
			requireNonNull(roundingMode, "roundingMode");

			if(value==null)
				return null;
			if(!constrains)
				return value;

			if(roundingMode==RoundingMode.UNNECESSARY)
			{
				check(exceptionFeature, value, exceptionItem);
				return value;
			}

			final GregorianCalendar cal = newGregorianCalendar(value);
			for(final int field : fields)
				//noinspection MagicConstant OK: generically using constants
				cal.set(field, 0);

			if(roundingMode==RoundingMode.FUTURE && (cal.getTimeInMillis()!=value.getTime()))
				//noinspection MagicConstant OK: generically using constants
				cal.add(field, 1);

			return cal.getTime();
		}

		private static GregorianCalendar newGregorianCalendar(final Date value)
		{
			final GregorianCalendar result = new GregorianCalendar(ZONE, Locale.ENGLISH);
			result.setTime(value);
			return result;
		}
	}

	/**
	 * @param roundingMode
	 *    specifies the rounding mode of the current date
	 *    if there is a precision constraint on this field.
	 *    Does not make any difference, if there is no precision constraint.
	 */
	public DateField roundingMode(final RoundingMode roundingMode)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultS, precision, roundingMode);
	}

	public RoundingMode getRoundingMode()
	{
		return roundingMode;
	}

	public enum RoundingMode
	{
		/**
		 * Rounding mode to round towards future.
		 * This rounding mode is analogous to {@link java.math.RoundingMode#CEILING}.
		 */
		FUTURE,

		/**
		 * Rounding mode to round towards past.
		 * This rounding mode is analogous to {@link java.math.RoundingMode#FLOOR}.
		 */
		PAST,

		/**
		 * Rounding mode that does not round.
		 * Instead it asserts that the value is already rounded.
		 * Otherwise a {@link DatePrecisionViolationException} is thrown.
		 * This rounding mode is analogous to {@link java.math.RoundingMode#UNNECESSARY}.
		 */
		UNNECESSARY
	}


	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Date> getValueType()
	{
		return SimpleSelectType.DATE;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(getPrecision().constrains())
			result.add(DatePrecisionViolationException.class);
		return result;
	}

	private DefaultSupplier<Date> defaultConstantChecked(final Date value)
	{
		if(suspiciousForWrongDefaultNow(value))
		{
			final String suspicion =
					"Very probably you called \"DateField.defaultTo(new Date())\". " +
					"This will not work as expected, use \"defaultToNow()\" instead.";

			logWithStacktrace(LoggerFactory.getLogger(getClass()), suspicion);
			return defaultConstantSuspicious(value, suspicion);
		}
		else
			return defaultConstant(value);
	}

	private static boolean suspiciousForWrongDefaultNow(final Date defaultConstant)
	{
		if(defaultConstant==null)
			return false;

		return Duration.ofMillis(100).compareTo(
				Duration.between(
						defaultConstant.toInstant(),
						Instant.now()).abs()
		) > 0;
	}

	static void logWithStacktrace(final Logger logger, final String message)
	{
		logger.error(
				message,
				new RuntimeException("exception just for creating stacktrace"));
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return
				connect.supportsNativeDate
				? new TimestampColumn(table, name, optional, minimum, maximum, precision)
				: new IntegerColumn  (table, name, false, optional, minimum, maximum, true, precision);
	}

	@Override
	Date get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date((Long)cell);
	}

	@Override
	void set(final Row row, final Date surface)
	{
		row.put(getColumn(), surface==null ? null : surface.getTime());
	}

	@Override
	void checkNotNull(final Date value, final Item exceptionItem)
	{
		final long valueMillis = value.getTime();
		if(valueMillis<minimum)
			throw new DateRangeViolationException(this, exceptionItem, valueMillis, true, minimum);
		if(valueMillis>maximum)
			throw new DateRangeViolationException(this, exceptionItem, valueMillis, false, maximum);

		precision.check(this, value, exceptionItem);
	}

	public SetValue<Date> mapRounded(final Date value, final RoundingMode roundingMode)
	{
		return SetValue.map(this, precision.round(value, roundingMode, this, null));
	}

	@Wrap(order=4,
			doc="Sets a new value for {0}, but rounds it before according to the precision of the field.",
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, PrecisionConstrainsGetter.class, RoundingModeUnnecessaryGetter.class},
			thrownGetter=InitialThrownRounded.class)
	public void setRounded(
			@Nonnull final Item item,
			@Parameter(nullability=NullableIfOptional.class) final Date value)
	{
		item.set(this, precision.round(value, roundingMode, this, item));
	}

	@Wrap(order=5,
			doc="Sets a new value for {0}, but rounds it before according to the precision of the field.",
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, PrecisionConstrainsGetter.class},
			thrownGetter=InitialThrown.class)
	public void setRounded(
			@Nonnull final Item item,
			@Parameter(nullability=NullableIfOptional.class) final Date value,
			@Nonnull @Parameter("roundingMode") final RoundingMode roundingMode)
	{
		item.set(this, precision.round(value, roundingMode, this, item));
	}

	@Override
	public CompareCondition<Date> less(final Date value)
	{
		return super.less(precision.round(value, RoundingMode.FUTURE, null, null));
	}

	@Override
	public CompareCondition<Date> lessOrEqual(final Date value)
	{
		return super.lessOrEqual(precision.round(value, RoundingMode.PAST, null, null));
	}

	@Override
	public CompareCondition<Date> greater(final Date value)
	{
		return super.greater(precision.round(value, RoundingMode.PAST, null, null));
	}

	@Override
	public CompareCondition<Date> greaterOrEqual(final Date value)
	{
		return super.greaterOrEqual(precision.round(value, RoundingMode.FUTURE, null, null));
	}

	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
	 */
	@Wrap(order=10,
			doc="Sets the current date for the date field {0}.", // TODO better text
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, TouchGetter.class})
	public void touch(@Nonnull final Item item)
	{
		set(item, precision.round(Clock.newDate(), roundingMode, this, item)); // TODO: make a more efficient implementation
	}


	static SimpleDateFormat format()
	{
		return format("yyyy-MM-dd HH:mm:ss.SSS");
	}

	static SimpleDateFormat format(final String pattern)
	{
		final SimpleDateFormat result = new SimpleDateFormat(pattern, Locale.ENGLISH);
		result.setTimeZone(ZONE);
		result.setLenient(false);
		return result;
	}

	private static final TimeZone ZONE = getTimeZone("GMT");
}
