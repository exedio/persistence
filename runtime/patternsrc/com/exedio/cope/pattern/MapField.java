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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class MapField<K,V> extends Pattern implements MapFieldInterface<K,V>
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<K> key;
	private final FunctionField<V> value;
	private final CopyFields keyCopyWiths;
	private final CopyFields valueCopyWiths;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
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
		final Type<PatternItem> relationType = newSourceType(PatternItem.class, features);
		this.mountIfMounted = new Mount(parent, uniqueConstraint, relationType);
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

	private Mount mount()
	{
		return requireMounted(mountIfMounted);
	}

	@Wrap(order=200, name="{1}Parent",
			doc="Returns the parent field of the type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getParent(@Nonnull final Class<P> parentClass)
	{
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

	public Type<?> getRelationType()
	{
		return mount().relationType;
	}

	private static final String KEY = "k";

	@Override
	@Wrap(order=10,
			doc="Returns the value mapped to <tt>" + KEY + "</tt> by the field map {0}.")
	@Nullable
	public V get(
			@Nonnull final Item item,
			@Nonnull @Parameter(KEY) final K key)
	{
		requireNonNull(key, "key");

		final Item relationItem =
			mount().uniqueConstraint.search(item, key);

		if(relationItem!=null)
			return value.get(relationItem);
		else
			return null;
	}

	@Override
	@Wrap(order=20,
			doc="Associates <tt>" + KEY + "</tt> to a new value in the field map {0}.")
	public void set(
			@Nonnull final Item item,
			@Nonnull @Parameter(KEY) final K key,
			@Nullable final V value)
	{
		requireNonNull(key, "key");

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

	@Override
	@Wrap(order=110)
	@Nonnull
	public Map<K,V> getMap(final Item item)
	{
		final Mount mount = mount();
		final HashMap<K,V> result = new HashMap<>();
		for(final PatternItem relationItem : mount.relationType.search(Cope.equalAndCast(mount.parent, item)))
			result.put(key.get(relationItem), value.get(relationItem));
		return Collections.unmodifiableMap(result);
	}

	@Override
	@Wrap(order=120)
	public void setMap(@Nonnull final Item item, @Nonnull final Map<? extends K,? extends V> map)
	{
		MandatoryViolationException.requireNonNull(map, this, item);

		final Mount mount = mount();
		final HashMap<K,V> done = new HashMap<>();

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
		return get(item, this.key.getValueClass().cast(key));
	}

	public void setAndCast(final Item item, final Object key, final Object value)
	{
		set(item, this.key.getValueClass().cast(key), this.value.getValueClass().cast(value));
	}

	public Join join(final Query<?> q, final K key)
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
	public static <K,V> MapField<K,V> newMap(final FunctionField<K> key, final FunctionField<V> value)
	{
		return create(key, value);
	}
}
