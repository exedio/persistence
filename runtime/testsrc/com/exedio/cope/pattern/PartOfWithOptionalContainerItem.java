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

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.instrument.WrapperInitial;

class PartOfWithOptionalContainerItem extends Item
{
	@WrapperInitial
	static final ItemField<PartOfOptionalContainerItem> container = ItemField.create(PartOfOptionalContainerItem.class).optional();

	static final PartOf<PartOfOptionalContainerItem> parts = PartOf.create(container);

	/**
	 * Creates a new PartOfWithOptionalContainerItem with all the fields initially needed.
	 * @param container the initial value for field {@link #container}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PartOfWithOptionalContainerItem(
				@javax.annotation.Nullable final PartOfOptionalContainerItem container)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(PartOfWithOptionalContainerItem.container,container),
		});
	}

	/**
	 * Creates a new PartOfWithOptionalContainerItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected PartOfWithOptionalContainerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #container}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final PartOfOptionalContainerItem getContainer()
	{
		return PartOfWithOptionalContainerItem.container.get(this);
	}

	/**
	 * Sets a new value for {@link #container}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setContainer(@javax.annotation.Nullable final PartOfOptionalContainerItem container)
	{
		PartOfWithOptionalContainerItem.container.set(this,container);
	}

	/**
	 * Returns the container this item is part of by {@link #parts}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContainer")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final PartOfOptionalContainerItem getPartsContainer()
	{
		return PartOfWithOptionalContainerItem.parts.getContainer(this);
	}

	/**
	 * Returns the parts of the given container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.List<PartOfWithOptionalContainerItem> getPartsParts(@javax.annotation.Nullable final PartOfOptionalContainerItem container)
	{
		return PartOfWithOptionalContainerItem.parts.getParts(PartOfWithOptionalContainerItem.class,container);
	}

	/**
	 * Returns the parts of the given container matching the given condition.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.List<PartOfWithOptionalContainerItem> getPartsParts(@javax.annotation.Nullable final PartOfOptionalContainerItem container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return PartOfWithOptionalContainerItem.parts.getParts(PartOfWithOptionalContainerItem.class,container,condition);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for partOfWithOptionalContainerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<PartOfWithOptionalContainerItem> TYPE = com.exedio.cope.TypesBound.newType(PartOfWithOptionalContainerItem.class,PartOfWithOptionalContainerItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected PartOfWithOptionalContainerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
