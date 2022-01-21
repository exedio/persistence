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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ItemSerializationItem2()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new ItemSerializationItem2 and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ItemSerializationItem2(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getName2()
	{
		return ItemSerializationItem2.name2.get(this);
	}

	/**
	 * Sets a new value for {@link #name2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setName2(@javax.annotation.Nullable final java.lang.String name2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		ItemSerializationItem2.name2.set(this,name2);
	}

	/**
	 * Returns the value of {@link #list}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<String> getList()
	{
		return ItemSerializationItem2.list.get(this);
	}

	/**
	 * Returns a query for the value of {@link #list}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.Query<String> getListQuery()
	{
		return ItemSerializationItem2.list.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #list} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<ItemSerializationItem2> getDistinctParentsOfList(final String element)
	{
		return ItemSerializationItem2.list.getDistinctParents(ItemSerializationItem2.class,element);
	}

	/**
	 * Adds a new value for {@link #list}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addToList(@javax.annotation.Nonnull final String list)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ItemSerializationItem2.list.add(this,list);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #list}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean removeAllFromList(@javax.annotation.Nonnull final String list)
	{
		return ItemSerializationItem2.list.removeAll(this,list);
	}

	/**
	 * Sets a new value for {@link #list}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setList(@javax.annotation.Nonnull final java.util.Collection<? extends String> list)
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<ItemSerializationItem2> listParent()
	{
		return ItemSerializationItem2.list.getParent(ItemSerializationItem2.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemSerializationItem2.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemSerializationItem2> TYPE = com.exedio.cope.TypesBound.newType(ItemSerializationItem2.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ItemSerializationItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
