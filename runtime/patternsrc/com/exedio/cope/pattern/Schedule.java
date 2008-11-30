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

import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Schedule extends Pattern
{
	ItemField<?> runParent = null;
	PartOf<?> runRuns = null;
	final DateField runFrom = new DateField().toFinal();
	final DateField runUntil = new DateField().toFinal();
	Type<Run> runType = null;
	
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
		runType = newSourceType(Run.class, features, "Run");
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
			new Wrapper("run").
			setReturn(int.class).
			setStatic());
				
		return Collections.unmodifiableList(result);
	}
	
	public <P extends Item> int run(final Class<P> parentClass)
	{
		return run(parentClass, new Date());
	}
	
	<P extends Item> int run(final Class<P> parentClass, final Date now)
	{
		final Type<P> type = getType().castType(parentClass);
		final Type.This<P> typeThis = type.getThis();
		final Model model = type.getModel();
		final String featureID = getID();
		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(now);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		final Date until = cal.getTime();
		
		final List<P> toRun;
		try
		{
			model.startTransaction(featureID + " search");
			final Query<P> q = type.newQuery(runType.getThis().isNull());
			q.joinOuterLeft(runType, runParent.as(parentClass).equal(typeThis).and(runUntil.greaterOrEqual(until)));
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
			final String itemID = item.getCopeID();
			cal.setTime(until);
			cal.add(GregorianCalendar.DAY_OF_WEEK, -1);
			final Date from = cal.getTime();
			try
			{
				model.startTransaction(featureID + " schedule " + itemID);
				((Scheduleable)item).run(this, from, until);
				runType.newItem(
					Cope.mapAndCast(this.runParent, item),
					this.runFrom.map(from),
					this.runUntil.map(until));
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
	}
}
