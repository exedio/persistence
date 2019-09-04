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
import static com.exedio.cope.pattern.FeatureTimer.timer;
import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
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
import com.exedio.cope.IntegerField;
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
import com.exedio.cope.misc.SetValueUtil;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Timer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
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
		finalFailure,

		/**
		 * @deprecated
		 * A historical failure where it is not known, whether it was
		 * {@link #transientFailure transient} or  {@link #finalFailure final}.
		 */
		@Deprecated
		//@CopeSchemaValue(0) is redundant, matches value "false" of former BooleanField
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

	private final boolean supportRemaining;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Variant variant;

	private abstract static class Variant
	{
		final Supplier<? extends AutoCloseable> session;

		protected Variant(final Supplier<? extends AutoCloseable> session)
		{
			this.session = session;
		}

		abstract void dispatch(Dispatcher dispatcher, Item item, AutoCloseable session) throws Exception;
		abstract void notifyFinalFailure(Dispatcher dispatcher, Item item, Exception cause);
	}

	@FunctionalInterface
	public interface Target<I extends Item>
	{
		void dispatch(I item) throws Exception;
	}

	@FunctionalInterface
	public interface SessionTarget<I extends Item, S extends AutoCloseable>
	{
		void dispatch(I item, S session) throws Exception;
	}

	private static final class TargetVariant extends Variant
	{
		private final SessionTarget<?,?> target;
		private final BiConsumer<? extends Item,Exception> onFinalFailure;

		private TargetVariant(
				final Supplier<? extends AutoCloseable> session,
				final SessionTarget<?,?> target,
				final BiConsumer<? extends Item,Exception> onFinalFailure)
		{
			super(session);
			this.target = requireNonNull(target, "target");
			this.onFinalFailure = onFinalFailure;
		}

		@SuppressWarnings("unchecked")
		@Override void dispatch(final Dispatcher dispatcher, final Item item, final AutoCloseable session) throws Exception
		{
			assert (this.session==null) == (session==null);
			((SessionTarget<Item,AutoCloseable>)target).dispatch(item, session);
		}

		@SuppressWarnings("unchecked")
		@Override void notifyFinalFailure(final Dispatcher dispatcher, final Item item, final Exception cause)
		{
			if(onFinalFailure!=null)
				((BiConsumer<Item,Exception>)onFinalFailure).accept(item, cause);
		}
	}

	private static final Variant INTERFACE_VARIANT = new Variant(null)
	{
		@Override void dispatch(final Dispatcher dispatcher, final Item item, final AutoCloseable session) throws Exception
		{
			assert session==null;
			((Dispatchable)item).dispatch(dispatcher);
		}

		@Override void notifyFinalFailure(final Dispatcher dispatcher, final Item item, final Exception cause)
		{
			((Dispatchable)item).notifyFinalFailure(dispatcher, cause);
		}
	};

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private RunType runTypeIfMounted = null;

	private volatile boolean probeRequired = true;

	/**
	 * If your code now looks like this:
	 * <pre>
	 * class Mail extends Item <span style="text-decoration: line-through;">implements Dispatchable</span>
	 * {
	 *    static final Dispatcher toSmtp =
	 *       <span style="text-decoration: line-through;">new</span> Dispatcher();
	 *
	 *    <span style="text-decoration: line-through;">@Override</span>
	 *    <span style="text-decoration: line-through;">public</span> void dispatch(<span style="text-decoration: line-through;">Dispatcher dispatcher</span>)
	 *    {
	 *       // your code
	 *    }
	 * }
	 * </pre>
	 * then change it to this:
	 * <pre>
	 * class Mail extends Item
	 * {
	 *    static final Dispatcher toSmtp =
	 *       Dispatcher<b>.create</b>(<b>Mail::dispatch</b>);
	 *
	 *    <b>@{@link com.exedio.cope.instrument.WrapInterim WrapInterim}(methodBody=false)</b>
	 *    <b>private</b> void dispatch()
	 *    {
	 *       // your code
	 *    }
	 * }
	 * </pre>
	 * @deprecated Use {@link #create(Target,BiConsumer)} instead as described.
	 */
	@Deprecated
	public Dispatcher()
	{
		this(new BooleanField().defaultTo(true), true, true, INTERFACE_VARIANT);
	}

	public static <I extends Item> Dispatcher create(
			final Target<I> target)
	{
		return create(target, null);
	}

	public static <I extends Item> Dispatcher create(
			final Target<I> target,
			final BiConsumer<I,Exception> finalFailureListener)
	{
		return createWithSession(
				null,
				toSession(target),
				finalFailureListener);
	}

	private static <I extends Item, S extends AutoCloseable> SessionTarget<I,S> toSession(final Target<I> target)
	{
		requireNonNull(target, "target");
		return (item, session) ->
		{
			assert session==null;
			target.dispatch(item);
		};
	}

	public static <I extends Item, S extends AutoCloseable> Dispatcher createWithSession(
			final Supplier<S> session,
			final SessionTarget<I,S> target)
	{
		return createWithSession(session, target, null);
	}

	public static <I extends Item, S extends AutoCloseable> Dispatcher createWithSession(
			final Supplier<S> session,
			final SessionTarget<I,S> target,
			final BiConsumer<I,Exception> finalFailureListener)
	{
		return new Dispatcher(
				new BooleanField().defaultTo(true), true, true,
				new TargetVariant(session, target, finalFailureListener));
	}

	private Dispatcher(final BooleanField pending, final boolean supportPurge, final boolean supportRemaining, final Variant variant)
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
		this.supportRemaining = supportRemaining;
		this.variant = requireNonNull(variant);
	}

	public Dispatcher defaultPendingTo(final boolean defaultConstant)
	{
		return new Dispatcher(pending.defaultTo(defaultConstant), supportsPurge(), supportRemaining, variant);
	}

	/**
	 * Disables {@link #purge(DispatcherPurgeProperties, JobContext)} functionality.
	 * Avoids additional columns in database needed for purge functionality.
	 * Additionally disables resetting failureLimit on unpend.
	 */
	public Dispatcher withoutPurge()
	{
		return new Dispatcher(pending.copy(), false, supportRemaining, variant);
	}

	boolean supportsPurge()
	{
		return unpend!=null;
	}

	/**
	 * Disables {@link Run#getRemaining()} and {@link Run#getLimit()} fields.
	 * Avoids additional columns in database.
	 */
	public Dispatcher withoutRemaining()
	{
		return new Dispatcher(pending.copy(), supportsPurge(), false, variant);
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();
		if(variant==INTERFACE_VARIANT &&
			!Dispatchable.class.isAssignableFrom(type.getJavaClass()))
			throw new ClassCastException(
					"type of " + getID() + " must implement " + Dispatchable.class +
					", but was " + type.getJavaClass().getName());

		this.runTypeIfMounted = new RunType(type);

		if(variant.session!=null)
			FeatureMeter.onMount(this, sessionCreateTimer, sessionCloseTimer);
		FeatureMeter.onMount(this, succeedTimer, failTimer, probeTimer);
		if(supportsPurge())
			FeatureMeter.onMount(this, purgeTimer);
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
		requireParentClass(parentClass, "parentClass");
		return runType().parent.as(parentClass);
	}

	public PartOf<?> getRunRuns()
	{
		return runType().runs;
	}

	public DateField getRunDate()
	{
		return runType().date;
	}

	public LongField getRunElapsed()
	{
		return runType().elapsed;
	}

	public IntegerField getRunRemaining()
	{
		return runType().remaining;
	}

	public IntegerField getRunLimit()
	{
		return runType().limit;
	}

	public EnumField<Result> getRunResult()
	{
		return runType().result;
	}

	public DataField getRunFailure()
	{
		return runType().failure;
	}

	public Type<Run> getRunType()
	{
		return runType().type;
	}

	@Wrap(order=20, doc="Dispatch by {0}.")
	public <P extends Item> void dispatch(
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
	public <P extends Item> void dispatch(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("config") final Config config,
			@Nonnull @Parameter("probe") final Runnable probe,
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		final Type<P> type =
				requireParentClass(parentClass, "parentClass");
		requireNonNull(config, "config");
		requireNonNull(probe, "probe");
		requireNonNull(ctx, "ctx");

		final RunType runType = runType();
		final String id = getID();
		final ItemField<P> runParent = runType.parent.as(parentClass);
		final Logger logger = LoggerFactory.getLogger(Dispatcher.class.getName() + '.' + id);

		try(DispatcherSessionManager session = new DispatcherSessionManager(
				variant.session, config, ctx, id, sessionCreateTimer, sessionCloseTimer, logger))
		{
			// TODO indent

		for(final P item : Iterables.once(
				iterateTypeTransactionally(
						type,
						pending.equal(true).and(config.narrowCondition),
						config.getSearchSize())))
		{
			if(probe!=EMPTY_PROBE && probeRequired)
			{
				if(ctx.supportsMessage())
					ctx.setMessage("probe");
				deferOrStopIfRequested(ctx);
				if(logger.isDebugEnabled())
					logger.debug("probing");
				final Timer.Sample start = Timer.start();
				probe.run();
				final long elapsed = probeTimer.stopMillies(start);
				probeRequired = false;
				logger.info("probed, took {}ms", elapsed);
			}

			session.prepare();

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

				final int limit = config.getFailureLimit();
				if(logger.isDebugEnabled())
					logger.debug("dispatching {}", itemID);
				final long start = Clock.currentTimeMillis();
				final Timer.Sample startSample = Timer.start();
				try
				{
					variant.dispatch(this, item, session.get());

					final long elapsed = succeedTimer.stopMillies(startSample);
					runType.newItem(
							parentClass, item, new Date(start), elapsed,
							0, limit,
							Result.success, null);
					tx.commit();

					unpend(item, true, new Date(start));
					logger.info("success for {}, took {}ms", itemID, elapsed);
				}
				catch(final DispatchDeferredException deferred)
				{
					tx.commit();
					if(logger.isDebugEnabled())
						logger.debug("is deferred: {}", itemID);
				}
				catch(final Exception failureCause)
				{
					final long elapsed = failTimer.stopMillies(startSample);
					probeRequired = true;
					tx.rollbackIfNotCommitted();

					final ByteArrayOutputStream failureCauseStackTrace = new ByteArrayOutputStream();
					try(PrintStream out = new PrintStream(failureCauseStackTrace, false, ENCODING.name()))
					{
						failureCause.printStackTrace(out);
					}
					catch(final UnsupportedEncodingException e)
					{
						throw new RuntimeException(ENCODING.name(), e);
					}

					tx.startTransaction(id + " register failure " + itemID);

					final boolean isFinal;
					final int remaining;
					{
						final Query<Run> query = runType.type.newQuery(runParent.equal(item));
						if(supportsPurge())
						{
							final Date unpendDate = unpend.of(Unpend.date).get(item);
							// effectively resets failureLimit on unpend
							if(unpendDate!=null)
								query.narrow(runType.date.greater(unpendDate));
						}
						final int total = query.total();
						isFinal = total >= limit - 1;
						remaining = isFinal ? 0 : (limit - 1 - total);
					}

					runType.newItem(
							parentClass, item, new Date(start), elapsed,
							remaining, limit,
							Result.failure(isFinal), failureCauseStackTrace.toByteArray());
					tx.commit();

					if(isFinal)
						unpend(item, false, new Date(start));

					if(isFinal)
					{
						if(logger.isErrorEnabled())
							//noinspection StringConcatenationArgumentToLogCall
							logger.error(
									"final failure for " + itemID + ", " +
									"took " + elapsed + "ms, " +
									limit + " runs exhausted",
									failureCause);
						variant.notifyFinalFailure(this, item, failureCause);
					}
					else
					{
						if(logger.isWarnEnabled())
							//noinspection StringConcatenationArgumentToLogCall
							logger.warn(
									"transient failure for " + itemID + ", " +
									"took " + elapsed + "ms, " +
									remaining + " of " + limit + " runs remaining",
									failureCause);
					}
					session.onFailure();
				}
			}
			ctx.incrementProgress();
		}
		}
	}

	private void unpend(final Item item, final boolean success, final Date date)
	{
		final ArrayList<SetValue<?>> sv = new ArrayList<>(3);
		sv.add(pending.map(false));
		if(supportsPurge())
			sv.add(unpend.map(new Unpend(success, date)));

		// A separate transaction for unpend helps to avoid TemporaryTransactionException
		// if there are two Dispatchers on the same type dispatching the same item at
		// the same time.
		try(TransactionTry tx = getType().getModel().startTransactionTry(getID() + " unpend " + item.getCopeID()))
		{
			item.set(sv.toArray(EMPTY_SET_VALUE_ARRAY));
			tx.commit();
		}
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
		final Run success = runType().getLastSuccess(item);
		return success!=null ? success.getDate() : null;
	}

	@Wrap(order=60, doc="Returns the milliseconds, this item needed to be last successfully dispatched by {0}.")
	@Nullable
	public Long getLastSuccessElapsed(@Nonnull final Item item)
	{
		final Run success = runType().getLastSuccess(item);
		return success!=null ? success.getElapsed() : null;
	}

	@Wrap(order=70, doc="Returns the attempts to dispatch this item by {0}.")
	@Nonnull
	public List<Run> getRuns(final Item item)
	{
		return runType().getRuns(item);
	}

	@Wrap(order=80, doc="Returns the failed attempts to dispatch this item by {0}.")
	@Nonnull
	public List<Run> getFailures(final Item item)
	{
		return runType().getFailures(item);
	}

	public static final class Config
	{
		static final int DEFAULT_FAILURE_LIMIT = 5;
		static final int DEFAULT_SEARCH_SIZE = 1000;
		static final int DEFAULT_SESSION_LIMIT = 100;
		static final Condition DEFAULT_NARROW_CONDITION = Condition.TRUE;

		private final int failureLimit;
		private final int searchSize;
		private final int sessionLimit;
		private final Condition narrowCondition;

		public Config()
		{
			this(DEFAULT_FAILURE_LIMIT, DEFAULT_SEARCH_SIZE);
		}

		public Config(final int failureLimit, final int searchSize)
		{
			this(failureLimit, searchSize, DEFAULT_SESSION_LIMIT, DEFAULT_NARROW_CONDITION);
		}

		Config(
				final int failureLimit,
				final int searchSize,
				final int sessionLimit,
				final Condition narrowCondition)
		{
			this.failureLimit = requireGreaterZero(failureLimit, "failureLimit");
			this.searchSize = requireGreaterZero(searchSize, "searchSize");
			this.sessionLimit = requireGreaterZero(sessionLimit, "sessionLimit");
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

		public Config sessionLimit(final int sessionLimit)
		{
			return new Config(failureLimit, searchSize, sessionLimit, narrowCondition);
		}

		public int getSessionLimit()
		{
			return sessionLimit;
		}

		public Config narrow(final Condition condition)
		{
			return new Config(failureLimit, searchSize, sessionLimit,
					narrowCondition.and(condition));
		}

		public Config resetNarrow()
		{
			return new Config(failureLimit, searchSize, sessionLimit, DEFAULT_NARROW_CONDITION);
		}

		public Condition getNarrowCondition()
		{
			return narrowCondition;
		}
	}


	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	private final class RunType
	{
		final ItemField<?> parent;
		final DateField date = new DateField().toFinal();
		final PartOf<?> runs;
		final LongField elapsed = new LongField().toFinal().min(0);
		final IntegerField remaining = supportRemaining ? new IntegerField().toFinal().min(0) : null;
		final IntegerField limit     = supportRemaining ? new IntegerField().toFinal().min(1) : null;
		final EnumField<Result> result = EnumField.create(Result.class).toFinal();
		final DataField failure = new DataField().toFinal().optional();
		final Type<Run> type;

		RunType(final Type<?> parentType)
		{
			parent = parentType.newItemField(CASCADE).toFinal();
			runs = PartOf.create(parent, date);
			final Features features = new Features();
			features.put("parent", parent);
			features.put("date", date);
			features.put("runs", runs);
			features.put("elapsed", elapsed);
			if(supportRemaining)
			{
				features.put("remaining", remaining);
				features.put("limit", limit);
			}
			features.put("result", result, CustomAnnotatedElement.create(CopeSchemaNameElement.get("success")));
			features.put("failure", failure);
			type = newSourceType(Run.class, features, "Run");
		}

		private <P extends Item> void newItem(
				@Nonnull final Class<P> parentClass,
				final P parent,
				final Date date,
				final long elapsed,
				final int remaining,
				final int limit,
				final Result result,
				final byte[] failure)
		{
			SetValue<?>[] setValues = {
					this.parent.as(parentClass).map(parent),
					this.date.map(date),
					this.elapsed.map(elapsed),
					this.result.map(result),
					this.failure.map(failure)};
			if(supportRemaining)
			{
				setValues = SetValueUtil.add(setValues, this.remaining.map(remaining));
				setValues = SetValueUtil.add(setValues, this.limit.map(limit));
			}
			type.newItem(setValues);
		}

		private Run getLastSuccess(final Item item)
		{
			final Query<Run> q =
					type.newQuery(Cope.and(
							Cope.equalAndCast(parent, item),
							result.equal(Result.success)));
			q.setOrderBy(type.getThis(), false);
			q.setPage(0, 1);
			return q.searchSingleton();
		}

		private List<Run> getRuns(final Item item)
		{
			return
					type.search(
							Cope.equalAndCast(parent, item),
							type.getThis(),
							true);
		}

		private List<Run> getFailures(final Item item)
		{
			return
					type.search(
							Cope.and(
									Cope.equalAndCast(parent, item),
									result.notEqual(Result.success)),
							type.getThis(),
							true);
		}
	}

	private RunType runType()
	{
		return requireMounted(runTypeIfMounted);
	}

	@Computed
	public static final class Run extends Item
	{
		public Dispatcher getPattern()
		{
			return (Dispatcher)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return type().parent.get(this);
		}

		public Date getDate()
		{
			return type().date.get(this);
		}

		public long getElapsed()
		{
			return type().elapsed.getMandatory(this);
		}

		public int getRemaining()
		{
			return requireRemaining(type().remaining).getMandatory(this);
		}

		public int getLimit()
		{
			return requireRemaining(type().limit).getMandatory(this);
		}

		private IntegerField requireRemaining(final IntegerField f)
		{
			if(f==null)
				throw new IllegalArgumentException(
						"remaining has been disabled for Dispatcher " + getPattern().getID() +
						" by method withoutRemaining()");
			return f;
		}

		public Result getResult()
		{
			return type().result.get(this);
		}

		public boolean isSuccess()
		{
			return getResult().isSuccess();
		}

		public String getFailure()
		{
			final byte[] bytes = type().failure.getArray(this);
			return
					bytes!=null
					? new String(bytes, ENCODING)
					: null;
		}

		private static final long serialVersionUID = 1l;

		private RunType type()
		{
			return getPattern().runType();
		}

		private Run(final ActivationParameters ap) { super(ap); }
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
		{
			final Timer.Sample start = Timer.start();
			Delete.delete(
					query,
					properties.deleteLimit,
					"Dispatcher#purge " + getID(),
					ctx);
			purgeTimer.stop(start);
		}
	}

	Query<? extends Item> purgeQuery(
			final DispatcherPurgeProperties properties,
			final Condition restriction)
	{
		final Duration success = properties.retainSuccess;
		final Duration failure = properties.retainFinalFailure;
		if(success.isZero() && failure.isZero())
			return null;

		final long now = Clock.currentTimeMillis();
		final Condition dateCondition;

		if(success.equals(failure))
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

	private Condition dateBefore(final long now, final Duration duration)
	{
		if(duration.isZero())
			return Condition.FALSE;

		//noinspection ConstantConditions OK: getUnpendDate cannot return null if supportsPurge return true
		return getUnpendDate().less(new Date(now - duration.toMillis()));
	}

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer sessionCreateTimer = timer("session", "A session for dispatching was created/closed.", "event", "create");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer sessionCloseTimer = sessionCreateTimer.newValue("close");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer succeedTimer = timer("dispatch", "An item was dispatched.", "result", "success");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer failTimer = succeedTimer.newValue("failure");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer probeTimer = timer("probe", "The dispatcher probe was run successfully.");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer purgeTimer = timer("purge", "Items were purged (Dispatcher#purge).");
}
