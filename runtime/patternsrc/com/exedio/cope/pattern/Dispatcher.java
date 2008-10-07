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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
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

public final class Dispatcher extends Pattern
{
	private static final String ENCODING = "utf8";
	
	private final int failureLimit;
	private final int searchSize;
	private final BooleanField pending = new BooleanField().defaultTo(true);
	private final DateField successDate = new DateField().optional();
	private final LongField successElapsed = new LongField().optional();

	ItemField<?> failureParent = null;
	PartOf<?> failureFailures = null;
	final DateField failureDate = new DateField().toFinal();
	final LongField failureElapsed = new LongField();
	final DataField failureCause = new DataField().toFinal();
	Type<Failure> failureType = null;
	
	public Dispatcher()
	{
		this(5, 100);
	}
	
	public Dispatcher(final int failureLimit, final int searchSize)
	{
		this.failureLimit = failureLimit;
		this.searchSize = searchSize;
		if(failureLimit<1)
			throw new IllegalArgumentException("failureLimit must be greater zero, but was " + failureLimit + ".");
		if(searchSize<1)
			throw new IllegalArgumentException("searchSize must be greater zero, but was " + searchSize + ".");
		
		addSource(pending, "Pending");
		addSource(successDate, "SuccessDate");
		addSource(successElapsed, "SuccessElapsed");
	}
	
	@Override
	protected void initialize()
	{
		final Type<?> type = getType();
		if(!Dispatchable.class.isAssignableFrom(type.getJavaClass()))
			throw new ClassCastException(
					"type of " + getID() + " must implement " + Dispatchable.class +
					", but was " + type.getJavaClass().getName());

		failureParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		failureFailures = PartOf.newPartOf(failureParent);
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", failureParent);
		features.put("failures", failureFailures);
		features.put("date", failureDate);
		features.put("elapsed", failureElapsed);
		features.put("cause", failureCause);
		failureType = newSourceType(Failure.class, features, "Failure");
	}
	
	public int getFailureLimit()
	{
		return failureLimit;
	}
	
	public int getSearchSize()
	{
		return searchSize;
	}
	
	public BooleanField getPending()
	{
		return pending;
	}
	
	public DateField getSuccessDate()
	{
		return successDate;
	}
	
	public LongField getSuccessElapsed()
	{
		return successElapsed;
	}
	
	public <P extends Item> ItemField<P> getFailureParent(final Class<P> parentClass)
	{
		assert failureParent!=null;
		return failureParent.as(parentClass);
	}
	
	public PartOf getFailureFailures()
	{
		return failureFailures;
	}
	
	public DateField getFailureDate()
	{
		return failureDate;
	}
	
	public LongField getFailureElapsed()
	{
		return failureElapsed;
	}
	
	public DataField getFailureCause()
	{
		return failureCause;
	}
	
	public Type<Failure> getFailureType()
	{
		assert failureType!=null;
		return failureType;
	}
	
	public interface Interrupter
	{
		boolean isRequested();
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("dispatch").
			addComment("Dispatch by {0}.").
			addParameter(Interrupter.class, "interrupter").
			setStatic());
			
		result.add(
			new Wrapper("isPending").
			addComment("Returns, whether this item is yet to be dispatched by {0}.").
			setReturn(boolean.class));
				
		result.add(
			new Wrapper("getSuccessDate").
			addComment("Returns the date, this item was successfully dispatched by {0}.").
			setReturn(Date.class));
			
		result.add(
			new Wrapper("getSuccessElapsed").
			addComment("Returns the milliseconds, this item needed to be successfully dispatched by {0}.").
			setReturn(Long.class));
				
		result.add(
			new Wrapper("getFailures").
			addComment("Returns the failed attempts to dispatch this item by {0}.").
			setReturn(Wrapper.generic(List.class, Failure.class)));
			
		result.add(
			new Wrapper("getFailureParent").
			addComment("Returns the parent field of the failure type of {0}.").
			setReturn(Wrapper.generic(ItemField.class, Wrapper.ClassVariable.class)).
			setMethodWrapperPattern("{1}FailureParent").
			setStatic());
			
