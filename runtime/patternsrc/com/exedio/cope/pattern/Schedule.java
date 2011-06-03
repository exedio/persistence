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

import static com.exedio.cope.misc.TimeUtil.toMillies;
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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

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
import com.exedio.cope.This;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.JobContext;

public final class Schedule extends Pattern
{
	private static final long serialVersionUID = 1l;

	public enum Interval
	{
		DAILY,
		WEEKLY,
		MONTHLY;
	}

	private final Locale locale;

	private final BooleanField enabled = new BooleanField().defaultTo(true);
	private final EnumField<Interval> interval = Item.newEnumField(Interval.class).defaultTo(Interval.DAILY);

	final DateField runFrom = new DateField().toFinal();
	final DateField runUntil = new DateField().toFinal();
	final DateField runRun = new DateField().toFinal();
	final LongField runElapsed = new LongField().toFinal();

	private Mount mount = null;

	/**
	 * @param locale
	 *        specifies the locale used for creating the {@link GregorianCalendar}
	 *        that does all the date computations.
	 *        Is important for specifying the first day of week (Monday vs. Sunday)
	 */
	public Schedule(final Locale locale)
	{
		if(locale==null)
			throw new NullPointerException("locale");

		this.locale = locale;
		addSource(enabled,  "enabled");
		addSource(interval, "interval");
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

		final ItemField<?> runParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final PartOf<?> runRuns = PartOf.newPartOf(runParent, runFrom);
		final Features features = new Features();
		features.put("parent", runParent);
		features.put("from",  runFrom);
		features.put("runs",  runRuns);
		features.put("until", runUntil);
		features.put("run",   runRun);
		features.put("elapsed", runElapsed);
		final Type<Run> runType = newSourceType(Run.class, features, "Run");
		this.mount = new Mount(runParent, runRuns, runType);
	}

	private static final class Mount
	{
		final ItemField<?> runParent;
		final PartOf<?> runRuns;
		final Type<Run> runType;

		Mount(
				final ItemField<?> runParent,
				final PartOf<?> runRuns,
				final Type<Run> runType)
		{
			assert runParent!=null;
			assert runRuns!=null;
			assert runType!=null;

			this.runParent = runParent;
			this.runRuns = runRuns;
			this.runType = runType;
		}
	}

	final Mount mount()
	{
		final Mount mount = this.mount;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	public BooleanField getEnabled()
	{
		return enabled;
	}

	public EnumField<Interval> getInterval()
	{
		return interval;
	}

	public ItemField<?> getRunParent()
	{
		return mount().runParent;
	}

	public PartOf<?> getRunRuns()
	{
		return mount().runRuns;
	}

	public DateField getRunFrom()
	{
		return runFrom;
	}

	public DateField getRunUntil()
	{
		return runUntil;
	}

	public DateField getRunRun()
	{
		return runRun;
	}

	public LongField getRunElapsed()
	{
		return runElapsed;
	}

	public Type<Run> getRunType()
	{
		return mount().runType;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(Schedule.class, this, super.getWrappers());
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
	public void run(
			@Parameter("ctx") final JobContext ctx)
	{
		run(ctx, new Date());
	}

	void run(final JobContext ctx, final Date now)
	{
		run(getType(), ctx, now);
	}

	private <P extends Item> void run(final Type<P> type, final JobContext ctx, final Date now)
	{
		if(ctx==null)
			throw new NullPointerException("ctx");

		final Mount mount = mount();
		final This<P> typeThis = type.getThis();
		final Model model = type.getModel();
		final String featureID = getID();
		final GregorianCalendar cal = new GregorianCalendar(locale);
		cal.setTime(now);
		cal.set(MILLISECOND, 0);
		cal.set(SECOND, 0);
		cal.set(MINUTE, 0);
		cal.set(HOUR_OF_DAY, 0);
		final Date untilDaily = cal.getTime();
		cal.set(DAY_OF_WEEK, MONDAY);
		final Date untilWeekly = cal.getTime();
		cal.setTime(untilDaily);
		cal.set(DAY_OF_MONTH, 1);
		final Date untilMonthly = cal.getTime();


		final List<P> toRun;
		try
		{
			model.startTransaction(featureID + " search");
			final Query<P> q = type.newQuery(Cope.and(
					enabled.equal(true),
					mount.runType.getThis().isNull()));
			q.joinOuterLeft(mount.runType,
					Cope.and(
						mount.runParent.as(type.getJavaClass()).equal(typeThis),
						Cope.or(
							interval.equal(Interval.DAILY ).and(runUntil.greaterOrEqual(untilDaily)),
							interval.equal(Interval.WEEKLY).and(runUntil.greaterOrEqual(untilWeekly)),
							interval.equal(Interval.MONTHLY).and(runUntil.greaterOrEqual(untilMonthly))
						)
					)
			);
			q.setOrderBy(typeThis, true);
			toRun = q.search();
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}

		if(toRun.isEmpty())
			return;

		for(final P item : toRun)
		{
			if(ctx.requestedToStop())
				return;

			final Scheduleable itemCasted = (Scheduleable)item;
			final String itemID = item.getCopeID();
			try
			{
				model.startTransaction(featureID + " schedule " + itemID);
				final Interval interval = this.interval.get(item);
				final Date until;
				switch(interval)
				{
					case DAILY:  until = untilDaily ; break;
					case WEEKLY: until = untilWeekly; break;
					case MONTHLY:until = untilMonthly;break;
					default: throw new RuntimeException(interval.name());
				}
				cal.setTime(until);
				switch(interval)
				{
					case DAILY:  cal.add(DAY_OF_WEEK  , -1); break;
					case WEEKLY: cal.add(WEEK_OF_MONTH, -1); break;
					case MONTHLY:cal.add(MONTH,         -1); break;
					default: throw new RuntimeException(interval.name());
				}
				final Date from = cal.getTime();
				final long elapsedStart = nanoTime();
				itemCasted.run(this, from, until, ctx);
				final long elapsedEnd = nanoTime();
				mount.runType.newItem(
					Cope.mapAndCast(mount.runParent, item),
					this.runFrom.map(from),
					this.runUntil.map(until),
					this.runRun.map(now),
					this.runElapsed.map(toMillies(elapsedEnd, elapsedStart)));
				model.commit();
				ctx.incrementProgress();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
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
			return getPattern().mount().runParent.get(this);
		}

		public Date getFrom()
		{
			return getPattern().runFrom.get(this);
		}

		public Date getUntil()
		{
			return getPattern().runUntil.get(this);
		}

		public Date getRun()
		{
			return getPattern().runRun.get(this);
		}

		public long getElapsed()
		{
			return getPattern().runElapsed.getMandatory(this);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #Schedule(Locale)} instead.
	 */
	@Deprecated
	public Schedule()
	{
		this(Locale.getDefault());
	}

	/**
	 * @deprecated Use {@link #run(com.exedio.cope.util.Interrupter)} instead.
	 */
	@Deprecated
	public <P extends Item> int run(@SuppressWarnings("unused") final Class<P> parentClass, final com.exedio.cope.util.Interrupter interrupter)
	{
		return run(interrupter);
	}

	/**
	 * @deprecated Use {@link #run(JobContext)} instead.
	 */
	@Wrap(order=50)
	@Deprecated
	public int run(
			@Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return run(interrupter, new Date());
	}

	@Deprecated
	private int run(final com.exedio.cope.util.Interrupter interrupter, final Date now)
	{
		final Schedule s = this;
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				s.run(ctx, now);
			}}
		);
	}
}
