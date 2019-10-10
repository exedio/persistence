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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;

public final class PartOfItem extends Item
{
	static final ItemField<PartOfContainerItem> container = ItemField.create(PartOfContainerItem.class).cascade().toFinal();

	static final IntegerField order = new IntegerField();

	static final PartOf<PartOfContainerItem> unordered = PartOf.create(container);
	static final PartOf<PartOfContainerItem> ordered   = PartOf.create(container, order);

	static final StringField partString = new StringField();
	static final IntegerField partInteger = new IntegerField();


	/**
	 * Creates a new PartOfItem with all the fields initially needed.
	 * @param container the initial value for field {@link #container}.
	 * @param order the initial value for field {@link #order}.
	 * @param partString the initial value for field {@link #partString}.
	 * @param partInteger the initial value for field {@link #partInteger}.
	 * @throws com.exedio.cope.MandatoryViolationException if container, partString is null.
	 * @throws com.exedio.cope.StringLengthViolationException if partString violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PartOfItem(
				@javax.annotation.Nonnull final PartOfContainerItem container,
				final int order,
				@javax.annotation.Nonnull final java.lang.String partString,
				final int partInteger)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			PartOfItem.container.map(container),
			PartOfItem.order.map(order),
			PartOfItem.partString.map(partString),
			PartOfItem.partInteger.map(partInteger),
		});
	}

	/**
	 * Creates a new PartOfItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private PartOfItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #container}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	PartOfContainerItem getContainer()
	{
		return PartOfItem.container.get(this);
	}

	/**
	 * Returns the value of {@link #order}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getOrder()
	{
		return PartOfItem.order.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #order}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOrder(final int order)
	{
		PartOfItem.order.set(this,order);
	}

	/**
	 * Returns the container this item is part of by {@link #unordered}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContainer")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	PartOfContainerItem getUnorderedContainer()
	{
		return PartOfItem.unordered.getContainer(this);
	}

	/**
	 * Returns the parts of the given container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<PartOfItem> getUnorderedParts(@javax.annotation.Nonnull final PartOfContainerItem container)
	{
		return PartOfItem.unordered.getParts(PartOfItem.class,container);
	}

	/**
	 * Returns the parts of the given container matching the given condition.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<PartOfItem> getUnorderedParts(@javax.annotation.Nonnull final PartOfContainerItem container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return PartOfItem.unordered.getParts(PartOfItem.class,container,condition);
	}

	/**
	 * Returns the container this item is part of by {@link #ordered}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContainer")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	PartOfContainerItem getOrderedContainer()
	{
		return PartOfItem.ordered.getContainer(this);
	}

	/**
	 * Returns the parts of the given container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<PartOfItem> getOrderedParts(@javax.annotation.Nonnull final PartOfContainerItem container)
	{
		return PartOfItem.ordered.getParts(PartOfItem.class,container);
	}

	/**
	 * Returns the parts of the given container matching the given condition.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<PartOfItem> getOrderedParts(@javax.annotation.Nonnull final PartOfContainerItem container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return PartOfItem.ordered.getParts(PartOfItem.class,container,condition);
	}

	/**
	 * Returns the value of {@link #partString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getPartString()
	{
		return PartOfItem.partString.get(this);
	}

	/**
	 * Sets a new value for {@link #partString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPartString(@javax.annotation.Nonnull final java.lang.String partString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		PartOfItem.partString.set(this,partString);
	}

	/**
	 * Returns the value of {@link #partInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getPartInteger()
	{
		return PartOfItem.partInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #partInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPartInteger(final int partInteger)
	{
		PartOfItem.partInteger.set(this,partInteger);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for partOfItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PartOfItem> TYPE = com.exedio.cope.TypesBound.newType(PartOfItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PartOfItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