		return Collections.unmodifiableList(result);
	}
	
	public <P extends Item> void dispatch(final Class<P> parentClass, final Interrupter interrupter)
	{
		final Type<P> type = getType().castType(parentClass);
		final Type.This<P> typeThis = type.getThis();
		final Model model = type.getModel();
		final String featureID = getID();
		
		P lastDispatched = null;
		while(true)
		{
			final List<P> toDispatch;
			try
			{
				model.startTransaction(featureID + " search");
				final Query<P> q  = type.newQuery(pending.equal(true));
				if(lastDispatched!=null)
					q.narrow(typeThis.greater(lastDispatched));
				q.setOrderBy(typeThis, true);
				q.setLimit(0, searchSize);
				toDispatch = q.search();
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			
			if(toDispatch.isEmpty())
				break;
			
			for(final P item : toDispatch)
			{
				if(interrupter!=null && interrupter.isRequested())
					return;
				
				lastDispatched = item;
				final String itemID = item.getCopeID();
				try
				{
					model.startTransaction(featureID + " dispatch " + itemID);
					
					if(!isPending(item))
					{
						System.out.println("Already dispatched " + itemID + " by " + featureID + ", probably due to concurrent dispatching.");
						continue;
					}
					
					final long start = System.currentTimeMillis();
					try
					{
						((Dispatchable)item).dispatch();

						final long elapsed = System.currentTimeMillis() - start;
						item.set(
							pending.map(false),
							successDate.map(new Date(start)),
							successElapsed.map(elapsed));
						
						model.commit();
					}
					catch(Exception cause)
					{
						final long elapsed = System.currentTimeMillis() - start;
						model.rollbackIfNotCommitted();
						
						model.startTransaction(featureID + " register failure " + itemID);
						final ByteArrayOutputStream baos = new ByteArrayOutputStream();
						final PrintStream out;
						try
						{
							out = new PrintStream(baos, false, ENCODING);
						}
						catch(UnsupportedEncodingException e)
						{
							throw new RuntimeException(e);
						}
						cause.printStackTrace(out);
						out.close();
						
						final ItemField<P> failureParent = this.failureParent.as(parentClass);
						
						failureType.newItem(
							failureParent.map(item),
							failureDate.map(new Date(start)),
							failureElapsed.map(elapsed),
							failureCause.map(baos.toByteArray()));
						
						final boolean done = failureType.newQuery(failureParent.equal(item)).total()>=failureLimit;
						if(done)
							pending.set(item, false);
						
						model.commit();
						
						if(done)
							((Dispatchable)item).notifyFinalFailure(cause);
					}
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
		}
	}
	
	public boolean isPending(final Item item)
	{
		return pending.getMandatory(item);
	}
	
	public Date getSuccessDate(final Item item)
	{
		return successDate.get(item);
	}
	
	public Long getSuccessElapsed(final Item item)
	{
		return successElapsed.get(item);
	}
	
	public List<Failure> getFailures(final Item item)
	{
		return failureType.search(Cope.equalAndCast(failureParent, item), failureType.getThis(), true);
	}

	public static final class Failure extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Failure(final SetValue[] setValues, final Type<? extends Item> type)
		{
			super(setValues, type);
			assert type!=null;
		}

		Failure(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
		{
			super(reactivationDummy, pk, type);
		}
		
		public Dispatcher getPattern()
		{
			return (Dispatcher)getCopeType().getPattern();
		}
		
		public Item getParent()
		{
			return getPattern().failureParent.get(this);
		}
		
		public Date getDate()
		{
			return getPattern().failureDate.get(this);
		}
		
		public long getElapsed()
		{
			return getPattern().failureElapsed.getMandatory(this);
		}
		
		public String getCause()
		{
			try
			{
				return new String(getPattern().failureCause.get(this).asArray(), ENCODING);
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
