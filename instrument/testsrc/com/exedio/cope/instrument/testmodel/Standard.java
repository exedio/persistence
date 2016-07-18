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
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.testmodel.EnumContainer.Enum4;
import com.exedio.cope.pattern.Hash;
import com.exedio.cope.pattern.MessageDigestHash;

/**
 * TODO: length constraint
 * TODO: unique with multiple attributes
 * TODO: item attributes
 * TODO: functions
 * TODO: qualifiers
 */
public final class Standard extends Item
{
	public static final StringField defaultString = new StringField().optional();
	public static final StringField notNullString = new StringField();
	public static final StringField finalString = new StringField().toFinal().optional();
	public static final StringField defaultToString = new StringField().defaultTo("defaultConstant for finalDefaultString");
	// this fails in Generics#remove:
	// private static final StringField defaultToLessThanSignString = new StringField().lengthMax(256).defaultTo("<");
	private static final StringField defaultToEscapedLessThanSignString = new StringField().lengthMax(256).defaultTo("\u003C");

	public static final StringField uniqueString = new StringField().optional().unique();
	/** @cope.initial */
	public static final StringField initialString = new StringField().optional();

	public static final IntegerField defaultInteger = new IntegerField().optional();
	public static final IntegerField nativeInteger = new IntegerField();

	public static final LongField defaultLong = new LongField().optional();
	public static final LongField nativeLong = new LongField();

	public static final DoubleField defaultDouble = new DoubleField().optional();
	public static final DoubleField nativeDouble = new DoubleField();

	public static final BooleanField defaultBoolean = new BooleanField().optional();
	public static final BooleanField nativeBoolean = new BooleanField();

	public static final DateField mandatoryDate = new DateField();
	private static final DateField privateDate = new DateField().optional();
	public static final DateField nowDate = new DateField().defaultToNow();

	public static final EnumField<Enum1> mandatoryEnum = EnumField.create(Enum1.class).defaultTo(Enum1.ENUM1A);
	protected static final EnumField<Enum2> protectedEnum = EnumField.create(Enum2.class).optional().defaultTo(Enum2.ENUM2A);
	protected static final EnumField<EnumContainer.Enum3> externalEnum = EnumField.create(EnumContainer.Enum3.class).optional().defaultTo(EnumContainer.Enum3.ENUM3A);
	protected static final EnumField<Enum4> shortExternalEnum = EnumField.create(Enum4.class).optional().defaultTo(Enum4.ENUM4A);

	public static final IntegerField
		inlineA = new IntegerField().defaultTo(0),
		inlineB = new IntegerField().defaultTo(1);

	public enum Enum1
	{
		ENUM1A, ENUM1B;
	}

	private static final StringField privateString = new StringField().optional();

	/**
	 * @cope.get none
	 */
	public static final StringField noneGetterString = new StringField().optional();

	/**
	 * @cope.get private
	 */
	public static final StringField privateGetterString = new StringField().optional();

	/**
	 * @cope.get internal
	 */
	public static final StringField internalGetterString = new StringField().optional();

	/**
	 * @cope.set none
	 * @cope.get boolean-as-is
	 */
	public static final StringField noneSetterString = new StringField().optional();

	/**
	 * @cope.set private
	 * @cope.get boolean-as-is
	 */
	public static final StringField privateSetterString = new StringField().optional();

	/**
	 * @cope.set internal
	 * @cope.get boolean-as-is
	 */
	public static final StringField internalSetterString = new StringField().optional();

	/**
	 * @cope.get non-final
	 * @cope.set protected
	 */
	public static final StringField nonfinalGetterString = new StringField().optional();

	/**
	 * @cope.get protected
	 * @cope.set non-final
	 */
	public static final StringField nonfinalSetterString = new StringField().optional();

	/**
	 * @cope.get boolean-as-is
	 */
	public static final BooleanField asIsBoolean = new BooleanField().optional();

	public static final StringField doubleUnique1 = new StringField().optional();
	public static final IntegerField doubleUnique2 = new IntegerField().optional();
	public static final UniqueConstraint doubleUnique = new UniqueConstraint(doubleUnique1, doubleUnique2);

	/** @cope.ignore */
	public static final StringField ignoreString = new StringField().optional();

