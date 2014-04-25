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

package com.exedio.cope.pattern;

import static com.exedio.cope.misc.Iterables.once;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static com.exedio.cope.misc.TypeIterator.iterateTransactionally;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static java.lang.System.nanoTime;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.WEEK_OF_MONTH;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Schedule extends Pattern
{
	private static final Logger logger = LoggerFactory.getLogger(Schedule.class);

	private static final long serialVersionUID = 1l;

	public enum Interval
	{
		DAILY
		{
			@Override void setToFrom(final GregorianCalendar cal)
			{
				cal.set(MILLISECOND, 0);
				cal.set(SECOND, 0);
				cal.set(MINUTE, 0);
				cal.set(HOUR_OF_DAY, 0);
			}
			@Override void add(final GregorianCalendar cal, final int amount)
			{
				cal.add(DAY_OF_WEEK, amount);
			}
		},
		WEEKLY
		{
			@Override void setToFrom(final GregorianCalendar cal)
			{
				DAILY.setToFrom(cal);
				cal.set(DAY_OF_WEEK, MONDAY);
			}
			@Override void add(final GregorianCalendar cal, final int amount)
			{
				cal.add(WEEK_OF_MONTH, amount);
			}
		},
		MONTHLY
		{
			@Override void setToFrom(final GregorianCalendar cal)
			{
				DAILY.setToFrom(cal);
				cal.set(DAY_OF_MONTH, 1);
			}
			@Override void add(final GregorianCalendar cal, final int amount)
			{
				cal.add(MONTH, amount);
			}
		};

		abstract void setToFrom(GregorianCalendar cal);
		abstract void add(GregorianCalendar cal, int amount);
	}

	private final TimeZone timeZone;
	private final Locale locale;

	private final BooleanField enabled = new BooleanField().defaultTo(true);
	private final EnumField<Interval> interval = EnumField.create(Interval.class).defaultTo(DAILY);

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	final Runs runs = new Runs();

	/**
	 * @param locale
	 *        specifies the locale used for creating the {@link GregorianCalendar}
	 *        that does all the date computations.
	 *        Is important for specifying the first day of week (Monday vs. Sunday)
	 */
	public Schedule(final TimeZone timeZone, final Locale locale)
	{
		this.timeZone = requireNonNull(timeZone, "timeZone");
		this.locale = requireNonNull(locale, "locale");
		addSource(enabled,  "enabled");
		addSource(interval, "interval");
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	public Locale getLocale()
	{
		return locale;
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
	public <P extends Item> ItemField<P> getRunParent(final Class<P> parentClass)
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

	public LongField getRunElapsed()
	{
		return runs.elapsed;
	}

	public Type<Run> getRunType()
	{
		return runs.mount().type;
	}

	@Wrap(order=10)
	public boolean isEnabled(final Item item)
	{
		return this.enabled.getMandatory(item);
	}

	@Wrap(order=20)
	public void setEnabled(
			final Item item,
			@Parameter("enabled") final boolean enabled)
	{
		this.enabled.set(item, enabled);
	}

	@Wrap(order=30)
	public Interval getInterval(final Item item)
	{
		return this.interval.get(item);
	}

	@Wrap(order=40)
	public void setInterval(
			final Item item,
			@Parameter("interval") final Interval interval)
	{
		this.interval.set(item, interval);
	}

	@Wrap(order=60)
	public <P extends Item & Scheduleable> void run(
			final Class<P> parentClass,
			@Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(ctx, "ctx");

		final Type<P> type = getType().as(parentClass);
		final Model model = type.getModel();
		final String id = getID();
		final Date now = new Date(Clock.currentTimeMillis()); // TODO per item

		for(final P item : once(iterateTransactionally(type, enabled.equal(true), 1000)))
		{
			ctx.stopIfRequested();
			try(TransactionTry tx = model.startTransactionTry(id + " run " + item.getCopeID()))
			{
				runInternal(parentClass, now, item, ctx);
				tx.commit();
			}
		}
	}

	private <P extends Item & Scheduleable> void runInternal(
			final Class<P> parentClass,
			final Date now,
			final P item,
			final JobContext ctx)
	{
		if(!isEnabled(item))
		{
			if(logger.isInfoEnabled())
				logger.info(
						"{} is not enabled anymore for {}, probably due to concurrent modification.",
						item.getCopeID(), getID());
			return;
		}

		final Date lastUntil = new Query<>(
				runs.until.max(),
				runs.mount().parent.as(parentClass).equal(item)).
				searchSingleton();

		final GregorianCalendar cal = new GregorianCalendar(timeZone, locale);
		cal.setTime(now);
		final Interval interval = this.interval.get(item);
		interval.setToFrom(cal);

		if(lastUntil==null)
		{
			final Date until = cal.getTime();
			interval.add(cal, -1);
			final Date from = cal.getTime();
			runNow(item, interval, from, until, now, ctx);
		}
		else
		{
			final LinkedList<Date> dates = new LinkedList<>();
			dates.add(0, cal.getTime());
			while(lastUntil.before(cal.getTime()))
			{
				interval.add(cal, -1);
				dates.add(0, cal.getTime());
			}

			final Iterator<Date> i = dates.iterator();
			Date from = i.next();
			while(i.hasNext())
			{
				final Date until = i.next();
				// TODO separate transactions for different runs
				runNow(item, interval, from, until, now, ctx);
				from = until;
			}
		}
	}

	private <P extends Item & Scheduleable> void runNow(
			final P item,
			final Interval interval,
			final Date from, final Date until,
			final Date now,
			final JobContext ctx)
	{
		final long elapsedStart = nanoTime();
		item.run(this, from, until, ctx);
		final long elapsedEnd = nanoTime();
		runs.newItem(item, interval, from, until, now, toMillies(elapsedEnd, elapsedStart));
		ctx.incrementProgress();
	}

	private static final class Runs
	{
		final EnumField<Interval> interval = EnumField.create(Interval.class).toFinal();
		final DateField from = new DateField().toFinal();
		final DateField until = new DateField().toFinal();
		final DateField run = new DateField().toFinal();
		final LongField elapsed = new LongField().toFinal().min(0);

		private Mount mountIfMounted = null;

		Runs()
		{
			// make non-private
		}

		void onMount(final Schedule pattern, final Type<?> type)
		{
			final ItemField<?> parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
			final PartOf<?> runs = PartOf.create(parent, from);
			final Features features = new Features();
			features.put("parent", parent);
			features.put("interval", interval);
			features.put("from",  from);
			features.put("runs",  runs);
			features.put("until", until);
			features.put("run",   run);
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

		final Mount mount()
		{
			final Mount mount = this.mountIfMounted;
			if(mount==null)
				throw new IllegalStateException("feature not mounted");
			return mount;
		}

		Run newItem(
				final Item item,
				final Interval interval,
				final Date from,
				final Date until,
				final Date now,
				final long elapsed)
		{
			final Mount mount = mount();
			return
				mount.type.newItem(
					Cope.mapAndCast(mount.parent, item),
					this.interval.map(interval),
					this.from.map(from),
					this.until.map(until),
					this.run.map(now),
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

		public long getElapsed()
		{
			return getPattern().runs.elapsed.getMandatory(this);
		}
	}

	// ------------------- deprecated stuff -------------------

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
			@SuppressWarnings("unused") final Class<P> parentClass,
			@Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				Schedule.this.run(parentClass, ctx);
			}}
		);
	}
}
