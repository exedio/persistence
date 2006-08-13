/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DayAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;
import com.exedio.cope.pattern.Media;

/**
 * An item having many attributes.
 * @author Ralf Wiebicke
 */
public class AttributeItem extends Item
{
	/**
	 * A string attribute.
	 */
	public static final StringAttribute someString = new StringAttribute(OPTIONAL);

	/**
	 * Test non-persistent static final attributes.
	 */
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
	public static final StringAttribute someNotNullString = new StringAttribute();

	/**
	 * An integer attribute
	 */
	public static final IntegerAttribute someInteger = new IntegerAttribute(OPTIONAL);

	/**
	 * A mandatory integer attribute
	 */
	public static final IntegerAttribute someNotNullInteger = new IntegerAttribute();

	/**
	 * An integer attribute
	 */
	public static final LongAttribute someLong = new LongAttribute(OPTIONAL);

	/**
	 * A mandatory integer attribute
	 */
	public static final LongAttribute someNotNullLong = new LongAttribute();

	/**
	 * A double attribute
	 */
	public static final DoubleAttribute someDouble = new DoubleAttribute(OPTIONAL);

	/**
	 * A mandatory double attribute
	 */
	public static final DoubleAttribute someNotNullDouble = new DoubleAttribute();

	public static final DateAttribute someDate = new DateAttribute(OPTIONAL);

	public static final DayAttribute day = new DayAttribute(OPTIONAL);

	/**
	 * An boolean attribute
	 */
	public static final BooleanAttribute someBoolean = new BooleanAttribute(OPTIONAL);

	/**
	 * A mandatory boolean attribute
	 */
	public static final BooleanAttribute someNotNullBoolean = new BooleanAttribute();
	
	/**
	 * An attribute referencing another persistent item
	 */
	public static final ItemAttribute<EmptyItem> someItem = newItemAttribute(OPTIONAL, EmptyItem.class);

	/**
	 * An mandatory attribute referencing another persistent item
	 */
	public static final ItemAttribute<EmptyItem> someNotNullItem = newItemAttribute(EmptyItem.class);

	/**
	 * An enumeration attribute
	 */
	public static final EnumAttribute<SomeEnum> someEnum = newEnumAttribute(OPTIONAL, SomeEnum.class);

	/**
	 * A mandatory enumeration attribute
	 */
	public static final EnumAttribute<SomeEnum> someNotNullEnum = newEnumAttribute(SomeEnum.class);

	/**
	 * A data attribute.
	 */
	public static final Media someData = new Media(OPTIONAL);
	
	/**
	 * An enum for the persistent enumeration attribute {@link #someEnum}.
	 */
	public static enum SomeEnum
	{
		enumValue1,
		enumValue2,
		enumValue3;
	}


