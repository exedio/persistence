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

package com.exedio.cope;

class ConnectItem extends Item
{
	static final ItemField<ConnectItem2> itemField = ItemField.create(ConnectItem2.class);
	static final ItemField<ConnectItem> itemFieldSelf = ItemField.create(ConnectItem.class);
	static final StringField stringField = new StringField();
	static final IntegerField integerField = new IntegerField().defaultToNext(1234);

	/**
	 * Creates a new ConnectItem with all the fields initially needed.
	 * @param itemField the initial value for field {@link #itemField}.
	 * @param itemFieldSelf the initial value for field {@link #itemFieldSelf}.
	 * @param stringField the initial value for field {@link #stringField}.
	 * @throws com.exedio.cope.MandatoryViolationException if itemField, itemFieldSelf, stringField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if stringField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ConnectItem(
				@javax.annotation.Nonnull final ConnectItem2 itemField,
				@javax.annotation.Nonnull final ConnectItem itemFieldSelf,
				@javax.annotation.Nonnull final java.lang.String stringField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(ConnectItem.itemField,itemField),
			com.exedio.cope.SetValue.map(ConnectItem.itemFieldSelf,itemFieldSelf),
			com.exedio.cope.SetValue.map(ConnectItem.stringField,stringField),
		});
	}

	/**
	 * Creates a new ConnectItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected ConnectItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #itemField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final ConnectItem2 getItemField()
	{
		return ConnectItem.itemField.get(this);
	}

	/**
	 * Sets a new value for {@link #itemField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setItemField(@javax.annotation.Nonnull final ConnectItem2 itemField)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		ConnectItem.itemField.set(this,itemField);
	}

	/**
	 * Returns the value of {@link #itemFieldSelf}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final ConnectItem getItemFieldSelf()
	{
		return ConnectItem.itemFieldSelf.get(this);
	}

	/**
	 * Sets a new value for {@link #itemFieldSelf}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setItemFieldSelf(@javax.annotation.Nonnull final ConnectItem itemFieldSelf)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		ConnectItem.itemFieldSelf.set(this,itemFieldSelf);
	}

	/**
	 * Returns the value of {@link #stringField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getStringField()
	{
		return ConnectItem.stringField.get(this);
	}

	/**
	 * Sets a new value for {@link #stringField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setStringField(@javax.annotation.Nonnull final java.lang.String stringField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ConnectItem.stringField.set(this,stringField);
	}

	/**
	 * Returns the value of {@link #integerField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getIntegerField()
	{
		return ConnectItem.integerField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integerField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setIntegerField(final int integerField)
	{
		ConnectItem.integerField.set(this,integerField);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for connectItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ConnectItem> TYPE = com.exedio.cope.TypesBound.newType(ConnectItem.class,ConnectItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected ConnectItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
