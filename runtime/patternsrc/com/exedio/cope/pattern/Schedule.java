/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Schedule extends Pattern
{
	public enum Interval
	{
		DAILY,
		WEEKLY,
		MONTHLY;
	}
	
	private final BooleanField enabled = new BooleanField().defaultTo(true);
	private final EnumField<Interval> interval = Item.newEnumField(Interval.class).defaultTo(Interval.DAILY);
	
	ItemField<?> runParent = null;
	PartOf<?> runRuns = null;
	final DateField runFrom = new DateField().toFinal();
	final DateField runUntil = new DateField().toFinal();
	final DateField runRun = new DateField().toFinal();
	final LongField runElapsed = new LongField().toFinal();
	Type<Run> runType = null;
	
	public Schedule()
	{
		addSource(enabled,  "Enabled");
		addSource(interval, "Interval");
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		if(!Scheduleable.class.isAssignableFrom(type.getJavaClass()))
			throw new ClassCastException(
					"type of " + getID() + " must implement " + Scheduleable.class +
					", but was " + type.getJavaClass().getName());
		
		runParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		runRuns = PartOf.newPartOf(runParent);
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", runParent);
		features.put("runs",  runRuns);
		features.put("from",  runFrom);
		features.put("until", runUntil);
		features.put("run",   runRun);
		features.put("elapsed", runElapsed);
		runType = newSourceType(Run.class, features, "Run");
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
		return runParent;
	}
	
	public PartOf<?> getRunRuns()
	{
		return runRuns;
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
		return runType;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("isEnabled").
			setReturn(boolean.class));
		result.add(
			new Wrapper("setEnabled").
			addParameter(boolean.class, "enabled"));
		result.add(
			new Wrapper("getInterval").
			setReturn(Interval.class));
		result.add(
			new Wrapper("setInterval").
			addParameter(Interval.class, "interval"));
		result.add(
			new Wrapper("run").
			setReturn(int.class).
			addParameter(Interrupter.class, "interrupter").
			setStatic());
				
		return Collections.unmodifiableList(result);
	}
	
	public boolean isEnabled(final Item item)
	{
		return this.enabled.getMandatory(item);
	}
	
	public void setEnabled(final Item item, final boolean enabled)
	{
		this.enabled.set(item, enabled);
	}
	
	public Interval getInterval(final Item item)
	{
		return this.interval.get(item);
	}
	
	public void setInterval(final Item item, final Interval interval)
	{
		this.interval.set(item, interval);
	}
	
	public <P extends Item> int run(final Class<P> parentClass, final Interrupter interrupter)
	{
		return run(parentClass, interrupter, new Date());
	}
	
	<P extends Item> int run(final Class<P> parentClass, final Interrupter interrupter, final Date now)
	{
		final Type<P> type = getType().as(parentClass);
		final Type.This<P> typeThis = type.getThis();
		final Model model = type.getModel();
		final String featureID = getID();
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		final Date untilDaily = cal.getTime();
		cal.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		final Date untilWeekly = cal.getTime();
		cal.setTime(untilDaily);
		cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
		final Date untilMonthly = cal.getTime();
		
		
		final List<P> toRun;
		try
		{
			model.startTransaction(featureID + " search");
			final Query<P> q = type.newQuery(runType.getThis().isNull());
			q.joinOuterLeft(runType,
					Cope.and(
						runParent.as(parentClass).equal(typeThis),
						enabled.equal(true),
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
			return 0;
		
		int result = 0;
		for(final P item : toRun)
		{
			if(interrupter!=null && interrupter.isRequested())
				return result;
			
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
					case DAILY:  cal.add(GregorianCalendar.DAY_OF_WEEK  , -1); break;
					case WEEKLY: cal.add(GregorianCalendar.WEEK_OF_MONTH, -1); break;
					case MONTHLY:cal.add(GregorianCalendar.MONTH,         -1); break;
					default: throw new RuntimeException(interval.name());
				}
				final Date from = cal.getTime();
				final long elapsedStart = System.currentTimeMillis();
				((Scheduleable)item).run(this, from, until, interrupter!=null ? interrupter : DEFAULT_INTERRUPTER);
				final long elapsed = System.currentTimeMillis() - elapsedStart;
				runType.newItem(
					Cope.mapAndCast(this.runParent, item),
					this.runFrom.map(from),
					this.runUntil.map(until),
					this.runRun.map(now),
					this.runElapsed.map(elapsed));
				model.commit();
				result++;
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		return result;
	}
	
	private static final Interrupter DEFAULT_INTERRUPTER = new Interrupter()
	{
		public boolean isRequested()
		{
			return false;
		}
	};
	
	public static final class Run extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Run(final SetValue[] setValues, final Type<? extends Item> type)
		{
			super(setValues, type);
			assert type!=null;
		}
		
		Run(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
		{
			super(reactivationDummy, pk, type);
		}
		
		public Schedule getPattern()
		{
			return (Schedule)getCopeType().getPattern();
		}
		
		public Item getParent()
		{
			return getPattern().runParent.get(this);
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
}
