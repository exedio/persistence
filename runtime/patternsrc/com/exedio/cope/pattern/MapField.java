/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Cast.verboseCast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Join;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;

public final class MapField<K,V> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<K> key;
	private final FunctionField<V> value;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace 
	private Mount mount = null;

	private MapField(final FunctionField<K> key, final FunctionField<V> value)
	{
		this.key = key;
		this.value = value;
		if(key==null)
			throw new NullPointerException("key");
		if(key.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("key must not be unique");
		if(value==null)
			throw new NullPointerException("value");
		if(value.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("value must not be unique");
	}

	public static final <K,V> MapField<K,V> create(final FunctionField<K> key, final FunctionField<V> value)
	{
		return new MapField<K,V>(key, value);
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final UniqueConstraint uniqueConstraint = new UniqueConstraint(parent, key);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("key", key);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("value", value);
		final Type<PatternItem> relationType = newSourceType(PatternItem.class, features);
		this.mount = new Mount(parent, uniqueConstraint, relationType);
	}

	private static final class Mount
	{
		final ItemField<?> parent;
		final UniqueConstraint uniqueConstraint;
		final Type<PatternItem> relationType;

		Mount(
				final ItemField<?> parent,
				final UniqueConstraint uniqueConstraint,
				final Type<PatternItem> relationType)
		{
			assert parent!=null;
			assert uniqueConstraint!=null;
			assert relationType!=null;

			this.parent = parent;
			this.uniqueConstraint = uniqueConstraint;
			this.relationType = relationType;
		}
	}

	private final Mount mount()
	{
		final Mount mount = this.mount;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	@Wrap(order=200, name="{1}Parent",
			doc="Returns the parent field of the type of {0}.")
	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		return mount().parent.as(parentClass);
	}

	public ItemField<?> getParent()
	{
		return mount().parent;
	}

	public FunctionField<K> getKey()
	{
		return key;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return mount().uniqueConstraint;
	}

	public FunctionField<V> getValue()
	{
		return value;
	}

	public Type<? extends Item> getRelationType()
	{
		return mount().relationType;
	}

	private static final String KEY = "k";

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(MapField.class, this, super.getWrappers());
	}

	@Wrap(order=10,
			doc="Returns the value mapped to <tt>" + KEY + "</tt> by the field map {0}.")
	public V get(
			final Item item,
			@Parameter(KEY) final K key)
	{
		final Item relationItem =
			mount().uniqueConstraint.search(item, key);

		if(relationItem!=null)
			return value.get(relationItem);
		else
			return null;
	}

	@Wrap(order=20,
			doc="Associates <tt>" + KEY + "</tt> to a new value in the field map {0}.")
	public void set(
			final Item item,
			@Parameter(KEY) final K key,
			final V value)
	{
		final Mount mount = mount();

		final Item relationItem =
			mount.uniqueConstraint.search(item, key);

		if(relationItem==null)
		{
			if(value!=null)
				mount.relationType.newItem(
						Cope.mapAndCast(mount.parent, item),
						this.key.map(key),
						this.value.map(value)
				);
		}
		else
		{
			if(value!=null)
				this.value.set(relationItem, value);
			else
				relationItem.deleteCopeItem();
		}
	}

	@Wrap(order=110)
	public Map<K,V> getMap(final Item item)
	{
		final Mount mount = mount();
		final HashMap<K,V> result = new HashMap<K,V>();
		for(final PatternItem relationItem : mount.relationType.search(Cope.equalAndCast(mount.parent, item)))
			result.put(key.get(relationItem), value.get(relationItem));
		return result;
	}

	@Wrap(order=120)
	public void setMap(final Item item, final Map<? extends K,? extends V> map)
	{
		final Mount mount = mount();
		final HashMap<K,V> done = new HashMap<K,V>();

		for(final PatternItem relationItem : mount.relationType.search(Cope.equalAndCast(mount.parent, item)))
		{
			final K key = this.key.get(relationItem);
			if(map.containsKey(key))
				value.set(relationItem, map.get(key));
			else
				relationItem.deleteCopeItem();

			done.put(key, null); // value not needed here
		}
		for(final Map.Entry<? extends K, ? extends V> entry : map.entrySet())
		{
			final K key = entry.getKey();
			if(!done.containsKey(key))
				mount.relationType.newItem(
						Cope.mapAndCast(mount.parent, item),
						this.key.map(key),
						this.value.map(entry.getValue()));
		}
	}

	public V getAndCast(final Item item, final Object key)
	{
		return get(item, verboseCast(this.key.getValueClass(), key));
	}

	public void setAndCast(final Item item, final Object key, final Object value)
	{
		set(item, verboseCast(this.key.getValueClass(), key), verboseCast(this.value.getValueClass(), value));
	}

	public Join join(final Query q, final K key)
	{
		return q.joinOuterLeft(
				getRelationType(),
				mount().parent.equalTarget().
					and(this.key.equal(key)));
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField,FunctionField)} instead
	 */
	@Deprecated
	public static final <K,V> MapField<K,V> newMap(final FunctionField<K> key, final FunctionField<V> value)
	{
		return create(key, value);
	}
}
