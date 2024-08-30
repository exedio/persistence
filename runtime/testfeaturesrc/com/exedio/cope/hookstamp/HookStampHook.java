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

package com.exedio.cope.hookstamp;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

final class HookStampHook implements ChangeHook
{
	private final History[] histories;
	final Supplier<String> author;

	HookStampHook(
			final Model model,
			final Supplier<String> author)
	{
		final ArrayList<History> histories = new ArrayList<>();
		for(final Type<?> type : model.getTypesSortedByHierarchy())
			for(final Field<?> field : type.getDeclaredFields())
				if(field.isAnnotationPresent(HookStampWatcher.class))
					histories.add(new History((StringField)field)); // ClassCastException intended

		this.histories = histories.toArray(new History[histories.size()]);

		this.author = requireNonNull(author);
	}

	@Override
	public void afterNew(final Item item)
	{
		for(final History history : histories)
			history.afterNew(item);
	}

	@Override
	public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
	{
		for(final History history : histories)
			history.beforeSet(item, setValues);

		return setValues;
	}

	private final class History
	{
		private final StringField store;
		private final Model model;
		private final HashSet<Type<?>> types;

		History(final StringField store)
		{
			this.store = store;
			this.model = store.getType().getModel();
			this.types = new HashSet<>();
			this.types.addAll(store.getType().getTypesOfInstances());
		}

		void afterNew(final Item item)
		{
			if(types.contains(item.getCopeType()))
				addHook(item, true);
		}

		void beforeSet(final Item item, final SetValue<?>[] setValues)
		{
			if(types.contains(item.getCopeType()) &&
				!isHistoryOnly(setValues))
				addHook(item, false);
		}

		private void addHook(final Item item, final boolean isNew)
		{
			model.addPreCommitHookIfAbsent(new Hook(store, author)).add(item, isNew);
		}

		private static boolean isHistoryOnly(final SetValue<?>[] setValues)
		{
			return
				setValues.length==1 &&
				((Feature)setValues[0].settable).isAnnotationPresent(HookStampWatcher.class);
		}
	}

	private static final class Hook implements Runnable
	{
		private final StringField store;
		private final Supplier<String> author;
		private final StringField[] watched;
		private HashMap<Item,LinkedHashMap<StringField,String>> items;

		Hook(
				final StringField store,
				final Supplier<String> author)
		{
			this.store = store;
			this.author = author;

			final ArrayList<StringField> watched = new ArrayList<>();

			for(final Field<?> field : store.getType().getFields())
				if(field.isAnnotationPresent(HookStampWatched.class))
					watched.add((StringField)field); // ClassCastException intended

			for(final Type<?> type : store.getType().getSubtypesTransitively())
			{
				if(type==store.getType())
					continue;

				for(final Field<?> field : type.getDeclaredFields())
					if(field.isAnnotationPresent(HookStampWatched.class))
						watched.add((StringField)field); // ClassCastException intended
			}

			this.watched = watched.toArray(new StringField[watched.size()]);
		}

		void add(final Item item, final boolean isNew)
		{
			if(items==null)
				items = new HashMap<>();

			if(items.containsKey(item))
				return;

			final LinkedHashMap<StringField,String> values;
			if(isNew)
			{
				values = null;
			}
			else
			{
				values = new LinkedHashMap<>();
				final Type<?> type = item.getCopeType();
				for(final StringField field : watched)
					if(field.getType().isAssignableFrom(type))
						values.put(field, field.get(item));
			}

			items.put(item, values);
		}

		@Override
		public void run()
		{
			for(final Map.Entry<Item,LinkedHashMap<StringField,String>> e : items.entrySet())
			{
				final Item item = e.getKey();
				if(!item.existsCopeItem())
					continue;

				final StringBuilder bf = new StringBuilder();
				final HashMap<StringField,String> values = e.getValue();

				if(values==null) // new item
				{
					final Type<?> type = item.getCopeType();
					for(final Field<?> field : watched)
						if(field.getType().isAssignableFrom(type))
						{
							if(!bf.isEmpty())
								bf.append(',');

							bf.append(name(field)).
								append('=').
								append(field.get(item));
						}
				}
				else
				{
					for(final Map.Entry<StringField,String> e2 : values.entrySet())
					{
						final StringField field = e2.getKey();
						final String newValue = field.get(item);
						if(Objects.equals(newValue, e2.getValue()))
							continue;

						if(!bf.isEmpty())
							bf.append(',');

						bf.append(name(field)).
							append('=').
							append(newValue);
					}
				}

				if(!bf.isEmpty())
					store.set(item, store.get(item) + '{' + (values==null?"NEW:":"") + author.get() + ':' + bf + '}');
			}
		}

		private static String name(final Field<?> f)
		{
			return f.getAnnotation(HookStampWatched.class).value();
		}

		@Override
		public boolean equals(final Object o)
		{
			return
					o instanceof Hook &&
					store == ((Hook)o).store;
		}

		@Override
		public int hashCode()
		{
			return Hook.class.hashCode() ^ store.hashCode();
		}
	}
}
