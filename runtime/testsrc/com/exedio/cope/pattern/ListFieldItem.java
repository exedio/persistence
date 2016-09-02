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

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import java.util.Date;

public final class ListFieldItem extends Item
{
	static final ListField<String> strings = ListField.create(new StringField().optional().lengthRange(4, 8));
	static final ListField<Date> dates = ListField.create(new DateField());
	static final ListField<ListFieldItem> items = ListField.create(ItemField.create(ListFieldItem.class).cascade());

	/**
	 * Creates a new ListFieldItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public ListFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ListFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ListFieldItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.List<String> getStrings()
	{
		return ListFieldItem.strings.get(this);
	}

	/**
	 * Returns a query for the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<String> getStringsQuery()
	{
		return ListFieldItem.strings.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #strings} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getDistinctParentsOf")
	@javax.annotation.Nonnull
	static final java.util.List<ListFieldItem> getDistinctParentsOfStrings(final String element)
	{
		return ListFieldItem.strings.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	final void addToStrings(@javax.annotation.Nullable final String strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.strings.add(this,strings);
	}

	/**
	 * Sets a new value for {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStrings(@javax.annotation.Nonnull final java.util.Collection<? extends String> strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.strings.set(this,strings);
	}

	/**
	 * Returns the parent field of the type of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<ListFieldItem> stringsParent()
	{
		return ListFieldItem.strings.getParent(ListFieldItem.class);
	}

	/**
	 * Returns the value of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.List<Date> getDates()
	{
		return ListFieldItem.dates.get(this);
	}

	/**
	 * Returns a query for the value of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<Date> getDatesQuery()
	{
		return ListFieldItem.dates.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #dates} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getDistinctParentsOf")
	@javax.annotation.Nonnull
	static final java.util.List<ListFieldItem> getDistinctParentsOfDates(final Date element)
	{
		return ListFieldItem.dates.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	final void addToDates(@javax.annotation.Nonnull final Date dates)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.dates.add(this,dates);
	}

	/**
	 * Sets a new value for {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDates(@javax.annotation.Nonnull final java.util.Collection<? extends Date> dates)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.dates.set(this,dates);
	}

	/**
	 * Returns the parent field of the type of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<ListFieldItem> datesParent()
	{
		return ListFieldItem.dates.getParent(ListFieldItem.class);
	}

	/**
	 * Returns the value of {@link #items}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.List<ListFieldItem> getItems()
	{
		return ListFieldItem.items.get(this);
	}

	/**
	 * Returns a query for the value of {@link #items}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<ListFieldItem> getItemsQuery()
	{
		return ListFieldItem.items.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #items} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getDistinctParentsOf")
	@javax.annotation.Nonnull
	static final java.util.List<ListFieldItem> getDistinctParentsOfItems(final ListFieldItem element)
	{
		return ListFieldItem.items.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #items}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	final void addToItems(@javax.annotation.Nonnull final ListFieldItem items)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.items.add(this,items);
	}

	/**
	 * Sets a new value for {@link #items}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItems(@javax.annotation.Nonnull final java.util.Collection<? extends ListFieldItem> items)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.items.set(this,items);
	}

	/**
	 * Returns the parent field of the type of {@link #items}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<ListFieldItem> itemsParent()
	{
		return ListFieldItem.items.getParent(ListFieldItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for listFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<ListFieldItem> TYPE = com.exedio.cope.TypesBound.newType(ListFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ListFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
