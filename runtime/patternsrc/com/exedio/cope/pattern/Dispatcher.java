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
import static com.exedio.cope.misc.QueryIterators.iterateTypeTransactionally;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.lang.System.nanoTime;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.Conditions;
import com.exedio.cope.misc.Delete;
import com.exedio.cope.misc.Iterables;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.TimeZoneStrict;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Dispatcher extends Pattern
{
	private static final long serialVersionUID = 1l;

	static final Charset ENCODING = StandardCharsets.UTF_8;

	private final BooleanField pending;
	private final BooleanField    unpendSuccess;
	private final DateField       unpendDate;
	private final CheckConstraint unpendUnison;

	final DateField runDate = new DateField().toFinal();
	final LongField runElapsed = new LongField().toFinal().min(0);
	final BooleanField runSuccess = new BooleanField().toFinal();
	final DataField runFailure = new DataField().toFinal().optional();
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;

	private volatile boolean probeRequired = true;

	public Dispatcher()
	{
		this(new BooleanField().defaultTo(true), true);
	}

	private Dispatcher(final BooleanField pending, final boolean supportPurge)
	{
		this.pending = pending;
		addSource(pending, "pending");
		if(supportPurge)
		{
			unpendSuccess = new BooleanField().optional();
			unpendDate    = new DateField   ().optional();
			unpendUnison  = new CheckConstraint(Conditions.unisonNull(Arrays.asList(unpendSuccess, unpendDate)));
			addSource(unpendSuccess, "unpendSuccess", ComputedElement.get());
			addSource(unpendDate,    "unpendDate",    ComputedElement.get());
			addSource(unpendUnison,  "unpendUnison");
		}
		else
		{
			unpendSuccess = null;
			unpendDate    = null;
			unpendUnison  = null;
		}
	}

	public Dispatcher defaultPendingTo(final boolean defaultConstant)
	{
		return new Dispatcher(pending.defaultTo(defaultConstant), supportsPurge());
	}

	/**
	 * Disables {@link #purge(DispatcherPurgeProperties, JobContext)} functionality.
	 * Avoids additional columns in database needed for purge functionality.
	 */
	public Dispatcher withoutPurge()
	{
		return new Dispatcher(pending.copy(), false);
	}

	boolean supportsPurge()
	{
		return unpendSuccess!=null;
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();
		if(!Dispatchable.class.isAssignableFrom(type.getJavaClass()))
			throw new ClassCastException(
					"type of " + getID() + " must implement " + Dispatchable.class +
					", but was " + type.getJavaClass().getName());

		final ItemField<?> runParent = type.newItemField(CASCADE).toFinal();
		final PartOf<?> runRuns = PartOf.create(runParent, runDate);
		final Features features = new Features();
		features.put("parent", runParent);
		features.put("date", runDate);
		features.put("runs", runRuns);
		features.put("elapsed", runElapsed);
		features.put("success", runSuccess);
		features.put("failure", runFailure);
		final Type<Run> runType = newSourceType(Run.class, features, "Run");
		this.mountIfMounted = new Mount(runParent, runRuns, runType);
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
		final Mount mount = this.mountIfMounted;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	public BooleanField getPending()
	{
		return pending;
	}

	public BooleanField getUnpendSuccess()
	{
		return unpendSuccess;
	}

	public DateField getUnpendDate()
	{
		return unpendDate;
	}

	CheckConstraint getUnpendUnison()
	{
		return unpendUnison;
	}

	@Wrap(order=1000, name="{1}RunParent", doc="Returns the parent field of the run type of {0}.")
	public <P extends Item> ItemField<P> getRunParent(final Class<P> parentClass)
	{
		return mount().runParent.as(parentClass);
	}

	public PartOf<?> getRunRuns()
	{
		return mount().runRuns;
	}

	public DateField getRunDate()
	{
		return runDate;
	}

	public LongField getRunElapsed()
	{
		return runElapsed;
	}

	public BooleanField getRunSuccess()
	{
		return runSuccess;
	}

	public DataField getRunFailure()
	{
		return runFailure;
	}

	public Type<Run> getRunType()
	{
		return mount().runType;
	}

	@Wrap(order=20, doc="Dispatch by {0}.")
	public <P extends Item & Dispatchable> void dispatch(
			final Class<P> parentClass,
			@Parameter("config") final Config config,
			@Parameter("ctx") final JobContext ctx)
	{
		dispatch(parentClass, config, EMPTY_PROBE, ctx);
	}

	private static final EmptyProbe EMPTY_PROBE = new EmptyProbe();

	private static final class EmptyProbe implements Runnable
	{
		EmptyProbe() { // make constructor non-private
		}
		public void run() { // do nothing
		}
	}

	@SuppressFBWarnings("REC_CATCH_EXCEPTION") // Exception is caught when Exception is not thrown
	@Wrap(order=21, doc="Dispatch by {0}.")
	public <P extends Item & Dispatchable> void dispatch(
			final Class<P> parentClass,
			@Parameter("config") final Config config,
			@Parameter("probe") final Runnable probe,
			@Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(config, "config");
		requireNonNull(probe, "probe");
		requireNonNull(ctx, "ctx");

		final Mount mount = mount();
		final Type<P> type = getType().as(parentClass);
		final String id = getID();
		final ItemField<P> runParent = mount.runParent.as(parentClass);
		final Logger logger = LoggerFactory.getLogger(Dispatcher.class.getName() + '.' + id);

		for(final P item : Iterables.once(
				iterateTypeTransactionally(type, pending.equal(true), config.getSearchSize())))
		{
			ctx.stopIfRequested();
			if(probeRequired)
			{
				probe.run();
				probeRequired = false;
			}

			final String itemID = item.getCopeID();
			try(TransactionTry tx = type.getModel().startTransactionTry(id + " dispatch " + itemID))
			{
				if(!isPending(item))
				{
					if(logger.isWarnEnabled())
						logger.warn("Already dispatched {}, probably due to concurrent dispatching.", itemID);
					continue;
				}

				if(isDeferred(item))
				{
					tx.commit();
					if(logger.isDebugEnabled())
						logger.debug("is deferred: {}", itemID);
					continue;
				}

				if(logger.isDebugEnabled())
					logger.debug("dispatching {}", itemID);
				final long start = Clock.currentTimeMillis();
				final long nanoStart = nanoTime();
				try
				{
					item.dispatch(this);

					final long elapsed = toMillies(nanoTime(), nanoStart);
					unpend(item, true, new Date(start));
					mount.runType.newItem(
							runParent.map(item),
							runDate.map(new Date(start)),
							runElapsed.map(elapsed),
							runSuccess.map(true));

					tx.commit();
					logger.info("success for {}, took {}ms", itemID, elapsed);
				}
				catch(final Exception cause)
				{
					final long elapsed = toMillies(nanoTime(), nanoStart);
					probeRequired = true;
					tx.rollbackIfNotCommitted();

					tx.startTransaction(id + " register failure " + itemID);
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try(PrintStream out = new PrintStream(baos, false, ENCODING.name()))
					{
						cause.printStackTrace(out);
					}
					catch(final UnsupportedEncodingException e)
					{
						throw new RuntimeException(ENCODING.name(), e);
					}

					mount.runType.newItem(
						runParent.map(item),
						runDate.map(new Date(start)),
						runElapsed.map(elapsed),
						runSuccess.map(false),
						runFailure.map(baos.toByteArray()));

					final boolean finalFailure =
						mount.runType.newQuery(runParent.equal(item)).total()>=config.getFailureLimit();
					if(finalFailure)
						unpend(item, false, new Date(start));

					tx.commit();

					if(finalFailure)
					{
						if(logger.isErrorEnabled())
							logger.error("final failure for " + itemID + ", took " + elapsed + "ms", cause);
						item.notifyFinalFailure(this, cause);
					}
					else
					{
						if(logger.isWarnEnabled())
							logger.warn("temporary failure for " + itemID + ", took " + elapsed + "ms", cause);
					}
				}
			}
			ctx.incrementProgress();
		}
	}

	private void unpend(final Item item, final boolean success, final Date date)
	{
		final ArrayList<SetValue<?>> sv = new ArrayList<>(3);
		sv.add(pending.map(false));
		if(supportsPurge())
		{
			sv.add(unpendSuccess.map(success));
			sv.add(unpendDate   .map(date));
		}
		item.set(sv.toArray(new SetValue<?>[sv.size()]));
	}

	/**
	 * This helper method is needed to work around two unchecked cast warnings
	 * issued by jdk1.7 javac.
	 */
	private boolean isDeferred(final Item item)
	{
		return
			(item instanceof DispatchDeferrable) &&
			((DispatchDeferrable)item).isDeferred(this);
	}

	/**
	 * For junit tests only
	 */
	void setProbeRequired(final boolean probeRequired)
	{
		this.probeRequired = probeRequired;
	}

	@Wrap(order=30, doc="Returns, whether this item is yet to be dispatched by {0}.")
	public boolean isPending(final Item item)
	{
		return pending.getMandatory(item);
	}

	@Wrap(order=40, doc="Sets whether this item is yet to be dispatched by {0}.")
	public void setPending(
			final Item item,
			@Parameter("pending") final boolean pending)
	{
		this.pending.set(item, pending);
	}

	@Wrap(order=50, doc="Returns the date, this item was last successfully dispatched by {0}.")
	public Date getLastSuccessDate(final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? runDate.get(success) : null;
	}

	@Wrap(order=60, doc="Returns the milliseconds, this item needed to be last successfully dispatched by {0}.")
	public Long getLastSuccessElapsed(final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? runElapsed.get(success) : null;
	}

	private Run getLastSuccess(final Item item)
	{
		final Mount mount = mount();
		final Query<Run> q =
			mount.runType.newQuery(Cope.and(
				Cope.equalAndCast(mount.runParent, item),
				runSuccess.equal(true)));
		q.setOrderBy(mount.runType.getThis(), false);
		q.setLimit(0, 1);
		return q.searchSingleton();
	}

	@Wrap(order=70, doc="Returns the attempts to dispatch this item by {0}.")
	public List<Run> getRuns(final Item item)
	{
		final Mount mount = mount();
		return
			mount.runType.search(
					Cope.equalAndCast(mount.runParent, item),
					mount.runType.getThis(),
					true);
	}

	@Wrap(order=80, doc="Returns the failed attempts to dispatch this item by {0}.")
	public List<Run> getFailures(final Item item)
	{
		final Mount mount = mount();
		return
			mount.runType.search(
					Cope.and(
							Cope.equalAndCast(mount.runParent, item),
							runSuccess.equal(false)),
					mount.runType.getThis(),
					true);
	}

	public static final class Config
	{
		private final int failureLimit;
		private final int searchSize;

		public Config()
		{
			this(5, 100);
		}

		public Config(final int failureLimit, final int searchSize)
		{
			if(failureLimit<1)
				throw new IllegalArgumentException("failureLimit must be greater zero, but was " + failureLimit + '.');
			if(searchSize<1)
				throw new IllegalArgumentException("searchSize must be greater zero, but was " + searchSize + '.');

			this.failureLimit = failureLimit;
			this.searchSize = searchSize;
		}

		public int getFailureLimit()
		{
			return failureLimit;
		}

		public int getSearchSize()
		{
			return searchSize;
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

		public Dispatcher getPattern()
		{
			return (Dispatcher)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return getPattern().mount().runParent.get(this);
		}

		public Date getDate()
		{
			return getPattern().runDate.get(this);
		}

		public long getElapsed()
		{
			return getPattern().runElapsed.getMandatory(this);
		}

		public boolean isSuccess()
		{
			return getPattern().runSuccess.getMandatory(this);
		}

		public String getFailure()
		{
			return new String(getPattern().runFailure.getArray(this), ENCODING);
		}
	}


	/**
	 * @throws IllegalArgumentException if purge is disabled by {@link #withoutPurge()}.
	 */
	@Wrap(order=100, hide=SupportsPurgeGetter.class)
	public void purge(
			@Parameter("properties") final DispatcherPurgeProperties properties,
			@Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(properties, "properties");
		requireNonNull(ctx, "ctx");
		if(!supportsPurge())
			throw new IllegalArgumentException(
					"purge has been disabled for Dispatcher " + getID() +
					" by method withoutPurge()");

		final Query<? extends Item> query = purgeQuery(properties);
		if(query!=null)
			Delete.delete(query, "Dispatcher#purge " + getID(), ctx);
	}

	private static final class SupportsPurgeGetter implements BooleanGetter<Dispatcher>
	{
		public boolean get(final Dispatcher feature)
		{
			return !feature.supportsPurge();
		}
	}

	Query<? extends Item> purgeQuery(final DispatcherPurgeProperties properties)
	{
		final int success = properties.delayDaysSuccess;
		final int failure = properties.delayDaysFinalFailure;
		if(success==0 && failure==0)
			return null;

		final long now = Clock.currentTimeMillis();
		final Condition dateCondition;

		if(success==failure)
		{
			dateCondition = dateBefore(now, success);
		}
		else
		{
			dateCondition = Cope.or(
					unpendSuccess.equal(true ).and(dateBefore(now, success)),
					unpendSuccess.equal(false).and(dateBefore(now, failure))
				);
		}

		return getType().newQuery(pending.equal(false).and(dateCondition));
	}

	private Condition dateBefore(final long now, final int days)
	{
		if(days==0)
			return Condition.FALSE;

		final GregorianCalendar cal = new GregorianCalendar(
				TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.setTimeInMillis(now);
		cal.add(Calendar.DATE, -days);
		return unpendDate.less(cal.getTime());
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #dispatch(Class,Config,JobContext)} instead.
	 * @return the number of successfully dispatched items
	 */
	@Wrap(order=10,
			doc="Dispatch by {0}.",
			docReturn="the number of successfully dispatched items")
	@Deprecated
	public <P extends Item & Dispatchable> int dispatch(
			final Class<P> parentClass,
			@Parameter("config") final Config config,
			@Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				dispatch(parentClass, config, ctx);
			}}
		);
	}
}
