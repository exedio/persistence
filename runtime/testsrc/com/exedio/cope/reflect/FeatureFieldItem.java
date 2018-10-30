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

package com.exedio.cope.reflect;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class FeatureFieldItem extends Item
{
	static final IntegerField integer1 = new IntegerField().optional();
	static final IntegerField integer2 = new IntegerField().optional();
	static final IntegerField integer3 = new IntegerField().optional();
	static final StringField string1 = new StringField().optional();
	static final StringField string2 = new StringField().optional();
	static final StringField string3 = new StringField().optional();

	static final FeatureField<Feature> standard = FeatureField.create();
	static final FeatureField<Feature> isFinal  = FeatureField.create().toFinal();
	static final FeatureField<Feature> optional = FeatureField.create().optional();
	static final FeatureField<Feature> unique   = FeatureField.create().optional().unique();
	@CopeSchemaName("newname")
	static final FeatureField<Feature> renamed = FeatureField.create().optional();
	static final FeatureField<StringField> restricted = FeatureField.create(StringField.class).optional();

	/**
	 * Creates a new FeatureFieldItem with all the fields initially needed.
	 * @param standard the initial value for field {@link #standard}.
	 * @param isFinal the initial value for field {@link #isFinal}.
	 * @throws com.exedio.cope.MandatoryViolationException if standard, isFinal is null.
	 * @throws com.exedio.cope.StringLengthViolationException if standard, isFinal violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	FeatureFieldItem(
				@javax.annotation.Nonnull final Feature standard,
				@javax.annotation.Nonnull final Feature isFinal)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			FeatureFieldItem.standard.map(standard),
			FeatureFieldItem.isFinal.map(isFinal),
		});
	}

	/**
	 * Creates a new FeatureFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private FeatureFieldItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #integer1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getInteger1()
	{
		return FeatureFieldItem.integer1.get(this);
	}

	/**
	 * Sets a new value for {@link #integer1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger1(@javax.annotation.Nullable final java.lang.Integer integer1)
	{
		FeatureFieldItem.integer1.set(this,integer1);
	}

	/**
	 * Returns the value of {@link #integer2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getInteger2()
	{
		return FeatureFieldItem.integer2.get(this);
	}

	/**
	 * Sets a new value for {@link #integer2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger2(@javax.annotation.Nullable final java.lang.Integer integer2)
	{
		FeatureFieldItem.integer2.set(this,integer2);
	}

	/**
	 * Returns the value of {@link #integer3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getInteger3()
	{
		return FeatureFieldItem.integer3.get(this);
	}

	/**
	 * Sets a new value for {@link #integer3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger3(@javax.annotation.Nullable final java.lang.Integer integer3)
	{
		FeatureFieldItem.integer3.set(this,integer3);
	}

	/**
	 * Returns the value of {@link #string1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getString1()
	{
		return FeatureFieldItem.string1.get(this);
	}

	/**
	 * Sets a new value for {@link #string1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setString1(@javax.annotation.Nullable final java.lang.String string1)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.string1.set(this,string1);
	}

	/**
	 * Returns the value of {@link #string2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getString2()
	{
		return FeatureFieldItem.string2.get(this);
	}

	/**
	 * Sets a new value for {@link #string2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setString2(@javax.annotation.Nullable final java.lang.String string2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.string2.set(this,string2);
	}

	/**
	 * Returns the value of {@link #string3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getString3()
	{
		return FeatureFieldItem.string3.get(this);
	}

	/**
	 * Sets a new value for {@link #string3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setString3(@javax.annotation.Nullable final java.lang.String string3)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.string3.set(this,string3);
	}

	/**
	 * Returns the value of {@link #standard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	Feature getStandard()
	{
		return FeatureFieldItem.standard.get(this);
	}

	/**
	 * Sets a new value for {@link #standard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStandard(@javax.annotation.Nonnull final Feature standard)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.standard.set(this,standard);
	}

	/**
	 * Returns the value of {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	Feature getIsFinal()
	{
		return FeatureFieldItem.isFinal.get(this);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	Feature getOptional()
	{
		return FeatureFieldItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOptional(@javax.annotation.Nullable final Feature optional)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.optional.set(this,optional);
	}

	/**
	 * Returns the value of {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	Feature getUnique()
	{
		return FeatureFieldItem.unique.get(this);
	}

	/**
	 * Sets a new value for {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setUnique(@javax.annotation.Nullable final Feature unique)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.unique.set(this,unique);
	}

	/**
	 * Finds a featureFieldItem by it's {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static FeatureFieldItem forUnique(@javax.annotation.Nonnull final Feature unique)
	{
		return FeatureFieldItem.unique.searchUnique(FeatureFieldItem.class,unique);
	}

	/**
	 * Returns the value of {@link #renamed}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	Feature getRenamed()
	{
		return FeatureFieldItem.renamed.get(this);
	}

	/**
	 * Sets a new value for {@link #renamed}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setRenamed(@javax.annotation.Nullable final Feature renamed)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.renamed.set(this,renamed);
	}

	/**
	 * Returns the value of {@link #restricted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	StringField getRestricted()
	{
		return FeatureFieldItem.restricted.get(this);
	}

	/**
	 * Sets a new value for {@link #restricted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setRestricted(@javax.annotation.Nullable final StringField restricted)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		FeatureFieldItem.restricted.set(this,restricted);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for featureFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FeatureFieldItem> TYPE = com.exedio.cope.TypesBound.newType(FeatureFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private FeatureFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
