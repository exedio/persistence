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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.Interrupter;

public final class PasswordLimiter extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final Hash password;
	private final long period;
	private final int limit;

	ItemField<?> parent = null;
	PartOf<?> refusals = null;
	final DateField date = new DateField().toFinal();
	Type<Refusal> refusalType = null;

	public PasswordLimiter(
			final Hash password,
			final long period,
			final int limit)
	{
		this.password = password;
		this.period = period;
		this.limit = limit;

		if(password==null)
			throw new NullPointerException("password");
		if(period<=0)
			throw new IllegalArgumentException("period must be greater zero, but was " + period);
		if(limit<=0)
			throw new IllegalArgumentException("limit must be greater zero, but was " + limit);
	}

	public Hash getPassword()
	{
		return password;
	}

	public long getPeriod()
	{
		return period;
	}

	public int getLimit()
	{
		return limit;
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		refusals = PartOf.newPartOf(parent, date);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("date", date);
		features.put("refusals", refusals);
		refusalType = newSourceType(Refusal.class, features, "Refusal");
	}

	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		assert parent!=null;
		return parent.as(parentClass);
	}

	public PartOf getRefusals()
	{
		return refusals;
	}

	public DateField getDate()
	{
		return date;
	}

	public Type<Refusal> getRefusalType()
	{
		return refusalType;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("check").
			addParameter(String.class, "password").
			setReturn(boolean.class));
		result.add(
			new Wrapper("checkVerbosely").
			addParameter(String.class, "password").
			setReturn(boolean.class).
			addThrows(ExceededException.class));
		result.add(
			new Wrapper("purge").
			setStatic().
			addParameter(Interrupter.class, "interrupter").
			setReturn(int.class, "the number of refusals purged"));

		return Collections.unmodifiableList(result);
	}

	public boolean check(final Item item, final String password)
	{
		final Query<Refusal> query = getCheckQuery(item);
		if(query.total()>=limit)
		{
			// TODO
			// Prevent timing attacks by running some password check.
			// A helper method should be supplied by class Hash.
			// see http://en.wikipedia.org/wiki/Timing_attack
			return false;
		}
		return checkInternally(item, password);
	}

	public boolean checkVerbosely(final Item item, final String password) throws ExceededException
	{
		final Query<Refusal> query = getCheckQuery(item);
		if(query.total()>=limit)
		{
			query.setOrderBy(this.date, true);
			query.setLimit(0, 1);
			// TODO use one query to compute both
			throw new ExceededException(
					this,
					item,
					query.searchSingletonStrict().getDate().getTime() + period);
		}
		return checkInternally(item, password);
	}

	private Query<Refusal> getCheckQuery(final Item item)
	{
		return refusalType.newQuery(
				Cope.equalAndCast(this.parent, item).and(this.date.greater(new Date(System.currentTimeMillis()-period))));
	}

	private boolean checkInternally(final Item item, final String password)
	{
		final boolean result = this.password.check(item, password);

		if(!result)
			refusalType.newItem(
				Cope.mapAndCast(parent, item),
				this.date.map(new Date()));

		return result;
	}

	public static final class ExceededException extends Exception
	{
		private static final long serialVersionUID = 1l;

		private final PasswordLimiter limiter;
		private final Item item;
		private final long releaseDate;

		private ExceededException(
				final PasswordLimiter limiter,
				final Item item,
				final long releaseDate)
		{
			this.limiter = limiter;
			this.item = item;
			this.releaseDate = releaseDate;
		}

		public PasswordLimiter getLimiter()
		{
			return limiter;
		}

		public Item getItem()
		{
			return item;
		}

		public Date getReleaseDate()
		{
			return new Date(releaseDate);
		}

		@Override
		public String getMessage()
		{
			return
				"password limit exceeded on " + item.getCopeID() +
				" for "+ limiter.getID() +
				" until " + new Date(releaseDate);
		}
	}

	public int purge(final Class parentClass, final Interrupter interrupter)
	{
		assert parentClass!=null;

		final int LIMIT = 100;
		final Model model = getType().getModel();
		int result = 0;
		for(int transaction = 0; transaction<30; transaction++)
		{
			if(interrupter!=null && interrupter.isRequested())
				return result;

			try
			{
				model.startTransaction("PasswordLimiter#purge " + getID() + " #" + transaction);

				final Query<Refusal> query = refusalType.newQuery(
						this.date.less(new Date(System.currentTimeMillis()-period)));
				query.setLimit(0, LIMIT);
				final List<Refusal> refusals = query.search();
				final int refusalsSize = refusals.size();
				if(refusalsSize==0)
					return result;
				for(final Refusal refusal : refusals)
					refusal.deleteCopeItem();
				result += refusalsSize;
				if(refusalsSize<LIMIT)
				{
					model.commit();
					return result;
				}

				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}

		System.out.println("Aborting PasswordLimiter#purge " + getID() + " after " + result);
		return result;
	}

	@Computed
	public static final class Refusal extends Item
	{
		private static final long serialVersionUID = 1l;

		Refusal(final ActivationParameters ap)
		{
			super(ap);
		}

		public PasswordLimiter getPattern()
		{
			return (PasswordLimiter)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return getPattern().parent.get(this);
		}

		public Date getDate()
		{
			return getPattern().date.get(this);
		}
	}
}
