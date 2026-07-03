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

package com.exedio.cope;

import static com.exedio.cope.CompareCondition.assertComparable;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.util.concurrent.ConcurrentHashMap;

final class UniqueCache
{
	private final FunctionField<?> field;
	private final Model model;
	private final ConcurrentHashMap<Object, Item> map = new ConcurrentHashMap<>();
	private final Counter hits;
	private final Counter misses;

	UniqueCache(final UniqueConstraint constraint, final ModelMetrics metricsTemplate)
	{
		final ModelMetrics metrics = metricsTemplate.name(UniqueConstraint.class);
		assert constraint.getFields().size()==1;
		field = constraint.getFields().get(0);
		model = constraint.getType().getModel();
		hits   = counter(metrics, constraint, "hit");
		misses = counter(metrics, constraint, "miss");
	}

	// TODO use FeatureCounter instead
	private static Counter counter(
			final ModelMetrics metrics,
			final UniqueConstraint constraint,
			final String result)
	{
		return metrics.counter(
				"cache",
				"Cache hits/misses when searching by a unique constraint",
				Tags.of("feature", constraint.getID(), "result", result));
	}

	/**
	 * @return returning {@code null} means 'unknown'.
	 */
	Item search(final Object value)
	{
		assertComparableAndCast(field, value);
		final Row row = new Row(2); // ItemField has 2 columns if polymorphic TODO this temporary row is rather a workaround
		field.setAndCast(row, value);
		Item result = map.get(row.get(field.getColumn())); // fails if row.get(..) returns null
		if(result!=null && model.currentTransaction().isInvalidated(result))
			result = null;
		((result==null) ? misses : hits).increment();
		return result;
	}

	@SuppressWarnings("unchecked")
	private static <E> void assertComparableAndCast(final FunctionField<E> left, final Object right)
	{
		assertComparable(left, (E)right);
	}

	/**
	 * This method must be called by {@link ItemCache} only,
	 * and only when synchronized on its {@code map}.
	 */
	void putSynchronized(final Item item, final WrittenState state)
	{
		final Object key = state.store(field.getColumn()); // correctness of state.store depends on immutability of WrittenState
		if(key==null) // value of the field is null
			return;

		map.put(key, item); // fails if item==null
	}

	/**
	 * This method must be called by {@link ItemCache} only,
	 * and only when synchronized on its {@code map}.
	 */
	void removeSynchronized(
			final WrittenState state,
			final Item assertionItem)
	{
		final Object key = state.store(field.getColumn()); // correctness of state.store depends on immutability of WrittenState
		if(key==null) // value of the field is null
			return;

		final Item removed = map.remove(key);
		if(!assertionItem.equals(removed))
			throw new RuntimeException(field.toString() + '/' + assertionItem + '/' + removed);
	}

	/**
	 * This method must be called by {@link ItemCache} only,
	 * and only when synchronized on its {@code map}.
	 */
	void clearSynchronized()
	{
		map.clear();
	}
}
