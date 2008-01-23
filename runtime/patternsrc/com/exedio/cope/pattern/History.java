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
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.Wrapper;

/**
 * This pattern is still experimental, and its API may change any time.
 */
public final class History extends Pattern
{
	ItemField<?> eventParent = null;
	final DateField eventDate = new DateField().toFinal();
	private UniqueConstraint eventUnique = null;
	final StringField eventOrigin = new StringField().toFinal();
	final BooleanField eventCreation = new BooleanField().toFinal();
	Type<?> eventType = null;
	
	ItemField<?> featureEvent = null;
	final StringField featureId = new StringField().toFinal();
	private UniqueConstraint featureUnique = null;
	final StringField featureName = new StringField().toFinal();
	final StringField featureOld = new StringField().toFinal().optional();
	final StringField featureNew = new StringField().toFinal().optional();
	Type<?> featureType = null;

	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		eventParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		eventUnique = new UniqueConstraint(eventParent, eventDate);
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", eventParent);
		features.put("date", eventDate);
		features.put("uniqueConstraint", eventUnique);
		features.put("origin", eventOrigin);
		features.put("creation", eventCreation);
		eventType = newType(features, "Event");
		
		features.clear();
		featureEvent = eventType.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		featureUnique = new UniqueConstraint(featureEvent, featureId);
		features.put("event", featureEvent);
		features.put("id", featureId);
		features.put("uniqueConstraint", featureUnique);
		features.put("name", featureName);
		features.put("old", featureOld);
		features.put("new", featureNew);
		featureType = newType(features, "Feature");
	}
	
	public <P extends Item> ItemField<P> getEventParent(final Class<P> parentClass)
	{
		assert eventParent!=null;
		return eventParent.as(parentClass);
	}
	
	public DateField getEventDate()
	{
		return eventDate;
	}
	
	public UniqueConstraint getEventUniqueConstraint()
	{
		assert eventUnique!=null;
		return eventUnique;
	}
	
	public StringField getEventOrigin()
	{
		return eventOrigin;
	}
	
	public BooleanField getEventCreation()
	{
		return eventCreation;
	}
	
	public Type<? extends Item> getEventType()
	{
		assert eventType!=null;
		return eventType;
	}
	
	public ItemField<?> getFeatureEvent()
	{
		assert featureEvent!=null;
		return featureEvent;
	}
	
	public StringField getFeatureId()
	{
		return featureId;
	}
	
	public UniqueConstraint getFeatureUniqueConstraint()
	{
		assert featureUnique!=null;
		return featureUnique;
	}
	
	public StringField getFeatureName()
	{
		return featureName;
	}
	
	public StringField getFeatureOld()
	{
		return featureOld;
	}
	
	public StringField getFeatureNew()
	{
		return featureNew;
	}
	
	public Type<? extends Item> getFeatureType()
	{
		assert featureType!=null;
		return featureType;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			Wrapper.makeTypeExtends(List.class, Event.class), "getEvents",
			"Returns the events of the history {0}.",
			"getter"));
		
		result.add(new Wrapper(
			Event.class, "createEvent",
			"Creates a new event for the history {0}.",
			"setter"
			).
			addParameter(String.class, "cause").
			addParameter(boolean.class, "created"));
			
		result.add(new Wrapper(
			Wrapper.makeType(ItemField.class, Wrapper.ClassVariable.class), "getEventParent",
			"Returns the parent field of the event type of {0}.",
			"parent").
			setMethodWrapperPattern("{1}EventParent").
			setStatic());
					
		return Collections.unmodifiableList(result);
	}
	
	public List<? extends Event> getEvents(final Item item)
	{
		final List<? extends Item> eventItems = eventType.search(Cope.equalAndCast(eventParent, item), eventDate, false);
		final ArrayList<Event> result = new ArrayList<Event>(eventItems.size());
		for(final Item eventItem : eventItems)
			result.add(new Event(eventItem));
		return Collections.unmodifiableList(result);
	}
	
	public Event createEvent(final Item item, final String origin, final boolean creation)
	{
		return new Event(eventType.newItem(
				Cope.mapAndCast(eventParent, item),
				eventDate.map(new Date()),
				eventOrigin.map(origin),
				eventCreation.map(creation)
			));
	}

	public final class Event extends BackedItem
	{
		Event(final Item backingItem)
		{
			super(backingItem);
			assert backingItem.getCopeType()==eventType;
		}
		
		public History getPattern()
		{
			return History.this;
		}
		
		public Item getParent()
		{
			return eventParent.get(backingItem);
		}
		
		public Date getDate()
		{
			return eventDate.get(backingItem);
		}
		
		public String getCause()
		{
			return eventOrigin.get(backingItem);
		}
		
		public boolean isCreation()
		{
			return eventCreation.getMandatory(backingItem);
		}
		
		public List<? extends Feature> getFeatures()
		{
			final List<? extends Item> featureItems = featureType.search(Cope.equalAndCast(featureEvent, backingItem), featureType.getThis(), true);
			final ArrayList<Feature> result = new ArrayList<Feature>(featureItems.size());
			for(final Item featureItem : featureItems)
				result.add(new Feature(featureItem));
			return Collections.unmodifiableList(result);
		}
		
		public Feature createFeature(final com.exedio.cope.Feature f, final String name, final Object oldValue, final Object newValue)
		{
			return new Feature(featureType.newItem(
					Cope.mapAndCast(featureEvent, backingItem),
					featureId.map(f.getID()),
					featureName.map(name),
					featureOld.map(oldValue!=null ? oldValue.toString() : null),
					featureNew.map(newValue!=null ? newValue.toString() : null)
				));
		}
	}

	public final class Feature extends BackedItem
	{
		Feature(final Item backingItem)
		{
			super(backingItem);
			assert backingItem.getCopeType()==featureType;
		}
		
		public History getPattern()
		{
			return History.this;
		}
		
		public Event getEvent()
		{
			return new Event(featureEvent.get(backingItem));
		}
		
		public com.exedio.cope.Feature getFeature()
		{
			return featureId.getType().getModel().getFeature(featureId.get(backingItem));
		}
		
		public String getId()
		{
			return featureId.get(backingItem);
		}
		
		public String getName()
		{
			return featureName.get(backingItem);
		}
		
		public String getOld()
		{
			return featureOld.get(backingItem);
		}
		
		public String getNew()
		{
			return featureNew.get(backingItem);
		}
	}
}
