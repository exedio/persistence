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
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.util.Clock;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.AnnotatedElement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateField extends FunctionField<Date>
{
	private static final Logger logger = LoggerFactory.getLogger(DateField.class);

	private static final long serialVersionUID = 1l;

	private final Precision precision;
	private final RoundingMode roundingMode;

	private DateField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Date> defaultSource,
			final Precision precision,
			final RoundingMode roundingMode)
	{
		super(isfinal, optional, Date.class, unique, copyFrom, defaultSource);
		this.precision = requireNonNull(precision, "precision");
		this.roundingMode = requireNonNull(roundingMode, "roundingMode");

		mountDefaultSource();
	}

	public DateField()
	{
		this(false, false, false, null, null, Precision.MILLI, RoundingMode.UNNECESSARY);
	}

	@Override
	public DateField copy()
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultSource, precision, roundingMode);
	}

	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, unique, copyFrom, defaultSource, precision, roundingMode);
	}

	@Override
	public DateField optional()
	{
		return new DateField(isfinal, true, unique, copyFrom, defaultSource, precision, roundingMode);
	}

	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, copyFrom, defaultSource, precision, roundingMode);
	}

	@Override
	public DateField nonUnique()
	{
		return new DateField(isfinal, optional, false, copyFrom, defaultSource, precision, roundingMode);
	}

	@Override
	public DateField copyFrom(final ItemField<?> copyFrom)
	{
		return new DateField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource, precision, roundingMode);
	}

	@Override
	public DateField noDefault()
	{
		return new DateField(isfinal, optional, unique, copyFrom, null, precision, roundingMode);
	}

	@Override
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstantWithCreatedTime(defaultConstant), precision, roundingMode);
	}

	private static final class DefaultNow extends DefaultSource<Date>
	{
		@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
		private Precision precision;
		@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
		private RoundingMode roundingMode;

		DefaultNow()
		{
			// just make package private
		}

		@Override
		Date generate(final long now)
		{
			return precision.round(new Date(now), roundingMode, null, null);
		}

		@Override
		DefaultSource<Date> forNewField()
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
		return defaultSource instanceof DefaultNow;
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
		return new DateField(isfinal, optional, unique, copyFrom, defaultSource, precision, roundingMode);
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
				throw new IllegalArgumentException("" + this);

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
				cal.set(field, 0);

			if(roundingMode==RoundingMode.FUTURE && (cal.getTimeInMillis()!=value.getTime()))
				cal.add(field, 1);

			return cal.getTime();
		}

		private static GregorianCalendar newGregorianCalendar(final Date value)
		{
			final GregorianCalendar result = new GregorianCalendar(ZONE, Locale.ENGLISH);
			result.setTime(value);
			return result;
		}

		static final String ZONE_ID = "GMT";
		static final TimeZone ZONE = getTimeZone(ZONE_ID);
	}

	/**
	 * @param roundingMode
	 *    specifies the rounding mode of the current date
	 *    if there is a precision constraint on this field.
	 *    Does not make any difference, if there is no precision constraint.
	 */
	public DateField roundingMode(final RoundingMode roundingMode)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultSource, precision, roundingMode);
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
		UNNECESSARY;
	}


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

	@Override
	final void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(suspiciousForWrongDefaultNow() && logger.isWarnEnabled())
			logger.warn(
					"Very probably you called \"DateField.defaultTo(new Date())\" on field {}. " +
					"This will not work as expected, use \"defaultToNow()\" instead.",
					getID());
	}

	private boolean suspiciousForWrongDefaultNow()
	{
		final Date defaultConstant = getDefaultConstant();
		if(defaultConstant==null)
			return false;

		return Math.abs(defaultConstant.getTime()-getDefaultConstantCreatedTimeMillis())<100;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return
				getType().getModel().connect().supportsNativeDate()
				? (Column)new TimestampColumn(table, name, optional, precision)
				: (Column)new IntegerColumn(table, name, false, optional, Long.MIN_VALUE, Long.MAX_VALUE, true, precision);
	}

	@Override
	Date get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}

	@Override
	void set(final Row row, final Date surface)
	{
		row.put(getColumn(), surface==null ? null : Long.valueOf(surface.getTime()));
	}

	@Override
	void checkNotNull(final Date value, final Item exceptionItem)
	{
		precision.check(this, value, exceptionItem);
	}

	public SetValue<Date> mapRounded(final Date value, final RoundingMode roundingMode)
	{
		return SetValue.map(this, precision.round(value, roundingMode, this, null));
	}

	@Wrap(order=4,
			doc="Sets a new value for {0}, but rounds it before according to the precision of the field.",
			hide={FinalSettableGetter.class, PrecisionGetter.class, RoundingModeUnnecessaryGetter.class},
			thrownGetter=InitialThrownRounded.class)
	public void setRounded(
			final Item item,
			final Date value)
	{
		item.set(this, precision.round(value, roundingMode, this, item));
	}

	private static final class RoundingModeUnnecessaryGetter implements BooleanGetter<DateField>
	{
		public boolean get(final DateField feature)
		{
			return feature.getRoundingMode()==RoundingMode.UNNECESSARY;
		}
	}

	private static final class InitialThrownRounded implements ThrownGetter<DateField>
	{
		public Set<Class<? extends Throwable>> get(final DateField feature)
		{
			final Set<Class<? extends Throwable>> result = feature.getInitialExceptions();
			result.remove(DatePrecisionViolationException.class);
			return result;
		}
	}

	@Wrap(order=5,
			doc="Sets a new value for {0}, but rounds it before according to the precision of the field.",
			hide={FinalSettableGetter.class, PrecisionGetter.class},
			thrownGetter=InitialThrown.class)
	public void setRounded(
			final Item item,
			final Date value,
			@Parameter("roundingMode") final RoundingMode roundingMode)
	{
		item.set(this, precision.round(value, roundingMode, this, item));
	}

	private static final class PrecisionGetter implements BooleanGetter<DateField>
	{
		public boolean get(final DateField feature)
		{
			return !feature.getPrecision().constrains();
		}
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
			hide={FinalSettableGetter.class, NowGetter.class})
	public void touch(final Item item)
	{
		set(item, precision.round(Clock.newDate(), roundingMode, this, item)); // TODO: make a more efficient implementation
	}

	private static final class NowGetter implements BooleanGetter<DateField>
	{
		public boolean get(final DateField feature)
		{
			return
					feature.getPrecision().constrains() &&
					feature.getRoundingMode()==RoundingMode.UNNECESSARY;
		}
	}
}
