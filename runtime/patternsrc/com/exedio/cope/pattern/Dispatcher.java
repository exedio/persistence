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
import static com.exedio.cope.misc.Check.requireGreaterZero;
import static com.exedio.cope.misc.QueryIterators.iterateTypeTransactionally;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static java.lang.System.nanoTime;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopeSchemaValue;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.CopeSchemaNameElement;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WrapFeature
public final class Dispatcher extends Pattern
{
	private static final long serialVersionUID = 1l;

	static final Charset ENCODING = StandardCharsets.UTF_8;
	private static final SetValue<?>[] EMPTY_SET_VALUE_ARRAY = new SetValue<?>[0];

	private final BooleanField pending;
	private final BooleanField noPurge;
	private final CompositeField<Unpend> unpend;

	private static final class Unpend extends Composite
	{
		static final BooleanField success = new BooleanField();
		static final DateField date = new DateField();

		Unpend(final boolean success, final Date date)
		{
			super(
				Unpend.success.map(success),
				Unpend.date.map(date));
		}
		private Unpend(final SetValue<?>... setValues) { super(setValues); }
		private static final long serialVersionUID = 1l;
	}

	public enum Result
	{
		/**
		 * A failure that leaves {@link Dispatcher#isPending(Item) pending} unchanged.
		 */
		@CopeSchemaValue(-20)
		transientFailure,

		/**
		 * A failure that causes {@link Dispatcher#isPending(Item) pending} to be set to false.
		 */
		@CopeSchemaValue(-10)
		finalFailure,

		/**
		 * @deprecated
		 * A historical failure where it is not known, whether it was
		 * {@link #transientFailure transient} or  {@link #finalFailure final}.
		 */
		@Deprecated
		@CopeSchemaValue(0) // matches value "false" of former BooleanField
		failure,

		@CopeSchemaValue(1) // matches value "true" of former BooleanField
		success;

		public boolean isSuccess()
		{
			return this==success;
		}

		static Result failure(final boolean isFinal)
		{
			return isFinal ? finalFailure : transientFailure;
		}
	}

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;

	private volatile boolean probeRequired = true;

	public Dispatcher()
	{
		this(new BooleanField().defaultTo(true), true);
	}

	private Dispatcher(final BooleanField pending, final boolean supportPurge)
	{
		this.pending = addSourceFeature(pending, "pending");
		if(supportPurge)
		{
			noPurge = addSourceFeature(new BooleanField().defaultTo(false), "noPurge");
			unpend = addSourceFeature(CompositeField.create(Unpend.class).optional(), "unpend", ComputedElement.get());
		}
		else
		{
			noPurge = null;
			unpend = null;
		}
	}

	public Dispatcher defaultPendingTo(final boolean defaultConstant)
	{
		return new Dispatcher(pending.defaultTo(defaultConstant), supportsPurge());
	}

	/**
	 * Disables {@link #purge(DispatcherPurgeProperties, JobContext)} functionality.
	 * Avoids additional columns in database needed for purge functionality.
	 * Additionally disables resetting failureLimit on unpend.
	 */
	public Dispatcher withoutPurge()
	{
		return new Dispatcher(pending.copy(), false);
	}

