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
import com.exedio.cope.StringField;

public final class DynamicModelItem extends Item
{
	static final StringField name = new StringField().toFinal();

	static final DynamicModel<DynamicModelLocalizationItem> features = DynamicModel.create(ItemField.create(DynamicModelLocalizationItem.class), 1, 1, 1, 1, 2);
	static final DynamicModel<DynamicModelLocalizationItem> small = DynamicModel.create(ItemField.create(DynamicModelLocalizationItem.class), 1, 0, 0, 0, 0);


	/**
	 * Creates a new DynamicModelItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.MandatoryViolationException if name is null.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DynamicModelItem(
				@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DynamicModelItem.name,name),
		});
	}

	/**
	 * Creates a new DynamicModelItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DynamicModelItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getName()
	{
		return DynamicModelItem.name.get(this);
	}

	/**
	 * Returns the dynamic type of this item in the model {@link #features}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.DynamicModel.Type<DynamicModelLocalizationItem> getFeaturesType()
	{
		return DynamicModelItem.features.getType(this);
	}

	/**
	 * Sets the dynamic type of this item in the model {@link #features}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFeaturesType(@javax.annotation.Nullable final com.exedio.cope.pattern.DynamicModel.Type<DynamicModelLocalizationItem> type)
	{
		DynamicModelItem.features.setType(this,type);
	}

	/**
	 * Returns the value of {@code field} for this item in the model {@link #features}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Object getFeatures(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<DynamicModelLocalizationItem> field)
	{
		return DynamicModelItem.features.get(this,field);
	}

	/**
	 * Sets the value of {@code field} for this item in the model {@link #features}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFeatures(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<DynamicModelLocalizationItem> field,@javax.annotation.Nullable final java.lang.Object value)
	{
		DynamicModelItem.features.set(this,field,value);
	}

	/**
	 * Returns the dynamic type of this item in the model {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.DynamicModel.Type<DynamicModelLocalizationItem> getSmallType()
	{
		return DynamicModelItem.small.getType(this);
	}

	/**
	 * Sets the dynamic type of this item in the model {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSmallType(@javax.annotation.Nullable final com.exedio.cope.pattern.DynamicModel.Type<DynamicModelLocalizationItem> type)
	{
		DynamicModelItem.small.setType(this,type);
	}

	/**
	 * Returns the value of {@code field} for this item in the model {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Object getSmall(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<DynamicModelLocalizationItem> field)
	{
		return DynamicModelItem.small.get(this,field);
	}

	/**
	 * Sets the value of {@code field} for this item in the model {@link #small}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSmall(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<DynamicModelLocalizationItem> field,@javax.annotation.Nullable final java.lang.Object value)
	{
		DynamicModelItem.small.set(this,field,value);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dynamicModelItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DynamicModelItem> TYPE = com.exedio.cope.TypesBound.newType(DynamicModelItem.class,DynamicModelItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DynamicModelItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
