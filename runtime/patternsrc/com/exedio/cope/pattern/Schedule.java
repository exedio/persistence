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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.misc.Iterables.once;
import static com.exedio.cope.misc.QueryIterators.iterateTypeTransactionally;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static java.lang.System.nanoTime;
import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.CopeSchemaValue;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Features;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.ProxyJobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WrapFeature
public final class Schedule extends Pattern
{
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

	private static final long serialVersionUID = 1l;

	public enum Interval
	{
		@CopeSchemaValue(7)
		HOURLY(HOURS, 30*24) // limit: 30 days
		{
			@Override ZonedDateTime toFrom(final ZonedDateTime date)
			{
				return date.withNano(0).withSecond(0).withMinute(0);
			}
		},
		DAILY(DAYS, 2*31) // limit: 2 months
		{
			@Override ZonedDateTime toFrom(final ZonedDateTime date)
			{
				return HOURLY.toFrom(date).withHour(0);
			}
		},
		WEEKLY(WEEKS, 25) // limit: half a year
		{
			@Override ZonedDateTime toFrom(final ZonedDateTime date)
			{
				return DAILY.toFrom(date).with(DAY_OF_WEEK, MONDAY.getValue());
			}
		},
		MONTHLY(MONTHS, 12) // limit: one year
		{
			@Override ZonedDateTime toFrom(final ZonedDateTime date)
			{
				return DAILY.toFrom(date).withDayOfMonth(1);
			}
		};

		abstract ZonedDateTime toFrom(ZonedDateTime date);

		final ChronoUnit unit;
		final int limit; // TODO allow customization

		Interval(final ChronoUnit unit, final int limit)
		{
			this.unit = unit;
			this.limit = limit;

			assert limit>=12 : limit;
		}
	}

	private final ZoneId zoneId;

	private final BooleanField enabled = new BooleanField().defaultTo(true);
	private final EnumField<Interval> interval = EnumField.create(Interval.class).defaultTo(DAILY);

	private static final Duration DELAY = Duration.ofMinutes(5);

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Runs runs = new Runs();

	public Schedule(final ZoneId zoneId)
	{
		this(zoneId, null);
	}

	private Schedule(final ZoneId zoneId, final Locale locale)
	{
		this.zoneId = requireNonNull(zoneId, "zoneId");
		this.localeIfSupported = locale;
		addSource(enabled,  "enabled");
		addSource(interval, "interval");
	}

	public ZoneId getZoneId()
	{
		return zoneId;
	}

	public TimeZone getTimeZone()
	{
		return TimeZone.getTimeZone(zoneId);
	}

	@Override
	public void onMount()
	{
		super.onMount();
		final Type<?> type = getType();
		if(!Scheduleable.class.isAssignableFrom(type.getJavaClass()))
			throw new ClassCastException(
					"type of " + getID() + " must implement " + Scheduleable.class +
					", but was " + type.getJavaClass().getName());

		runs.onMount(this, type);
	}

	public BooleanField getEnabled()
	{
		return enabled;
	}

	public EnumField<Interval> getInterval()
	{
		return interval;
	}