	/**

	 **
	 * Creates a new AttributeItem with all the attributes initially needed.
	 * @param someNotNullString the initial value for attribute {@link #someNotNullString}.
	 * @param someNotNullInteger the initial value for attribute {@link #someNotNullInteger}.
	 * @param someNotNullLong the initial value for attribute {@link #someNotNullLong}.
	 * @param someNotNullDouble the initial value for attribute {@link #someNotNullDouble}.
	 * @param someNotNullBoolean the initial value for attribute {@link #someNotNullBoolean}.
	 * @param someNotNullItem the initial value for attribute {@link #someNotNullItem}.
	 * @param someNotNullEnum the initial value for attribute {@link #someNotNullEnum}.
	 * @throws com.exedio.cope.LengthViolationException if someNotNullString violates its length constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if someNotNullString, someNotNullItem, someNotNullEnum is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of attributes.
	 */
	public AttributeItem(
				final java.lang.String someNotNullString,
				final int someNotNullInteger,
				final long someNotNullLong,
				final double someNotNullDouble,
				final boolean someNotNullBoolean,
				final EmptyItem someNotNullItem,
				final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.LengthViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue[]{
			AttributeItem.someNotNullString.map(someNotNullString),
			AttributeItem.someNotNullInteger.map(someNotNullInteger),
			AttributeItem.someNotNullLong.map(someNotNullLong),
			AttributeItem.someNotNullDouble.map(someNotNullDouble),
			AttributeItem.someNotNullBoolean.map(someNotNullBoolean),
			AttributeItem.someNotNullItem.map(someNotNullItem),
			AttributeItem.someNotNullEnum.map(someNotNullEnum),
		});
	}/**

	 **
	 * Creates a new AttributeItem and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private AttributeItem(final com.exedio.cope.SetValue[] setValues)
	{
		super(setValues);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private AttributeItem(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.String getSomeString()
	{
		return AttributeItem.someString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeString(final java.lang.String someString)
			throws
				com.exedio.cope.LengthViolationException
	{
		AttributeItem.someString.set(this,someString);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someStringUpperCase}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.String getSomeStringUpperCase()
	{
		return AttributeItem.someStringUpperCase.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someStringLength}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.Integer getSomeStringLength()
	{
		return AttributeItem.someStringLength.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.String getSomeNotNullString()
	{
		return AttributeItem.someNotNullString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullString(final java.lang.String someNotNullString)
			throws
				com.exedio.cope.LengthViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		AttributeItem.someNotNullString.set(this,someNotNullString);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.Integer getSomeInteger()
	{
		return AttributeItem.someInteger.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeInteger(final java.lang.Integer someInteger)
	{
		AttributeItem.someInteger.set(this,someInteger);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final int getSomeNotNullInteger()
	{
		return AttributeItem.someNotNullInteger.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullInteger(final int someNotNullInteger)
	{
		AttributeItem.someNotNullInteger.set(this,someNotNullInteger);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.Long getSomeLong()
	{
		return AttributeItem.someLong.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeLong(final java.lang.Long someLong)
	{
		AttributeItem.someLong.set(this,someLong);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final long getSomeNotNullLong()
	{
		return AttributeItem.someNotNullLong.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullLong(final long someNotNullLong)
	{
		AttributeItem.someNotNullLong.set(this,someNotNullLong);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.Double getSomeDouble()
	{
		return AttributeItem.someDouble.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeDouble(final java.lang.Double someDouble)
	{
		AttributeItem.someDouble.set(this,someDouble);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final double getSomeNotNullDouble()
	{
		return AttributeItem.someNotNullDouble.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullDouble(final double someNotNullDouble)
	{
		AttributeItem.someNotNullDouble.set(this,someNotNullDouble);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.util.Date getSomeDate()
	{
		return AttributeItem.someDate.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeDate(final java.util.Date someDate)
	{
		AttributeItem.someDate.set(this,someDate);
	}/**

	 **
	 * Sets the current date for the date attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void touchSomeDate()
	{
		AttributeItem.someDate.touch(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #day}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final com.exedio.cope.util.Day getDay()
	{
		return AttributeItem.day.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #day}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setDay(final com.exedio.cope.util.Day day)
	{
		AttributeItem.day.set(this,day);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final java.lang.Boolean getSomeBoolean()
	{
		return AttributeItem.someBoolean.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeBoolean(final java.lang.Boolean someBoolean)
	{
		AttributeItem.someBoolean.set(this,someBoolean);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final boolean getSomeNotNullBoolean()
	{
		return AttributeItem.someNotNullBoolean.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullBoolean(final boolean someNotNullBoolean)
	{
		AttributeItem.someNotNullBoolean.set(this,someNotNullBoolean);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final EmptyItem getSomeItem()
	{
		return AttributeItem.someItem.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeItem(final EmptyItem someItem)
	{
		AttributeItem.someItem.set(this,someItem);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final EmptyItem getSomeNotNullItem()
	{
		return AttributeItem.someNotNullItem.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullItem(final EmptyItem someNotNullItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AttributeItem.someNotNullItem.set(this,someNotNullItem);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final SomeEnum getSomeEnum()
	{
		return AttributeItem.someEnum.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeEnum(final SomeEnum someEnum)
	{
		AttributeItem.someEnum.set(this,someEnum);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the attribute.
	 */
	public final SomeEnum getSomeNotNullEnum()
	{
		return AttributeItem.someNotNullEnum.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setter public|package|protected|private|none|non-final</tt> in the comment of the attribute.
	 */
	public final void setSomeNotNullEnum(final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		AttributeItem.someNotNullEnum.set(this,someNotNullEnum);
	}/**

	 **
	 * Returns whether media {@link #someData} is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean isSomeDataNull()
	{
		return AttributeItem.someData.isNull(this);
	}/**

	 **
	 * Returns a URL the content of the media {@link #someData} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getSomeDataURL()
	{
		return AttributeItem.someData.getURL(this);
	}/**

	 **
	 * Returns the content type of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getSomeDataContentType()
	{
		return AttributeItem.someData.getContentType(this);
	}/**

	 **
	 * Returns the last modification date of media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getSomeDataLastModified()
	{
		return AttributeItem.someData.getLastModified(this);
	}/**

	 **
	 * Returns the body length of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getSomeDataLength()
	{
		return AttributeItem.someData.getLength(this);
	}/**

	 **
	 * Returns the body of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final byte[] getSomeDataBody()
	{
		return AttributeItem.someData.getBody(this);
	}/**

	 **
	 * Writes the body of media {@link #someData} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void getSomeDataBody(final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getBody(this,body);
	}/**

	 **
	 * Writes the body of media {@link #someData} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void getSomeDataBody(final java.io.File body)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getBody(this,body);
	}/**

	 **
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setSomeData(final byte[] body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setSomeData(final java.io.InputStream body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}/**

	 **
	 * Sets the content of media {@link #someData}.
	 * @throws java.io.IOException if accessing <tt>body</tt> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setSomeData(final java.io.File body,final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,body,contentType);
	}/**

	 **
	 * Returns the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final com.exedio.cope.testmodel.AttributeEmptyItem getEmptyItem(final EmptyItem key)
	{
		return (com.exedio.cope.testmodel.AttributeEmptyItem)com.exedio.cope.testmodel.AttributeEmptyItem.emptyItem.getQualifier(new Object[]{this,key});
	}/**

	 **
	 * Returns the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getSomeQualifiedString(final EmptyItem key)
	{
		return com.exedio.cope.testmodel.AttributeEmptyItem.emptyItem.get(com.exedio.cope.testmodel.AttributeEmptyItem.someQualifiedString,new Object[]{this,key});
	}/**

	 **
	 * Sets the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setSomeQualifiedString(final EmptyItem key,final java.lang.String someQualifiedString)
			throws
				com.exedio.cope.LengthViolationException
	{
		com.exedio.cope.testmodel.AttributeEmptyItem.emptyItem.set(com.exedio.cope.testmodel.AttributeEmptyItem.someQualifiedString,someQualifiedString,new Object[]{this,key});
	}/**

	 **
	 * The persistent type information for attributeItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	public static final com.exedio.cope.Type<AttributeItem> TYPE = newType(AttributeItem.class)
;}
