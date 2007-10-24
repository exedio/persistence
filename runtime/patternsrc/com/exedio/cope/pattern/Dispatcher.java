/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.Type;
import com.exedio.cope.Wrapper;

public final class Dispatcher extends Pattern
{
	private static final String ENCODING = "utf8";
	
	private final int failureLimit;
	private final int searchSize;
	private final BooleanField pending = new BooleanField().defaultTo(true);
	private final DateField successDate = new DateField().optional();
	private final LongField successElapsed = new LongField().optional();

	ItemField<?> failureParent = null;
	final DateField failureDate = new DateField().toFinal();
	final LongField failureElapsed = new LongField();
	final DataField failureCause = new DataField().toFinal();
	private Type<?> failureType = null;
	
	public Dispatcher()
	{
		this(5, 100);
	}
	
	public Dispatcher(final int maxFailures, final int searchSize)
	{
		this.failureLimit = maxFailures;
		this.searchSize = searchSize;
		if(maxFailures<1)
			throw new IllegalArgumentException("failureLimit must be greater zero, but was " + maxFailures + ".");
		if(searchSize<1)
			throw new IllegalArgumentException("searchSize must be greater zero, but was " + searchSize + ".");
		
		registerSource(pending);
		registerSource(successDate);
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		initialize(pending, name + "Pending");
		initialize(successDate, name + "SuccessDate");
		initialize(successElapsed, name + "SuccessElapsed");
		
		final Type<?> type = getType();

		failureParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", failureParent);
		features.put("date", failureDate);
		features.put("elapsed", failureElapsed);
		features.put("cause", failureCause);
		failureType = newType(features, "Failure");
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
	
	public Type<? extends Item> getFailureType()
	{
		assert failureType!=null;
		return failureType;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			void.class, "dispatch",
			"Dispatch by {0}.",
			"dispatch").
			setStatic());
			
		result.add(new Wrapper(
			boolean.class, "isPending",
			"Returns, whether this item is yet to be dispatched by {0}.",
			"getter"));
				
		result.add(new Wrapper(
			Date.class, "getSuccessDate",
			"Returns the date, this item was successfully dispatched by {0}.",
			"getter"));
			
		result.add(new Wrapper(
			Long.class, "getSuccessElapsed",
			"Returns the milliseconds, this item needed to be successfully dispatched by {0}.",
			"getter"));
				
		result.add(new Wrapper(
			Wrapper.makeType(List.class, Failure.class), "getFailures",
			"Returns the failed attempts to dispatch this item by {0}.",
			"getter"));
			
		result.add(new Wrapper(
			Wrapper.makeType(ItemField.class, Wrapper.ClassVariable.class), "getFailureParent",
			"Returns the parent field of the failure type of {0}.",
			"parent").
			setMethodWrapperPattern("{1}FailureParent").
			setStatic());
			
		return Collections.unmodifiableList(result);
	}
	
	public <P extends Item> void dispatch(final Class<P> parentClass)
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
					}
					catch(Exception cause)
					{
						final long elapsed = System.currentTimeMillis() - start;
						
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
						
						if(failureType.newQuery(failureParent.equal(item)).total()>=failureLimit)
							pending.set(item, false);
					}
					model.commit();
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
		final List<? extends Item> eventItems = failureType.search(Cope.equalAndCast(failureParent, item), failureType.getThis(), true);
		final ArrayList<Failure> result = new ArrayList<Failure>(eventItems.size());
		for(final Item eventItem : eventItems)
			result.add(new Failure(eventItem));
		return Collections.unmodifiableList(result);
	}

	public final class Failure
	{
		private final Item backingItem;
		
		Failure(final Item backingItem)
		{
			this.backingItem = backingItem;
			assert backingItem!=null;
		}
		
		public Dispatcher getPattern()
		{
			return Dispatcher.this;
		}
		
		public Item getParent()
		{
			return failureParent.get(backingItem);
		}
		
		public Date getDate()
		{
			return failureDate.get(backingItem);
		}
		
		public long getElapsed()
		{
			return failureElapsed.getMandatory(backingItem);
		}
		
		public String getCause()
		{
			try
			{
				return new String(failureCause.get(backingItem).asArray(), ENCODING);
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		public Item getItem()
		{
			return backingItem;
		}
		
		@Override
		public boolean equals(final Object other)
		{
			return other instanceof Failure && backingItem.equals(((Failure)other).backingItem);
		}
		
		@Override
		public int hashCode()
		{
			return backingItem.hashCode() ^ 348765283;
		}
		
		@Override
		public String toString()
		{
			return backingItem.toString();
		}
	}
}
