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

package com.exedio.cope.serialize;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.ListField;

final class ItemSerializationItem2 extends Item
{
	static final StringField name2 = new StringField().optional();
	static final ListField<String> list = ListField.create(new StringField());

	/**
	 * Creates a new ItemSerializationItem2 with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemSerializationItem2()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ItemSerializationItem2 and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemSerializationItem2(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #name2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getName2()
	{
		return ItemSerializationItem2.name2.get(this);
	}

	/**
	 * Sets a new value for {@link #name2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setName2(@javax.annotation.Nullable final java.lang.String name2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		ItemSerializationItem2.name2.set(this,name2);
	}

	/**
	 * Returns the value of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.List<String> getList()
	{
		return ItemSerializationItem2.list.get(this);
	}

	/**
	 * Returns a query for the value of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<String> getListQuery()
	{
		return ItemSerializationItem2.list.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #list} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getDistinctParentsOf")
	@javax.annotation.Nonnull
	static final java.util.List<ItemSerializationItem2> getDistinctParentsOfList(final String element)
	{
		return ItemSerializationItem2.list.getDistinctParents(ItemSerializationItem2.class,element);
	}

	/**
	 * Adds a new value for {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	final void addToList(@javax.annotation.Nonnull final String list)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ItemSerializationItem2.list.add(this,list);
	}

	/**
	 * Sets a new value for {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setList(@javax.annotation.Nonnull final java.util.Collection<? extends String> list)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ItemSerializationItem2.list.set(this,list);
	}

	/**
	 * Returns the parent field of the type of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<ItemSerializationItem2> listParent()
	{
		return ItemSerializationItem2.list.getParent(ItemSerializationItem2.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemSerializationItem2.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemSerializationItem2> TYPE = com.exedio.cope.TypesBound.newType(ItemSerializationItem2.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemSerializationItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
