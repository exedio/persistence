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

package com.exedio.cope.testmodel;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LengthView;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.UppercaseView;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.pattern.Media;

/**
 * An item having many attributes.
 * @author Ralf Wiebicke
 */
public final class AttributeItem extends Item
{
	/**
	 * A string attribute.
	 */
	public static final StringField someString = new StringField().optional();

	/**
	 * Test non-persistent static final attributes.
	 */
	@SuppressWarnings("unused")
	public static final String someTransientString = "transientString";

	/**
	 * The code of the item in upper case.
	 */
	public static final UppercaseView someStringUpperCase = someString.toUpperCase();

	/**
	 * The length of code of the item.
	 */
	public static final LengthView someStringLength = someString.length();

	/**
	 * A mandatory string attribute.
	 */
	public static final StringField someNotNullString = new StringField();

	/**
	 * An integer attribute
	 */
	public static final IntegerField someInteger = new IntegerField().optional();

	/**
	 * A mandatory integer attribute
	 */
	public static final IntegerField someNotNullInteger = new IntegerField();

	/**
	 * An integer attribute
	 */
	public static final LongField someLong = new LongField().optional();

	/**
	 * A mandatory integer attribute
	 */
	public static final LongField someNotNullLong = new LongField();

	/**
	 * A double attribute
	 */
	public static final DoubleField someDouble = new DoubleField().optional();

	/**
	 * A mandatory double attribute
	 */
	public static final DoubleField someNotNullDouble = new DoubleField();

	public static final DateField someDate = new DateField().optional();

	public static final DayField day = new DayField().optional();

	/**
	 * An boolean attribute
	 */
	public static final BooleanField someBoolean = new BooleanField().optional();

	/**
	 * A mandatory boolean attribute
	 */
	public static final BooleanField someNotNullBoolean = new BooleanField();

	/**
	 * An attribute referencing another persistent item
	 */
	public static final ItemField<EmptyItem> someItem = ItemField.create(EmptyItem.class).optional();

	/**
	 * An mandatory attribute referencing another persistent item
	 */
	public static final ItemField<EmptyItem> someNotNullItem = ItemField.create(EmptyItem.class);

	/**
	 * An enumeration attribute
	 */
	public static final EnumField<SomeEnum> someEnum = EnumField.create(SomeEnum.class).optional();

	/**
	 * A mandatory enumeration attribute
	 */
	public static final EnumField<SomeEnum> someNotNullEnum = EnumField.create(SomeEnum.class);

	/**
	 * A data attribute.
	 */
	@Wrapper(wrap="getURL", visibility=NONE)
	public static final Media someData = new Media().optional();

	/**
	 * An enum for the persistent enumeration attribute {@link #someEnum}.
	 */
	public enum SomeEnum
	{
		enumValue1{@Override @SuppressWarnings("unused") void zack(){/*empty*/}},
		enumValue2{@Override @SuppressWarnings("unused") void zack(){/*empty*/}},
		enumValue3{@Override @SuppressWarnings("unused") void zack(){/*empty*/}};

		@SuppressWarnings("unused") abstract void zack();
	}


