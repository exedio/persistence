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
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class PartOf<C extends Item> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final ItemField<C> container;
	private final FunctionField<?> order;

	private PartOf(final ItemField<C> container, final FunctionField<?> order)
	{
		this.container = requireNonNull(container, "container");
		if(!container.isSourceAlready())
			addSourceFeature(container, "Container");
		this.order = order;
	}

	public static <C extends Item> PartOf<C> create(final ItemField<C> container)
	{
		return new PartOf<>(container, null);
	}

	public static <C extends Item> PartOf<C> create(final ItemField<C> container, final FunctionField<?> order)
	{
		return new PartOf<>(container, requireNonNull(order, "order"));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		if(order!=null)
			check(order, "order");
	}

	private void check(final FunctionField<?> field, final String name)
	{
		if(!field.getType().isAssignableFrom(getType()))
			throw new IllegalArgumentException(
					name + ' ' + field + " of PartOf " + this + " must be declared on the same type or super type");
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
		return getPartsQuery(partClass, container, condition).search();
	}

	@Nonnull
	public <P extends Item> Query<P> getPartsQuery(
			@Nonnull final Class<P> partClass,
			final C container,
			@Nullable final Condition condition)
	{
		final Type<P> type =
				requireParentClass(partClass, "partClass");
		final Condition parentCondition = this.container.equal(container);
		final Query<P> q = type.newQuery(condition!=null ? Cope.and(parentCondition, condition) : parentCondition);

		final This<?> typeThis = type.getThis(); // make search deterministic
		if(order!=null)
			q.setOrderBy(new Function<?>[]{order, typeThis}, new boolean[]{true, true});
		else
			q.setOrderBy(typeThis, true);

		return q;
	}

	public List<? extends Item> getParts(final Item container)
	{
		return getParts(getType().getJavaClass(), this.container.getValueClass().cast(container));
	}

	public Query<? extends Item> getPartsQuery(final Item container, @Nullable final Condition condition)
	{
		return getPartsQuery(getType().getJavaClass(), this.container.getValueClass().cast(container), condition);
	}

	// static convenience methods ---------------------------------

	/**
	 * Returns all part-of declarations where {@code type} or any of it's super types is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static List<PartOf<?>> getPartOfs(final Type<?> type)
	{
		return PartOfReverse.get(type);
	}

	/**
	 * Returns all part-of declarations where {@code type} is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static List<PartOf<?>> getDeclaredPartOfs(final Type<?> type)
	{
		return PartOfReverse.getDeclared(type);
	}

	/**
	 * Returns all partofs of the {@code pattern}. Considers a one step recursion
	 * for {@link History}.
	 */
	public static List<PartOf<?>> getPartOfs(final Pattern pattern)
	{
		final ArrayList<PartOf<?>> result = new ArrayList<>();
		for(final PartOf<?> partOf : getPartOfs(pattern.getType()))
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
	public static <C extends Item> PartOf<C> newPartOf(final ItemField<C> container)
	{
		return create(container);
	}

	/**
	 * @deprecated Use {@link #create(ItemField,FunctionField)} instead
	 */
	@Deprecated
	public static <C extends Item> PartOf<C> newPartOf(final ItemField<C> container, final FunctionField<?> order)
	{
		return create(container, order);
	}
}
