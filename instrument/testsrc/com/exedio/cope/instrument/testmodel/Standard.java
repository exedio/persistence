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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
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
@SuppressWarnings({"ProtectedMemberInFinalClass", "UnusedReturnValue", "EmptyClassInitializer"})
public final class Standard extends Item
{
	public static final StringField defaultString = new StringField().optional();
	public static final StringField notNullString = new StringField();
	public static final StringField finalString = new StringField().toFinal().optional();
	public static final StringField defaultToString = new StringField().defaultTo("defaultConstant for finalDefaultString");
	private static final StringField defaultToLessThanSignString = new StringField().lengthMax(256).defaultTo("<");
	@SuppressWarnings("UnnecessaryUnicodeEscape") // OK: testing escape sequence
	private static final StringField defaultToEscapedLessThanSignString = new StringField().lengthMax(256).defaultTo("\u003C");
	private static final StringField lessThanSignInCommentString = new StringField(/*<*/).optional();

	public static final StringField uniqueString = new StringField().optional().unique();
	@WrapperInitial
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

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	public enum Enum1
	{
		ENUM1A, ENUM1B
	}

	private static final StringField privateString = new StringField().optional();

	@Wrapper(wrap="get", visibility=NONE)
	public static final StringField noneGetterString = new StringField().optional();

	@Wrapper(wrap="get", visibility=PRIVATE)
	public static final StringField privateGetterString = new StringField().optional();

	@Wrapper(wrap="get", internal=true)
	public static final StringField internalGetterString = new StringField().optional();

	@Wrapper(wrap="get", internal=true)
	public static final StringField internalGetterStringAnnotated = new StringField().optional();

	@Wrapper(wrap="get", internal=true, visibility=PACKAGE)
	public static final StringField internalPackageGetterStringAnnotated = new StringField().optional();

	@Wrapper(wrap="set", visibility=NONE)
	@Wrapper(wrap="get", booleanAsIs=true)
	public static final StringField noneSetterString = new StringField().optional();

	@Wrapper(wrap="set", visibility=PRIVATE)
	@Wrapper(wrap="get", booleanAsIs=true)
	public static final StringField privateSetterString = new StringField().optional();

	@Wrapper(wrap="set", internal=true)
	@Wrapper(wrap="get", booleanAsIs=true)
	public static final StringField internalSetterString = new StringField().optional();

	@Wrapper(wrap="get", asFinal=false)
	@Wrapper(wrap="set", visibility=PROTECTED)
	public static final StringField nonfinalGetterString = new StringField().optional();

	@Wrapper(wrap="get", visibility=PROTECTED)
	@Wrapper(wrap="set", asFinal=false)
	public static final StringField nonfinalSetterString = new StringField().optional();

	@Wrapper(wrap="get", booleanAsIs=true)
	public static final BooleanField asIsBoolean = new BooleanField().optional();

	public static final StringField doubleUnique1 = new StringField().optional();
	public static final IntegerField doubleUnique2 = new IntegerField().optional();
	public static final UniqueConstraint doubleUnique = UniqueConstraint.create(doubleUnique1, doubleUnique2);

	@WrapperIgnore
	@SuppressWarnings("unused") // OK: test bad API usage
	public static final StringField ignoreString = new StringField().optional();

	private static String brokenFunction()
	{
		return "broken";
	}

	@WrapperIgnore
	@SuppressWarnings("unused") // OK: test bad API usage
	public static final StringField brokenString = new StringField().defaultTo(brokenFunction());

	static final DoubleField defaultFeature = new DoubleField().optional().unique().range(1.0, 2.0);

	public static final Hash publicHash = new Hash(MessageDigestHash.algorithm(5)).optional();
	private static final Hash privateHash = new Hash(MessageDigestHash.algorithm(5)).optional();
	public static final Hash mandatoryHash = new Hash(MessageDigestHash.algorithm(5));
	@Wrapper(wrap="set", visibility=PRIVATE)
	public static final Hash privateSetterHash = new Hash(MessageDigestHash.algorithm(5));

	/**
	 * An upper-case attribute
	 */
	static final StringField XMLReader = new StringField().optional();

