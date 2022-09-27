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

final class MultiItemFieldItem extends Item
{
	static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
			canBe(MultiItemFieldComponentxA.class).
			canBe(MultiItemFieldComponentxB.class);

	MultiItemFieldComponentxA getFieldA()
	{
		return field.of(MultiItemFieldComponentxA.class).get(this);
	}

	MultiItemFieldComponentxB getFieldB()
	{
		return field.of(MultiItemFieldComponentxB.class).get(this);
	}


	static final MultiItemField<MultiItemFieldValuex> optionalField = MultiItemField.create(MultiItemFieldValuex.class).
			canBe(MultiItemFieldComponentxA.class).
			canBe(MultiItemFieldComponentxB.class).
			optional();

	MultiItemFieldComponentxA getOptionalFieldA()
	{
		return optionalField.of(MultiItemFieldComponentxA.class).get(this);
	}

	MultiItemFieldComponentxB getOptionalFieldB()
	{
		return optionalField.of(MultiItemFieldComponentxB.class).get(this);
	}


	static final MultiItemField<MultiItemFieldValuex> uniqueField = MultiItemField.create(MultiItemFieldValuex.class).
			canBe(MultiItemFieldComponentxA.class).
			canBe(MultiItemFieldComponentxB.class).
			optional().
			unique();

	static final PartOf<MultiItemFieldComponentxA> partOfClassA = PartOf.create(field.of(MultiItemFieldComponentxA.class));


	/**
	 * Creates a new MultiItemFieldItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	MultiItemFieldItem(
				@javax.annotation.Nonnull final MultiItemFieldValuex field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MultiItemFieldItem.field.map(field),
		});
	}

	/**
	 * Creates a new MultiItemFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MultiItemFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	MultiItemFieldValuex getField()
	{
		return MultiItemFieldItem.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField(@javax.annotation.Nonnull final MultiItemFieldValuex field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		MultiItemFieldItem.field.set(this,field);
	}

	/**
	 * Returns the value of {@link #optionalField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	MultiItemFieldValuex getOptionalField()
	{
		return MultiItemFieldItem.optionalField.get(this);
	}

	/**
	 * Sets a new value for {@link #optionalField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOptionalField(@javax.annotation.Nullable final MultiItemFieldValuex optionalField)
	{
		MultiItemFieldItem.optionalField.set(this,optionalField);
	}

	/**
	 * Returns the value of {@link #uniqueField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	MultiItemFieldValuex getUniqueField()
	{
		return MultiItemFieldItem.uniqueField.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setUniqueField(@javax.annotation.Nullable final MultiItemFieldValuex uniqueField)
			throws
				com.exedio.cope.UniqueViolationException
	{
		MultiItemFieldItem.uniqueField.set(this,uniqueField);
	}

	/**
	 * Finds a multiItemFieldItem by its {@link #uniqueField}.
	 * @param uniqueField shall be equal to field {@link #uniqueField}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static MultiItemFieldItem forUniqueField(@javax.annotation.Nonnull final MultiItemFieldValuex uniqueField)
	{
		return MultiItemFieldItem.uniqueField.searchUnique(MultiItemFieldItem.class,uniqueField);
	}

	/**
	 * Finds a multiItemFieldItem by its {@link #uniqueField}.
	 * @param uniqueField shall be equal to field {@link #uniqueField}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static MultiItemFieldItem forUniqueFieldStrict(@javax.annotation.Nonnull final MultiItemFieldValuex uniqueField)
			throws
				java.lang.IllegalArgumentException
	{
		return MultiItemFieldItem.uniqueField.searchUniqueStrict(MultiItemFieldItem.class,uniqueField);
	}

	/**
	 * Returns the container this item is part of by {@link #partOfClassA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContainer")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	MultiItemFieldComponentxA getPartOfClassAContainer()
	{
		return MultiItemFieldItem.partOfClassA.getContainer(this);
	}

	/**
	 * Returns the parts of the given container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<MultiItemFieldItem> getPartOfClassAParts(@javax.annotation.Nullable final MultiItemFieldComponentxA container)
	{
		return MultiItemFieldItem.partOfClassA.getParts(MultiItemFieldItem.class,container);
	}

	/**
	 * Returns the parts of the given container matching the given condition.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getParts")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.util.List<MultiItemFieldItem> getPartOfClassAParts(@javax.annotation.Nullable final MultiItemFieldComponentxA container,@javax.annotation.Nullable final com.exedio.cope.Condition condition)
	{
		return MultiItemFieldItem.partOfClassA.getParts(MultiItemFieldItem.class,container,condition);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for multiItemFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MultiItemFieldItem> TYPE = com.exedio.cope.TypesBound.newType(MultiItemFieldItem.class,MultiItemFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MultiItemFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
