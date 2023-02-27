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

package com.exedio.cope.pattern;

import com.exedio.cope.BooleanField;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.CopeWarnings;

public final class CompositeValue extends Composite
{
	enum AnEnum
	{
		facet1, facet2
	}

	static final StringField aString = new StringField();
	static final IntegerField anInt = new IntegerField();
	static final EnumField<AnEnum> anEnum = EnumField.create(AnEnum.class);
	static final ItemField<CompositeOptionalItem> anItem = ItemField.create(CompositeOptionalItem.class);

	// test, that these fields do not become fields of the composite value
	@SuppressWarnings({"TransientFieldNotInitialized", CopeWarnings.FEATURE_NOT_STATIC_FINAL, "unused"}) // OK: test bad API usage
	final transient BooleanField notStatic = new BooleanField();
	@SuppressWarnings({CopeWarnings.FEATURE_NOT_STATIC_FINAL, "unused"}) // OK: test bad API usage
	static BooleanField notFinal = new BooleanField();
	@SuppressWarnings("unused") // OK: test bad API usage
	static final Object noFeature = new BooleanField();

	/**
	 * Creates a new CompositeValue with all the fields initially needed.
	 * @param aString the initial value for field {@link #aString}.
	 * @param anInt the initial value for field {@link #anInt}.
	 * @param anEnum the initial value for field {@link #anEnum}.
	 * @param anItem the initial value for field {@link #anItem}.
	 * @throws com.exedio.cope.MandatoryViolationException if aString, anEnum, anItem is null.
	 * @throws com.exedio.cope.StringLengthViolationException if aString violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CompositeValue(
				@javax.annotation.Nonnull final java.lang.String aString,
				final int anInt,
				@javax.annotation.Nonnull final AnEnum anEnum,
				@javax.annotation.Nonnull final CompositeOptionalItem anItem)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CompositeValue.aString,aString),
			com.exedio.cope.SetValue.map(CompositeValue.anInt,anInt),
			com.exedio.cope.SetValue.map(CompositeValue.anEnum,anEnum),
			com.exedio.cope.SetValue.map(CompositeValue.anItem,anItem),
		});
	}

	/**
	 * Creates a new CompositeValue and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompositeValue(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #aString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getAString()
	{
		return get(CompositeValue.aString);
	}

	/**
	 * Sets a new value for {@link #aString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAString(@javax.annotation.Nonnull final java.lang.String aString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(CompositeValue.aString,aString);
	}

	/**
	 * Returns the value of {@link #anInt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getAnInt()
	{
		return getMandatory(CompositeValue.anInt);
	}

	/**
	 * Sets a new value for {@link #anInt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAnInt(final int anInt)
	{
		set(CompositeValue.anInt,anInt);
	}

	/**
	 * Returns the value of {@link #anEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AnEnum getAnEnum()
	{
		return get(CompositeValue.anEnum);
	}

	/**
	 * Sets a new value for {@link #anEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAnEnum(@javax.annotation.Nonnull final AnEnum anEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		set(CompositeValue.anEnum,anEnum);
	}

	/**
	 * Returns the value of {@link #anItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CompositeOptionalItem getAnItem()
	{
		return get(CompositeValue.anItem);
	}

	/**
	 * Sets a new value for {@link #anItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAnItem(@javax.annotation.Nonnull final CompositeOptionalItem anItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		set(CompositeValue.anItem,anItem);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;
}
