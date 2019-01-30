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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.UniqueConstraint;

final class WrapPrimitiveItem extends Item
{
	static final IntegerField integer = new IntegerField();
	static final DateField date = new DateField();
	static final IntegerField integerOptional = new IntegerField().optional();
	static final UniqueConstraint mixed = new UniqueConstraint(integer, date, integerOptional);

	static final BooleanField booleanField = new BooleanField();
	static final LongField longField = new LongField();
	static final DoubleField doubleField = new DoubleField();
	static final UniqueConstraint primitive = new UniqueConstraint(booleanField, integer, longField, doubleField);

	/**
	 * Creates a new WrapPrimitiveItem with all the fields initially needed.
	 * @param integer the initial value for field {@link #integer}.
	 * @param date the initial value for field {@link #date}.
	 * @param booleanField the initial value for field {@link #booleanField}.
	 * @param longField the initial value for field {@link #longField}.
	 * @param doubleField the initial value for field {@link #doubleField}.
	 * @throws com.exedio.cope.MandatoryViolationException if date is null.
	 * @throws com.exedio.cope.UniqueViolationException if integer, date, booleanField, longField, doubleField is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	WrapPrimitiveItem(
				final int integer,
				@javax.annotation.Nonnull final java.util.Date date,
				final boolean booleanField,
				final long longField,
				final double doubleField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			WrapPrimitiveItem.integer.map(integer),
			WrapPrimitiveItem.date.map(date),
			WrapPrimitiveItem.booleanField.map(booleanField),
			WrapPrimitiveItem.longField.map(longField),
			WrapPrimitiveItem.doubleField.map(doubleField),
		});
	}

	/**
	 * Creates a new WrapPrimitiveItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private WrapPrimitiveItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getInteger()
	{
		return WrapPrimitiveItem.integer.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger(final int integer)
			throws
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.integer.set(this,integer);
	}

	/**
	 * Returns the value of {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getDate()
	{
		return WrapPrimitiveItem.date.get(this);
	}

	/**
	 * Sets a new value for {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDate(@javax.annotation.Nonnull final java.util.Date date)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.date.set(this,date);
	}

	/**
	 * Sets the current date for the date field {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDate()
	{
		WrapPrimitiveItem.date.touch(this);
	}

	/**
	 * Returns the value of {@link #integerOptional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntegerOptional()
	{
		return WrapPrimitiveItem.integerOptional.get(this);
	}

	/**
	 * Sets a new value for {@link #integerOptional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntegerOptional(@javax.annotation.Nullable final java.lang.Integer integerOptional)
			throws
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.integerOptional.set(this,integerOptional);
	}

	/**
	 * Finds a wrapPrimitiveItem by it's unique fields.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param date shall be equal to field {@link #date}.
	 * @param integerOptional shall be equal to field {@link #integerOptional}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static WrapPrimitiveItem forMixed(final int integer,@javax.annotation.Nonnull final java.util.Date date,@javax.annotation.Nonnull final java.lang.Integer integerOptional)
	{
		return WrapPrimitiveItem.mixed.search(WrapPrimitiveItem.class,integer,date,integerOptional);
	}

	/**
	 * Finds a wrapPrimitiveItem by its unique fields.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param date shall be equal to field {@link #date}.
	 * @param integerOptional shall be equal to field {@link #integerOptional}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static WrapPrimitiveItem forMixedStrict(final int integer,@javax.annotation.Nonnull final java.util.Date date,@javax.annotation.Nonnull final java.lang.Integer integerOptional)
			throws
				java.lang.IllegalArgumentException
	{
		return WrapPrimitiveItem.mixed.searchStrict(WrapPrimitiveItem.class,integer,date,integerOptional);
	}

	/**
	 * Returns the value of {@link #booleanField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getBooleanField()
	{
		return WrapPrimitiveItem.booleanField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanField(final boolean booleanField)
			throws
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.booleanField.set(this,booleanField);
	}

	/**
	 * Returns the value of {@link #longField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	long getLongField()
	{
		return WrapPrimitiveItem.longField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #longField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLongField(final long longField)
			throws
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.longField.set(this,longField);
	}

	/**
	 * Returns the value of {@link #doubleField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	double getDoubleField()
	{
		return WrapPrimitiveItem.doubleField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #doubleField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDoubleField(final double doubleField)
			throws
				com.exedio.cope.UniqueViolationException
	{
		WrapPrimitiveItem.doubleField.set(this,doubleField);
	}

	/**
	 * Finds a wrapPrimitiveItem by it's unique fields.
	 * @param booleanField shall be equal to field {@link #booleanField}.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param longField shall be equal to field {@link #longField}.
	 * @param doubleField shall be equal to field {@link #doubleField}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static WrapPrimitiveItem forPrimitive(final boolean booleanField,final int integer,final long longField,final double doubleField)
	{
		return WrapPrimitiveItem.primitive.search(WrapPrimitiveItem.class,booleanField,integer,longField,doubleField);
	}

	/**
	 * Finds a wrapPrimitiveItem by its unique fields.
	 * @param booleanField shall be equal to field {@link #booleanField}.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param longField shall be equal to field {@link #longField}.
	 * @param doubleField shall be equal to field {@link #doubleField}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static WrapPrimitiveItem forPrimitiveStrict(final boolean booleanField,final int integer,final long longField,final double doubleField)
			throws
				java.lang.IllegalArgumentException
	{
		return WrapPrimitiveItem.primitive.searchStrict(WrapPrimitiveItem.class,booleanField,integer,longField,doubleField);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapPrimitiveItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapPrimitiveItem> TYPE = com.exedio.cope.TypesBound.newType(WrapPrimitiveItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private WrapPrimitiveItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