	/**
	 *  fully-qualified variable type
	 */
	public static final com.exedio.cope.StringField stringFullyQualified = new StringField().optional();

	/**
	 * Some other variable
	 */
	private static final String SUPER = "duper";

	@SuppressWarnings("unused") // OK: just for testing instrumentor
	void useFeaturesToAvoidWarning()
	{
		System.out.println(SUPER);
		checkPrivateHash(null);
		getInternalGetterStringInternal();
		getInternalGetterStringAnnotatedInternal();
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

	{
		// test non-static initializer
	}


	/**
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
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
			com.exedio.cope.SetValue.map(Standard.notNullString,notNullString),
			com.exedio.cope.SetValue.map(Standard.finalString,finalString),
			com.exedio.cope.SetValue.map(Standard.initialString,initialString),
			com.exedio.cope.SetValue.map(Standard.nativeInteger,nativeInteger),
			com.exedio.cope.SetValue.map(Standard.nativeLong,nativeLong),
			com.exedio.cope.SetValue.map(Standard.nativeDouble,nativeDouble),
			com.exedio.cope.SetValue.map(Standard.nativeBoolean,nativeBoolean),
			com.exedio.cope.SetValue.map(Standard.mandatoryDate,mandatoryDate),
			com.exedio.cope.SetValue.map(Standard.mandatoryHash,mandatoryHash),
			com.exedio.cope.SetValue.map(Standard.privateSetterHash,privateSetterHash),
		});
	}

	/**
	 * Creates a new Standard and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private Standard(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #defaultString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getDefaultString()
	{
		return Standard.defaultString.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultString(@javax.annotation.Nullable final java.lang.String defaultString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultString.set(this,defaultString);
	}

	/**
	 * Returns the value of {@link #notNullString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getNotNullString()
	{
		return Standard.notNullString.get(this);
	}

	/**
	 * Sets a new value for {@link #notNullString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNotNullString(@javax.annotation.Nonnull final java.lang.String notNullString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.notNullString.set(this,notNullString);
	}

	/**
	 * Returns the value of {@link #finalString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getFinalString()
	{
		return Standard.finalString.get(this);
	}

	/**
	 * Returns the value of {@link #defaultToString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getDefaultToString()
	{
		return Standard.defaultToString.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultToString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultToString(@javax.annotation.Nonnull final java.lang.String defaultToString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultToString.set(this,defaultToString);
	}

	/**
	 * Returns the value of {@link #defaultToLessThanSignString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private java.lang.String getDefaultToLessThanSignString()
	{
		return Standard.defaultToLessThanSignString.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultToLessThanSignString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setDefaultToLessThanSignString(@javax.annotation.Nonnull final java.lang.String defaultToLessThanSignString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultToLessThanSignString.set(this,defaultToLessThanSignString);
	}

	/**
	 * Returns the value of {@link #defaultToEscapedLessThanSignString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private java.lang.String getDefaultToEscapedLessThanSignString()
	{
		return Standard.defaultToEscapedLessThanSignString.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultToEscapedLessThanSignString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setDefaultToEscapedLessThanSignString(@javax.annotation.Nonnull final java.lang.String defaultToEscapedLessThanSignString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.defaultToEscapedLessThanSignString.set(this,defaultToEscapedLessThanSignString);
	}

	/**
	 * Returns the value of {@link #lessThanSignInCommentString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getLessThanSignInCommentString()
	{
		return Standard.lessThanSignInCommentString.get(this);
	}

	/**
	 * Sets a new value for {@link #lessThanSignInCommentString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setLessThanSignInCommentString(@javax.annotation.Nullable final java.lang.String lessThanSignInCommentString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.lessThanSignInCommentString.set(this,lessThanSignInCommentString);
	}

	/**
	 * Returns the value of {@link #uniqueString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getUniqueString()
	{
		return Standard.uniqueString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.uniqueString.set(this,uniqueString);
	}

	/**
	 * Finds a standard by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static Standard forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return Standard.uniqueString.searchUnique(Standard.class,uniqueString);
	}

	/**
	 * Finds a standard by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static Standard forUniqueStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueString)
			throws
				java.lang.IllegalArgumentException
	{
		return Standard.uniqueString.searchUniqueStrict(Standard.class,uniqueString);
	}

	/**
	 * Returns the value of {@link #initialString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getInitialString()
	{
		return Standard.initialString.get(this);
	}

	/**
	 * Sets a new value for {@link #initialString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInitialString(@javax.annotation.Nullable final java.lang.String initialString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.initialString.set(this,initialString);
	}

	/**
	 * Returns the value of {@link #defaultInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getDefaultInteger()
	{
		return Standard.defaultInteger.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultInteger(@javax.annotation.Nullable final java.lang.Integer defaultInteger)
	{
		Standard.defaultInteger.set(this,defaultInteger);
	}

	/**
	 * Returns the value of {@link #nativeInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public int getNativeInteger()
	{
		return Standard.nativeInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nativeInteger}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNativeInteger(final int nativeInteger)
	{
		Standard.nativeInteger.set(this,nativeInteger);
	}

	/**
	 * Returns the value of {@link #defaultLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getDefaultLong()
	{
		return Standard.defaultLong.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultLong(@javax.annotation.Nullable final java.lang.Long defaultLong)
	{
		Standard.defaultLong.set(this,defaultLong);
	}

	/**
	 * Returns the value of {@link #nativeLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public long getNativeLong()
	{
		return Standard.nativeLong.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nativeLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNativeLong(final long nativeLong)
	{
		Standard.nativeLong.set(this,nativeLong);
	}

	/**
	 * Returns the value of {@link #defaultDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getDefaultDouble()
	{
		return Standard.defaultDouble.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultDouble(@javax.annotation.Nullable final java.lang.Double defaultDouble)
	{
		Standard.defaultDouble.set(this,defaultDouble);
	}

	/**
	 * Returns the value of {@link #nativeDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public double getNativeDouble()
	{
		return Standard.nativeDouble.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nativeDouble}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNativeDouble(final double nativeDouble)
	{
		Standard.nativeDouble.set(this,nativeDouble);
	}

	/**
	 * Returns the value of {@link #defaultBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Boolean getDefaultBoolean()
	{
		return Standard.defaultBoolean.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDefaultBoolean(@javax.annotation.Nullable final java.lang.Boolean defaultBoolean)
	{
		Standard.defaultBoolean.set(this,defaultBoolean);
	}

	/**
	 * Returns the value of {@link #nativeBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean getNativeBoolean()
	{
		return Standard.nativeBoolean.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nativeBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNativeBoolean(final boolean nativeBoolean)
	{
		Standard.nativeBoolean.set(this,nativeBoolean);
	}

	/**
	 * Returns the value of {@link #mandatoryDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.util.Date getMandatoryDate()
	{
		return Standard.mandatoryDate.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatoryDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatoryDate(@javax.annotation.Nonnull final java.util.Date mandatoryDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryDate.set(this,mandatoryDate);
	}

	/**
	 * Sets the current date for the date field {@link #mandatoryDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void touchMandatoryDate()
	{
		Standard.mandatoryDate.touch(this);
	}

	/**
	 * Returns the value of {@link #privateDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.util.Date getPrivateDate()
	{
		return Standard.privateDate.get(this);
	}

	/**
	 * Sets a new value for {@link #privateDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateDate(@javax.annotation.Nullable final java.util.Date privateDate)
	{
		Standard.privateDate.set(this,privateDate);
	}

	/**
	 * Sets the current date for the date field {@link #privateDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void touchPrivateDate()
	{
		Standard.privateDate.touch(this);
	}

	/**
	 * Returns the value of {@link #nowDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.util.Date getNowDate()
	{
		return Standard.nowDate.get(this);
	}

	/**
	 * Sets a new value for {@link #nowDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNowDate(@javax.annotation.Nonnull final java.util.Date nowDate)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.nowDate.set(this,nowDate);
	}

	/**
	 * Sets the current date for the date field {@link #nowDate}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void touchNowDate()
	{
		Standard.nowDate.touch(this);
	}

	/**
	 * Returns the value of {@link #mandatoryEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public Enum1 getMandatoryEnum()
	{
		return Standard.mandatoryEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatoryEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatoryEnum(@javax.annotation.Nonnull final Enum1 mandatoryEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryEnum.set(this,mandatoryEnum);
	}

	/**
	 * Returns the value of {@link #protectedEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	protected Enum2 getProtectedEnum()
	{
		return Standard.protectedEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #protectedEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void setProtectedEnum(@javax.annotation.Nullable final Enum2 protectedEnum)
	{
		Standard.protectedEnum.set(this,protectedEnum);
	}

	/**
	 * Returns the value of {@link #externalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	protected EnumContainer.Enum3 getExternalEnum()
	{
		return Standard.externalEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #externalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void setExternalEnum(@javax.annotation.Nullable final EnumContainer.Enum3 externalEnum)
	{
		Standard.externalEnum.set(this,externalEnum);
	}

	/**
	 * Returns the value of {@link #shortExternalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	protected Enum4 getShortExternalEnum()
	{
		return Standard.shortExternalEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #shortExternalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void setShortExternalEnum(@javax.annotation.Nullable final Enum4 shortExternalEnum)
	{
		Standard.shortExternalEnum.set(this,shortExternalEnum);
	}

	/**
	 * Returns the value of {@link #inlineA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public int getInlineA()
	{
		return Standard.inlineA.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #inlineA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInlineA(final int inlineA)
	{
		Standard.inlineA.set(this,inlineA);
	}

	/**
	 * Returns the value of {@link #inlineB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public int getInlineB()
	{
		return Standard.inlineB.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #inlineB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInlineB(final int inlineB)
	{
		Standard.inlineB.set(this,inlineB);
	}

	/**
	 * Returns the value of {@link #privateString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getPrivateString()
	{
		return Standard.privateString.get(this);
	}

	/**
	 * Sets a new value for {@link #privateString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateString(@javax.annotation.Nullable final java.lang.String privateString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateString.set(this,privateString);
	}

	/**
	 * Sets a new value for {@link #noneGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNoneGetterString(@javax.annotation.Nullable final java.lang.String noneGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.noneGetterString.set(this,noneGetterString);
	}

	/**
	 * Returns the value of {@link #privateGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getPrivateGetterString()
	{
		return Standard.privateGetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #privateGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setPrivateGetterString(@javax.annotation.Nullable final java.lang.String privateGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateGetterString.set(this,privateGetterString);
	}

	/**
	 * Returns the value of {@link #internalGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getInternalGetterStringInternal()
	{
		return Standard.internalGetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #internalGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInternalGetterString(@javax.annotation.Nullable final java.lang.String internalGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalGetterString.set(this,internalGetterString);
	}

	/**
	 * Returns the value of {@link #internalGetterStringAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getInternalGetterStringAnnotatedInternal()
	{
		return Standard.internalGetterStringAnnotated.get(this);
	}

	/**
	 * Sets a new value for {@link #internalGetterStringAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInternalGetterStringAnnotated(@javax.annotation.Nullable final java.lang.String internalGetterStringAnnotated)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalGetterStringAnnotated.set(this,internalGetterStringAnnotated);
	}

	/**
	 * Returns the value of {@link #internalPackageGetterStringAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getInternalPackageGetterStringAnnotatedInternal()
	{
		return Standard.internalPackageGetterStringAnnotated.get(this);
	}

	/**
	 * Sets a new value for {@link #internalPackageGetterStringAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setInternalPackageGetterStringAnnotated(@javax.annotation.Nullable final java.lang.String internalPackageGetterStringAnnotated)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalPackageGetterStringAnnotated.set(this,internalPackageGetterStringAnnotated);
	}

	/**
	 * Returns the value of {@link #noneSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getNoneSetterString()
	{
		return Standard.noneSetterString.get(this);
	}

	/**
	 * Returns the value of {@link #privateSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getPrivateSetterString()
	{
		return Standard.privateSetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #privateSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateSetterString(@javax.annotation.Nullable final java.lang.String privateSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.privateSetterString.set(this,privateSetterString);
	}

	/**
	 * Returns the value of {@link #internalSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getInternalSetterString()
	{
		return Standard.internalSetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #internalSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setInternalSetterStringInternal(@javax.annotation.Nullable final java.lang.String internalSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.internalSetterString.set(this,internalSetterString);
	}

	/**
	 * Returns the value of {@link #nonfinalGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getNonfinalGetterString()
	{
		return Standard.nonfinalGetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #nonfinalGetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected void setNonfinalGetterString(@javax.annotation.Nullable final java.lang.String nonfinalGetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.nonfinalGetterString.set(this,nonfinalGetterString);
	}

	/**
	 * Returns the value of {@link #nonfinalSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	protected java.lang.String getNonfinalSetterString()
	{
		return Standard.nonfinalSetterString.get(this);
	}

	/**
	 * Sets a new value for {@link #nonfinalSetterString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNonfinalSetterString(@javax.annotation.Nullable final java.lang.String nonfinalSetterString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.nonfinalSetterString.set(this,nonfinalSetterString);
	}

	/**
	 * Returns the value of {@link #asIsBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Boolean isAsIsBoolean()
	{
		return Standard.asIsBoolean.get(this);
	}

	/**
	 * Sets a new value for {@link #asIsBoolean}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setAsIsBoolean(@javax.annotation.Nullable final java.lang.Boolean asIsBoolean)
	{
		Standard.asIsBoolean.set(this,asIsBoolean);
	}

	/**
	 * Returns the value of {@link #doubleUnique1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getDoubleUnique1()
	{
		return Standard.doubleUnique1.get(this);
	}

	/**
	 * Sets a new value for {@link #doubleUnique1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDoubleUnique1(@javax.annotation.Nullable final java.lang.String doubleUnique1)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Standard.doubleUnique1.set(this,doubleUnique1);
	}

	/**
	 * Returns the value of {@link #doubleUnique2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getDoubleUnique2()
	{
		return Standard.doubleUnique2.get(this);
	}

	/**
	 * Sets a new value for {@link #doubleUnique2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setDoubleUnique2(@javax.annotation.Nullable final java.lang.Integer doubleUnique2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		Standard.doubleUnique2.set(this,doubleUnique2);
	}

	/**
	 * Finds a standard by it's unique fields.
	 * @param doubleUnique1 shall be equal to field {@link #doubleUnique1}.
	 * @param doubleUnique2 shall be equal to field {@link #doubleUnique2}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static Standard forDoubleUnique(@javax.annotation.Nonnull final java.lang.String doubleUnique1,@javax.annotation.Nonnull final java.lang.Integer doubleUnique2)
	{
		return Standard.doubleUnique.search(Standard.class,doubleUnique1,doubleUnique2);
	}

	/**
	 * Finds a standard by its unique fields.
	 * @param doubleUnique1 shall be equal to field {@link #doubleUnique1}.
	 * @param doubleUnique2 shall be equal to field {@link #doubleUnique2}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static Standard forDoubleUniqueStrict(@javax.annotation.Nonnull final java.lang.String doubleUnique1,@javax.annotation.Nonnull final java.lang.Integer doubleUnique2)
			throws
				java.lang.IllegalArgumentException
	{
		return Standard.doubleUnique.searchStrict(Standard.class,doubleUnique1,doubleUnique2);
	}

	/**
	 * Returns the value of {@link #defaultFeature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double get()
	{
		return Standard.defaultFeature.get(this);
	}

	/**
	 * Sets a new value for {@link #defaultFeature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void set(@javax.annotation.Nullable final java.lang.Double defaultFeature)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.DoubleRangeViolationException
	{
		Standard.defaultFeature.set(this,defaultFeature);
	}

	/**
	 * Finds a standard by its {@link #defaultFeature}.
	 * @param defaultFeature shall be equal to field {@link #defaultFeature}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static Standard forDefaultFeature(@javax.annotation.Nonnull final java.lang.Double defaultFeature)
	{
		return Standard.defaultFeature.searchUnique(Standard.class,defaultFeature);
	}

	/**
	 * Finds a standard by its {@link #defaultFeature}.
	 * @param defaultFeature shall be equal to field {@link #defaultFeature}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static Standard forStrict(@javax.annotation.Nonnull final java.lang.Double defaultFeature)
			throws
				java.lang.IllegalArgumentException
	{
		return Standard.defaultFeature.searchUniqueStrict(Standard.class,defaultFeature);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #publicHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean checkPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		return Standard.publicHash.check(this,publicHash);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPublicHash} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static void blindPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.blind(publicHash);
	}

	/**
	 * Sets a new value for {@link #publicHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setPublicHash(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.set(this,publicHash);
	}

	/**
	 * Returns the encoded hash value for hash {@link #publicHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getPublicHashSHA512s8i5()
	{
		return Standard.publicHash.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #publicHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setPublicHashSHA512s8i5(@javax.annotation.Nullable final java.lang.String publicHash)
	{
		Standard.publicHash.setHash(this,publicHash);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #privateHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private boolean checkPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		return Standard.privateHash.check(this,privateHash);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPrivateHash} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private static void blindPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.blind(privateHash);
	}

	/**
	 * Sets a new value for {@link #privateHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateHash(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.set(this,privateHash);
	}

	/**
	 * Returns the encoded hash value for hash {@link #privateHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	private java.lang.String getPrivateHashSHA512s8i5()
	{
		return Standard.privateHash.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #privateHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateHashSHA512s8i5(@javax.annotation.Nullable final java.lang.String privateHash)
	{
		Standard.privateHash.setHash(this,privateHash);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #mandatoryHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean checkMandatoryHash(@javax.annotation.Nullable final java.lang.String mandatoryHash)
	{
		return Standard.mandatoryHash.check(this,mandatoryHash);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkMandatoryHash} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static void blindMandatoryHash(@javax.annotation.Nullable final java.lang.String mandatoryHash)
	{
		Standard.mandatoryHash.blind(mandatoryHash);
	}

	/**
	 * Sets a new value for {@link #mandatoryHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatoryHash(@javax.annotation.Nonnull final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryHash.set(this,mandatoryHash);
	}

	/**
	 * Returns the encoded hash value for hash {@link #mandatoryHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getMandatoryHashSHA512s8i5()
	{
		return Standard.mandatoryHash.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #mandatoryHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatoryHashSHA512s8i5(@javax.annotation.Nonnull final java.lang.String mandatoryHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.mandatoryHash.setHash(this,mandatoryHash);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #privateSetterHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean checkPrivateSetterHash(@javax.annotation.Nullable final java.lang.String privateSetterHash)
	{
		return Standard.privateSetterHash.check(this,privateSetterHash);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPrivateSetterHash} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static void blindPrivateSetterHash(@javax.annotation.Nullable final java.lang.String privateSetterHash)
	{
		Standard.privateSetterHash.blind(privateSetterHash);
	}

	/**
	 * Sets a new value for {@link #privateSetterHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setPrivateSetterHash(@javax.annotation.Nonnull final java.lang.String privateSetterHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.privateSetterHash.set(this,privateSetterHash);
	}

	/**
	 * Returns the encoded hash value for hash {@link #privateSetterHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getPrivateSetterHashSHA512s8i5()
	{
		return Standard.privateSetterHash.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #privateSetterHash}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setSHA512s8i5")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setPrivateSetterHashSHA512s8i5(@javax.annotation.Nonnull final java.lang.String privateSetterHash)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		Standard.privateSetterHash.setHash(this,privateSetterHash);
	}

	/**
	 * Returns the value of {@link #XMLReader}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getXMLReader()
	{
		return Standard.XMLReader.get(this);
	}

	/**
	 * Sets a new value for {@link #XMLReader}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setXMLReader(@javax.annotation.Nullable final java.lang.String XMLReader)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.XMLReader.set(this,XMLReader);
	}

	/**
	 * Returns the value of {@link #stringFullyQualified}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getStringFullyQualified()
	{
		return Standard.stringFullyQualified.get(this);
	}

	/**
	 * Sets a new value for {@link #stringFullyQualified}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setStringFullyQualified(@javax.annotation.Nullable final java.lang.String stringFullyQualified)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		Standard.stringFullyQualified.set(this,stringFullyQualified);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for standard.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<Standard> TYPE = com.exedio.cope.TypesBound.newType(Standard.class,Standard::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Standard(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