	private static String brokenFunction()
	{
		return "broken";
	}

	/**
	 * @cope.ignore
	 */
	public static final StringField brokenString = new StringField().defaultTo(brokenFunction());

	static final DoubleField defaultFeature = new DoubleField().optional().unique().range(1.0, 2.0);

	public static final Hash publicHash = new Hash(MessageDigestHash.algorithm(5)).optional();
	private static final Hash privateHash = new Hash(MessageDigestHash.algorithm(5)).optional();
	public static final Hash mandatoryHash = new Hash(MessageDigestHash.algorithm(5));
	/**
	 * @cope.set private
	 */
	public static final Hash privateSetterHash = new Hash(MessageDigestHash.algorithm(5));

	/**
	 * An upper-case attribute
	 */
	static final StringField XMLReader = new StringField().optional();

	/**
	 * Some other variable
	 */
	private static final String SUPER = "duper";

	void useFeaturesToAvoidWarning()
	{
		System.out.println(SUPER);
		checkPrivateHash(null);
		getInternalGetterStringInternal();
		getInternalSetterString();
		getPrivateDate();
		getPrivateGetterString();
		getPrivateHashSHA512s8i5();
		getPrivateString();
		setInternalSetterStringInternal(null);
		setPrivateDate(null);
		setPrivateHash(null);
		setPrivateHashSHA512s8i5(null);
		blindPrivateHash(null);
		setPrivateSetterHash(null);
		setPrivateSetterString(null);
		setPrivateString(null);
		touchPrivateDate();
	}


