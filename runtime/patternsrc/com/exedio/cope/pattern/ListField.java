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
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.Features;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ListField<E> extends AbstractListField<E> implements Copyable
{
	private static final long serialVersionUID = 1l;

	private final IntegerField order;
	private final FunctionField<E> element;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;

	private ListField(final FunctionField<E> element)
	{
		this.order = new IntegerField().toFinal().min(0);
		this.element = requireNonNull(element, "element");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
	}

	public static final <E> ListField<E> create(final FunctionField<E> element)
	{
		return new ListField<>(element);
	}

	@Override
	public ListField<E> copy(final CopyMapper mapper)
	{
		return new ListField<>(mapper.copy(element));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint uniqueConstraint = new UniqueConstraint(parent, order);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("order", order);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("element", element);
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

	private final Mount mount()
	{
		final Mount mount = this.mountIfMounted;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	@Wrap(order=1000, name="{1}Parent",
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

	public IntegerField getOrder()
	{
		return order;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return mount().uniqueConstraint;
	}

	@Override
	public FunctionField<E> getElement()
	{
		return element;
	}

	public Type<?> getRelationType()
	{
		return mount().relationType;
	}

	/**
	 * @see #getQuery(Item)
	 */
	@Wrap(order=10, doc="Returns the value of {0}.")
	@Override
	@Nonnull
	public List<E> get(final Item item)
	{
		return getQuery(item).search();
	}

	/**
	 * Returns the query that is used to implement {@link #get(Item)}.
	 */
	@Wrap(order=20, doc="Returns a query for the value of {0}.")
	@Nonnull
	public Query<E> getQuery(final Item item)
	{
		final Query<E> q =
			new Query<>(element, Cope.equalAndCast(mount().parent, item));
		q.setOrderBy(order, true);
		return q;
	}

	/**
	 * Returns the items, for which this field list contains the given element.
	 * The result does not contain any duplicates,
	 * even if the element is contained in this field list for an item more than once.
	 * The order of the result is unspecified.
	 */
	@Wrap(order=30, name="getDistinctParentsOf{0}",
			doc="Returns the items, for which field list {0} contains the given element.")
	@Nonnull
	public <P extends Item> List<P> getDistinctParents(
			@Nonnull final Class<P> parentClass,
			@Parameter("element") final E element)
	{
		final Query<P> q = new Query<>(
				mount().parent.as(parentClass),
				Cope.equalAndCast(this.element, element));
		q.setDistinct(true);
		return q.search();
	}

	@Wrap(order=40, name="addTo{0}",
			doc="Adds a new value for {0}.",
			thrownGetter=Thrown.class)
	public void add(@Nonnull final Item item, @Parameter(nullability=NullableIfElementOptional.class) final E value)
	{
		final Mount mount = mount();
		final Query<Integer> q = new Query<>(
				this.order.max(),
				Cope.equalAndCast(mount.parent, item));
		final Integer max = q.searchSingleton();
		final int newOrder = max!=null ? (max.intValue()+1) : 0;
		mount.relationType.newItem(
				Cope.mapAndCast(mount.parent, item),
				this.order.map(newOrder),
				this.element.map(value));
	}

	@Wrap(order=50,
			doc="Sets a new value for {0}.",
			thrownGetter=Thrown.class)
	@Override
	public void set(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, item);

		final Mount mount = mount();
		final Iterator<PatternItem> actual =
			mount.relationType.search(
					Cope.equalAndCast(mount.parent, item),
					this.order,
					true).
			iterator();
		final Iterator<? extends E> expected = value.iterator();

		for(int order = 0; ; order++)
		{
			if(!actual.hasNext())
			{
				while(expected.hasNext())
				{
					mount.relationType.newItem(
							Cope.mapAndCast(mount.parent, item),
							this.element.map(expected.next()),
							this.order.map(order++)
					);
				}
				return;
			}
			else if(!expected.hasNext())
			{
				while(actual.hasNext())
					actual.next().deleteCopeItem();
				return;
			}
			else
			{
				final PatternItem tupel = actual.next();
				final int currentOrder = this.order.get(tupel);
				assert order<=currentOrder : String.valueOf(order) + '/' + currentOrder;
				order = currentOrder;
				this.element.set(tupel, expected.next());
			}
		}
	}

	private static final class Thrown implements ThrownGetter<ListField<?>>
	{
		public Set<Class<? extends Throwable>> get(final ListField<?> feature)
		{
			final Set<Class<? extends Throwable>> exceptions =
				feature.getElement().getInitialExceptions();
			exceptions.add(ClassCastException.class);
			return exceptions;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField)} instead
	 */
	@Deprecated
	public static final <E> ListField<E> newList(final FunctionField<E> element)
	{
		return create(element);
	}

	static class NullableIfElementOptional implements NullabilityGetter<ListField<?>>
	{
		@Override
		public Nullability getNullability(final ListField<?> feature)
		{
			return feature.element.isMandatory()?Nullability.NONNULL:Nullability.NULLABLE;
		}
	}
}
