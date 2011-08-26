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
import static com.exedio.cope.misc.TypeIterator.iterateTransactionally;
import static java.lang.System.nanoTime;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.JobContext;

public final class Dispatcher extends Pattern
{
	private static final Logger logger = Logger.getLogger(Dispatcher.class.getName());

	private static final long serialVersionUID = 1l;

	private static final String ENCODING = "utf8";

	private final BooleanField pending;

	final DateField runDate = new DateField().toFinal();
	final LongField runElapsed = new LongField().toFinal().min(0);
	final BooleanField runSuccess = new BooleanField().toFinal();
	final DataField runFailure = new DataField().toFinal().optional();
	private Mount mount = null;

	public Dispatcher()
	{
		this(new BooleanField().defaultTo(true));
	}

	private Dispatcher(final BooleanField pending)
	{
		this.pending = pending;
		addSource(pending, "pending");
	}

	public Dispatcher defaultPendingTo(final boolean defaultConstant)
	{
		return new Dispatcher(pending.defaultTo(defaultConstant));
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

		final ItemField<?> runParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final PartOf<?> runRuns = PartOf.newPartOf(runParent, runDate);
		final Features features = new Features();
		features.put("parent", runParent);
		features.put("date", runDate);
		features.put("runs", runRuns);
		features.put("elapsed", runElapsed);
		features.put("success", runSuccess);
		features.put("failure", runFailure);
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

	public BooleanField getPending()
	{
		return pending;
	}

	@Wrap(order=1000, name="{1}RunParent", doc="Returns the parent field of the run type of {0}.")
	public <P extends Item> ItemField<P> getRunParent(final Class<P> parentClass)
	{
		return mount().runParent.as(parentClass);
	}

	public PartOf getRunRuns()
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

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(Dispatcher.class, this, super.getWrappers());
	}

	@Wrap(order=20, doc="Dispatch by {0}.")
	public <P extends Item> void dispatch(
			final Class<P> parentClass,
			@Parameter("config") final Config config,
			@Parameter("ctx") final JobContext ctx)
	{
		if(config==null)
			throw new NullPointerException("config");
		if(ctx==null)
			throw new NullPointerException("ctx");

		final Mount mount = mount();
		final Type<P> type = getType().as(parentClass);
		final Model model = type.getModel();
		final String id = getID();
		final ItemField<P> runParent = mount.runParent.as(parentClass);

		for(
				final Iterator<P> iterator = iterateTransactionally(
					type,
					pending.equal(true),
					config.getSearchSize());
				iterator.hasNext(); )
		{
			ctx.stopIfRequested();
			final P item = iterator.next();
			final Dispatchable itemCasted = (Dispatchable)item;
			final String itemID = item.getCopeID();
			try
			{
				model.startTransaction(id + " dispatch " + itemID);

				if(!isPending(item))
				{
					if(logger.isInfoEnabled())
						logger.info( MessageFormat.format(
								"Already dispatched {1} by {0}, probably due to concurrent dispatching.",
								id, itemID ) );
					continue;
				}

				final long start = System.currentTimeMillis();
				final long nanoStart = nanoTime();
				try
				{
					itemCasted.dispatch(this);

					final long elapsed = toMillies(nanoTime(), nanoStart);
					pending.set(item, false);
					mount.runType.newItem(
							runParent.map(item),
							runDate.map(new Date(start)),
							runElapsed.map(elapsed),
							runSuccess.map(true));

					model.commit();
					ctx.incrementProgress();
				}
				catch(final Exception cause)
				{
					final long elapsed = toMillies(nanoTime(), nanoStart);
					model.rollbackIfNotCommitted();

					model.startTransaction(id + " register failure " + itemID);
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					final PrintStream out;
					try
					{
						out = new PrintStream(baos, false, ENCODING);
					}
					catch(final UnsupportedEncodingException e)
					{
						throw new RuntimeException(ENCODING, e);
					}
					cause.printStackTrace(out);
					out.close();

					mount.runType.newItem(
						runParent.map(item),
						runDate.map(new Date(start)),
						runElapsed.map(elapsed),
						runSuccess.map(false),
						runFailure.map(baos.toByteArray()));

					final boolean finalFailure =
						mount.runType.newQuery(runParent.equal(item)).total()>=config.getFailureLimit();
					if(finalFailure)
						pending.set(item, false);

					model.commit();

					if(finalFailure)
						((Dispatchable)item).notifyFinalFailure(this, cause);
				}
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
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
			try
			{
				return new String(getPattern().runFailure.get(this).asArray(), ENCODING);
			}
			catch(final UnsupportedEncodingException e)
			{
				throw new RuntimeException(ENCODING, e);
			}
		}
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
	public <P extends Item> int dispatch(
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