   /**

	 **
	 * Creates a new Standard with all the fields initially needed.
	 * @param notNullString the initial value for field {@link #notNullString}.
	 * @param finalString the initial value for field {@link #finalString}.
	 * @param initialString the initial value for field {@link #initialString}.
	 * @param nativeInteger the initial value for field {@link #nativeInteger}.
	 * @param nativeLong the initial value for field {@link #nativeLong}.
	 * @param nativeDouble the initial value for field {@link #nativeDouble}.
	 * @param nativeBoolean the initial value for field {@link #nativeBoolean}.
	 * @param mandatoryDate the initial value for field {@link #mandatoryDate}.
	 * @param mandatoryHash the initial value for field {@link #mandatoryHash}.
	 * @param privateSetterHash the initial value for field {@link #privateSetterHash}.
	 * @throws com.exedio.cope.MandatoryViolationException if notNullString, mandatoryDate, mandatoryHash, privateSetterHash is null.
	 * @throws com.exedio.cope.StringLengthViolationException if notNullString, finalString, initialString violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public Standard(
				@javax.annotation.Nonnull final java.lang.String notNullString,
				@javax.annotation.Nullable final java.lang.String finalString,
				@javax.annotation.Nullable final java.lang.String initialString,
				final int nativeInteger,
				final long nativeLong,
				final double nativeDouble,
				final boolean nativeBoolean,
				@javax.annotation.Nonnull final java.util.Date mandatoryDate,
				@javax.annotation.Nonnull final java.lang.String mandatoryHash,
				@javax.annotation.Nonnull final java.lang.String privateSetterHash)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			Standard.notNullString.map(notNullString),
			Standard.finalString.map(finalString),
			Standard.initialString.map(initialString),
			Standard.nativeInteger.map(nativeInteger),
			Standard.nativeLong.map(nativeLong),
			Standard.nativeDouble.map(nativeDouble),
			Standard.nativeBoolean.map(nativeBoolean),
			Standard.mandatoryDate.map(mandatoryDate),
			Standard.mandatoryHash.map(mandatoryHash),
			Standard.privateSetterHash.map(privateSetterHash),
		});
	}/**

	 **
	 * Creates a new Standard and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private Standard(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getDefaultString()
	{
		return Standard.defaultString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultString(@javax.annotation.Nullable final java.lang.String defaultString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultString.set(this,defaultString);
	}/**

	 **
	 * Returns the value of {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.lang.String getNotNullString()
	{
		return Standard.notNullString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #notNullString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNotNullString(@javax.annotation.Nonnull final java.lang.String notNullString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.notNullString.set(this,notNullString);
	}/**

	 **
	 * Returns the value of {@link #finalString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getFinalString()
	{
		return Standard.finalString.get(this);
	}/**

	 **
	 * Returns the value of {@link #defaultToString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.lang.String getDefaultToString()
	{
		return Standard.defaultToString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultToString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultToString(@javax.annotation.Nonnull final java.lang.String defaultToString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultToString.set(this,defaultToString);
	}/**

	 **
	 * Returns the value of {@link #defaultToEscapedLessThanSignString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	private final java.lang.String getDefaultToEscapedLessThanSignString()
	{
		return Standard.defaultToEscapedLessThanSignString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultToEscapedLessThanSignString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setDefaultToEscapedLessThanSignString(@javax.annotation.Nonnull final java.lang.String defaultToEscapedLessThanSignString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultToEscapedLessThanSignString.set(this,defaultToEscapedLessThanSignString);
	}/**

	 **
	 * Returns the value of {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getUniqueString()
	{
		return Standard.uniqueString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #uniqueString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.uniqueString.set(this,uniqueString);
	}/**

	 **
	 * Finds a standard by it's {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.for public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public static final Standard forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return Standard.uniqueString.searchUnique(Standard.class,uniqueString);
	}/**

	 **
	 * Returns the value of {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getInitialString()
	{
		return Standard.initialString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #initialString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setInitialString(@javax.annotation.Nullable final java.lang.String initialString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.initialString.set(this,initialString);
	}/**

	 **
	 * Returns the value of {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Integer getDefaultInteger()
	{
		return Standard.defaultInteger.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultInteger(@javax.annotation.Nullable final java.lang.Integer defaultInteger)
	{
		Standard.defaultInteger.set(this,defaultInteger);
	}/**

	 **
	 * Returns the value of {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final int getNativeInteger()
	{
		return Standard.nativeInteger.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #nativeInteger}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNativeInteger(final int nativeInteger)
	{
		Standard.nativeInteger.set(this,nativeInteger);
	}/**

	 **
	 * Returns the value of {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Long getDefaultLong()
	{
		return Standard.defaultLong.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultLong(@javax.annotation.Nullable final java.lang.Long defaultLong)
	{
		Standard.defaultLong.set(this,defaultLong);
	}/**

	 **
	 * Returns the value of {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final long getNativeLong()
	{
		return Standard.nativeLong.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #nativeLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNativeLong(final long nativeLong)
	{
		Standard.nativeLong.set(this,nativeLong);
	}/**

	 **
	 * Returns the value of {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Double getDefaultDouble()
	{
		return Standard.defaultDouble.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultDouble(@javax.annotation.Nullable final java.lang.Double defaultDouble)
	{
		Standard.defaultDouble.set(this,defaultDouble);
	}/**

	 **
	 * Returns the value of {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final double getNativeDouble()
	{
		return Standard.nativeDouble.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #nativeDouble}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNativeDouble(final double nativeDouble)
	{
		Standard.nativeDouble.set(this,nativeDouble);
	}/**

	 **
	 * Returns the value of {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Boolean getDefaultBoolean()
	{
		return Standard.defaultBoolean.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDefaultBoolean(@javax.annotation.Nullable final java.lang.Boolean defaultBoolean)
	{
		Standard.defaultBoolean.set(this,defaultBoolean);
	}/**

	 **
	 * Returns the value of {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final boolean getNativeBoolean()
	{
		return Standard.nativeBoolean.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #nativeBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNativeBoolean(final boolean nativeBoolean)
	{
		Standard.nativeBoolean.set(this,nativeBoolean);
	}/**

	 **
	 * Returns the value of {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.util.Date getMandatoryDate()
	{
		return Standard.mandatoryDate.get(this);
	}/**

	 **
	 * Sets a new value for {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setMandatoryDate(@javax.annotation.Nonnull final java.util.Date mandatoryDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryDate.set(this,mandatoryDate);
	}/**

	 **
	 * Sets the current date for the date field {@link #mandatoryDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.touch public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void touchMandatoryDate()
	{
		Standard.mandatoryDate.touch(this);
	}/**

	 **
	 * Returns the value of {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	private final java.util.Date getPrivateDate()
	{
		return Standard.privateDate.get(this);
	}/**

	 **
	 * Sets a new value for {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateDate(@javax.annotation.Nullable final java.util.Date privateDate)
	{
		Standard.privateDate.set(this,privateDate);
	}/**

	 **
	 * Sets the current date for the date field {@link #privateDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.touch public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void touchPrivateDate()
	{
		Standard.privateDate.touch(this);
	}/**

	 **
	 * Returns the value of {@link #nowDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.util.Date getNowDate()
	{
		return Standard.nowDate.get(this);
	}/**

	 **
	 * Sets a new value for {@link #nowDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNowDate(@javax.annotation.Nonnull final java.util.Date nowDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.nowDate.set(this,nowDate);
	}/**

	 **
	 * Sets the current date for the date field {@link #nowDate}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.touch public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void touchNowDate()
	{
		Standard.nowDate.touch(this);
	}/**

	 **
	 * Returns the value of {@link #mandatoryEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final Enum1 getMandatoryEnum()
	{
		return Standard.mandatoryEnum.get(this);
	}/**

	 **
	 * Sets a new value for {@link #mandatoryEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setMandatoryEnum(@javax.annotation.Nonnull final Enum1 mandatoryEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryEnum.set(this,mandatoryEnum);
	}/**

	 **
	 * Returns the value of {@link #protectedEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	protected final Enum2 getProtectedEnum()
	{
		return Standard.protectedEnum.get(this);
	}/**

	 **
	 * Sets a new value for {@link #protectedEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected final void setProtectedEnum(@javax.annotation.Nullable final Enum2 protectedEnum)
	{
		Standard.protectedEnum.set(this,protectedEnum);
	}/**

	 **
	 * Returns the value of {@link #externalEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	protected final EnumContainer.Enum3 getExternalEnum()
	{
		return Standard.externalEnum.get(this);
	}/**

	 **
	 * Sets a new value for {@link #externalEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected final void setExternalEnum(@javax.annotation.Nullable final EnumContainer.Enum3 externalEnum)
	{
		Standard.externalEnum.set(this,externalEnum);
	}/**

	 **
	 * Returns the value of {@link #shortExternalEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	protected final Enum4 getShortExternalEnum()
	{
		return Standard.shortExternalEnum.get(this);
	}/**

	 **
	 * Sets a new value for {@link #shortExternalEnum}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected final void setShortExternalEnum(@javax.annotation.Nullable final Enum4 shortExternalEnum)
	{
		Standard.shortExternalEnum.set(this,shortExternalEnum);
	}/**

	 **
	 * Returns the value of {@link #inlineA}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final int getInlineA()
	{
		return Standard.inlineA.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #inlineA}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setInlineA(final int inlineA)
	{
		Standard.inlineA.set(this,inlineA);
	}/**

	 **
	 * Returns the value of {@link #inlineB}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final int getInlineB()
	{
		return Standard.inlineB.getMandatory(this);
	}/**

	 **
	 * Sets a new value for {@link #inlineB}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setInlineB(final int inlineB)
	{
		Standard.inlineB.set(this,inlineB);
	}/**

	 **
	 * Returns the value of {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	private final java.lang.String getPrivateString()
	{
		return Standard.privateString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #privateString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateString(@javax.annotation.Nullable final java.lang.String privateString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateString.set(this,privateString);
	}/**

	 **
	 * Sets a new value for {@link #noneGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setNoneGetterString(@javax.annotation.Nullable final java.lang.String noneGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.noneGetterString.set(this,noneGetterString);
	}/**

	 **
	 * Returns the value of {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	private final java.lang.String getPrivateGetterString()
	{
		return Standard.privateGetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #privateGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setPrivateGetterString(@javax.annotation.Nullable final java.lang.String privateGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateGetterString.set(this,privateGetterString);
	}/**

	 **
	 * Returns the value of {@link #internalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	private final java.lang.String getInternalGetterStringInternal()
	{
		return Standard.internalGetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #internalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setInternalGetterString(@javax.annotation.Nullable final java.lang.String internalGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalGetterString.set(this,internalGetterString);
	}/**

	 **
	 * Returns the value of {@link #noneSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getNoneSetterString()
	{
		return Standard.noneSetterString.get(this);
	}/**

	 **
	 * Returns the value of {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getPrivateSetterString()
	{
		return Standard.privateSetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #privateSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateSetterString(@javax.annotation.Nullable final java.lang.String privateSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateSetterString.set(this,privateSetterString);
	}/**

	 **
	 * Returns the value of {@link #internalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getInternalSetterString()
	{
		return Standard.internalSetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #internalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setInternalSetterStringInternal(@javax.annotation.Nullable final java.lang.String internalSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalSetterString.set(this,internalSetterString);
	}/**

	 **
	 * Returns the value of {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public java.lang.String getNonfinalGetterString()
	{
		return Standard.nonfinalGetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #nonfinalGetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected final void setNonfinalGetterString(@javax.annotation.Nullable final java.lang.String nonfinalGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.nonfinalGetterString.set(this,nonfinalGetterString);
	}/**

	 **
	 * Returns the value of {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	protected final java.lang.String getNonfinalSetterString()
	{
		return Standard.nonfinalSetterString.get(this);
	}/**

	 **
	 * Sets a new value for {@link #nonfinalSetterString}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public void setNonfinalSetterString(@javax.annotation.Nullable final java.lang.String nonfinalSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.nonfinalSetterString.set(this,nonfinalSetterString);
	}/**

	 **
	 * Returns the value of {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Boolean isAsIsBoolean()
	{
		return Standard.asIsBoolean.get(this);
	}/**

	 **
	 * Sets a new value for {@link #asIsBoolean}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setAsIsBoolean(@javax.annotation.Nullable final java.lang.Boolean asIsBoolean)
	{
		Standard.asIsBoolean.set(this,asIsBoolean);
	}/**

	 **
	 * Returns the value of {@link #doubleUnique1}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getDoubleUnique1()
	{
		return Standard.doubleUnique1.get(this);
	}/**

	 **
	 * Sets a new value for {@link #doubleUnique1}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDoubleUnique1(@javax.annotation.Nullable final java.lang.String doubleUnique1)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.doubleUnique1.set(this,doubleUnique1);
	}/**

	 **
	 * Returns the value of {@link #doubleUnique2}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.Integer getDoubleUnique2()
	{
		return Standard.doubleUnique2.get(this);
	}/**

	 **
	 * Sets a new value for {@link #doubleUnique2}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setDoubleUnique2(@javax.annotation.Nullable final java.lang.Integer doubleUnique2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		Standard.doubleUnique2.set(this,doubleUnique2);
	}/**

	 **
	 * Finds a standard by it's unique fields.
	 * @param doubleUnique1 shall be equal to field {@link #doubleUnique1}.
	 * @param doubleUnique2 shall be equal to field {@link #doubleUnique2}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.finder public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public static final Standard forDoubleUnique(@javax.annotation.Nonnull final java.lang.String doubleUnique1,@javax.annotation.Nonnull final java.lang.Integer doubleUnique2)
	{
		return Standard.doubleUnique.search(Standard.class,doubleUnique1,doubleUnique2);
	}/**

	 **
	 * Returns the value of {@link #defaultFeature}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final java.lang.Double get()
	{
		return Standard.defaultFeature.get(this);
	}/**

	 **
	 * Sets a new value for {@link #defaultFeature}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void set(@javax.annotation.Nullable final java.lang.Double defaultFeature)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.DoubleRangeViolationException
	{
		Standard.defaultFeature.set(this,defaultFeature);
	}/**

	 **
	 * Finds a standard by it's {@link #defaultFeature}.
	 * @param defaultFeature shall be equal to field {@link #defaultFeature}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.for public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	static final Standard forDefaultFeature(@javax.annotation.Nonnull final java.lang.Double defaultFeature)
	{
		return Standard.defaultFeature.searchUnique(Standard.class,defaultFeature);
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.check public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final boolean checkPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		return Standard.publicHash.check(this,publicHash);
	}/**

	 **
	 * Wastes (almost) as much cpu cycles, as a call to <tt>checkPublicHash</tt> would have needed.
	 * Needed to prevent Timing Attacks.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.blind public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final void blindPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.blind(publicHash);
	}/**

	 **
	 * Sets a new value for {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.set(this,publicHash);
	}/**

	 **
	 * Returns the encoded hash value for hash {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	public final java.lang.String getPublicHashSHA512s8i5()
	{
		return Standard.publicHash.getHash(this);
	}/**

	 **
	 * Sets the encoded hash value for hash {@link #publicHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setPublicHashSHA512s8i5(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.setHash(this,publicHash);
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.check public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final boolean checkPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		return Standard.privateHash.check(this,privateHash);
	}/**

	 **
	 * Wastes (almost) as much cpu cycles, as a call to <tt>checkPrivateHash</tt> would have needed.
	 * Needed to prevent Timing Attacks.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.blind public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final void blindPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.blind(privateHash);
	}/**

	 **
	 * Sets a new value for {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.set(this,privateHash);
	}/**

	 **
	 * Returns the encoded hash value for hash {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	private final java.lang.String getPrivateHashSHA512s8i5()
	{
		return Standard.privateHash.getHash(this);
	}/**

	 **
	 * Sets the encoded hash value for hash {@link #privateHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateHashSHA512s8i5(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.setHash(this,privateHash);
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.check public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final boolean checkMandatoryHash(@javax.annotation.Nullable final java.lang.String mandatoryHash)
	{
		return Standard.mandatoryHash.check(this,mandatoryHash);
	}/**

	 **
	 * Wastes (almost) as much cpu cycles, as a call to <tt>checkMandatoryHash</tt> would have needed.
	 * Needed to prevent Timing Attacks.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.blind public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final void blindMandatoryHash(@javax.annotation.Nullable final java.lang.String mandatoryHash)
	{
		Standard.mandatoryHash.blind(mandatoryHash);
	}/**

	 **
	 * Sets a new value for {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setMandatoryHash(@javax.annotation.Nonnull final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryHash.set(this,mandatoryHash);
	}/**

	 **
	 * Returns the encoded hash value for hash {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.lang.String getMandatoryHashSHA512s8i5()
	{
		return Standard.mandatoryHash.getHash(this);
	}/**

	 **
	 * Sets the encoded hash value for hash {@link #mandatoryHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setMandatoryHashSHA512s8i5(@javax.annotation.Nonnull final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryHash.setHash(this,mandatoryHash);
	}/**

	 **
	 * Returns whether the given value corresponds to the hash in {@link #privateSetterHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.check public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final boolean checkPrivateSetterHash(@javax.annotation.Nullable final java.lang.String privateSetterHash)
	{
		return Standard.privateSetterHash.check(this,privateSetterHash);
	}/**

	 **
	 * Wastes (almost) as much cpu cycles, as a call to <tt>checkPrivateSetterHash</tt> would have needed.
	 * Needed to prevent Timing Attacks.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.blind public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final void blindPrivateSetterHash(@javax.annotation.Nullable final java.lang.String privateSetterHash)
	{
		Standard.privateSetterHash.blind(privateSetterHash);
	}/**

	 **
	 * Sets a new value for {@link #privateSetterHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private final void setPrivateSetterHash(@javax.annotation.Nonnull final java.lang.String privateSetterHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.privateSetterHash.set(this,privateSetterHash);
	}/**

	 **
	 * Returns the encoded hash value for hash {@link #privateSetterHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	public final java.lang.String getPrivateSetterHashSHA512s8i5()
	{
		return Standard.privateSetterHash.getHash(this);
	}/**

	 **
	 * Sets the encoded hash value for hash {@link #privateSetterHash}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setSHA512s8i5 public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final void setPrivateSetterHashSHA512s8i5(@javax.annotation.Nonnull final java.lang.String privateSetterHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.privateSetterHash.setHash(this,privateSetterHash);
	}/**

	 **
	 * Returns the value of {@link #XMLReader}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final java.lang.String getXMLReader()
	{
		return Standard.XMLReader.get(this);
	}/**

	 **
	 * Sets a new value for {@link #XMLReader}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setXMLReader(@javax.annotation.Nullable final java.lang.String XMLReader)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.XMLReader.set(this,XMLReader);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for standard.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public static final com.exedio.cope.Type<Standard> TYPE = com.exedio.cope.TypesBound.newType(Standard.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private Standard(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
