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

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public final class SetOrderedFieldItem extends Item
{
	static final SetField<String> strings = SetField.create(new StringField().lengthRange(4, 8)).ordered();

	void assertStrings(final String... expected)
	{
		final Set<String> actual = getStrings();
		assertUnmodifiable(actual);
		assertEquals(Arrays.asList(expected), new ArrayList<>(actual));
	}

	/**
	 * Creates a new SetOrderedFieldItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public SetOrderedFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new SetOrderedFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private SetOrderedFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Set<String> getStrings()
	{
		return SetOrderedFieldItem.strings.get(this);
	}

	/**
	 * Returns a query for the value of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getStringsQuery()
	{
		return SetOrderedFieldItem.strings.getQuery(this);
	}

	/**
	 * Returns the items, for which field set {@link #strings} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getParentsOf")
	@javax.annotation.Nonnull
	static java.util.List<SetOrderedFieldItem> getParentsOfStrings(@javax.annotation.Nonnull final String element)
	{
		return SetOrderedFieldItem.strings.getParents(SetOrderedFieldItem.class,element);
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
		SetOrderedFieldItem.strings.set(this,strings);
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
		return SetOrderedFieldItem.strings.add(this,element);
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
		return SetOrderedFieldItem.strings.remove(this,element);
	}

	/**
	 * Returns the parent field of the type of {@link #strings}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<SetOrderedFieldItem> stringsParent()
	{
		return SetOrderedFieldItem.strings.getParent(SetOrderedFieldItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for setOrderedFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SetOrderedFieldItem> TYPE = com.exedio.cope.TypesBound.newType(SetOrderedFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SetOrderedFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
