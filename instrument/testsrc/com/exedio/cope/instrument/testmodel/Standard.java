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
 * TODO: creation constructor with different exceptions
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
	
	public static final Media anyData = new Media(OPTIONAL);

	public static final Media majorData = new Media(OPTIONAL, "major");
	
	public static final Media minorData = new Media(OPTIONAL, "major", "minor");
	
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
	 *
 */public Standard(
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
	 *
 */private Standard(final com.exedio.cope.AttributeValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */private Standard(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getDefaultString()
	{
		return (java.lang.String)get(Standard.defaultString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDefaultString(final java.lang.String defaultString)
	{
		try
		{
			set(Standard.defaultString,defaultString);
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
	 * Returns whether the given value corresponds to the hash in {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */private final boolean checkPrivateHash(final java.lang.String privateHash)
	{
		return Standard.privateHash.check(this,privateHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */private final void setPrivateHash(final java.lang.String privateHash)
	{
		try
		{
			Standard.privateHash.set(this,privateHash);
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
	 * Returns the value of the persistent attribute {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getNotNullString()
	{
		return (java.lang.String)get(Standard.notNullString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNotNullString(final java.lang.String notNullString)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			set(Standard.notNullString,notNullString);
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
	 * Returns the value of the persistent attribute {@link #readOnlyString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getReadOnlyString()
	{
		return (java.lang.String)get(Standard.readOnlyString);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getUniqueString()
	{
		return (java.lang.String)get(Standard.uniqueString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setUniqueString(final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException
	{
		try
		{
			set(Standard.uniqueString,uniqueString);
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
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getInitialString()
	{
		return (java.lang.String)get(Standard.initialString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setInitialString(final java.lang.String initialString)
	{
		try
		{
			set(Standard.initialString,initialString);
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
	 * Returns the value of the persistent attribute {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Integer getDefaultInteger()
	{
		return (java.lang.Integer)get(Standard.defaultInteger);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDefaultInteger(final java.lang.Integer defaultInteger)
	{
		try
		{
			set(Standard.defaultInteger,defaultInteger);
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
	 * Returns the value of the persistent attribute {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final int getNativeInteger()
	{
		return ((java.lang.Integer)get(Standard.nativeInteger)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNativeInteger(final int nativeInteger)
	{
		try
		{
			set(Standard.nativeInteger,new java.lang.Integer(nativeInteger));
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
	 * Returns the value of the persistent attribute {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Long getDefaultLong()
	{
		return (java.lang.Long)get(Standard.defaultLong);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDefaultLong(final java.lang.Long defaultLong)
	{
		try
		{
			set(Standard.defaultLong,defaultLong);
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
	 * Returns the value of the persistent attribute {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final long getNativeLong()
	{
		return ((java.lang.Long)get(Standard.nativeLong)).longValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNativeLong(final long nativeLong)
	{
		try
		{
			set(Standard.nativeLong,new java.lang.Long(nativeLong));
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
	 * Returns the value of the persistent attribute {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Double getDefaultDouble()
	{
		return (java.lang.Double)get(Standard.defaultDouble);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDefaultDouble(final java.lang.Double defaultDouble)
	{
		try
		{
			set(Standard.defaultDouble,defaultDouble);
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
	 * Returns the value of the persistent attribute {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final double getNativeDouble()
	{
		return ((java.lang.Double)get(Standard.nativeDouble)).doubleValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNativeDouble(final double nativeDouble)
	{
		try
		{
			set(Standard.nativeDouble,new java.lang.Double(nativeDouble));
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
	 * Returns the value of the persistent attribute {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Boolean getDefaultBoolean()
	{
		return (java.lang.Boolean)get(Standard.defaultBoolean);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setDefaultBoolean(final java.lang.Boolean defaultBoolean)
	{
		try
		{
			set(Standard.defaultBoolean,defaultBoolean);
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
	 * Returns the value of the persistent attribute {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final boolean getNativeBoolean()
	{
		return ((java.lang.Boolean)get(Standard.nativeBoolean)).booleanValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNativeBoolean(final boolean nativeBoolean)
	{
		try
		{
			set(Standard.nativeBoolean,(nativeBoolean?java.lang.Boolean.TRUE:java.lang.Boolean.FALSE));
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
	 * Returns the value of the persistent attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.util.Date getMandatoryDate()
	{
		return (java.util.Date)get(Standard.mandatoryDate);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setMandatoryDate(final java.util.Date mandatoryDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			set(Standard.mandatoryDate,mandatoryDate);
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
	 * Sets the current date for the date attribute {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void touchMandatoryDate()
	{
		try
		{
			touch(Standard.mandatoryDate);
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
	 * Returns the value of the persistent attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */private final java.util.Date getPrivateDate()
	{
		return (java.util.Date)get(Standard.privateDate);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */private final void setPrivateDate(final java.util.Date privateDate)
	{
		try
		{
			set(Standard.privateDate,privateDate);
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
	 * Sets the current date for the date attribute {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */private final void touchPrivateDate()
	{
		try
		{
			touch(Standard.privateDate);
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
	 * Returns the value of the persistent attribute {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */private final java.lang.String getPrivateString()
	{
		return (java.lang.String)get(Standard.privateString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */private final void setPrivateString(final java.lang.String privateString)
	{
		try
		{
			set(Standard.privateString,privateString);
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
	 * Returns whether the given value corresponds to the hash in {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean checkPublicHash(final java.lang.String publicHash)
	{
		return Standard.publicHash.check(this,publicHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setPublicHash(final java.lang.String publicHash)
	{
		try
		{
			Standard.publicHash.set(this,publicHash);
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
	 * Sets a new value for the persistent attribute {@link #noneGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setNoneGetterString(final java.lang.String noneGetterString)
	{
		try
		{
			set(Standard.noneGetterString,noneGetterString);
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
	 * Returns the value of the persistent attribute {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */private final java.lang.String getPrivateGetterString()
	{
		return (java.lang.String)get(Standard.privateGetterString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setPrivateGetterString(final java.lang.String privateGetterString)
	{
		try
		{
			set(Standard.privateGetterString,privateGetterString);
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
	 * Returns the value of the persistent attribute {@link #noneSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getNoneSetterString()
	{
		return (java.lang.String)get(Standard.noneSetterString);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.String getPrivateSetterString()
	{
		return (java.lang.String)get(Standard.privateSetterString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */private final void setPrivateSetterString(final java.lang.String privateSetterString)
	{
		try
		{
			set(Standard.privateSetterString,privateSetterString);
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
	 * Returns the value of the persistent attribute {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public java.lang.String getNonfinalGetterString()
	{
		return (java.lang.String)get(Standard.nonfinalGetterString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */protected final void setNonfinalGetterString(final java.lang.String nonfinalGetterString)
	{
		try
		{
			set(Standard.nonfinalGetterString,nonfinalGetterString);
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
	 * Returns the value of the persistent attribute {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */protected final java.lang.String getNonfinalSetterString()
	{
		return (java.lang.String)get(Standard.nonfinalSetterString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public void setNonfinalSetterString(final java.lang.String nonfinalSetterString)
	{
		try
		{
			set(Standard.nonfinalSetterString,nonfinalSetterString);
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
	 * Returns the value of the persistent attribute {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.getter public|package|protected|private|none|non-final|boolean-as-is</code> in the comment of the attribute.
	 *
 */public final java.lang.Boolean isAsIsBoolean()
	{
		return (java.lang.Boolean)get(Standard.asIsBoolean);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.setter public|package|protected|private|none|non-final</code> in the comment of the attribute.
	 *
 */public final void setAsIsBoolean(final java.lang.Boolean asIsBoolean)
	{
		try
		{
			set(Standard.asIsBoolean,asIsBoolean);
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
	 * Returns whether the given value corresponds to the hash in {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean checkMandatoryHash(final java.lang.String mandatoryHash)
	{
		return Standard.mandatoryHash.check(this,mandatoryHash);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setMandatoryHash(final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		try
		{
			Standard.mandatoryHash.set(this,mandatoryHash);
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
	 * Finds a standard by it's unique attributes.
	 * @param uniqueString shall be equal to attribute {@link #uniqueString}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public static final Standard findByUniqueString(final java.lang.String uniqueString)
	{
		return (Standard)Standard.uniqueString.searchUnique(uniqueString);
	}/**

	 **
	 * Returns whether this media {@link #anyData} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean isAnyDataNull()
	{
		return Standard.anyData.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #anyData} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getAnyDataURL()
	{
		return Standard.anyData.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #anyData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getAnyDataMimeMajor()
	{
		return Standard.anyData.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #anyData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getAnyDataMimeMinor()
	{
		return Standard.anyData.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #anyData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getAnyDataContentType()
	{
		return Standard.anyData.getContentType(this);
	}/**

	 **
	 * Returns the data of the media {@link #anyData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.io.InputStream getAnyDataData()
	{
		return Standard.anyData.getData(this);
	}/**

	 **
	 * Sets the new data for the media {@link #anyData}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setAnyData(final java.io.InputStream data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.anyData.set(this,data,mimeMajor,mimeMinor);
	}/**

	 **
	 * Returns whether this media {@link #majorData} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean isMajorDataNull()
	{
		return Standard.majorData.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #majorData} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMajorDataURL()
	{
		return Standard.majorData.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #majorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMajorDataMimeMajor()
	{
		return Standard.majorData.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #majorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMajorDataMimeMinor()
	{
		return Standard.majorData.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #majorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMajorDataContentType()
	{
		return Standard.majorData.getContentType(this);
	}/**

	 **
	 * Returns the data of the media {@link #majorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.io.InputStream getMajorDataData()
	{
		return Standard.majorData.getData(this);
	}/**

	 **
	 * Sets the new data for the media {@link #majorData}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setMajorData(final java.io.InputStream data,final java.lang.String mimeMinor)
			throws
				java.io.IOException
	{
		Standard.majorData.set(this,data,null,mimeMinor);
	}/**

	 **
	 * Returns whether this media {@link #minorData} has data available.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final boolean isMinorDataNull()
	{
		return Standard.minorData.isNull(this);
	}/**

	 **
	 * Returns a URL the data of the media {@link #minorData} is available under.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMinorDataURL()
	{
		return Standard.minorData.getURL(this);
	}/**

	 **
	 * Returns the major mime type of the media {@link #minorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMinorDataMimeMajor()
	{
		return Standard.minorData.getMimeMajor(this);
	}/**

	 **
	 * Returns the minor mime type of the media {@link #minorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMinorDataMimeMinor()
	{
		return Standard.minorData.getMimeMinor(this);
	}/**

	 **
	 * Returns the content type of the media {@link #minorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.lang.String getMinorDataContentType()
	{
		return Standard.minorData.getContentType(this);
	}/**

	 **
	 * Returns the data of the media {@link #minorData}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final java.io.InputStream getMinorDataData()
	{
		return Standard.minorData.getData(this);
	}/**

	 **
	 * Sets the new data for the media {@link #minorData}.
	 * @throws java.io.IOException if accessing <code>data</code> throws an IOException.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *
 */public final void setMinorData(final java.io.InputStream data)
			throws
				java.io.IOException
	{
		Standard.minorData.set(this,data,null,null);
	}/**

	 **
	 * The persistent type information for standard.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <code>@cope.type public|package|protected|private|none</code> in the class comment.
	 *
 */public static final com.exedio.cope.Type TYPE =
		new com.exedio.cope.Type(Standard.class)
;}
