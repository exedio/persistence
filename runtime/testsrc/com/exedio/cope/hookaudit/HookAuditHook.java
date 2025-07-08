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

package com.exedio.cope.hookaudit;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.Field;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

final class HookAuditHook implements ChangeHook
{
	private final Model model;
	final Supplier<String> author;

	HookAuditHook(
			final Model model,
			final Supplier<String> author)
	{
		this.model = requireNonNull(model);
		this.author = requireNonNull(author);

		if(!model.getTypesSortedByHierarchy().contains(HookAudit.TYPE))
			throw new RuntimeException(HookAudit.TYPE + " missing in " + model);
	}

	@Override
	public void afterNew(final Item item)
	{
		addHook(item, Action.neW);
	}

	@Override
	public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
	{
		addHook(item, Action.set);
		return setValues;
	}

	@Override
	public void beforeDelete(final Item item)
	{
		addHook(item, Action.delete);
	}

	private void addHook(final Item item, final Action action)
	{
		if(HookAudit.TYPE!=item.getCopeType())
			model.addPreCommitHookIfAbsent(new Hook(author)).add(item, action);
	}

	private static final class Hook implements Runnable
	{
		private final Supplier<String> author;
		private HashMap<Item,LinkedHashMap<StringField,String>> items;

		Hook(final Supplier<String> author)
		{
			this.author = author;
		}

		void add(final Item item, final Action action)
		{
			if(items==null)
				items = new HashMap<>();

			if(items.containsKey(item))
				return;

			final LinkedHashMap<StringField,String> values;
			switch(action)
			{
				case neW ->
					values = null;

				case set,
						delete -> {
					values = new LinkedHashMap<>();
					final Type<?> type = item.getCopeType();
					for(final Field<?> field : type.getFields())
						if(field.getType().isAssignableFrom(type) &&
							field.isAnnotationPresent(HookAuditWatched.class))
						{
							final StringField sf = (StringField)field;
							values.put(sf, sf.get(item));
						}
				}
				default ->
					throw new RuntimeException(action.name());
			}

			items.put(item, values);
		}

		@Override
		public void run()
		{
			for(final Map.Entry<Item,LinkedHashMap<StringField,String>> e : items.entrySet())
			{
				final Item item = e.getKey();
				final boolean exists = item.existsCopeItem();
				final StringBuilder sb = new StringBuilder();
				final HashMap<StringField,String> values = e.getValue();
				if(!exists && values==null) // item created and deleted in the same transaction
					continue;

				if(values==null) // new item
				{
					final Type<?> type = item.getCopeType();
					for(final Field<?> field : type.getFields())
						if(field.getType().isAssignableFrom(type) &&
							field.isAnnotationPresent(HookAuditWatched.class))
						{
							if(!sb.isEmpty())
								sb.append(',');

							sb.append(name(field)).
								append('=').
								append(field.get(item));
						}
				}
				else
				{
					for(final Map.Entry<StringField,String> e2 : values.entrySet())
					{
						final StringField field = e2.getKey();
						final String newValue = exists ? field.get(item) : null;
						if(exists && Objects.equals(newValue, e2.getValue()))
							continue;

						if(!sb.isEmpty())
							sb.append(',');

						sb.append(name(field)).
							append('=').
							append(exists ? newValue : e2.getValue());
					}
				}

				if(!sb.isEmpty())
					new HookAudit(
							author.get() + ':' +
							item.getCopeID() + ':' +
							(values==null?"NEW:":(exists?"":"DEL:")) + sb);
			}
		}

		private static String name(final Field<?> f)
		{
			assertTrue(f.isAnnotationPresent(HookAuditWatched.class), f.getID());
			return f.getAnnotation(HookAuditWatched.class).value();
		}

		@Override
		public boolean equals(final Object o)
		{
			return o instanceof Hook;
		}

		@Override
		public int hashCode()
		{
			return Hook.class.hashCode();
		}
	}

	enum Action { neW, set, delete }
}
