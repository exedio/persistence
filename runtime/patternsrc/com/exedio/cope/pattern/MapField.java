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
import static com.exedio.cope.SetValue.map;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class MapField<K,V> extends Pattern implements MapFieldInterface<K,V>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final FunctionField<K> key;
	private final FunctionField<V> value;
	private final CopyFields keyCopyWiths;
	private final CopyFields valueCopyWiths;
	private Mount mountIfMounted = null;

	private MapField(
			final FunctionField<K> key, final FunctionField<V> value,
			final CopyFields keyCopyWiths, final CopyFields valueCopyWiths)
	{
		this.key   = check(key,   "key"  );
		this.value = check(value, "value");
		this.keyCopyWiths = keyCopyWiths;
		this.valueCopyWiths = valueCopyWiths;
	}

	private static <K> FunctionField<K> check(final FunctionField<K> field, final String name)
	{
		requireNonNull(field, name);
		if(!field.isMandatory())
			throw new IllegalArgumentException(name + " must be mandatory");
		if(field.hasDefault())
			throw new IllegalArgumentException(name + " must not have any default");
		if(field.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException(name + " must not be unique");
		return field;
	}

	public static <K,V> MapField<K,V> create(final FunctionField<K> key, final FunctionField<V> value)
	{
		return new MapField<>(key, value, CopyFields.EMPTY, CopyFields.EMPTY);
	}

	public MapField<K,V> copyKeyWith(final FunctionField<?> copyWith)
	{
		return new MapField<>(key.copy(), value.copy(), keyCopyWiths.add(copyWith), valueCopyWiths.copy());
	}

	public MapField<K,V> copyValueWith(final FunctionField<?> copyWith)
	{
		return new MapField<>(key.copy(), value.copy(), keyCopyWiths.copy(), valueCopyWiths.add(copyWith));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint uniqueConstraint = UniqueConstraint.create(parent, key);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("key", key);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("value", value);
		CopyFields.onMountAll(features, parent, new FunctionField<?>[]{key, value}, new CopyFields[]{keyCopyWiths, valueCopyWiths});
		final Type<PatternItem> entryType = newSourceType(PatternItem.class, PatternItem::new, features);
		this.mountIfMounted = new Mount(parent, uniqueConstraint, entryType);
	}

	private record Mount(
			ItemField<?> parent,
			UniqueConstraint uniqueConstraint,
			Type<PatternItem> entryType)
	{
		Mount
		{
			assert parent!=null;
			assert uniqueConstraint!=null;
			assert entryType!=null;
		}
	}

	private Mount mount()
	{
		return requireMounted(mountIfMounted);
	}

	@Wrap(order=200, name="{1}Parent",
			doc="Returns the parent field of the type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getParent(@Nonnull final Class<P> parentClass)
	{
		requireParentClass(parentClass, "parentClass");
		return mount().parent.as(parentClass);
	}

	public ItemField<?> getParent()
	{
		return mount().parent;
	}

	@Override
	public Class<K> getKeyClass()
	{
		return key.getValueClass();
	}

	public FunctionField<K> getKey()
	{
		return key;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return mount().uniqueConstraint;
	}

	@Override
	public Class<V> getValueClass()
	{
		return value.getValueClass();
	}

	public FunctionField<V> getValue()
	{
		return value;
	}

	/**
	 * @deprecated Use {@link #getEntryType()} instead
	 */
	@Deprecated
	public Type<?> getRelationType()
	{
		return getEntryType();
	}

	public Type<?> getEntryType()
	{
		return mount().entryType;
	}

	@Override
	@Wrap(order=10, doc=Wrap.MAP_GET_DOC)
	@Nullable
	public V get(
			@Nonnull final Item item,
			@Nonnull @Parameter(Wrap.MAP_KEY) final K key)
	{
		requireNonNull(key, "key");

		final Item entry =
			mount().uniqueConstraint.search(item, key);

		if(entry!=null)
			return value.get(entry);
		else
			return null;
	}

	@Override
	@Wrap(order=20, doc=Wrap.MAP_SET_DOC)
	public void set(
			@Nonnull final Item item,
			@Nonnull @Parameter(Wrap.MAP_KEY) final K key,
			@Nullable final V value)
	{
		requireNonNull(key, "key");

		final Mount mount = mount();

		final Item entry =
			mount.uniqueConstraint.search(item, key);

		if(entry==null)
		{
			if(value!=null)
				mount.entryType.newItem(
						Cope.mapAndCast(mount.parent, item),
						map(this.key, key),
						map(this.value, value)
				);
		}
		else
		{
			if(value!=null)
				this.value.set(entry, value);
			else
				entry.deleteCopeItem();
		}
	}

	@Override
	@Wrap(order=110)
	@Nonnull
	public Map<K,V> getMap(final Item item)
	{
		final Mount mount = mount();
		final LinkedHashMap<K,V> result = new LinkedHashMap<>();
		final Query<PatternItem> query = mount.entryType.newQuery(Cope.equalAndCast(mount.parent, item));
		query.setOrderBy(key, true);
		for(final PatternItem entry : query.search())
			result.put(key.get(entry), value.get(entry));
		return Collections.unmodifiableMap(result);
	}

	@Override
	@Wrap(order=120)
	public void setMap(@Nonnull final Item item, @Nonnull final Map<? extends K,? extends V> map)
	{
		MandatoryViolationException.requireNonNull(map, this, item);

		for(final Map.Entry<? extends K,? extends V> e : map.entrySet())
		{
			key.check(e.getKey());
			value.check(e.getValue());
		}

		final Mount mount = mount();
		final HashMap<K,V> done = new HashMap<>();

		for(final PatternItem entry : mount.entryType.search(Cope.equalAndCast(mount.parent, item)))
		{
			final K key = this.key.get(entry);
			if(map.containsKey(key))
				value.set(entry, map.get(key));
			else
				entry.deleteCopeItem();

			done.put(key, null); // value not needed here
		}
		for(final Map.Entry<? extends K, ? extends V> entry : map.entrySet())
		{
			final K key = entry.getKey();
			if(!done.containsKey(key))
				mount.entryType.newItem(
						Cope.mapAndCast(mount.parent, item),
						map(this.key, key),
						map(this.value, entry.getValue()));
		}
	}

	public V getAndCast(final Item item, final Object key)
	{
		return get(item, this.key.getValueClass().cast(key));
	}

	public void setAndCast(final Item item, final Object key, final Object value)
	{
		set(item, this.key.getValueClass().cast(key), this.value.getValueClass().cast(value));
	}

	public Join join(final Query<?> q, final K key)
	{
		return q.joinOuterLeft(
				getEntryType(),
				mount().parent.equalTarget().
					and(this.key.equal(key)));
	}
}