	@Wrap(order=1000, name="{1}RunParent", doc="Returns the parent field of the run type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getRunParent(@Nonnull final Class<P> parentClass)
	{
		return runs.mount().parent.as(parentClass);
	}

	public ItemField<?> getRunParent()
	{
		return runs.mount().parent;
	}

	public EnumField<Interval> getRunInterval()
	{
		return runs.interval;
	}

	public PartOf<?> getRunRuns()
	{
		return runs.mount().parentPartOf;
	}

	public DateField getRunFrom()
	{
		return runs.from;
	}

	public DateField getRunUntil()
	{
		return runs.until;
	}

	public DateField getRunRun()
	{
		return runs.run;
	}

	public IntegerField getRunProgress()
	{
		return runs.progress;
	}

	public LongField getRunElapsed()
	{
		return runs.elapsed;
	}

	public Type<Run> getRunType()
	{
		return runs.mount().type;
	}

	@Wrap(order=10)
	public boolean isEnabled(@Nonnull final Item item)
	{
		return this.enabled.getMandatory(item);
	}

	@Wrap(order=20)
	public void setEnabled(
			@Nonnull final Item item,
			@Parameter("enabled") final boolean enabled)
	{
		this.enabled.set(item, enabled);
	}

	@Wrap(order=30)
	@Nonnull
	public Interval getInterval(@Nonnull final Item item)
	{
		return this.interval.get(item);
	}

	@Wrap(order=40)
	public void setInterval(
			@Nonnull final Item item,
			@Nonnull @Parameter("interval") final Interval interval)
	{
		this.interval.set(item, interval);
	}

	@Wrap(order=60)
	public <P extends Item & Scheduleable> void run(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(ctx, "ctx");

		final Instant now = Clock.newDate().toInstant(); // TODO per item

		for(final P item : once(iterateTypeTransactionally(
				getType().as(parentClass), enabled.equal(true), 1000)))
		{
			ctx.stopIfRequested();
			runInternal(parentClass, now, item, ctx);
		}
	}

	private <P extends Item & Scheduleable> void runInternal(
			final Class<P> parentClass,
			final Instant now,
			final P item,
			final JobContext ctx)
	{
		final Instant lastUntil;
		final Interval interval;
		try(TransactionTry tx = startTransaction(item, "check"))
		{
			if(!isEnabled(item))
			{
				if(logger.isWarnEnabled())
					logger.warn(
							"{} is not enabled anymore for {}, probably due to concurrent modification.",
							item.getCopeID(), getID());
				tx.commit();
				return;
			}

			final Date lastUntilDate = new Query<>(
					runs.until.max(),
					runs.mount().parent.as(parentClass).equal(item)).
					searchSingleton();
			lastUntil = lastUntilDate!=null ? lastUntilDate.toInstant() : null;
			interval = this.interval.get(item);
			tx.commit();
		}

		ZonedDateTime current = interval.toFrom(ZonedDateTime.ofInstant(now.minus(DELAY), zoneId));
		Instant currentInstant = current.toInstant();
		assert !currentInstant.isAfter(now);

		if(lastUntil==null)
		{
			final Instant until = currentInstant;
			current = current.minus(1, interval.unit);
			final Instant from = current.toInstant();
			runNow(item, interval, from, until, 1, 1, now, ctx);
		}
		else
		{
			final LinkedList<Instant> dates = new LinkedList<>();
			while(lastUntil.isBefore(currentInstant))
			{
				dates.add(0, currentInstant);
				current = current.minus(1, interval.unit);
				currentInstant = current.toInstant();
			}
			dates.add(0, lastUntil);

			final int total = dates.size() - 1;
			if(total>interval.limit)
				throw new RuntimeException(
						"schedule aborting because suspicious amount of work to do: " +
						getID() + ',' + item.getCopeID() + ',' + total + ',' + interval + ',' + interval.limit);

			final Iterator<Instant> i = dates.iterator();
			Instant from = i.next();
			int count = 1;
			while(i.hasNext())
			{
				final Instant until = i.next();
				runNow(item, interval, from, until, count++, total, now, ctx);
				from = until;
			}
		}
	}

	private <P extends Item & Scheduleable> void runNow(
			final P item,
			final Interval interval,
			final Instant from, final Instant until,
			final int count, final int total,
			final Instant now,
			final JobContext ctx)
	{
		assert from.isBefore(until);
		assert !now.isBefore(until);
		assert count>0 : count;
		assert total>0 : total;
		assert count<=total : "" + count + '/' + total;

		ctx.stopIfRequested();
		final RunContext runCtx = new RunContext(ctx);
		try(TransactionTry tx = startTransaction(item, "run " + count + '/' + total))
		{
			final Date fromDate  = Date.from(from);
			final Date untilDate = Date.from(until);
			final long elapsedStart = nanoTime();
			item.run(this, fromDate, untilDate, runCtx); // TODO switch to Instant
			final long elapsedEnd = nanoTime();
			runs.newItem(
					item, interval, fromDate, untilDate, Date.from(now), // TODO switch to InstantField
					runCtx.getProgress(),
					toMillies(elapsedEnd, elapsedStart));
			tx.commit();
		}
		ctx.incrementProgress();
	}

	private static final class RunContext extends ProxyJobContext
	{
		private int progress = 0;

		RunContext(final JobContext target)
		{
			super(target);
		}

		@Override
		public boolean supportsProgress()
		{
			return true;
		}

		@Override
		public void incrementProgress()
		{
			super.incrementProgress();
			progress++;
		}

		@Override
		public void incrementProgress(final int delta)
		{
			super.incrementProgress(delta);
			progress += delta;
		}

		int getProgress()
		{
			return progress;
		}
	}

	private TransactionTry startTransaction(final Item item, final String name)
	{
		return getType().getModel().startTransactionTry(
				getID() + ' ' + item.getCopeID() + ' ' + name);
	}

	private static final class Runs
	{
		final EnumField<Interval> interval = EnumField.create(Interval.class).toFinal();
		final DateField from = new DateField().toFinal();
		final DateField until = new DateField().toFinal();
		final DateField run = new DateField().toFinal();
		final IntegerField progress = new IntegerField().toFinal().min(0);
		final LongField elapsed = new LongField().toFinal().min(0);

		private Mount mountIfMounted = null;

		Runs()
		{
			// make non-private
		}

		void onMount(final Schedule pattern, final Type<?> type)
		{
			final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
			final PartOf<?> runs = PartOf.create(parent, from);
			final Features features = new Features();
			features.put("parent", parent);
			features.put("interval", interval);
			features.put("from",  from);
			features.put("runs",  runs);
			features.put("until", until);
			features.put("run",   run);
			features.put("progress", progress);
			features.put("elapsed", elapsed);
			@SuppressWarnings("synthetic-access")
			final Type<Run> runType = pattern.newSourceType(Run.class, features, "Run");
			this.mountIfMounted = new Mount(parent, runs, runType);
		}

		private static final class Mount
		{
			final ItemField<?> parent;
			final PartOf<?> parentPartOf;
			final Type<Run> type;

			Mount(
					final ItemField<?> parent,
					final PartOf<?> parentPartOf,
					final Type<Run> type)
			{
				assert parent!=null;
				assert parentPartOf!=null;
				assert type!=null;

				this.parent = parent;
				this.parentPartOf = parentPartOf;
				this.type = type;
			}
		}

		Mount mount()
		{
			return requireMounted(mountIfMounted);
		}

		void newItem(
				final Item item,
				final Interval interval,
				final Date from,
				final Date until,
				final Date now,
				final int progress,
				final long elapsed)
		{
			final Mount mount = mount();
			mount.type.newItem(
					Cope.mapAndCast(mount.parent, item),
					this.interval.map(interval),
					this.from.map(from),
					this.until.map(until),
					this.run.map(now),
					this.progress.map(progress),
					this.elapsed.map(elapsed));
		}
	}

	@Computed
	public static final class Run extends Item
	{
		private static final long serialVersionUID = 1l;

		Run(final ActivationParameters ap)
		{
			super(ap);
		}

		public Schedule getPattern()
		{
			return (Schedule)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return getPattern().runs.mount().parent.get(this);
		}

		public Interval getInterval()
		{
			return getPattern().runs.interval.get(this);
		}

		public Date getFrom()
		{
			return getPattern().runs.from.get(this);
		}

		public Date getUntil()
		{
			return getPattern().runs.until.get(this);
		}

		public Date getRun()
		{
			return getPattern().runs.run.get(this);
		}

		public int getProgress()
		{
			return getPattern().runs.progress.getMandatory(this);
		}

		public long getElapsed()
		{
			return getPattern().runs.elapsed.getMandatory(this);
		}
	}

	// ------------------- deprecated stuff -------------------

	@Deprecated
	private final Locale localeIfSupported;

	/**
	 * @deprecated Use {@link #Schedule(ZoneId)} instead
	 * @param locale
	 *        specifies the locale used for creating the {@link GregorianCalendar}
	 *        that does all the date computations.
	 *        Is important for specifying the first day of week (Monday vs. Sunday)
	 */
	@Deprecated
	public Schedule(final TimeZone timeZone, final Locale locale)
	{
		this(
			requireNonNull(timeZone, "timeZone").toZoneId(),
			requireNonNull(locale, "locale"));
	}

	/**
	 * @deprecated Supported only if constructed by {@link #Schedule(TimeZone, Locale)}.
	 * @throws IllegalStateException if not supported
	 */
	@Deprecated
	public Locale getLocale()
	{
		if(localeIfSupported==null)
			throw new IllegalStateException(toString());

		return localeIfSupported;
	}

	/**
	 * @deprecated Supported only if constructed by {@link #Schedule(TimeZone, Locale)}.
	 * @throws IllegalStateException if not supported
	 */
	@Deprecated
	public GregorianCalendar newGregorianCalendar()
	{
		final GregorianCalendar result = new GregorianCalendar(getTimeZone(), getLocale());
		result.setLenient(false);
		return result;
	}

	/**
	 * @deprecated Use {@link #Schedule(TimeZone,Locale)} instead.
	 */
	@Deprecated
	public Schedule()
	{
		this(Locale.getDefault());
	}

	/**
	 * @deprecated Use {@link #Schedule(TimeZone,Locale)} instead.
	 */
	@Deprecated
	public Schedule(final Locale locale)
	{
		this(TimeZone.getDefault(), locale);
	}

	/**
	 * @deprecated Use {@link #run(Class,JobContext)} instead.
	 */
	@Wrap(order=50)
	@Deprecated
	public <P extends Item & Scheduleable> int run(
			@Nonnull @SuppressWarnings("unused") final Class<P> parentClass,
			@Nullable @Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			ctx -> run(parentClass, ctx)
		);
	}
}
