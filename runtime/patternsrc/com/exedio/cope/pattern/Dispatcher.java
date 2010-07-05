/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static java.lang.System.nanoTime;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.exedio.cope.This;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.Interrupter;

public final class Dispatcher extends Pattern
{
	private static final long serialVersionUID = 1l;

	private static final String ENCODING = "utf8";

	private final BooleanField pending;

	ItemField<?> runParent = null;
	private PartOf<?> runRuns = null;
	final DateField runDate = new DateField().toFinal();
	final LongField runElapsed = new LongField().toFinal();
	final BooleanField runSuccess = new BooleanField().toFinal();
	final DataField runFailure = new DataField().toFinal().optional();
	private Type<Run> runType = null;

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

		runParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		runRuns = PartOf.newPartOf(runParent, runDate);
		final Features features = new Features();
		features.put("parent", runParent);
		features.put("date", runDate);
		features.put("runs", runRuns);
		features.put("elapsed", runElapsed);
		features.put("success", runSuccess);
		features.put("failure", runFailure);
		runType = newSourceType(Run.class, features, "Run");
	}

	public BooleanField getPending()
	{
		return pending;
	}

	public <P extends Item> ItemField<P> getRunParent(final Class<P> parentClass)
	{
		assert runParent!=null;
		return runParent.as(parentClass);
	}

	public PartOf getRunRuns()
	{
		return runRuns;
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
		assert runType!=null;
		return runType;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("dispatch").
			addComment("Dispatch by {0}.").
			setReturn(int.class, "the number of successfully dispatched items").
			addParameter(Config.class, "config").
			addParameter(Interrupter.class, "interrupter").
			setStatic());

		result.add(
			new Wrapper("isPending").
			addComment("Returns, whether this item is yet to be dispatched by {0}.").
			setReturn(boolean.class));

		result.add(
			new Wrapper("setPending").
			addComment("Sets whether this item is yet to be dispatched by {0}.").
			addParameter(boolean.class, "pending"));

		result.add(
			new Wrapper("getLastSuccessDate").
			addComment("Returns the date, this item was last successfully dispatched by {0}.").
			setReturn(Date.class));

		result.add(
			new Wrapper("getLastSuccessElapsed").
			addComment("Returns the milliseconds, this item needed to be last successfully dispatched by {0}.").
			setReturn(Long.class));

		result.add(
			new Wrapper("getRuns").
			addComment("Returns the attempts to dispatch this item by {0}.").
			setReturn(Wrapper.generic(List.class, Run.class)));

		result.add(
			new Wrapper("getFailures").
			addComment("Returns the failed attempts to dispatch this item by {0}.").
			setReturn(Wrapper.generic(List.class, Run.class)));

		result.add(
			new Wrapper("getRunParent").
			addComment("Returns the parent field of the run type of {0}.").
			setReturn(Wrapper.generic(ItemField.class, Wrapper.ClassVariable.class)).
			setMethodWrapperPattern("{1}RunParent").
			setStatic());

		return Collections.unmodifiableList(result);
	}

	/**
	 * @return the number of successfully dispatched items
	 */
	public <P extends Item> int dispatch(final Class<P> parentClass, final Config config, final Interrupter interrupter)
	{
		if(config==null)
			throw new NullPointerException("config");

		final Type<P> type = getType().as(parentClass);
		final This<P> typeThis = type.getThis();
		final Model model = type.getModel();
		final String id = getID();

		P lastDispatched = null;
		int result = 0;
		while(true)
		{
			final List<P> toDispatch;
			try
			{
				model.startTransaction(id + " search");
				final Query<P> q  = type.newQuery(pending.equal(true));
				if(lastDispatched!=null)
					q.narrow(typeThis.greater(lastDispatched));
				q.setOrderBy(typeThis, true);
				q.setLimit(0, config.getSearchSize());
				toDispatch = q.search();
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}

			if(toDispatch.isEmpty())
				break;

			final ItemField<P> runParent = this.runParent.as(parentClass);

			for(final P item : toDispatch)
			{
				if(interrupter!=null && interrupter.isRequested())
					return result;

				lastDispatched = item;
				final Dispatchable itemCasted = (Dispatchable)item;
				final String itemID = item.getCopeID();
				try
				{
					model.startTransaction(id + " dispatch " + itemID);

					if(!isPending(item))
					{
						System.out.println("Already dispatched " + itemID + " by " + id + ", probably due to concurrent dispatching.");
						continue;
					}

					final long start = System.currentTimeMillis();
					final long nanoStart = nanoTime();
					try
					{
						itemCasted.dispatch(this);

						final long elapsed = (nanoTime() - nanoStart) / 1000000;
						pending.set(item, false);
						runType.newItem(
								runParent.map(item),
								runDate.map(new Date(start)),
								runElapsed.map(elapsed),
								runSuccess.map(true));

						model.commit();
						result++;
					}
					catch(Exception cause)
					{
						final long elapsed = (nanoTime() - nanoStart) / 1000000;
						model.rollbackIfNotCommitted();

						model.startTransaction(id + " register failure " + itemID);
						final ByteArrayOutputStream baos = new ByteArrayOutputStream();
						final PrintStream out;
						try
						{
							out = new PrintStream(baos, false, ENCODING);
						}
						catch(UnsupportedEncodingException e)
						{
							throw new RuntimeException(ENCODING, e);
						}
						cause.printStackTrace(out);
						out.close();

						runType.newItem(
							runParent.map(item),
							runDate.map(new Date(start)),
							runElapsed.map(elapsed),
							runSuccess.map(false),
							runFailure.map(baos.toByteArray()));

						final boolean finalFailure = runType.newQuery(runParent.equal(item)).total()>=config.getFailureLimit();
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

		return result;
	}

	public boolean isPending(final Item item)
	{
		return pending.getMandatory(item);
	}

	public void setPending(final Item item, final boolean pending)
	{
		this.pending.set(item, pending);
	}

	public Date getLastSuccessDate(final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? runDate.get(success) : null;
	}

	public Long getLastSuccessElapsed(final Item item)
	{
		final Run success = getLastSuccess(item);
		return success!=null ? runElapsed.get(success) : null;
	}

	private Run getLastSuccess(final Item item)
	{
		final Query<Run> q = runType.newQuery(Cope.equalAndCast(runParent, item).and(runSuccess.equal(true)));
		q.setOrderBy(runType.getThis(), false);
		q.setLimit(0, 1);
		return q.searchSingleton();
	}

	public List<Run> getRuns(final Item item)
	{
		return runType.search(Cope.equalAndCast(runParent, item), runType.getThis(), true);
	}

	public List<Run> getFailures(final Item item)
	{
		return runType.search(Cope.equalAndCast(runParent, item).and(runSuccess.equal(false)), runType.getThis(), true);
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
			return getPattern().runParent.get(this);
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
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(ENCODING, e);
			}
		}
	}
}
