/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.EnumValue;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.function.LengthFunction;
import com.exedio.cope.function.UppercaseFunction;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.Qualifier;

/**
 * An item having many attributes.
 * @cope.persistent
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
	public static final UppercaseFunction someStringUpperCase = someString.uppercase();

	/**
	 * The length of code of the item.
	 */
	public static final LengthFunction someStringLength = someString.length();

	/**
	 * A mandatory string attribute.
	 */
	public static final StringAttribute someNotNullString = new StringAttribute(MANDATORY);

	/**
	 * An integer attribute
	 */
	public static final IntegerAttribute someInteger = new IntegerAttribute(OPTIONAL);

	/**
	 * A mandatory integer attribute
	 */
	public static final IntegerAttribute someNotNullInteger = new IntegerAttribute(MANDATORY);

	/**
	 * An integer attribute
	 */
	public static final LongAttribute someLong = new LongAttribute(OPTIONAL);

	/**
	 * A mandatory integer attribute
	 */
	public static final LongAttribute someNotNullLong = new LongAttribute(MANDATORY);

	/**
	 * A double attribute
	 */
	public static final DoubleAttribute someDouble = new DoubleAttribute(OPTIONAL);

	/**
	 * A mandatory double attribute
	 */
	public static final DoubleAttribute someNotNullDouble = new DoubleAttribute(MANDATORY);

	public static final DateAttribute someDate = new DateAttribute(OPTIONAL);

	public static final DayAttribute day = new DayAttribute(OPTIONAL);

	/**
	 * An boolean attribute
	 */
	public static final BooleanAttribute someBoolean = new BooleanAttribute(OPTIONAL);

	/**
	 * A mandatory boolean attribute
	 */
	public static final BooleanAttribute someNotNullBoolean = new BooleanAttribute(MANDATORY);
	
	/**
	 * An attribute referencing another persistent item
	 */
	public static final ItemAttribute someItem = new ItemAttribute(OPTIONAL, EmptyItem.class);

	/**
	 * An mandatory attribute referencing another persistent item
	 */
	public static final ItemAttribute someNotNullItem = new ItemAttribute(MANDATORY, EmptyItem.class);

	/**
	 * An enumeration attribute
	 */
	public static final EnumAttribute someEnum = new EnumAttribute(OPTIONAL, SomeEnum.class);

	/**
	 * A mandatory enumeration attribute
	 */
	public static final EnumAttribute someNotNullEnum = new EnumAttribute(MANDATORY, SomeEnum.class);

	/**
	 * A data attribute.
	 */
	public static final Media someData = new Media(OPTIONAL);
	
	public static final Qualifier emptyItem = new Qualifier(AttributeEmptyItem.parentKey);

	/**
	 * A class representing the possible states of the persistent enumeration attribute {@link #someEnum}.
	 */
	public static final class SomeEnum extends EnumValue
	{
		public static final int enumValue1NUM = 100;
		public static final SomeEnum enumValue1 = new SomeEnum();

		public static final int enumValue2NUM = 200;
		public static final SomeEnum enumValue2 = new SomeEnum();

		public static final int enumValue3NUM = 300;
		public static final SomeEnum enumValue3 = new SomeEnum();
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
	 * @throws com.exedio.cope.MandatoryViolationException if someNotNullString, someNotNullItem, someNotNullEnum is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <code>@cope.constructor public|package|protected|private|none</code> in the class comment and <code>@cope.initial</code> in the comment of attributes.
	 *
 */public AttributeItem(
				final java.lang.String someNotNullString,
				final int someNotNullInteger,
				final long someNotNullLong,
				final double someNotNullDouble,
				final boolean someNotNullBoolean,
				final EmptyItem someNotNullItem,
				final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.AttributeValue[]{
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullString,someNotNullString),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullInteger,new java.lang.Integer(someNotNullInteger)),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullLong,new java.lang.Long(someNotNullLong)),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullDouble,new java.lang.Double(someNotNullDouble)),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullBoolean,(someNotNullBoolean?java.lang.Boolean.TRUE:java.lang.Boolean.FALSE)),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullItem,someNotNullItem),
			new com.exedio.cope.AttributeValue(AttributeItem.someNotNullEnum,someNotNullEnum),
		});
		throwInitialMandatoryViolationException();
	}/**

	 **
	 * Creates a new AttributeItem and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.generic.constructor public|package|protected|private|none</code> in the class comment.
	 *
 */private AttributeItem(final com.exedio.cope.AttributeValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */private AttributeItem(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getSomeString()
	{
		return AttributeItem.someString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeString(final java.lang.String someString)
	{
		try
		{
			AttributeItem.someString.set(this,someString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someStringUpperCase}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getSomeStringUpperCase()
	{
		return AttributeItem.someStringUpperCase.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someStringLength}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Integer getSomeStringLength()
	{
		return AttributeItem.someStringLength.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getSomeNotNullString()
	{
		return AttributeItem.someNotNullString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullString(final java.lang.String someNotNullString)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			AttributeItem.someNotNullString.set(this,someNotNullString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Integer getSomeInteger()
	{
		return AttributeItem.someInteger.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeInteger(final java.lang.Integer someInteger)
	{
		try
		{
			AttributeItem.someInteger.set(this,someInteger);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final int getSomeNotNullInteger()
	{
		return AttributeItem.someNotNullInteger.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullInteger(final int someNotNullInteger)
	{
		try
		{
			AttributeItem.someNotNullInteger.set(this,someNotNullInteger);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Long getSomeLong()
	{
		return AttributeItem.someLong.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeLong(final java.lang.Long someLong)
	{
		try
		{
			AttributeItem.someLong.set(this,someLong);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final long getSomeNotNullLong()
	{
		return AttributeItem.someNotNullLong.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullLong(final long someNotNullLong)
	{
		try
		{
			AttributeItem.someNotNullLong.set(this,someNotNullLong);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Double getSomeDouble()
	{
		return AttributeItem.someDouble.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeDouble(final java.lang.Double someDouble)
	{
		try
		{
			AttributeItem.someDouble.set(this,someDouble);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final double getSomeNotNullDouble()
	{
		return AttributeItem.someNotNullDouble.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullDouble(final double someNotNullDouble)
	{
		try
		{
			AttributeItem.someNotNullDouble.set(this,someNotNullDouble);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.util.Date getSomeDate()
	{
		return AttributeItem.someDate.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeDate(final java.util.Date someDate)
	{
		try
		{
			AttributeItem.someDate.set(this,someDate);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Sets the current date for the date attribute {@link #someDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void touchSomeDate()
	{
		try
		{
			AttributeItem.someDate.touch(this);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #day}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final com.exedio.cope.util.Day getDay()
	{
		return AttributeItem.day.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #day}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDay(final com.exedio.cope.util.Day day)
	{
		try
		{
			AttributeItem.day.set(this,day);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Boolean getSomeBoolean()
	{
		return AttributeItem.someBoolean.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeBoolean(final java.lang.Boolean someBoolean)
	{
		try
		{
			AttributeItem.someBoolean.set(this,someBoolean);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final boolean getSomeNotNullBoolean()
	{
		return AttributeItem.someNotNullBoolean.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullBoolean(final boolean someNotNullBoolean)
	{
		try
		{
			AttributeItem.someNotNullBoolean.set(this,someNotNullBoolean);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final EmptyItem getSomeItem()
	{
		return (EmptyItem)AttributeItem.someItem.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeItem(final EmptyItem someItem)
	{
		try
		{
			AttributeItem.someItem.set(this,someItem);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final EmptyItem getSomeNotNullItem()
	{
		return (EmptyItem)AttributeItem.someNotNullItem.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullItem(final EmptyItem someNotNullItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			AttributeItem.someNotNullItem.set(this,someNotNullItem);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final SomeEnum getSomeEnum()
	{
		return (SomeEnum)AttributeItem.someEnum.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeEnum(final SomeEnum someEnum)
	{
		try
		{
			AttributeItem.someEnum.set(this,someEnum);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final SomeEnum getSomeNotNullEnum()
	{
		return (SomeEnum)AttributeItem.someNotNullEnum.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setSomeNotNullEnum(final SomeEnum someNotNullEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			AttributeItem.someNotNullEnum.set(this,someNotNullEnum);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final AttributeEmptyItem getEmptyItem(final EmptyItem key)
	{
		return (AttributeEmptyItem)emptyItem.getQualifier(new Object[]{this,key});
	}/**

	 **
	 * Returns the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getSomeQualifiedString(final EmptyItem key)
	{
		return (java.lang.String)emptyItem.get(new Object[]{this,key},AttributeEmptyItem.someQualifiedString);
	}/**

	 **
	 * Sets the qualifier.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setSomeQualifiedString(final EmptyItem key,final java.lang.String someQualifiedString)
	{
		try
		{
			AttributeEmptyItem.someQualifiedString.set(emptyItem.getForSet(new Object[]{this,key}),someQualifiedString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new com.exedio.cope.NestingRuntimeException(e);
		}
	}/**

	 **
	 * Returns whether this media {@link #someData} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean isSomeDataNull()
	{
		return AttributeItem.someData.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #someData} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getSomeDataURL()
	{
		return AttributeItem.someData.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getSomeDataMimeMajor()
	{
		return AttributeItem.someData.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getSomeDataMimeMinor()
	{
		return AttributeItem.someData.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getSomeDataContentType()
	{
		return AttributeItem.someData.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final long getSomeDataLength()
	{
		return AttributeItem.someData.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #someData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final long getSomeDataLastModified()
	{
		return AttributeItem.someData.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #someData}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.io.InputStream getSomeDataData()
	{
		return AttributeItem.someData.getData(this);
	}/**

	 **
	 * Reads data of media {@link #someData}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void getSomeDataData(final java.io.File data)
			throws
				java.io.IOException
	{
		AttributeItem.someData.getData(this,data);
	}/**

	 **
	 * Sets the new data for the media {@link #someData}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setSomeData(final java.io.InputStream data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Sets the new data for the media {@link #someData}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setSomeData(final java.io.File data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		AttributeItem.someData.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * The persistent type information for attributeItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.type public|package|protected|private|none</code> in the class comment.
	 *
 */public static final com.exedio.cope.Type TYPE =
		new com.exedio.cope.Type(AttributeItem.class)
;}
