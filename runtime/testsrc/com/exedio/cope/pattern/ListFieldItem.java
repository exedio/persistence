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
	static final StringField value = new StringField().optional().toFinal();
	static final ListField<String> strings = ListField.create(new StringField().optional().lengthRange(4, 8));
	static final ListField<Date> dates = ListField.create(new DateField());
	static final ListField<ListFieldItem> items = ListField.create(ItemField.create(ListFieldItem.class).cascade());
	static final ListField<ListFieldItem> itemsSameValue = ListField.create(ItemField.create(ListFieldItem.class).optional()).copyWith(value);

	ListFieldItem()
	{
		this((String)null);
	}

	/**
	 * Creates a new ListFieldItem with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ListFieldItem(
				@javax.annotation.Nullable final java.lang.String value)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(ListFieldItem.value,value),
		});
	}

	/**
	 * Creates a new ListFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ListFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getValue()
	{
		return ListFieldItem.value.get(this);
	}

	/**
	 * Returns the value of {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<String> getStrings()
	{
		return ListFieldItem.strings.get(this);
	}

	/**
	 * Returns a query for the value of {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getStringsQuery()
	{
		return ListFieldItem.strings.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #strings} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<ListFieldItem> getDistinctParentsOfStrings(final String element)
	{
		return ListFieldItem.strings.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToStrings(@javax.annotation.Nullable final String strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.strings.add(this,strings);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #strings}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromStrings(@javax.annotation.Nullable final String strings)
	{
		return ListFieldItem.strings.removeAll(this,strings);
	}

	/**
	 * Sets a new value for {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStrings(@javax.annotation.Nonnull final java.util.Collection<? extends String> strings)
			throws
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.strings.set(this,strings);
	}

	/**
	 * Returns the parent field of the type of {@link #strings}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ListFieldItem> stringsParent()
	{
		return ListFieldItem.strings.getParent(ListFieldItem.class);
	}

	/**
	 * Returns the value of {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<Date> getDates()
	{
		return ListFieldItem.dates.get(this);
	}

	/**
	 * Returns a query for the value of {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<Date> getDatesQuery()
	{
		return ListFieldItem.dates.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #dates} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<ListFieldItem> getDistinctParentsOfDates(final Date element)
	{
		return ListFieldItem.dates.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToDates(@javax.annotation.Nonnull final Date dates)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.dates.add(this,dates);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #dates}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromDates(@javax.annotation.Nonnull final Date dates)
	{
		return ListFieldItem.dates.removeAll(this,dates);
	}

	/**
	 * Sets a new value for {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDates(@javax.annotation.Nonnull final java.util.Collection<? extends Date> dates)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.dates.set(this,dates);
	}

	/**
	 * Returns the parent field of the type of {@link #dates}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ListFieldItem> datesParent()
	{
		return ListFieldItem.dates.getParent(ListFieldItem.class);
	}

	/**
	 * Returns the value of {@link #items}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<ListFieldItem> getItems()
	{
		return ListFieldItem.items.get(this);
	}

	/**
	 * Returns a query for the value of {@link #items}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<ListFieldItem> getItemsQuery()
	{
		return ListFieldItem.items.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #items} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<ListFieldItem> getDistinctParentsOfItems(final ListFieldItem element)
	{
		return ListFieldItem.items.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #items}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToItems(@javax.annotation.Nonnull final ListFieldItem items)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.items.add(this,items);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #items}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromItems(@javax.annotation.Nonnull final ListFieldItem items)
	{
		return ListFieldItem.items.removeAll(this,items);
	}

	/**
	 * Sets a new value for {@link #items}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItems(@javax.annotation.Nonnull final java.util.Collection<? extends ListFieldItem> items)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		ListFieldItem.items.set(this,items);
	}

	/**
	 * Returns the parent field of the type of {@link #items}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ListFieldItem> itemsParent()
	{
		return ListFieldItem.items.getParent(ListFieldItem.class);
	}

	/**
	 * Returns the value of {@link #itemsSameValue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<ListFieldItem> getItemsSameValue()
	{
		return ListFieldItem.itemsSameValue.get(this);
	}

	/**
	 * Returns a query for the value of {@link #itemsSameValue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<ListFieldItem> getItemsSameValueQuery()
	{
		return ListFieldItem.itemsSameValue.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #itemsSameValue} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<ListFieldItem> getDistinctParentsOfItemsSameValue(final ListFieldItem element)
	{
		return ListFieldItem.itemsSameValue.getDistinctParents(ListFieldItem.class,element);
	}

	/**
	 * Adds a new value for {@link #itemsSameValue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToItemsSameValue(@javax.annotation.Nullable final ListFieldItem itemsSameValue)
			throws
				java.lang.ClassCastException
	{
		ListFieldItem.itemsSameValue.add(this,itemsSameValue);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #itemsSameValue}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromItemsSameValue(@javax.annotation.Nullable final ListFieldItem itemsSameValue)
	{
		return ListFieldItem.itemsSameValue.removeAll(this,itemsSameValue);
	}

	/**
	 * Sets a new value for {@link #itemsSameValue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItemsSameValue(@javax.annotation.Nonnull final java.util.Collection<? extends ListFieldItem> itemsSameValue)
			throws
				java.lang.ClassCastException
	{
		ListFieldItem.itemsSameValue.set(this,itemsSameValue);
	}

	/**
	 * Returns the parent field of the type of {@link #itemsSameValue}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ListFieldItem> itemsSameValueParent()
	{
		return ListFieldItem.itemsSameValue.getParent(ListFieldItem.class);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for listFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<ListFieldItem> TYPE = com.exedio.cope.TypesBound.newType(ListFieldItem.class,ListFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ListFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
