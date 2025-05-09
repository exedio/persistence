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
import com.exedio.cope.CopyConstraint;
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
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

@WrapFeature
public final class ListField<E> extends AbstractListField<E> implements Copyable
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final IntegerField order;
	private final FunctionField<E> element;
	private Mount mountIfMounted = null;
	private final CopyFields copyWith;

	private ListField(final FunctionField<E> element, final CopyFields copyWith)
	{
		this.order = new IntegerField().toFinal().min(0);
		this.element = requireNonNull(element, "element");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(element.hasDefault())
			throw new IllegalArgumentException("element must not have any default");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
		this.copyWith = copyWith;
	}

	public static <E> ListField<E> create(final FunctionField<E> element)
	{
		return new ListField<>(element, CopyFields.EMPTY);
	}

	@Override
	public ListField<E> copy(final CopyMapper mapper)
	{
		copyWith.failIfNotEmpty();
		return new ListField<>(mapper.copy(element), copyWith);
	}

	/**
	 * Returns a new ListField, that differs from this ListField
	 * by enforcing that parent and element items have the same value in the given field.
	 * @throws IllegalStateException if the {@link #getElement() element} field is not an {@link ItemField}
	 * @throws IllegalArgumentException if the field given as parameter is not final
	 */
	public ListField<E> copyWith(final FunctionField<?> copyWith)
	{
		if (!(element instanceof ItemField))
		{
			throw new IllegalStateException("copyWith requires the ListField's element to be an ItemField");
		}
		return new ListField<>(element.copy(), this.copyWith.add(copyWith));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint uniqueConstraint = UniqueConstraint.create(parent, order);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("order", order);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("element", element, new MysqlExtendedVarcharAnnotationProxy(this));
		copyWith.onMount(features, parent, element);
		final Type<Entry> entryType = newSourceType(Entry.class, Entry::new, features);
		this.mountIfMounted = new Mount(parent, uniqueConstraint, entryType);
	}

	private record Mount(
			ItemField<?> parent,
			UniqueConstraint uniqueConstraint,
			Type<Entry> entryType)
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

	@Wrap(order=1000, name="{1}Parent",
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

	/**
	 * Get the template fields added with {@link #copyWith(FunctionField)}.
	 */
	public List<FunctionField<?>> getCopyWithTemplateFields()
	{
		return copyWith.getTemplates();
	}

	/**
	 * Get the field that stores a redudant copy of a parent item's value at the relation item, to enforce a
	 * {@link CopyConstraint} added with {@link #copyWith(FunctionField)}.
	 * @throws IllegalArgumentException if the field given as parameter does not belong to this ListField's {@link #getType() type}.
	 * @throws IllegalStateException if there is no CopyConstraint on the given field
	 */
	@Nonnull
	public <T> FunctionField<T> getCopyWithCopyField(final FunctionField<T> template)
	{
		if (!template.getType().equals(getType()))
		{
			throw new IllegalArgumentException("field from wrong type: expected "+getType()+" but was "+template.getType());
		}
		return copyWith.getCopyField(template);
	}

	/**
	 * @see #getQuery(Item)
	 */
	@Wrap(order=10, doc=Wrap.GET_DOC)
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
			new Query<>(element, mount().parent.isCasted(item));
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
		requireParentClass(parentClass, "parentClass");
		final Query<P> q = new Query<>(
				mount().parent.as(parentClass),
				this.element.isCasted(element));
		q.setDistinct(true);
		return q.search();
	}

	@Wrap(order=40, name="addTo{0}",
			doc="Adds a new value for {0}.",
			thrownGetter=ListThrown.class)
	public void add(@Nonnull final Item item, @Parameter(nullability=NullableIfElementOptional.class) final E value)
	{
		final Mount mount = mount();
		final Query<Integer> q = new Query<>(
				this.order.max(),
				mount.parent.isCasted(item));
		final Integer max = q.searchSingleton();
		final int newOrder = max!=null ? (max+1) : 0;
		mount.entryType.newItem(
				Cope.mapAndCast(mount.parent, item),
				map(this.order, newOrder),
				map(this.element, value));
	}

	/**
	 * @return {@code true} if the result of {@link #get(Item)} changed as a result of the call.
	 */
	@Wrap(order=45, name="removeAllFrom{0}",
			doc="Removes all occurrences of '{@code element}' from {0}.",
			docReturn="'{@code true}' if the field set changed as a result of the call.")
	public boolean removeAll(
			@Nonnull final Item item,
			@Parameter(nullability=NullableIfElementOptional.class) final E element)
	{
		final Mount mount = mount();
		final List<Entry> entries =
				mount.entryType.search(Cope.and(
						mount.parent.isCasted(item),
						this.element.is(element)));

		if(entries.isEmpty())
			return false;

		for(final Entry entry : entries)
			entry.deleteCopeItem();

		return true;
	}

	@Wrap(order=50,
			doc=Wrap.SET_DOC,
			thrownGetter=ListThrown.class)
	@Override
	@SuppressWarnings("AssignmentToForLoopParameter")
	public void set(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		MandatoryViolationException.requireNonNull(value, this, item);
		for(final E e : value)
			element.check(e);

		final Mount mount = mount();
		final Iterator<Entry> actual =
			mount.entryType.search(
					mount.parent.isCasted(item),
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
					mount.entryType.newItem(
							Cope.mapAndCast(mount.parent, item),
							map(this.element, expected.next()),
							map(this.order, order++)
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
				final Entry entry = actual.next();
				final int currentOrder = this.order.get(entry);
				assert order<=currentOrder : String.valueOf(order) + '/' + currentOrder;
				order = currentOrder;
				this.element.set(entry, expected.next());
			}
		}
	}
}