	/**
	 * Creates a new AttributeItem with all the fields initially needed.
	 * @param someNotNullString the initial value for field {@link #someNotNullString}.
	 * @param someNotNullInteger the initial value for field {@link #someNotNullInteger}.
	 * @param someNotNullLong the initial value for field {@link #someNotNullLong}.
	 * @param someNotNullDouble the initial value for field {@link #someNotNullDouble}.
	 * @param someNotNullBoolean the initial value for field {@link #someNotNullBoolean}.
	 * @param someNotNullItem the initial value for field {@link #someNotNullItem}.
	 * @param someNotNullEnum the initial value for field {@link #someNotNullEnum}.
	 * @throws com.exedio.cope.MandatoryViolationException if someNotNullString, someNotNullItem, someNotNullEnum is null.
	 * @throws com.exedio.cope.StringLengthViolationException if someNotNullString violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public AttributeItem(
				@javax.annotation.Nonnull final java.lang.String someNotNullString,
				final int someNotNullInteger,
				final long someNotNullLong,
				final double someNotNullDouble,
				final boolean someNotNullBoolean,
				@javax.annotation.Nonnull final EmptyItem someNotNullItem,
				@javax.annotation.Nonnull final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AttributeItem.someNotNullString.map(someNotNullString),
			AttributeItem.someNotNullInteger.map(someNotNullInteger),
			AttributeItem.someNotNullLong.map(someNotNullLong),
			AttributeItem.someNotNullDouble.map(someNotNullDouble),
			AttributeItem.someNotNullBoolean.map(someNotNullBoolean),
			AttributeItem.someNotNullItem.map(someNotNullItem),
			AttributeItem.someNotNullEnum.map(someNotNullEnum),
		});
	}

	/**
	 * Creates a new AttributeItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AttributeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #someString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.String getSomeString()
	{
		return AttributeItem.someString.get(this);
	}

	/**
	 * Sets a new value for {@link #someString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeString(@javax.annotation.Nullable final java.lang.String someString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		AttributeItem.someString.set(this,someString);
	}

	/**
	 * Returns the value of {@link #someStringUpperCase}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public java.lang.String getSomeStringUpperCase()
	{
		return AttributeItem.someStringUpperCase.get(this);
	}

	/**
	 * Returns the value of {@link #someStringLength}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public java.lang.Integer getSomeStringLength()
	{
		return AttributeItem.someStringLength.get(this);
	}

	/**
	 * Returns the value of {@link #someNotNullString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public java.lang.String getSomeNotNullString()
	{
		return AttributeItem.someNotNullString.get(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullString(@javax.annotation.Nonnull final java.lang.String someNotNullString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		AttributeItem.someNotNullString.set(this,someNotNullString);
	}

	/**
	 * Returns the value of {@link #someInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getSomeInteger()
	{
		return AttributeItem.someInteger.get(this);
	}

	/**
	 * Sets a new value for {@link #someInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeInteger(@javax.annotation.Nullable final java.lang.Integer someInteger)
	{
		AttributeItem.someInteger.set(this,someInteger);
	}

	/**
	 * Returns the value of {@link #someNotNullInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public int getSomeNotNullInteger()
	{
		return AttributeItem.someNotNullInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullInteger(final int someNotNullInteger)
	{
		AttributeItem.someNotNullInteger.set(this,someNotNullInteger);
	}

	/**
	 * Returns the value of {@link #someLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Long getSomeLong()
	{
		return AttributeItem.someLong.get(this);
	}

	/**
	 * Sets a new value for {@link #someLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeLong(@javax.annotation.Nullable final java.lang.Long someLong)
	{
		AttributeItem.someLong.set(this,someLong);
	}

	/**
	 * Returns the value of {@link #someNotNullLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public long getSomeNotNullLong()
	{
		return AttributeItem.someNotNullLong.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullLong(final long someNotNullLong)
	{
		AttributeItem.someNotNullLong.set(this,someNotNullLong);
	}

	/**
	 * Returns the value of {@link #someDouble}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getSomeDouble()
	{
		return AttributeItem.someDouble.get(this);
	}

	/**
	 * Sets a new value for {@link #someDouble}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeDouble(@javax.annotation.Nullable final java.lang.Double someDouble)
	{
		AttributeItem.someDouble.set(this,someDouble);
	}

	/**
	 * Returns the value of {@link #someNotNullDouble}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public double getSomeNotNullDouble()
	{
		return AttributeItem.someNotNullDouble.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullDouble}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullDouble(final double someNotNullDouble)
	{
		AttributeItem.someNotNullDouble.set(this,someNotNullDouble);
	}

	/**
	 * Returns the value of {@link #someDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.util.Date getSomeDate()
	{
		return AttributeItem.someDate.get(this);
	}

	/**
	 * Sets a new value for {@link #someDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeDate(@javax.annotation.Nullable final java.util.Date someDate)
	{
		AttributeItem.someDate.set(this,someDate);
	}

	/**
	 * Sets the current date for the date field {@link #someDate}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	public void touchSomeDate()
	{
		AttributeItem.someDate.touch(this);
	}

	/**
	 * Returns the value of {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public com.exedio.cope.util.Day getDay()
	{
		return AttributeItem.day.get(this);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setDay(@javax.annotation.Nullable final com.exedio.cope.util.Day day)
	{
		AttributeItem.day.set(this,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	public void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		AttributeItem.day.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #someBoolean}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Boolean getSomeBoolean()
	{
		return AttributeItem.someBoolean.get(this);
	}

	/**
	 * Sets a new value for {@link #someBoolean}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeBoolean(@javax.annotation.Nullable final java.lang.Boolean someBoolean)
	{
		AttributeItem.someBoolean.set(this,someBoolean);
	}

	/**
	 * Returns the value of {@link #someNotNullBoolean}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public boolean getSomeNotNullBoolean()
	{
		return AttributeItem.someNotNullBoolean.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullBoolean}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullBoolean(final boolean someNotNullBoolean)
	{
		AttributeItem.someNotNullBoolean.set(this,someNotNullBoolean);
	}

	/**
	 * Returns the value of {@link #someItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public EmptyItem getSomeItem()
	{
		return AttributeItem.someItem.get(this);
	}

	/**
	 * Sets a new value for {@link #someItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeItem(@javax.annotation.Nullable final EmptyItem someItem)
	{
		AttributeItem.someItem.set(this,someItem);
	}

	/**
	 * Returns the value of {@link #someNotNullItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public EmptyItem getSomeNotNullItem()
	{
		return AttributeItem.someNotNullItem.get(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullItem(@javax.annotation.Nonnull final EmptyItem someNotNullItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AttributeItem.someNotNullItem.set(this,someNotNullItem);
	}

	/**
	 * Returns the value of {@link #someEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public SomeEnum getSomeEnum()
	{
		return AttributeItem.someEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #someEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeEnum(@javax.annotation.Nullable final SomeEnum someEnum)
	{
		AttributeItem.someEnum.set(this,someEnum);
	}

	/**
	 * Returns the value of {@link #someNotNullEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public SomeEnum getSomeNotNullEnum()
	{
		return AttributeItem.someNotNullEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #someNotNullEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeNotNullEnum(@javax.annotation.Nonnull final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AttributeItem.someNotNullEnum.set(this,someNotNullEnum);
	}

	/**
	 * Returns a Locator the content of {@link #someData} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	public com.exedio.cope.pattern.MediaPath.Locator getSomeDataLocator()
	{
		return AttributeItem.someData.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #someData}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	public java.lang.String getSomeDataContentType()
	{
		return AttributeItem.someData.getContentType(this);
	}

	/**
	 * Returns whether media {@link #someData} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	public boolean isSomeDataNull()
	{
		return AttributeItem.someData.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #someData}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	public java.util.Date getSomeDataLastModified()
	{
		return AttributeItem.someData.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #someData}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	public long getSomeDataLength()
	{
		return AttributeItem.someData.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #someData}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	public byte[] getSomeDataBody()
	{
		return AttributeItem.someData.getBody(this);
	}

	/**
	 * Writes the body of media {@link #someData} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	public void getSomeDataBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #someData} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	public void getSomeDataBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #someData} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	public void getSomeDataBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeData(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value someData)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,someData);
	}

	/**
	 * Sets the content of media {@link #someData}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeData(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AttributeItem.someData.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeData(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeData(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSomeData(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for attributeItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<AttributeItem> TYPE = com.exedio.cope.TypesBound.newType(AttributeItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AttributeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
