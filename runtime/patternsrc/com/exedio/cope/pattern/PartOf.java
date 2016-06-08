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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.This;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.util.Cast;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PartOf<C extends Item> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final ItemField<C> container;
	private final FunctionField<?> order;

	private PartOf(final ItemField<C> container, final FunctionField<?> order)
	{
		this.container = requireNonNull(container, "container");
		if(!container.isSourceAlready())
			addSource(container, "Container");
		this.order = order;
		if(order!=null && !order.isSourceAlready())
			addSource(order, "Order");
	}

	public static final <C extends Item> PartOf<C> create(final ItemField<C> container)
	{
		return new PartOf<>(container, null);
	}

	public static final <C extends Item> PartOf<C> create(final ItemField<C> container, final FunctionField<?> order)
	{
		return new PartOf<>(container, requireNonNull(order, "order"));
	}

	public ItemField<C> getContainer()
	{
		return container;
	}

	public FunctionField<?> getOrder()
	{
		return order;
	}

	@Wrap(order=10, doc="Returns the container this item is part of by {0}.", nullability=NullableIfContainerOptional.class)
	public C getContainer(@Nonnull final Item part)
	{
		return container.get(part);
	}

	@Wrap(order=20, doc="Returns the parts of the given container.")
	@Nonnull
	public <P extends Item> List<P> getParts(
			@Nonnull final Class<P> partClass,
			@Parameter(value="container", nullability=NullableIfContainerOptional.class) final C container)
	{
		return getParts(partClass, container, null);
	}

	@Wrap(order=30, doc="Returns the parts of the given container matching the given condition.")
	@Nonnull
	public <P extends Item> List<P> getParts(
			@Nonnull final Class<P> partClass,
			@Parameter(value="container", nullability=NullableIfContainerOptional.class) final C container,
			@Nullable @Parameter("condition") final Condition condition)
	{
		final Type<P> type = getType().as(partClass);
		final Condition parentCondition = this.container.equal(container);
		final Query<P> q = type.newQuery(condition!=null ? Cope.and(parentCondition, condition) : parentCondition);

		final This<?> typeThis = type.getThis(); // make search deterministic
		if(order!=null)
			q.setOrderBy(new Function<?>[]{order, typeThis}, new boolean[]{true, true});
		else
			q.setOrderBy(typeThis, true);

		return q.search();
	}

	public List<? extends Item> getParts(final Item container)
	{
		return getParts(getType().getJavaClass(), Cast.verboseCast(this.container.getValueClass(), container));
	}

	// static convenience methods ---------------------------------

	/**
	 * Returns all part-of declarations where <tt>type</tt> or any of it's super types is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static final List<PartOf<?>> getPartOfs(final Type<?> type)
	{
		return PartOfReverse.get(type);
	}

	/**
	 * Returns all part-of declarations where <tt>type</tt> is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static final List<PartOf<?>> getDeclaredPartOfs(final Type<?> type)
	{
		return PartOfReverse.getDeclared(type);
	}

	/**
	 * Returns all partofs of the <tt>pattern</tt>. Considers a one step recursion
	 * for {@link History}.
	 */
	public static final List<PartOf<?>> getPartOfs(final Pattern pattern)
	{
		final ArrayList<PartOf<?>> result = new ArrayList<>();
		for(final PartOf<?> partOf : PartOf.getPartOfs(pattern.getType()))
		{
			if (pattern.getSourceTypes().contains(partOf.getType()) ||
					( pattern.getType().getPattern()!=null &&
					  pattern.getType().getPattern().getSourceTypes().contains(partOf.getType()) )
				)
			{
				result.add(partOf);
			}
		}
		return result;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getParts(Item)} instead
	 */
	@Deprecated
	public List<? extends Item> getPartsAndCast(final Item container)
	{
		return getParts(container);
	}

	/**
	 * @deprecated Use {@link #create(ItemField)} instead
	 */
	@Deprecated
	public static final <C extends Item> PartOf<C> newPartOf(final ItemField<C> container)
	{
		return create(container);
	}

	/**
	 * @deprecated Use {@link #create(ItemField,FunctionField)} instead
	 */
	@Deprecated
	public static final <C extends Item> PartOf<C> newPartOf(final ItemField<C> container, final FunctionField<?> order)
	{
		return create(container, order);
	}

	static class NullableIfContainerOptional implements NullabilityGetter<PartOf<?>>
	{
		@Override
		public Nullability getNullability(final PartOf<?> feature)
		{
			return Nullability.forMandatory(feature.container.isMandatory());
		}
	}
}
