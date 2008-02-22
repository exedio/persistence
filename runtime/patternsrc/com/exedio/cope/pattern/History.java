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
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.Wrapper;
import com.exedio.cope.util.ReactivationConstructorDummy;

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
	Type<Event> eventType = null;
	
	ItemField<Event> featureEvent = null;
	final StringField featureId = new StringField().toFinal();
	private UniqueConstraint featureUnique = null;
	final StringField featureName = new StringField().toFinal();
	final StringField featureOld = new StringField().toFinal().optional();
	final StringField featureNew = new StringField().toFinal().optional();
	Type<Feature> featureType = null;

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
		eventType = newType(Event.class, features, "Event");
		
		features.clear();
		featureEvent = eventType.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		featureUnique = new UniqueConstraint(featureEvent, featureId);
		features.put("event", featureEvent);
		features.put("id", featureId);
		features.put("uniqueConstraint", featureUnique);
		features.put("name", featureName);
		features.put("old", featureOld);
		features.put("new", featureNew);
		featureType = newType(Feature.class, features, "Feature");
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
	
	public Type<Event> getEventType()
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
	
	public Type<Feature> getFeatureType()
	{
		assert featureType!=null;
		return featureType;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper(
				Wrapper.makeTypeExtends(List.class, Event.class),
				"getEvents",
				"Returns the events of the history {0}.",
				"getter"));
		
		result.add(
			new Wrapper(
				Event.class,
				"createEvent",
				"Creates a new event for the history {0}.",
				"setter").
			addParameter(String.class, "cause").
			addParameter(boolean.class, "created"));
			
		result.add(
			new Wrapper(
				Wrapper.makeType(ItemField.class, Wrapper.ClassVariable.class),
				"getEventParent",
				"Returns the parent field of the event type of {0}.",
				"parent").
			setMethodWrapperPattern("{1}EventParent").
			setStatic());
					
		return Collections.unmodifiableList(result);
	}
	
	public List<Event> getEvents(final Item item)
	{
		return eventType.search(Cope.equalAndCast(eventParent, item), eventDate, false);
	}
	
	public Event createEvent(final Item item, final String origin, final boolean creation)
	{
		return eventType.newItem(
				Cope.mapAndCast(eventParent, item),
				eventDate.map(new Date()),
				eventOrigin.map(origin),
				eventCreation.map(creation)
			);
	}

	public static final class Event extends Item
	{
		private static final long serialVersionUID = 1l;

		Event(final SetValue[] setValues, final Type<? extends Item> type)
		{
			super(setValues, type);
			assert type!=null;
		}

		Event(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
		{
			super(reactivationDummy, pk, type);
		}
		
		public History getPattern()
		{
			return (History)getCopeType().getPattern();
		}
		
		public Item getParent()
		{
			return getPattern().eventParent.get(this);
		}
		
		public Date getDate()
		{
			return getPattern().eventDate.get(this);
		}
		
		public String getCause()
		{
			return getPattern().eventOrigin.get(this);
		}
		
		public boolean isCreation()
		{
			return getPattern().eventCreation.getMandatory(this);
		}
		
		public List<? extends Feature> getFeatures()
		{
			final History pattern = getPattern();
			return pattern.featureType.search(Cope.equalAndCast(pattern.featureEvent, this), pattern.featureType.getThis(), true);
		}
		
		public Feature createFeature(final com.exedio.cope.Feature f, final String name, final Object oldValue, final Object newValue)
		{
			final History pattern = getPattern();
			return pattern.featureType.newItem(
					Cope.mapAndCast(pattern.featureEvent, this),
					pattern.featureId.map(f.getID()),
					pattern.featureName.map(name),
					pattern.featureOld.map(oldValue!=null ? oldValue.toString() : null),
					pattern.featureNew.map(newValue!=null ? newValue.toString() : null)
				);
		}
	}

	public static final class Feature extends Item
	{
		private static final long serialVersionUID = 1l;

		Feature(final SetValue[] setValues, final Type<? extends Item> type)
		{
			super(setValues, type);
			assert type!=null;
		}

		Feature(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
		{
			super(reactivationDummy, pk, type);
		}
		
		public History getPattern()
		{
			return (History)getCopeType().getPattern();
		}
		
		public Event getEvent()
		{
			return getPattern().featureEvent.get(this);
		}
		
		public com.exedio.cope.Feature getFeature()
		{
			final History pattern = getPattern();
			return pattern.featureId.getType().getModel().getFeature(pattern.featureId.get(this));
		}
		
		public String getId()
		{
			return getPattern().featureId.get(this);
		}
		
		public String getName()
		{
			return getPattern().featureName.get(this);
		}
		
		public String getOld()
		{
			return getPattern().featureOld.get(this);
		}
		
		public String getNew()
		{
			return getPattern().featureNew.get(this);
		}
	}
}
