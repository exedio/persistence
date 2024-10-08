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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.WrapperType;
import java.util.Date;

@WrapperType(genericConstructor=PACKAGE)
public final class DoubleItem extends Item
{
	public static final DoubleField any = new DoubleField().optional();

	public static final DoubleField mandatory = new DoubleField();

	public static final DoubleField min4 = new DoubleField().optional().min(4.0);
	public static final DoubleField max4 = new DoubleField().optional().max(4.0);
	public static final DoubleField min4Max8 = new DoubleField().optional().range(4.0, 8.0);

	DoubleItem(final Double mandatory) throws StringLengthViolationException, MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			SetValue.map(DoubleItem.mandatory, mandatory),
		});
	}

	DoubleItem(final Double max4, @SuppressWarnings("unused") final Date dummy) throws StringLengthViolationException, MandatoryViolationException
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		this(new com.exedio.cope.SetValue<?>[]{
			SetValue.map(DoubleItem.mandatory, 7777777.77),
			SetValue.map(DoubleItem.max4, max4),
		});
	}

	/**
	 * Creates a new DoubleItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public DoubleItem(
				final double mandatory)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DoubleItem.mandatory,mandatory),
		});
	}

	/**
	 * Creates a new DoubleItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	DoubleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getAny()
	{
		return DoubleItem.any.get(this);
	}

	/**
	 * Sets a new value for {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setAny(@javax.annotation.Nullable final java.lang.Double any)
	{
		DoubleItem.any.set(this,any);
	}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public double getMandatory()
	{
		return DoubleItem.mandatory.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatory(final double mandatory)
	{
		DoubleItem.mandatory.set(this,mandatory);
	}

	/**
	 * Returns the value of {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getMin4()
	{
		return DoubleItem.min4.get(this);
	}

	/**
	 * Sets a new value for {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4(@javax.annotation.Nullable final java.lang.Double min4)
			throws
				com.exedio.cope.DoubleRangeViolationException
	{
		DoubleItem.min4.set(this,min4);
	}

	/**
	 * Returns the value of {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getMax4()
	{
		return DoubleItem.max4.get(this);
	}

	/**
	 * Sets a new value for {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMax4(@javax.annotation.Nullable final java.lang.Double max4)
			throws
				com.exedio.cope.DoubleRangeViolationException
	{
		DoubleItem.max4.set(this,max4);
	}

	/**
	 * Returns the value of {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getMin4Max8()
	{
		return DoubleItem.min4Max8.get(this);
	}

	/**
	 * Sets a new value for {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4Max8(@javax.annotation.Nullable final java.lang.Double min4Max8)
			throws
				com.exedio.cope.DoubleRangeViolationException
	{
		DoubleItem.min4Max8.set(this,min4Max8);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for doubleItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DoubleItem> TYPE = com.exedio.cope.TypesBound.newType(DoubleItem.class,DoubleItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
