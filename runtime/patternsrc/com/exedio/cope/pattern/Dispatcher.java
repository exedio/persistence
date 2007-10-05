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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.Wrapper;

public final class Dispatcher extends Pattern
{
	private final int searchSize;
	private final BooleanField done = new BooleanField().defaultTo(false);
	private final DateField doneDate = new DateField().optional();

	private ItemField<?> failureParent = null;
	private final DateField failureDate = new DateField().toFinal();
	private final StringField failureCause = new StringField().toFinal().lengthMax(3000);
	private Type<?> failureType = null;
	
	public Dispatcher()
	{
		this(100);
	}
	
	public Dispatcher(final int searchSize)
	{
		this.searchSize = searchSize;
		if(searchSize<1)
			throw new IllegalArgumentException("searchSize must be greater zero, but was " + searchSize + ".");
		
		registerSource(done);
		registerSource(doneDate);
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		initialize(done, name + "Done");
		initialize(doneDate, name + "DoneDate");
		
		final Type<?> type = getType();

		failureParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", failureParent);
		features.put("date", failureDate);
		features.put("cause", failureCause);
		failureType = newType(features, "Failure");
	}
	
	public int getSearchSize()
	{
		return searchSize;
	}
	
	public BooleanField getDone()
	{
		return done;
	}
	
	public DateField getDoneDate()
	{
		return doneDate;
	}
	
	public <P extends Item> ItemField<P> getFailureParent(final Class<P> parentClass)
	{
		assert failureParent!=null;
		return failureParent.cast(parentClass);
	}
	
	public DateField getFailureDate()
	{
		return failureDate;
	}
	
	public StringField getFailureCause()
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
			boolean.class, "isDone",
			"Returns, whether this item was already dispatched by {0}.",
			"getter"));
				
		result.add(new Wrapper(
			Date.class, "getDoneDate",
			"Returns the date, this item was dispatched by {0}.",
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
	
	<P extends Item> void dispatch(final Class<P> parentClass)
	{
		final Type<P> type = getType().castType(parentClass);
		final Model model = type.getModel();
		final String featureID = getID();
		
		final List<P> toDispatch;
		try
		{
			model.startTransaction(featureID + " search");
			final Query<P> q  = type.newQuery(done.equal(false));
			q.setOrderBy(type.getThis(), true);
			q.setLimit(0, searchSize);
			toDispatch = q.search();
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
		
		for(final P item : toDispatch)
		{
			final String itemID = item.getCopeID();
			try
			{
				model.startTransaction(featureID + " dispatch " + itemID);
				try
				{
					if(isDone(item))
					{
						System.out.println("Already dispatched " + itemID + " by " + featureID + ", probably due to concurrent dispatching.");
						continue;
					}
					
					((Dispatchable)item).dispatch();

					item.set(
						done.map(true),
						doneDate.map(new Date()));
				}
				catch(Exception cause)
				{
					final StringBuffer bf = new StringBuffer();
					final int stopLength = failureCause.getMaximumLength()-15;
					
					final String causeText = cause.getMessage();
					if(causeText!=null)
						bf.append(causeText.length()>stopLength ? (causeText.substring(0, stopLength) + "\n shorted !!!") : causeText);
					
					boolean shorted=false;
					for(final StackTraceElement element : cause.getStackTrace())
					{
						if(bf.length()+element.toString().length()<stopLength)
						{
							bf.append(element.toString());
						}
						else
						{
							bf.append("\n shorted !!!");
							shorted=true;
							break;
						}
					}
					
					if(shorted)
					{
						System.out.println("------------ Dispatcher " + featureID + " had to short exception -------------");
						cause.printStackTrace();
						System.out.println("/----------- Dispatcher " + featureID + " had to short exception -------------");
					}
					
					failureType.newItem(
						((ItemField<Item>)failureParent).map(item),
						failureDate.map(new Date()),
						failureCause.map(bf.toString()));
				}
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
	}
	
	public boolean isDone(final Item item)
	{
		return done.getMandatory(item);
	}
	
	public Date getDoneDate(final Item item)
	{
		return doneDate.get(item);
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
		
		private Failure(final Item backingItem)
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
		
		public String getCause()
		{
			return failureCause.get(backingItem);
		}
		
		public final Item getItem()
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
