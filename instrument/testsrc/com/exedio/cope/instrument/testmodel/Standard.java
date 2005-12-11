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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.LongAttribute;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.MD5Hash;
import com.exedio.cope.pattern.Media;

/**
 * @cope.persistent
 * TODO: length constraint
 * TODO: unique with multiple attributes
 * TODO: item attributes
 * TODO: enum attributes
 * TODO: functions
 * TODO: qualifiers
 */
public class Standard extends Item
{
	public static final StringAttribute defaultString = new StringAttribute(OPTIONAL);
	public static final StringAttribute notNullString = new StringAttribute(MANDATORY);
	public static final StringAttribute readOnlyString = new StringAttribute(READ_ONLY_OPTIONAL);
	public static final StringAttribute uniqueString = new StringAttribute(UNIQUE_OPTIONAL);
	/** @cope.initial */
	public static final StringAttribute initialString = new StringAttribute(OPTIONAL);

	public static final IntegerAttribute defaultInteger = new IntegerAttribute(OPTIONAL);
	public static final IntegerAttribute nativeInteger = new IntegerAttribute(MANDATORY);

	public static final LongAttribute defaultLong = new LongAttribute(OPTIONAL);
	public static final LongAttribute nativeLong = new LongAttribute(MANDATORY);

	public static final DoubleAttribute defaultDouble = new DoubleAttribute(OPTIONAL);
	public static final DoubleAttribute nativeDouble = new DoubleAttribute(MANDATORY);

	public static final BooleanAttribute defaultBoolean = new BooleanAttribute(OPTIONAL);
	public static final BooleanAttribute nativeBoolean = new BooleanAttribute(MANDATORY);

	public static final DateAttribute mandatoryDate = new DateAttribute(MANDATORY);
	private static final DateAttribute privateDate = new DateAttribute(OPTIONAL);

	private static final StringAttribute privateString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter none
	 */
	public static final StringAttribute noneGetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter private
	 */
	public static final StringAttribute privateGetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter internal
	 */
	public static final StringAttribute internalGetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.setter none
	 * @cope.getter boolean-as-is
	 */
	public static final StringAttribute noneSetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.setter private
	 * @cope.getter boolean-as-is
	 */
	public static final StringAttribute privateSetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.setter internal
	 * @cope.getter boolean-as-is
	 */
	public static final StringAttribute internalSetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter non-final
	 * @cope.setter protected
	 */
	public static final StringAttribute nonfinalGetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter protected
	 * @cope.setter non-final
	 */
	public static final StringAttribute nonfinalSetterString = new StringAttribute(OPTIONAL);

	/**
	 * @cope.getter boolean-as-is
	 */
	public static final BooleanAttribute asIsBoolean = new BooleanAttribute(OPTIONAL);
	
	public static final StringAttribute doubleUnique1 = new StringAttribute(OPTIONAL);
	public static final IntegerAttribute doubleUnique2 = new IntegerAttribute(OPTIONAL);
	public static final UniqueConstraint doubleUnique = new UniqueConstraint(doubleUnique1, doubleUnique2);
	
	public static final Media anyMedia = new Media(OPTIONAL);
	static final Media majorMedia = new Media(OPTIONAL, "major");
	protected static final Media minorMedia = new Media(OPTIONAL, "major", "minor");
	/**
	 * @cope.setter none
	 */
	public static final Media noSetterMedia = new Media(OPTIONAL);
	/**
	 * @cope.setter private
	 */
	public static final Media privateSetterMedia = new Media(OPTIONAL);
	
	public static final Hash publicHash = new MD5Hash(privateString);
	private static final Hash privateHash = new MD5Hash(defaultString);
	public static final Hash mandatoryHash = new MD5Hash(MANDATORY);
	

