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
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Function;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class History extends Pattern
{
	ItemField<?> eventParent = null;
	PartOf<?> eventEvents = null;
	final DateField eventDate = new DateField().toFinal().defaultToNow();
	final StringField eventAuthor = new StringField().toFinal();
	final BooleanField eventNew = new BooleanField().toFinal();
	Type<Event> eventType = null;
	
	ItemField<Event> featureEvent = null;
	PartOf<?> featureFeatures = null;
	final StringField featureId = new StringField().toFinal();
	private UniqueConstraint featureUnique = null;
	final StringField featureName = new StringField().toFinal();
	final StringField featureOld = new StringField().toFinal().optional();
	final StringField featureNew = new StringField().toFinal().optional();
	Type<Feature> featureType = null;

	@Override
	protected void initialize()
	{
		final Type<?> type = getType();
		
		eventParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		eventEvents = PartOf.newPartOf(eventParent);
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", eventParent);
		features.put("events", eventEvents);
		features.put("date", eventDate);
		features.put("author", eventAuthor);
		features.put("new", eventNew);
		eventType = newSourceType(Event.class, features, "Event");
		
		features.clear();
		featureEvent = eventType.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		featureFeatures = PartOf.newPartOf(featureEvent);
		featureUnique = new UniqueConstraint(featureEvent, featureId);
		features.put("event", featureEvent);
		features.put("features", featureFeatures);
		features.put("id", featureId);
		features.put("uniqueConstraint", featureUnique);
		features.put("name", featureName);
		features.put("old", featureOld);
		features.put("new", featureNew);
		featureType = newSourceType(Feature.class, features, "Feature");
	}
	
	public <P extends Item> ItemField<P> getEventParent(final Class<P> parentClass)
	{
		assert eventParent!=null;
		return eventParent.as(parentClass);
	}
	
	public PartOf getEventEvents()
	{
		return eventEvents;
	}	
	
	public DateField getEventDate()
	{
		return eventDate;
	}
	
	/**
	 * @deprecated Use {@link #getEventAuthor()} instead
	 */
	@Deprecated
	public StringField getEventOrigin()
	{
		return getEventAuthor();
	}

	public StringField getEventAuthor()
	{
		return eventAuthor;
	}
	
	/**
	 * @deprecated Use {@link #getEventNew()} instead
	 */
	@Deprecated
	public BooleanField getEventCreation()
	{
		return getEventNew();
	}

	public BooleanField getEventNew()
	{
		return eventNew;
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
	
	public PartOf getFeatureFeatures()
	{
		return featureFeatures;
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
			new Wrapper("getEvents").
			addComment("Returns the events of the history {0}.").
			setReturn(Wrapper.genericExtends(List.class, Event.class)));
		
		result.add(
			new Wrapper("createEvent").
			addComment("Creates a new event for the history {0}.").
			setReturn(Event.class).
			addParameter(String.class, "author").
			addParameter(boolean.class, "isNew"));
			
		result.add(
			new Wrapper("getEventParent").
			addComment("Returns the parent field of the event type of {0}.").
			setReturn(Wrapper.generic(ItemField.class, Wrapper.ClassVariable.class)).
			setMethodWrapperPattern("{1}EventParent").
			setStatic());
					
		return Collections.unmodifiableList(result);
	}
	
	public List<Event> getEvents(final Item item)
	{
		final Query<Event> q = eventType.newQuery(Cope.equalAndCast(eventParent, item));
		q.setOrderBy(
				new Function[]{ eventDate, eventType.getThis() },
				new boolean []{ false,     false });
		return q.search();
	}
	
	public Event createEvent(final Item item, final String author, final boolean isNew)
	{
		return eventType.newItem(
				Cope.mapAndCast(eventParent, item),
				eventAuthor.map(author),
				eventNew.map(isNew)
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
		
		/**
		 * @deprecated Use {@link #getAuthor()} instead
		 */
		@Deprecated
		public String getCause()
		{
			return getAuthor();
		}

		public String getAuthor()
		{
			return getPattern().eventAuthor.get(this);
		}
		
		/**
		 * @deprecated Use {@link #isNew()} instead
		 */
		@Deprecated
		public boolean isCreation()
		{
			return isNew();
		}

		public boolean isNew()
		{
			return getPattern().eventNew.getMandatory(this);
		}
		
		public List<? extends Feature> getFeatures()
		{
			final History pattern = getPattern();
			return pattern.featureType.search(Cope.equalAndCast(pattern.featureEvent, this), pattern.featureType.getThis(), true);
		}
		
		private static final SetValue cut(final StringField f, final Object o)
		{
			final String result;
			
			if(o!=null)
			{
				final String s = o.toString();
				final int max = f.getMaximumLength();
				result = (max<s.length()) ? (s.substring(0, max-3) + "...") : s;
			}
			else
			{
				result = null;
			}
			
			return f.map(result);
		}
		
		public Feature createFeature(final com.exedio.cope.Feature f, final String name, final Object oldValue, final Object newValue)
		{
			final History pattern = getPattern();
			return pattern.featureType.newItem(
					Cope.mapAndCast(pattern.featureEvent, this),
					pattern.featureId.map(f.getID()),
					pattern.featureName.map(name),
					cut(pattern.featureOld, oldValue),
					cut(pattern.featureNew, newValue)
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
	
	public static final List<History> getHistories(final Type<?> type)
	{
		ArrayList<History> result = null;
		for(final com.exedio.cope.Feature f : type.getFeatures())
		{
			if(f instanceof History)
			{
				if(result==null)
					result = new ArrayList<History>();
				result.add((History)f);
			}
		}
		return
			result!=null
			? Collections.unmodifiableList(result)
			: Collections.<History>emptyList();
	}
}
