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

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import java.util.List;

public final class PartOfContainerItem extends Item
{

	static final StringField containerString = new StringField();

	List<PartOfItem> getUnordered() // TODO generate this
	{
		return PartOfItem.getUnorderedParts(this);
	}

	List<PartOfItem> getUnordered(final Condition condition) // TODO generate this
	{
		return PartOfItem.getUnorderedParts(this, condition);
	}

	PartOfItem addToUnordered(final String partString, final int partInteger) // TODO generate this
	{
		return new PartOfItem(this, 0, partString, partInteger);
	}

	List<PartOfItem> getOrdered() // TODO generate this
	{
		return PartOfItem.getOrderedParts(this);
	}

	List<PartOfItem> getOrdered(final Condition condition) // TODO generate this
	{
		return PartOfItem.getOrderedParts(this, condition);
	}

	PartOfItem addToOrdered(final int order, final String partString, final int partInteger) // TODO generate this
	{
		return new PartOfItem(this, order, partString, partInteger);
	}

	/**
	 * Creates a new PartOfContainerItem with all the fields initially needed.
	 * @param containerString the initial value for field {@link #containerString}.
	 * @throws com.exedio.cope.MandatoryViolationException if containerString is null.
	 * @throws com.exedio.cope.StringLengthViolationException if containerString violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PartOfContainerItem(
				@javax.annotation.Nonnull final java.lang.String containerString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(PartOfContainerItem.containerString,containerString),
		});
	}

	/**
	 * Creates a new PartOfContainerItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private PartOfContainerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #containerString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getContainerString()
	{
		return PartOfContainerItem.containerString.get(this);
	}

	/**
	 * Sets a new value for {@link #containerString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setContainerString(@javax.annotation.Nonnull final java.lang.String containerString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		PartOfContainerItem.containerString.set(this,containerString);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for partOfContainerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PartOfContainerItem> TYPE = com.exedio.cope.TypesBound.newType(PartOfContainerItem.class,PartOfContainerItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PartOfContainerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
