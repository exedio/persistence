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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.Delete;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;

public final class PasswordLimiter extends Pattern
{
	private static final long serialVersionUID = 1l;
	static final Clock clock = new Clock();

	private final HashInterface password;
	private final long period;
	private final int limit;

	final DateField date = new DateField().toFinal();
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;

	// for binary backwards compatibility
	public PasswordLimiter(
			final Hash password,
			final long period,
			final int limit)
	{
		this((HashInterface)password, period, limit);
	}

	public PasswordLimiter(
			final HashInterface password,
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

	public HashInterface getPassword()
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

		final ItemField<?> parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final PartOf<?>refusals = PartOf.create(parent, date);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("date", date);
		features.put("refusals", refusals);
		final Type<Refusal> refusalType = newSourceType(Refusal.class, features, "Refusal");
		this.mountIfMounted = new Mount(parent, refusals, refusalType);
	}

	private static final class Mount
	{
		final ItemField<?> parent;
		final PartOf<?> refusals;
		final Type<Refusal> refusalType;

		Mount(
				final ItemField<?> parent,
				final PartOf<?> refusals,
				final Type<Refusal> refusalType)
		{
			assert parent!=null;
			assert refusals!=null;
			assert refusalType!=null;

			this.parent = parent;
			this.refusals = refusals;
			this.refusalType = refusalType;
		}
	}

	Mount mount()
	{
		final Mount mount = this.mountIfMounted;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		return mount().parent.as(parentClass);
	}

	public PartOf<?> getRefusals()
	{
		return mount().refusals;
	}

	public DateField getDate()
	{
		return date;
	}

	public Type<Refusal> getRefusalType()
	{
		return mount().refusalType;
	}

	@Wrap(order=10)
	public boolean check(
			final Item item,
			@Parameter("password") final String password)
	{
		final long now = clock.currentTimeMillis();
		final Query<Refusal> query = getCheckQuery(item, now);
		if(query.total()>=limit)
		{
			// prevent Timing Attacks
			this.password.blind(password);
			return false;
		}
		return checkInternally(item, password, now);
	}

	@Wrap(order=20, thrown=@Wrap.Thrown(ExceededException.class))
	public boolean checkVerbosely(
			final Item item,
			@Parameter("password") final String password)
	throws ExceededException
	{
		final long now = clock.currentTimeMillis();
		final Query<Refusal> query = getCheckQuery(item, now);
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
		return checkInternally(item, password, now);
	}

	private Query<Refusal> getCheckQuery(final Item item, final long now)
	{
		final Mount mount = mount();
		return
			mount.refusalType.newQuery(Cope.and(
				Cope.equalAndCast(mount.parent, item),
				this.date.greater(getExpiryDate(now))));
	}

	private boolean checkInternally(final Item item, final String password, final long now)
	{
		final boolean result = this.password.check(item, password);

		if(!result)
		{
			final Mount mount = mount();
			mount.refusalType.newItem(
				Cope.mapAndCast(mount.parent, item),
				this.date.map(new Date(now)));
		}

		return result;
	}

	public static final class ExceededException extends Exception
	{
		private static final long serialVersionUID = 1l;

		private final PasswordLimiter limiter;
		private final Item item;
		private final long releaseDate;

		ExceededException(
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

	@Wrap(order=40)
	public void purge(
			@Parameter("ctx") final JobContext ctx)
	{
		final long now = clock.currentTimeMillis();
		Delete.delete(
				mount().refusalType.newQuery(
						this.date.less(getExpiryDate(now))),
				"PasswordLimiter#purge " + getID(),
				ctx);
	}

	private Date getExpiryDate(final long now)
	{
		return new Date(now-period);
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
			return getPattern().mount().parent.get(this);
		}

		public Date getDate()
		{
			return getPattern().date.get(this);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #purge(com.exedio.cope.util.Interrupter)} instead.
	 */
	@Deprecated
	public int purge(@SuppressWarnings("unused") final Class<?> parentClass, final com.exedio.cope.util.Interrupter interrupter)
	{
		return purge(interrupter);
	}

	/**
	 * @deprecated Use {@link #purge(JobContext)} instead.
	 */
	@Wrap(order=30, docReturn="the number of refusals purged")
	@Deprecated
	public int purge(
			@Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				purge(ctx);
			}}
		);
	}
}
