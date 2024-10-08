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

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testmodel.sub.SubTarget;

public final class DoubleUnique extends Item
{
	public static final StringField string = new StringField().toFinal();
	public static final ItemField<SubTarget> item = ItemField.create(SubTarget.class).cascade().toFinal();

	@Wrapper(wrap="finder", visibility=NONE)
	public static final UniqueConstraint unique = UniqueConstraint.create(string, item);


	/**
	 * Creates a new DoubleUnique with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param item the initial value for field {@link #item}.
	 * @throws com.exedio.cope.MandatoryViolationException if string, item is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if string, item is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public DoubleUnique(
				@javax.annotation.Nonnull final java.lang.String string,
				@javax.annotation.Nonnull final SubTarget item)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DoubleUnique.string,string),
			com.exedio.cope.SetValue.map(DoubleUnique.item,item),
		});
	}

	/**
	 * Creates a new DoubleUnique and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DoubleUnique(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getString()
	{
		return DoubleUnique.string.get(this);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public SubTarget getItem()
	{
		return DoubleUnique.item.get(this);
	}

	/**
	 * Finds a doubleUnique by its unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param item shall be equal to field {@link #item}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static DoubleUnique forUniqueStrict(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final SubTarget item)
			throws
				java.lang.IllegalArgumentException
	{
		return DoubleUnique.unique.searchStrict(DoubleUnique.class,string,item);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for doubleUnique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DoubleUnique> TYPE = com.exedio.cope.TypesBound.newType(DoubleUnique.class,DoubleUnique::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DoubleUnique(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
