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
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.util.Cast;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public final class SetField<E> extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final FunctionField<E> element;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;

	private SetField(final FunctionField<E> element)
	{
		this.element = requireNonNull(element, "element");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(!element.isMandatory())
			throw new IllegalArgumentException("element must be mandatory");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
	}

	public static final <E> SetField<E> create(final FunctionField<E> element)
	{
		return new SetField<>(element);
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> parent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint uniqueConstraint = new UniqueConstraint(parent, element);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("element", element);
		features.put("uniqueConstraint", uniqueConstraint);
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

	@Wrap(order=100, name="{1}Parent", doc="Returns the parent field of the type of {0}.")
	@Nonnull
	public <P extends Item> ItemField<P> getParent(@Nonnull final Class<P> parentClass)
	{
		return mount().parent.as(parentClass);
	}

	public ItemField<?> getParent()
	{
		return mount().parent;
	}

	public FunctionField<E> getElement()
	{
		return element;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		return mount().uniqueConstraint;
	}

	public Type<?> getRelationType()
	{
		return mount().relationType;
	}

	private static final String MODIFICATION_RETURN = "<tt>true</tt> if the field set changed as a result of the call.";

	@Wrap(order=10, doc="Returns the value of {0}.")
	@Nonnull
	public Set<E> get(@Nonnull final Item item)
	{
		return Collections.unmodifiableSet(new HashSet<>(getQuery(item).search()));
	}

	@Wrap(order=20, doc="Returns a query for the value of {0}.")
	@Nonnull
	public Query<E> getQuery(@Nonnull final Item item)
	{
		return new Query<>(element, Cope.equalAndCast(mount().parent, item));
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
		return new Query<>(
				mount().parent.as(parentClass),
				this.element.equal(element)).
			search();
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #get(Item)} changed as a result of the call.
	 */
	@Wrap(order=50, name="addTo{0}", doc="Adds a new element to {0}.", docReturn=MODIFICATION_RETURN, thrownGetter=Thrown.class)
	public boolean add(
			@Nonnull final Item item,
			@Nonnull @Parameter("element") final E element)
	{
		final Mount mount = mount();
		try
		{
			mount.relationType.newItem(
					Cope.mapAndCast(mount.parent, item),
					this.element.map(element)
			);
			return true;
		}
		catch(final UniqueViolationException e)
		{
			assert mount.uniqueConstraint==e.getFeature();
			return false;
		}
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #get(Item)} changed as a result of the call.
	 */
	@Wrap(order=60, name="removeFrom{0}", doc="Removes an element from {0}.", docReturn=MODIFICATION_RETURN, thrownGetter=Thrown.class)
	public boolean remove(
			@Nonnull final Item item,
			@Nonnull @Parameter("element") final E element)
	{
		final Item row =
			mount().uniqueConstraint.search(item, element);

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

	@Wrap(order=40, doc="Sets a new value for {0}.", thrownGetter=Thrown.class)
	public void set(@Nonnull final Item item, @Nonnull final Collection<? extends E> value)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, item);

		final Mount mount = mount();
		final LinkedHashSet<? extends E> toCreateSet = new LinkedHashSet<>(value);
		final ArrayList<Item> toDeleteList = new ArrayList<>();

		for(final PatternItem tupel : mount.relationType.search(Cope.equalAndCast(mount.parent, item)))
		{
			final Object element = this.element.get(tupel);

			if(toCreateSet.contains(element))
				toCreateSet.remove(element);
			else
				toDeleteList.add(tupel);
		}

		final Iterator<? extends E> toCreate = toCreateSet.iterator();
		final Iterator<Item> toDelete = toDeleteList.iterator();
		while(true)
		{
			if(!toDelete.hasNext())
			{
				while(toCreate.hasNext())
				{
					mount.relationType.newItem(
							Cope.mapAndCast(mount.parent, item),
							this.element.map(toCreate.next())
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

	public void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, Cast.castElements(element.getValueClass(), value));
	}

	private static final class Thrown implements ThrownGetter<SetField<?>>
	{
		@Override
		public Set<Class<? extends Throwable>> get(final SetField<?> feature)
		{
			final Set<Class<? extends Throwable>> result = feature.getElement().getInitialExceptions();
			result.add(ClassCastException.class);
			return result;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #create(FunctionField)} instead
	 */
	@Deprecated
	public static final <E> SetField<E> newSet(final FunctionField<E> element)
	{
		return create(element);
	}
}
