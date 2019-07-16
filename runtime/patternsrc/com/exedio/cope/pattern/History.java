/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.BooleanField;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Function;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.reflect.FeatureField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

@WrapFeature
public final class History extends Pattern
{
	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private EventType eventTypeIfMounted = null;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private FeatureType featureTypeIfMounted = null;

	@Override
	protected void onMount()
	{
		super.onMount();
		eventTypeIfMounted = new EventType(getType());
		featureTypeIfMounted = new FeatureType(eventTypeIfMounted);
	}

	@Wrap(order=100, name="{1}EventParent", doc="Returns the parent field of the event type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getEventParent(@Nonnull final Class<P> parentClass)
	{
		requireParentClass(parentClass, "parentClass");
		return eventType().parent.as(parentClass);
	}

	public PartOf<?> getEventEvents()
	{
		return eventType().events;
	}

	public DateField getEventDate()
	{
		return eventType().date;
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
		return eventType().author;
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
		return eventType().New;
	}

	public Type<Event> getEventType()
	{
		return eventType().type;
	}

	public ItemField<Event> getFeatureEvent()
	{
		return featureType().event;
	}

	public PartOf<?> getFeatureFeatures()
	{
		return featureType().features;
	}

	public FeatureField<?> getFeature()
	{
		return featureType().id;
	}

	public StringField getFeatureId()
	{
		return featureType().id.getIdField();
	}

	public UniqueConstraint getFeatureUniqueConstraint()
	{
		return featureType().uniqueConstraint;
	}

	public StringField getFeatureName()
	{
		return featureType().name;
	}

	public StringField getFeatureOld()
	{
		return featureType().old;
	}

	public StringField getFeatureNew()
	{
		return featureType().New;
	}

	public Type<Feature> getFeatureType()
	{
		return featureType().type;
	}

	@Wrap(order=10, doc="Returns the events of the history {0}.")
	@Nonnull
	public List<Event> getEvents(final Item item)
	{
		final EventType type = eventType();
		final Query<Event> q = type.type.newQuery(Cope.equalAndCast(type.parent, item));
		q.setOrderBy(
				new Function<?>[]{ type.date, type.type.getThis() },
				new boolean    []{ false,     false });
		return q.search();
	}

	@Wrap(order=20, doc="Creates a new event for the history {0}.")
	@Nonnull
	public Event createEvent(
			@Nonnull final Item item,
			@Nonnull @Parameter("author") final String author,
			@Parameter("isNew") final boolean isNew)
	{
		final EventType type = eventType();
		return type.type.newItem(
				Cope.mapAndCast(type.parent, item),
				type.author.map(author),
				type.New.map(isNew)
			);
	}


	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	private final class EventType
	{
		final ItemField<?> parent;
		final DateField date = new DateField().toFinal().defaultToNow();
		final PartOf<?> events;
		final StringField author = new StringField().toFinal();
		final BooleanField New = new BooleanField().toFinal();
		final Type<Event> type;

		EventType(final Type<?> parentType)
		{
			parent = parentType.newItemField(CASCADE).toFinal();
			events = PartOf.create(parent, date);
			final Features features = new Features();
			features.put("parent", parent);
			features.put("date", date);
			features.put("events", events);
			features.put("author", author);
			features.put("new", New);
			type = newSourceType(Event.class, features, "Event");
		}
	}

	private EventType eventType()
	{
		return requireMounted(eventTypeIfMounted);
	}

	@Computed
	public static final class Event extends Item
	{
		private static final long serialVersionUID = 1l;

		Event(final ActivationParameters ap)
		{
			super(ap);
		}

		public History getPattern()
		{
			return (History)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return type().parent.get(this);
		}

		public Date getDate()
		{
			return type().date.get(this);
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
			return type().author.get(this);
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
			return type().New.getMandatory(this);
		}

		@SuppressWarnings("TypeParameterExtendsFinalClass") // OK: effectively makes collection somewhat compiler-unmodifiable
		public List<? extends Feature> getFeatures()
		{
			final FeatureType type = getPattern().featureType();
			return type.type.search(Cope.equalAndCast(type.event, this), type.type.getThis(), true);
		}

		private static SetValue<?> cut(final StringField f, final Object o)
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
			final FeatureType type = getPattern().featureType();
			return type.type.newItem(
					Cope.mapAndCast(type.event, this),
					type.id.map(f),
					type.name.map(name),
					cut(type.old, oldValue),
					cut(type.New, newValue)
				);
		}

		private EventType type()
		{
			return getPattern().eventType();
		}
	}


	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	private final class FeatureType
	{
		final ItemField<Event> event;
		final PartOf<Event> features;
		final FeatureField<com.exedio.cope.Feature> id = FeatureField.create().toFinal();
		final UniqueConstraint uniqueConstraint;
		final StringField name = new StringField().toFinal();
		final StringField old = new StringField().toFinal().optional();
		final StringField New = new StringField().toFinal().optional();
		final Type<Feature> type;

		FeatureType(final EventType parentType)
		{
			final Features features = new Features();
			event = parentType.type.newItemField(CASCADE).toFinal();
			this.features = PartOf.create(event);
			uniqueConstraint = UniqueConstraint.create(event, id.getIdField());
			features.put("event", event);
			features.put("features", this.features);
			features.put("id", id);
			features.put("uniqueConstraint", uniqueConstraint);
			features.put("name", name);
			features.put("old", old);
			features.put("new", New);
			type = newSourceType(Feature.class, features, "Feature");
		}
	}

	private FeatureType featureType()
	{
		return requireMounted(featureTypeIfMounted);
	}

	@Computed
	public static final class Feature extends Item
	{
		private static final long serialVersionUID = 1l;

		Feature(final ActivationParameters ap)
		{
			super(ap);
		}

		public History getPattern()
		{
			return (History)getCopeType().getPattern();
		}

		public Event getEvent()
		{
			return type().event.get(this);
		}

		public com.exedio.cope.Feature getFeature()
		{
			return type().id.get(this);
		}

		/**
		 * @deprecated Use {@link #getFeatureID()} instead
		 */
		@Deprecated
		public String getId()
		{
			return getFeatureID();
		}

		public String getFeatureID()
		{
			return type().id.getId(this);
		}

		public String getName()
		{
			return type().name.get(this);
		}

		public String getOld()
		{
			return type().old.get(this);
		}

		public String getNew()
		{
			return type().New.get(this);
		}

		private FeatureType type()
		{
			return getPattern().featureType();
		}
	}

	public static List<History> getHistories(final Type<?> type)
	{
		ArrayList<History> result = null;
		for(final com.exedio.cope.Feature f : type.getFeatures())
		{
			if(f instanceof History)
			{
				if(result==null)
					result = new ArrayList<>();
				result.add((History)f);
			}
		}
		return
			result!=null
			? Collections.unmodifiableList(result)
			: Collections.emptyList();
	}
}
