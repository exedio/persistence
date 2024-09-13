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
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.util.Cast;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class SetField<E> extends Pattern implements Copyable
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final boolean ordered;
	private final IntegerField order;
	private final FunctionField<E> element;
	private Mount mountIfMounted = null;
	private final CopyFields copyWith;

	private SetField(
			final boolean ordered,
			final FunctionField<E> element,
			final CopyFields copyWith)
	{
		this.ordered = ordered;
		this.order = ordered ? new IntegerField().min(0) : null;
		this.element = requireNonNull(element, "element");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(!element.isMandatory())
			throw new IllegalArgumentException("element must be mandatory");
		if(element.hasDefault())
			throw new IllegalArgumentException("element must not have any default");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
		this.copyWith = copyWith;
	}

	public static <E> SetField<E> create(final FunctionField<E> element)
	{
		return new SetField<>(false, element, CopyFields.EMPTY);
	}

	@Override
	public SetField<E> copy(final CopyMapper mapper)
	{
		copyWith.failIfNotEmpty();
		return new SetField<>(ordered, mapper.copy(element), copyWith.copy());
	}

	/**
	 * Returns a new SetField,
	 * that differs from this SetField
	 * by being {@link #isOrdered() ordered}.
	 */
	public SetField<E> ordered()
	{
		return new SetField<>(true, element.copy(), copyWith.copy());
	}

	/**
	 * Returns a new SetField, that differs from this SetField
	 * by enforcing that parent and element items have the same value in the given field.
	 * @throws IllegalStateException if the {@link #getElement() element} field is not an {@link ItemField}
	 * @throws IllegalArgumentException if the field given as parameter is not final
	 */
	public SetField<E> copyWith(final FunctionField<?> copyWith)
	{
		if (!(element instanceof ItemField))
		{
			throw new IllegalStateException("copyWith requires the SetField's element to be an ItemField");
		}
		return new SetField<>(ordered, element.copy(), this.copyWith.add(copyWith));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint uniqueOrder = ordered ? UniqueConstraint.create(parent, order) : null;
		final UniqueConstraint uniqueElement = UniqueConstraint.create(parent, element);
		final Features features = new Features();
		features.put("parent", parent);
		if(ordered)
		{
			features.put("order", order);
			features.put("uniqueOrder", uniqueOrder);
		}
		features.put("element", element);
		features.put("uniqueConstraint", uniqueElement);
		copyWith.onMount(features, parent, element);
		final Type<PatternItem> entryType = newSourceType(PatternItem.class, PatternItem::new, features);
		this.mountIfMounted = new Mount(parent, uniqueOrder, uniqueElement, entryType);
	}

	private record Mount(
			ItemField<?> parent,
			UniqueConstraint uniqueOrder,
			UniqueConstraint uniqueElement,
			Type<PatternItem> entryType)
	{
		Mount
		{
			assert parent!=null;
			assert uniqueElement != null;
			assert entryType!=null;
		}
	}

	private Mount mount()
	{
		return requireMounted(mountIfMounted);
	}

	@Wrap(order=100, name="{1}Parent", doc="Returns the parent field of the type of {0}.")
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

	/**
	 * Returns whether this {@code SetField} is ordered.
	 * An ordered {@code SetField} maintains the insertion order of its elements.
	 */
	public boolean isOrdered()
	{
		return ordered;
	}

	public IntegerField getOrder()
	{
		return order;
	}

	public UniqueConstraint getUniqueConstraintForOrder()
	{
		return mount().uniqueOrder;
	}

	public FunctionField<E> getElement()
	{
		return element;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return mount().uniqueElement;
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
	 * @throws IllegalArgumentException if the field given as parameter does not belong to this SetField's {@link #getType() type}.
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

	private static final String MODIFICATION_RETURN = "'{@code true}' if the field set changed as a result of the call.";

	@Wrap(order=10, doc=Wrap.GET_DOC)
	@Nonnull
	public Set<E> get(@Nonnull final Item item)
	{
		return Collections.unmodifiableSet(new LinkedHashSet<>(getQuery(item).search()));
	}

	@Wrap(order=20, doc="Returns a query for the value of {0}.")
	@Nonnull
	public Query<E> getQuery(@Nonnull final Item item)
	{
		final Query<E> result = new Query<>(element, Cope.equalAndCast(mount().parent, item));
		result.setOrderBy(ordered ? order : element, true);
		return result;
	}

	/**
	 * Returns the items, for which this field set contains the given element.
	 * The order of the result is unspecified.
	 */
	@Wrap(order=30,
			name="getParentsOf{0}",
			doc="Returns the items, for which field set {0} contains the given element.")
	@Nonnull
	public <P extends Item> List<P> getParents(
			@Nonnull final Class<P> parentClass,
			@Nonnull @Parameter("element") final E element)
	{
		requireParentClass(parentClass, "parentClass");
		return new Query<>(
				mount().parent.as(parentClass),
				this.element.equal(element)).
			search();
	}

	/**
	 * @return {@code true} if the result of {@link #get(Item)} changed as a result of the call.
	 */
	@Wrap(order=50, name="addTo{0}", doc="Adds a new element to {0}.", docReturn=MODIFICATION_RETURN, thrownGetter=SetThrown.class)
	public boolean add(
			@Nonnull final Item item,
			@Nonnull @Parameter("element") final E element)
	{
		final Mount mount = mount();

		if(ordered)
		{
			final Query<Integer> q = new Query<>(
					this.order.max(),
					Cope.equalAndCast(mount.parent, item));
			final Integer max = q.searchSingleton();
			final int newOrder = max!=null ? (max+1) : 0;
			try
			{
				mount.entryType.newItem(
						Cope.mapAndCast(mount.parent, item),
						map(this.order, newOrder),
						map(this.element, element)
				);
				return true;
			}
			catch(final UniqueViolationException e)
			{
				assert mount.uniqueElement == e.getFeature();
				return false;
			}
		}

		try
		{
			mount.entryType.newItem(
					Cope.mapAndCast(mount.parent, item),
					map(this.element, element)
			);
			return true;
		}
		catch(final UniqueViolationException e)
		{
			assert mount.uniqueElement == e.getFeature();
			return false;
		}
	}

	/**
	 * @return {@code true} if the result of {@link #get(Item)} changed as a result of the call.
	 */
	@Wrap(order=60, name="removeFrom{0}", doc="Removes an element from {0}.", docReturn=MODIFICATION_RETURN, thrownGetter=SetThrown.class)
	public boolean remove(
			@Nonnull final Item item,
			@Nonnull @Parameter("element") final E element)
	{
		final Item row =
			mount().uniqueElement.search(item, element);

		if(row==null)
		{
			return false;
		}
		else
		{
			row.deleteCopeItem();
			return true;
		}
	}

	@Wrap(order=40, doc=Wrap.SET_DOC, thrownGetter=SetThrown.class)
	public void set(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		MandatoryViolationException.requireNonNull(value, this, item);

		if(ordered)
		{
			setOrdered(item, value);
			return;
		}

		final Mount mount = mount();
		final LinkedHashSet<? extends E> toCreateSet = new LinkedHashSet<>(value);
		for(final E e : toCreateSet)
			element.check(e);
		final ArrayList<PatternItem> toDeleteList = new ArrayList<>();

		for(final PatternItem entry : mount.entryType.search(Cope.equalAndCast(mount.parent, item)))
		{
			final E element = this.element.get(entry);

			if(toCreateSet.contains(element))
				toCreateSet.remove(element);
			else
				toDeleteList.add(entry);
		}

		final Iterator<? extends E> toCreate = toCreateSet.iterator();
		final Iterator<PatternItem> toDelete = toDeleteList.iterator();
		while(true)
		{
			if(!toDelete.hasNext())
			{
				while(toCreate.hasNext())
				{
					mount.entryType.newItem(
							Cope.mapAndCast(mount.parent, item),
							map(this.element, toCreate.next())
					);
				}
				return;
			}
			else if(!toCreate.hasNext())
			{
				while(toDelete.hasNext())
					toDelete.next().deleteCopeItem();
				return;
			}
			else
			{
				this.element.set(toDelete.next(), toCreate.next());
			}
		}
	}

	private void setOrdered(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		final Mount mount = mount();

		// TODO reuse entries that can be reused

		for(final PatternItem entry : mount.entryType.search(Cope.equalAndCast(mount.parent, item)))
			entry.deleteCopeItem();

		int order = 0;
		final LinkedHashSet<? extends E> toCreateSet = new LinkedHashSet<>(value);
		for(final E e : toCreateSet)
			element.check(e);
		for(final E element : toCreateSet)
			mount.entryType.newItem(
					Cope.mapAndCast(mount.parent, item),
					map(this.order, order++),
					map(this.element, element));
	}

	public void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, Cast.castElements(element.getValueClass(), value));
	}
}