	boolean supportsPurge()
	{
		return unpend!=null;
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

		this.mountIfMounted = new Mount(type);
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	private final class Mount
	{
		final ItemField<?> runParent;
		final DateField runDate = new DateField().toFinal();
		final PartOf<?> runRuns;
		final LongField runElapsed = new LongField().toFinal().min(0);
		final EnumField<Result> runResult = EnumField.create(Result.class).toFinal();
		final DataField runFailure = new DataField().toFinal().optional();
		final Type<Run> runType;

		Mount(final Type<?> type)
		{
			runParent = type.newItemField(CASCADE).toFinal();
			runRuns = PartOf.create(runParent, runDate);
			final Features features = new Features();
			features.put("parent", runParent);
			features.put("date", runDate);
			features.put("runs", runRuns);
			features.put("elapsed", runElapsed);
			features.put("result", runResult, CustomAnnotatedElement.create(CopeSchemaNameElement.get("success")));
			features.put("failure", runFailure);
			runType = newSourceType(Run.class, features, "Run");
		}
	}

	Mount mount()
	{
		return requireMounted(mountIfMounted);
	}

	public BooleanField getPending()
	{
		return pending;
	}

	public BooleanField getNoPurge()
	{
		return noPurge;
	}

	CompositeField<Unpend> getUnpend()
	{
		return unpend;
	}

	public BooleanField getUnpendSuccess()
	{
		return unpend!=null ? unpend.of(Unpend.success) : null;
	}

	public DateField getUnpendDate()
	{
		return unpend!=null ? unpend.of(Unpend.date) : null;
	}

	CheckConstraint getUnpendUnison()
	{
		return unpend!=null ? unpend.getUnison() : null;
	}

	@Wrap(order=1000, name="{1}RunParent", doc="Returns the parent field of the run type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getRunParent(@Nonnull final Class<P> parentClass)
	{
		return mount().runParent.as(parentClass);
	}

	public PartOf<?> getRunRuns()
	{
		return mount().runRuns;
	}

	public DateField getRunDate()
	{
		return mount().runDate;
	}

	public LongField getRunElapsed()
	{
		return mount().runElapsed;
	}

	public EnumField<Result> getRunResult()
	{
		return mount().runResult;
	}

	public DataField getRunFailure()
	{
		return mount().runFailure;
	}

	public Type<Run> getRunType()
	{
		return mount().runType;
	}

	@Wrap(order=20, doc="Dispatch by {0}.")
	public <P extends Item & Dispatchable> void dispatch(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("config") final Config config,
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		dispatch(parentClass, config, EMPTY_PROBE, ctx);
	}

	private static final EmptyProbe EMPTY_PROBE = new EmptyProbe();

	private static final class EmptyProbe implements Runnable
	{
		EmptyProbe() { // make constructor non-private
		}
		@Override
		public void run() { // do nothing
		}
	}

	@SuppressFBWarnings("REC_CATCH_EXCEPTION") // Exception is caught when Exception is not thrown
	@Wrap(order=21, doc="Dispatch by {0}.")
	public <P extends Item & Dispatchable> void dispatch(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("config") final Config config,
			@Nonnull @Parameter("probe") final Runnable probe,
			@Nonnull @Parameter("ctx") final JobContext ctx)
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
				iterateTypeTransactionally(
						type,
						pending.equal(true).and(config.narrowCondition),
						config.getSearchSize())))
		{
			if(probeRequired)
			{
				if(ctx.supportsMessage())
					ctx.setMessage("probe");
				deferOrStopIfRequested(ctx);
				probe.run();
				probeRequired = false;
			}

			final String itemID = item.getCopeID();
			if(ctx.supportsMessage())
				ctx.setMessage(itemID);
			deferOrStopIfRequested(ctx);
			try(TransactionTry tx = type.getModel().startTransactionTry(id + " dispatch " + itemID))
			{
				if(!isPending(item))
				{
					if(logger.isWarnEnabled())
						logger.warn("Already dispatched {}, probably due to concurrent dispatching.", itemID);
					continue;
				}

				if(item.isDeferred(this))
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
							mount.runDate.map(new Date(start)),
							mount.runElapsed.map(elapsed),
							mount.runResult.map(Result.success));

					tx.commit();
					logger.info("success for {}, took {}ms", itemID, elapsed);
				}
				catch(final Exception cause)
				{
					final long elapsed = toMillies(nanoTime(), nanoStart);
					probeRequired = true;
					tx.rollbackIfNotCommitted();

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try(PrintStream out = new PrintStream(baos, false, ENCODING.name()))
					{
						cause.printStackTrace(out);
					}
					catch(final UnsupportedEncodingException e)
					{
						throw new RuntimeException(ENCODING.name(), e);
					}

					tx.startTransaction(id + " register failure " + itemID);

					final int limit = config.getFailureLimit();
					final boolean isFinal;
					final int remaining;
					{
						final Query<Run> query = mount.runType.newQuery(runParent.equal(item));
						if(supportsPurge())
						{
							final Date unpendDate = unpend.of(Unpend.date).get(item);
							// effectively resets failureLimit on unpend
							if(unpendDate!=null)
								query.narrow(mount.runDate.greater(unpendDate));
						}
						final int total = query.total();
						isFinal = total >= limit - 1;
						remaining = isFinal ? 0 : (limit - 1 - total);
					}

					mount.runType.newItem(
						runParent.map(item),
						mount.runDate.map(new Date(start)),
						mount.runElapsed.map(elapsed),
						mount.runResult.map(Result.failure(isFinal)),
						mount.runFailure.map(baos.toByteArray()));

					if(isFinal)
						unpend(item, false, new Date(start));

					tx.commit();

					if(isFinal)
					{
						if(logger.isErrorEnabled())
							//noinspection StringConcatenationArgumentToLogCall
							logger.error(
									"final failure for " + itemID + ", " +
									"took " + elapsed + "ms, " +
									limit + " runs exhausted",
									cause);
						item.notifyFinalFailure(this, cause);
					}
					else
					{
						if(logger.isWarnEnabled())
							//noinspection StringConcatenationArgumentToLogCall
							logger.warn(
									"transient failure for " + itemID + ", " +
									"took " + elapsed + "ms, " +
									remaining + " of " + limit + " runs remaining",
									cause);
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
			sv.add(unpend.map(new Unpend(success, date)));
		item.set(sv.toArray(EMPTY_SET_VALUE_ARRAY));
	}

	/**
	 * For junit tests only
	 */
	void setProbeRequired(final boolean probeRequired)
	{
		this.probeRequired = probeRequired;
	}

	/**
	 * For junit tests only
	 */
	void reset()
	{
		this.probeRequired = true;
	}

	@Wrap(order=30, doc="Returns, whether this item is yet to be dispatched by {0}.")
	public boolean isPending(@Nonnull final Item item)
	{
		return pending.getMandatory(item);
	}

	@Wrap(order=40, doc="Sets whether this item is yet to be dispatched by {0}.")
	public void setPending(
			@Nonnull final Item item,
			@Parameter("pending") final boolean pending)
	{
		this.pending.set(item, pending);
	}

	@Wrap(order=45, doc="Returns, whether this item is allowed to be purged by {0}.", hide=SupportsPurgeGetter.class)
	public boolean isNoPurge(@Nonnull final Item item)
	{
		return noPurge.getMandatory(item);
	}

	@Wrap(order=47, doc="Sets whether this item is allowed to be purged by {0}.", hide=SupportsPurgeGetter.class)
	public void setNoPurge(
			@Nonnull final Item item,
			@Parameter("noPurge") final boolean noPurge)
	{
		this.noPurge.set(item, noPurge);
	}

	@Wrap(order=50, doc="Returns the date, this item was last successfully dispatched by {0}.")
	@Nullable
	public Date getLastSuccessDate(@Nonnull final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? mount().runDate.get(success) : null;
	}

	@Wrap(order=60, doc="Returns the milliseconds, this item needed to be last successfully dispatched by {0}.")
	@Nullable
	public Long getLastSuccessElapsed(@Nonnull final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? mount().runElapsed.get(success) : null;
	}

	private Run getLastSuccess(final Item item)
	{
		final Mount mount = mount();
		final Query<Run> q =
			mount.runType.newQuery(Cope.and(
				Cope.equalAndCast(mount.runParent, item),
				mount().runResult.equal(Result.success)));
		q.setOrderBy(mount.runType.getThis(), false);
		q.setPage(0, 1);
		return q.searchSingleton();
	}

	@Wrap(order=70, doc="Returns the attempts to dispatch this item by {0}.")
	@Nonnull
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
	@Nonnull
	public List<Run> getFailures(final Item item)
	{
		final Mount mount = mount();
		return
			mount.runType.search(
					Cope.and(
							Cope.equalAndCast(mount.runParent, item),
							mount.runResult.notEqual(Result.success)),
					mount.runType.getThis(),
					true);
	}

	public static final class Config
	{
		static final int DEFAULT_FAILURE_LIMIT = 5;
		static final int DEFAULT_SEARCH_SIZE = 1000;
		private static final Condition DEFAULT_NARROW_CONDITION = Condition.TRUE;

		private final int failureLimit;
		private final int searchSize;
		private final Condition narrowCondition;

		public Config()
		{
			this(DEFAULT_FAILURE_LIMIT, DEFAULT_SEARCH_SIZE);
		}

		public Config(final int failureLimit, final int searchSize)
		{
			this(failureLimit, searchSize, DEFAULT_NARROW_CONDITION);
		}

		private Config(
				final int failureLimit,
				final int searchSize,
				final Condition narrowCondition)
		{
			this.failureLimit = requireGreaterZero(failureLimit, "failureLimit");
			this.searchSize = requireGreaterZero(searchSize, "searchSize");
			this.narrowCondition = narrowCondition;
			assert narrowCondition!=null;
		}

		public int getFailureLimit()
		{
			return failureLimit;
		}

		public int getSearchSize()
		{
			return searchSize;
		}

		public Config narrow(final Condition condition)
		{
			return new Config(failureLimit, searchSize,
					narrowCondition.and(condition));
		}

		public Config resetNarrow()
		{
			return new Config(failureLimit, searchSize, DEFAULT_NARROW_CONDITION);
		}

		public Condition getNarrowCondition()
		{
			return narrowCondition;
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
			return mount().runParent.get(this);
		}

		public Date getDate()
		{
			return mount().runDate.get(this);
		}

		public long getElapsed()
		{
			return mount().runElapsed.getMandatory(this);
		}

		public Result getResult()
		{
			return mount().runResult.get(this);
		}

		public boolean isSuccess()
		{
			return getResult().isSuccess();
		}

		public String getFailure()
		{
			final byte[] bytes = mount().runFailure.getArray(this);
			return
					bytes!=null
					? new String(bytes, ENCODING)
					: null;
		}

		private Mount mount()
		{
			return getPattern().mount();
		}
	}


	/**
	 * @throws IllegalArgumentException if purge is disabled by {@link #withoutPurge()}.
	 */
	@Wrap(order=100, hide=SupportsPurgeGetter.class)
	public void purge(
			@Nonnull @Parameter("properties") final DispatcherPurgeProperties properties,
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(properties, "properties");
		requireNonNull(ctx, "ctx");
		purge(properties, Condition.TRUE, ctx);
	}

	/**
	 * @throws IllegalArgumentException if purge is disabled by {@link #withoutPurge()}.
	 */
	@Wrap(order=110, hide=SupportsPurgeGetter.class)
	public void purge(
			@Nonnull @Parameter("properties") final DispatcherPurgeProperties properties,
			@Nonnull @Parameter("restriction") final Condition restriction,
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		requireNonNull(properties, "properties");
		requireNonNull(restriction, "restriction");
		requireNonNull(ctx, "ctx");
		if(!supportsPurge())
			throw new IllegalArgumentException(
					"purge has been disabled for Dispatcher " + getID() +
					" by method withoutPurge()");

		final Query<? extends Item> query = purgeQuery(properties, restriction);
		if(query!=null)
			Delete.delete(query, "Dispatcher#purge " + getID(), ctx);
	}

	Query<? extends Item> purgeQuery(
			final DispatcherPurgeProperties properties,
			final Condition restriction)
	{
		final int success = properties.retainDaysSuccess;
		final int failure = properties.retainDaysFinalFailure;
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
			//noinspection ConstantConditions OK: getUnpendSuccess cannot return null if supportsPurge return true
			dateCondition = Cope.or(
					getUnpendSuccess().equal(true ).and(dateBefore(now, success)),
					getUnpendSuccess().equal(false).and(dateBefore(now, failure))
				);
		}

		return getType().newQuery(
				pending.equal(false).and(
				noPurge.equal(false)).and(
				restriction).and(
				dateCondition));
	}

	private Condition dateBefore(final long now, final int days)
	{
		if(days==0)
			return Condition.FALSE;

		final GregorianCalendar cal = new GregorianCalendar(
				TimeZoneStrict.getTimeZone("UTC"), Locale.ENGLISH);
		cal.setTimeInMillis(now);
		cal.add(Calendar.DATE, -days);
		//noinspection ConstantConditions OK: getUnpendDate cannot return null if supportsPurge return true
		return getUnpendDate().less(cal.getTime());
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
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("config") final Config config,
			@Nullable @Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			ctx -> dispatch(parentClass, config, ctx)
		);
	}
}
