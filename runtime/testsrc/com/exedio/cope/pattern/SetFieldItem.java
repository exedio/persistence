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

import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public final class SetFieldItem extends Item
{
	static final SetField<String> strings = SetField.create(new StringField().lengthRange(4, 8));
	static final SetField<Date> dates = SetField.create(new DateField());


	void assertStrings(final String... expected)
	{
		final Set<String> actual = getStrings();
		assertUnmodifiable(actual);
		assertEquals(Arrays.asList(expected), new ArrayList<>(actual));
	}

	void assertDates(final Date... expected)
	{
		final Set<Date> actual = getDates();
		assertUnmodifiable(actual);
		assertEquals(Arrays.asList(expected), new ArrayList<>(actual));
	}


	/**
	 * Creates a new SetFieldItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public SetFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new SetFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private SetFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Set<String> getStrings()
	{
		return SetFieldItem.strings.get(this);
	}

	/**
	 * Returns a query for the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getStringsQuery()
	{
		return SetFieldItem.strings.getQuery(this);
	}

	/**
	 * Returns the items, for which field set {@link #strings} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParentsOf")
	@javax.annotation.Nonnull
	static java.util.List<SetFieldItem> getParentsOfStrings(@javax.annotation.Nonnull final String element)
	{
		return SetFieldItem.strings.getParents(SetFieldItem.class,element);
	}

	/**
	 * Sets a new value for {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStrings(@javax.annotation.Nonnull final java.util.Collection<? extends String> strings)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		SetFieldItem.strings.set(this,strings);
	}

	/**
	 * Adds a new element to {@link #strings}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	boolean addToStrings(@javax.annotation.Nonnull final String element)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		return SetFieldItem.strings.add(this,element);
	}

	/**
	 * Removes an element from {@link #strings}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="removeFrom")
	boolean removeFromStrings(@javax.annotation.Nonnull final String element)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		return SetFieldItem.strings.remove(this,element);
	}

	/**
	 * Returns the parent field of the type of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<SetFieldItem> stringsParent()
	{
		return SetFieldItem.strings.getParent(SetFieldItem.class);
	}

	/**
	 * Returns the value of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Set<Date> getDates()
	{
		return SetFieldItem.dates.get(this);
	}

	/**
	 * Returns a query for the value of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	com.exedio.cope.Query<Date> getDatesQuery()
	{
		return SetFieldItem.dates.getQuery(this);
	}

	/**
	 * Returns the items, for which field set {@link #dates} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParentsOf")
	@javax.annotation.Nonnull
	static java.util.List<SetFieldItem> getParentsOfDates(@javax.annotation.Nonnull final Date element)
	{
		return SetFieldItem.dates.getParents(SetFieldItem.class,element);
	}

	/**
	 * Sets a new value for {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDates(@javax.annotation.Nonnull final java.util.Collection<? extends Date> dates)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		SetFieldItem.dates.set(this,dates);
	}

	/**
	 * Adds a new element to {@link #dates}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	boolean addToDates(@javax.annotation.Nonnull final Date element)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		return SetFieldItem.dates.add(this,element);
	}

	/**
	 * Removes an element from {@link #dates}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="removeFrom")
	boolean removeFromDates(@javax.annotation.Nonnull final Date element)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		return SetFieldItem.dates.remove(this,element);
	}

	/**
	 * Returns the parent field of the type of {@link #dates}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<SetFieldItem> datesParent()
	{
		return SetFieldItem.dates.getParent(SetFieldItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for setFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SetFieldItem> TYPE = com.exedio.cope.TypesBound.newType(SetFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SetFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