   /**

	 **
	 * Creates a new Standard with all the attributes initially needed.
	 * @param notNullString the initial value for attribute {@link #notNullString}.
	 * @param readOnlyString the initial value for attribute {@link #readOnlyString}.
	 * @param initialString the initial value for attribute {@link #initialString}.
	 * @param nativeInteger the initial value for attribute {@link #nativeInteger}.
	 * @param nativeLong the initial value for attribute {@link #nativeLong}.
	 * @param nativeDouble the initial value for attribute {@link #nativeDouble}.
	 * @param nativeBoolean the initial value for attribute {@link #nativeBoolean}.
	 * @param mandatoryDate the initial value for attribute {@link #mandatoryDate}.
	 * @throws com.exedio.cope.MandatoryViolationException if notNullString, mandatoryDate is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <code>@cope.constructor public|package|protected|private|none</code> in the class comment and <code>@cope.initial</code> in the comment of attributes.
	 */
	public Standard(
				final java.lang.String notNullString,
				final java.lang.String readOnlyString,
				final java.lang.String initialString,
				final int nativeInteger,
				final long nativeLong,
				final double nativeDouble,
				final boolean nativeBoolean,
				final java.util.Date mandatoryDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.AttributeValue[]{
			new com.exedio.cope.AttributeValue(Standard.notNullString,notNullString),
			new com.exedio.cope.AttributeValue(Standard.readOnlyString,readOnlyString),
			new com.exedio.cope.AttributeValue(Standard.initialString,initialString),
			new com.exedio.cope.AttributeValue(Standard.nativeInteger,new java.lang.Integer(nativeInteger)),
			new com.exedio.cope.AttributeValue(Standard.nativeLong,new java.lang.Long(nativeLong)),
			new com.exedio.cope.AttributeValue(Standard.nativeDouble,new java.lang.Double(nativeDouble)),
			new com.exedio.cope.AttributeValue(Standard.nativeBoolean,(nativeBoolean?java.lang.Boolean.TRUE:java.lang.Boolean.FALSE)),
			new com.exedio.cope.AttributeValue(Standard.mandatoryDate,mandatoryDate),
		});
		throwInitialMandatoryViolationException();
	}/**

	 **
	 * Creates a new Standard and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.generic.constructor public|package|protected|private|none</code> in the class comment.
	 */
	private Standard(final com.exedio.cope.AttributeValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private Standard(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getDefaultString()
	{
		return Standard.defaultString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDefaultString(final java.lang.String defaultString)
	{
		try
		{
			Standard.defaultString.set(this,defaultString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getNotNullString()
	{
		return Standard.notNullString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNotNullString(final java.lang.String notNullString)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			Standard.notNullString.set(this,notNullString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #readOnlyString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getReadOnlyString()
	{
		return Standard.readOnlyString.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getUniqueString()
	{
		return Standard.uniqueString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setUniqueString(final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException
	{
		try
		{
			Standard.uniqueString.set(this,uniqueString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Finds a standard by it's unique attributes.
	 * @param uniqueString shall be equal to attribute {@link #uniqueString}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public static final Standard findByUniqueString(final java.lang.String uniqueString)
	{
		return (Standard)Standard.uniqueString.searchUnique(uniqueString);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getInitialString()
	{
		return Standard.initialString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setInitialString(final java.lang.String initialString)
	{
		try
		{
			Standard.initialString.set(this,initialString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Integer getDefaultInteger()
	{
		return Standard.defaultInteger.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDefaultInteger(final java.lang.Integer defaultInteger)
	{
		try
		{
			Standard.defaultInteger.set(this,defaultInteger);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final int getNativeInteger()
	{
		return Standard.nativeInteger.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNativeInteger(final int nativeInteger)
	{
		try
		{
			Standard.nativeInteger.set(this,nativeInteger);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Long getDefaultLong()
	{
		return Standard.defaultLong.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDefaultLong(final java.lang.Long defaultLong)
	{
		try
		{
			Standard.defaultLong.set(this,defaultLong);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final long getNativeLong()
	{
		return Standard.nativeLong.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNativeLong(final long nativeLong)
	{
		try
		{
			Standard.nativeLong.set(this,nativeLong);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Double getDefaultDouble()
	{
		return Standard.defaultDouble.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDefaultDouble(final java.lang.Double defaultDouble)
	{
		try
		{
			Standard.defaultDouble.set(this,defaultDouble);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final double getNativeDouble()
	{
		return Standard.nativeDouble.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNativeDouble(final double nativeDouble)
	{
		try
		{
			Standard.nativeDouble.set(this,nativeDouble);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Boolean getDefaultBoolean()
	{
		return Standard.defaultBoolean.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDefaultBoolean(final java.lang.Boolean defaultBoolean)
	{
		try
		{
			Standard.defaultBoolean.set(this,defaultBoolean);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final boolean getNativeBoolean()
	{
		return Standard.nativeBoolean.getMandatory(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNativeBoolean(final boolean nativeBoolean)
	{
		try
		{
			Standard.nativeBoolean.set(this,nativeBoolean);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.util.Date getMandatoryDate()
	{
		return Standard.mandatoryDate.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setMandatoryDate(final java.util.Date mandatoryDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			Standard.mandatoryDate.set(this,mandatoryDate);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Sets the current date for the date attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void touchMandatoryDate()
	{
		try
		{
			Standard.mandatoryDate.touch(this);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	private final java.util.Date getPrivateDate()
	{
		return Standard.privateDate.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	private final void setPrivateDate(final java.util.Date privateDate)
	{
		try
		{
			Standard.privateDate.set(this,privateDate);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Sets the current date for the date attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private final void touchPrivateDate()
	{
		try
		{
			Standard.privateDate.touch(this);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	private final java.lang.String getPrivateString()
	{
		return Standard.privateString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	private final void setPrivateString(final java.lang.String privateString)
	{
		try
		{
			Standard.privateString.set(this,privateString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #noneGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setNoneGetterString(final java.lang.String noneGetterString)
	{
		try
		{
			Standard.noneGetterString.set(this,noneGetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	private final java.lang.String getPrivateGetterString()
	{
		return Standard.privateGetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setPrivateGetterString(final java.lang.String privateGetterString)
	{
		try
		{
			Standard.privateGetterString.set(this,privateGetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #internalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	private final java.lang.String getInternalGetterStringInternal()
	{
		return Standard.internalGetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #internalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setInternalGetterString(final java.lang.String internalGetterString)
	{
		try
		{
			Standard.internalGetterString.set(this,internalGetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #noneSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getNoneSetterString()
	{
		return Standard.noneSetterString.get(this);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getPrivateSetterString()
	{
		return Standard.privateSetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	private final void setPrivateSetterString(final java.lang.String privateSetterString)
	{
		try
		{
			Standard.privateSetterString.set(this,privateSetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #internalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getInternalSetterString()
	{
		return Standard.internalSetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #internalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	private final void setInternalSetterStringInternal(final java.lang.String internalSetterString)
	{
		try
		{
			Standard.internalSetterString.set(this,internalSetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public java.lang.String getNonfinalGetterString()
	{
		return Standard.nonfinalGetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	protected final void setNonfinalGetterString(final java.lang.String nonfinalGetterString)
	{
		try
		{
			Standard.nonfinalGetterString.set(this,nonfinalGetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	protected final java.lang.String getNonfinalSetterString()
	{
		return Standard.nonfinalSetterString.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public void setNonfinalSetterString(final java.lang.String nonfinalSetterString)
	{
		try
		{
			Standard.nonfinalSetterString.set(this,nonfinalSetterString);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Boolean isAsIsBoolean()
	{
		return Standard.asIsBoolean.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setAsIsBoolean(final java.lang.Boolean asIsBoolean)
	{
		try
		{
			Standard.asIsBoolean.set(this,asIsBoolean);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #doubleUnique1}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.String getDoubleUnique1()
	{
		return Standard.doubleUnique1.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #doubleUnique1}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDoubleUnique1(final java.lang.String doubleUnique1)
			throws
				com.exedio.cope.UniqueViolationException
	{
		try
		{
			Standard.doubleUnique1.set(this,doubleUnique1);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #doubleUnique2}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 */
	public final java.lang.Integer getDoubleUnique2()
	{
		return Standard.doubleUnique2.get(this);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #doubleUnique2}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 */
	public final void setDoubleUnique2(final java.lang.Integer doubleUnique2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		try
		{
			Standard.doubleUnique2.set(this,doubleUnique2);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Finds a standard by it's unique attributes.
	 * @param doubleUnique1 shall be equal to attribute {@link #doubleUnique1}.
	 * @param doubleUnique2 shall be equal to attribute {@link #doubleUnique2}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public static final Standard findByDoubleUnique(final java.lang.String doubleUnique1,final java.lang.Integer doubleUnique2)
	{
		return (Standard)Standard.doubleUnique.searchUnique(new Object[]{doubleUnique1,doubleUnique2});
	}/**

	 **
	 * Returns whether this media {@link #anyMedia} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean isAnyMediaNull()
	{
		return Standard.anyMedia.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #anyMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getAnyMediaURL()
	{
		return Standard.anyMedia.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #anyMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getAnyMediaMimeMajor()
	{
		return Standard.anyMedia.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #anyMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getAnyMediaMimeMinor()
	{
		return Standard.anyMedia.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #anyMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getAnyMediaContentType()
	{
		return Standard.anyMedia.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #anyMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getAnyMediaLength()
	{
		return Standard.anyMedia.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #anyMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getAnyMediaLastModified()
	{
		return Standard.anyMedia.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #anyMedia}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.io.InputStream getAnyMediaData()
	{
		return Standard.anyMedia.getData(this);
	}/**

	 **
	 * Reads data of media {@link #anyMedia}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void getAnyMediaData(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.anyMedia.getData(this,data);
	}/**

	 **
	 * Sets the new data for the media {@link #anyMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setAnyMedia(final java.io.InputStream data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.anyMedia.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Sets the new data for the media {@link #anyMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setAnyMedia(final java.io.File data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.anyMedia.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Returns whether this media {@link #majorMedia} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final boolean isMajorMediaNull()
	{
		return Standard.majorMedia.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #majorMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final java.lang.String getMajorMediaURL()
	{
		return Standard.majorMedia.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #majorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final java.lang.String getMajorMediaMimeMajor()
	{
		return Standard.majorMedia.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #majorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final java.lang.String getMajorMediaMimeMinor()
	{
		return Standard.majorMedia.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #majorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final java.lang.String getMajorMediaContentType()
	{
		return Standard.majorMedia.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #majorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final long getMajorMediaLength()
	{
		return Standard.majorMedia.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #majorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final long getMajorMediaLastModified()
	{
		return Standard.majorMedia.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #majorMedia}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final java.io.InputStream getMajorMediaData()
	{
		return Standard.majorMedia.getData(this);
	}/**

	 **
	 * Reads data of media {@link #majorMedia}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final void getMajorMediaData(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.majorMedia.getData(this,data);
	}/**

	 **
	 * Sets the new data for the media {@link #majorMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final void setMajorMedia(final java.io.InputStream data,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.majorMedia.set(this,data,null,mimeMinor);
	}/**

	 **
	 * Sets the new data for the media {@link #majorMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	final void setMajorMedia(final java.io.File data,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.majorMedia.set(this,data,null,mimeMinor);
	}/**

	 **
	 * Returns whether this media {@link #minorMedia} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final boolean isMinorMediaNull()
	{
		return Standard.minorMedia.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #minorMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final java.lang.String getMinorMediaURL()
	{
		return Standard.minorMedia.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #minorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final java.lang.String getMinorMediaMimeMajor()
	{
		return Standard.minorMedia.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #minorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final java.lang.String getMinorMediaMimeMinor()
	{
		return Standard.minorMedia.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #minorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final java.lang.String getMinorMediaContentType()
	{
		return Standard.minorMedia.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #minorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final long getMinorMediaLength()
	{
		return Standard.minorMedia.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #minorMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final long getMinorMediaLastModified()
	{
		return Standard.minorMedia.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #minorMedia}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final java.io.InputStream getMinorMediaData()
	{
		return Standard.minorMedia.getData(this);
	}/**

	 **
	 * Reads data of media {@link #minorMedia}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final void getMinorMediaData(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.minorMedia.getData(this,data);
	}/**

	 **
	 * Sets the new data for the media {@link #minorMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final void setMinorMedia(final java.io.InputStream data)
			throws
				java.io.IOException
	{
		Standard.minorMedia.set(this,data,null,null);
	}/**

	 **
	 * Sets the new data for the media {@link #minorMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	protected final void setMinorMedia(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.minorMedia.set(this,data,null,null);
	}/**

	 **
	 * Returns whether this media {@link #noSetterMedia} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean isNoSetterMediaNull()
	{
		return Standard.noSetterMedia.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #noSetterMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getNoSetterMediaURL()
	{
		return Standard.noSetterMedia.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #noSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getNoSetterMediaMimeMajor()
	{
		return Standard.noSetterMedia.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #noSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getNoSetterMediaMimeMinor()
	{
		return Standard.noSetterMedia.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #noSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getNoSetterMediaContentType()
	{
		return Standard.noSetterMedia.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #noSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getNoSetterMediaLength()
	{
		return Standard.noSetterMedia.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #noSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getNoSetterMediaLastModified()
	{
		return Standard.noSetterMedia.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #noSetterMedia}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.io.InputStream getNoSetterMediaData()
	{
		return Standard.noSetterMedia.getData(this);
	}/**

	 **
	 * Reads data of media {@link #noSetterMedia}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void getNoSetterMediaData(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.noSetterMedia.getData(this,data);
	}/**

	 **
	 * Returns whether this media {@link #privateSetterMedia} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean isPrivateSetterMediaNull()
	{
		return Standard.privateSetterMedia.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #privateSetterMedia} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getPrivateSetterMediaURL()
	{
		return Standard.privateSetterMedia.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #privateSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getPrivateSetterMediaMimeMajor()
	{
		return Standard.privateSetterMedia.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #privateSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getPrivateSetterMediaMimeMinor()
	{
		return Standard.privateSetterMedia.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #privateSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.lang.String getPrivateSetterMediaContentType()
	{
		return Standard.privateSetterMedia.getContentType(this);
	}/**

	 **
	 * Returns the data length of the media {@link #privateSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getPrivateSetterMediaLength()
	{
		return Standard.privateSetterMedia.getLength(this);
	}/**

	 **
	 * Returns the last modification date of the media {@link #privateSetterMedia}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final long getPrivateSetterMediaLastModified()
	{
		return Standard.privateSetterMedia.getLastModified(this);
	}/**

	 **
	 * Returns the data of the media {@link #privateSetterMedia}.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final java.io.InputStream getPrivateSetterMediaData()
	{
		return Standard.privateSetterMedia.getData(this);
	}/**

	 **
	 * Reads data of media {@link #privateSetterMedia}, and writes it into the given file.
	 * Does nothing, if there is no data for the media.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void getPrivateSetterMediaData(final java.io.File data)
			throws
				java.io.IOException
	{
		Standard.privateSetterMedia.getData(this,data);
	}/**

	 **
	 * Sets the new data for the media {@link #privateSetterMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private final void setPrivateSetterMedia(final java.io.InputStream data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.privateSetterMedia.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Sets the new data for the media {@link #privateSetterMedia}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private final void setPrivateSetterMedia(final java.io.File data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.privateSetterMedia.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean checkPublicHash(final java.lang.String publicHash)
	{
		return Standard.publicHash.check(this,publicHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setPublicHash(final java.lang.String publicHash)
	{
		try
		{
			Standard.publicHash.set(this,publicHash);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private final boolean checkPrivateHash(final java.lang.String privateHash)
	{
		return Standard.privateHash.check(this,privateHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private final void setPrivateHash(final java.lang.String privateHash)
	{
		try
		{
			Standard.privateHash.set(this,privateHash);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.MandatoryViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final boolean checkMandatoryHash(final java.lang.String mandatoryHash)
	{
		return Standard.mandatoryHash.check(this,mandatoryHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	public final void setMandatoryHash(final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			Standard.mandatoryHash.set(this,mandatoryHash);
		}
		catch(com.exedio.cope.LengthViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.ReadOnlyViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
		catch(com.exedio.cope.UniqueViolationException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}/**

	 **
	 * The persistent type information for standard.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.type public|package|protected|private|none</code> in the class comment.
	 */
	public static final com.exedio.cope.Type TYPE =
		new com.exedio.cope.Type(Standard.class)
;}
