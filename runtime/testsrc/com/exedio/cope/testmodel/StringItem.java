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

import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.LengthView;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.PlusView;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.UppercaseView;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.CharSet;
import java.util.Date;

@WrapperType(genericConstructor=PUBLIC)
public final class StringItem extends Item
{
	public static final StringField any = new StringField().optional().lengthMin(0);

	public static final StringField mandatory = new StringField().lengthMin(0);

	public static final StringField min4 = new StringField().optional().lengthMin(4);
	public static final StringField max4 = new StringField().optional().lengthRange(0, 4);
	public static final StringField min4Max8 = new StringField().optional().lengthRange(4, 8);
	public static final StringField exact6 = new StringField().optional().lengthExact(6);

	public static final StringField lowercase = new StringField().optional().lengthMin(0).charSet(new CharSet('a', 'z'));
	public static final StringField lowercaseMin4 = new StringField().optional().lengthMin(4).charSet(new CharSet('a', 'z'));

	public static final StringField long1K = new StringField().optional().lengthRange(0, 1000);
	public static final StringField long1M = new StringField().optional().lengthRange(0, 1000*1000);
	public static final StringField long40M = new StringField().optional().lengthRange(0, 40*1000*1000);

	public static final UppercaseView min4Upper = min4.toUpperCase();
	public static final UppercaseView max4Upper = max4.toUpperCase();

	public static final LengthView min4UpperLength = min4Upper.length();
	public static final LengthView max4UpperLength = max4Upper.length();

	public static final PlusView<Integer> min4AndMax4UpperLength = min4UpperLength.plus(max4UpperLength);

	public StringItem(final String any, @SuppressWarnings("unused") final boolean dummy)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		this(
				SetValue.map(StringItem.mandatory, "defaultByAny"),
				SetValue.map(StringItem.any, any)
		);
	}

	public StringItem(final String mandatory, @SuppressWarnings("unused") final double dummy) throws MandatoryViolationException
	{
		this(
				SetValue.map(StringItem.mandatory, mandatory)
		);
	}

	public StringItem(final String exact6, @SuppressWarnings("unused") final int dummy) throws StringLengthViolationException
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		this(
				SetValue.map(StringItem.mandatory, "defaultByExact6"),
				SetValue.map(StringItem.exact6, exact6)
		);
	}

	public StringItem(final String max4, @SuppressWarnings("unused") final Date dummy) throws StringLengthViolationException
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		this(
				SetValue.map(StringItem.mandatory, "defaultByMax4"),
				SetValue.map(StringItem.max4, max4)
		);
	}

	/**
	 * Creates a new StringItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatory is null.
	 * @throws com.exedio.cope.StringLengthViolationException if mandatory violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public StringItem(
				@javax.annotation.Nonnull final java.lang.String mandatory)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(StringItem.mandatory,mandatory),
		});
	}

	/**
	 * Creates a new StringItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	public StringItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getAny()
	{
		return StringItem.any.get(this);
	}

	/**
	 * Sets a new value for {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setAny(@javax.annotation.Nullable final java.lang.String any)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.any.set(this,any);
	}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getMandatory()
	{
		return StringItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatory(@javax.annotation.Nonnull final java.lang.String mandatory)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.mandatory.set(this,mandatory);
	}

	/**
	 * Returns the value of {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getMin4()
	{
		return StringItem.min4.get(this);
	}

	/**
	 * Sets a new value for {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4(@javax.annotation.Nullable final java.lang.String min4)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.min4.set(this,min4);
	}

	/**
	 * Returns the value of {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getMax4()
	{
		return StringItem.max4.get(this);
	}

	/**
	 * Sets a new value for {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMax4(@javax.annotation.Nullable final java.lang.String max4)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.max4.set(this,max4);
	}

	/**
	 * Returns the value of {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getMin4Max8()
	{
		return StringItem.min4Max8.get(this);
	}

	/**
	 * Sets a new value for {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4Max8(@javax.annotation.Nullable final java.lang.String min4Max8)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.min4Max8.set(this,min4Max8);
	}

	/**
	 * Returns the value of {@link #exact6}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getExact6()
	{
		return StringItem.exact6.get(this);
	}

	/**
	 * Sets a new value for {@link #exact6}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setExact6(@javax.annotation.Nullable final java.lang.String exact6)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.exact6.set(this,exact6);
	}

	/**
	 * Returns the value of {@link #lowercase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getLowercase()
	{
		return StringItem.lowercase.get(this);
	}

	/**
	 * Sets a new value for {@link #lowercase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLowercase(@javax.annotation.Nullable final java.lang.String lowercase)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringItem.lowercase.set(this,lowercase);
	}

	/**
	 * Returns the value of {@link #lowercaseMin4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getLowercaseMin4()
	{
		return StringItem.lowercaseMin4.get(this);
	}

	/**
	 * Sets a new value for {@link #lowercaseMin4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLowercaseMin4(@javax.annotation.Nullable final java.lang.String lowercaseMin4)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringItem.lowercaseMin4.set(this,lowercaseMin4);
	}

	/**
	 * Returns the value of {@link #long1K}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getLong1K()
	{
		return StringItem.long1K.get(this);
	}

	/**
	 * Sets a new value for {@link #long1K}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLong1K(@javax.annotation.Nullable final java.lang.String long1K)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.long1K.set(this,long1K);
	}

	/**
	 * Returns the value of {@link #long1M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getLong1M()
	{
		return StringItem.long1M.get(this);
	}

	/**
	 * Sets a new value for {@link #long1M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLong1M(@javax.annotation.Nullable final java.lang.String long1M)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.long1M.set(this,long1M);
	}

	/**
	 * Returns the value of {@link #long40M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getLong40M()
	{
		return StringItem.long40M.get(this);
	}

	/**
	 * Sets a new value for {@link #long40M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setLong40M(@javax.annotation.Nullable final java.lang.String long40M)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringItem.long40M.set(this,long40M);
	}

	/**
	 * Returns the value of {@link #min4Upper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public java.lang.String getMin4Upper()
	{
		return StringItem.min4Upper.getSupported(this);
	}

	/**
	 * Returns the value of {@link #max4Upper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public java.lang.String getMax4Upper()
	{
		return StringItem.max4Upper.getSupported(this);
	}

	/**
	 * Returns the value of {@link #min4UpperLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public java.lang.Integer getMin4UpperLength()
	{
		return StringItem.min4UpperLength.getSupported(this);
	}

	/**
	 * Returns the value of {@link #max4UpperLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public java.lang.Integer getMax4UpperLength()
	{
		return StringItem.max4UpperLength.getSupported(this);
	}

	/**
	 * Returns the value of {@link #min4AndMax4UpperLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getMin4AndMax4UpperLength()
	{
		return StringItem.min4AndMax4UpperLength.getSupported(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for stringItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<StringItem> TYPE = com.exedio.cope.TypesBound.newType(StringItem.class,StringItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private StringItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
