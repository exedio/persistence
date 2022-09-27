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
public final class LongItem extends Item
{
	public static final LongField any = new LongField().optional();

	public static final LongField mandatory = new LongField();

	public static final LongField min4 = new LongField().optional().min(4);
	public static final LongField max4 = new LongField().optional().max(4);
	public static final LongField min4Max8 = new LongField().optional().range(4, 8);

	LongItem(final Long mandatory) throws StringLengthViolationException, MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			LongItem.mandatory.map(mandatory),
		});
	}

	LongItem(final Long max4, @SuppressWarnings("unused") final Date dummy) throws StringLengthViolationException, MandatoryViolationException
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		this(new com.exedio.cope.SetValue<?>[]{
			LongItem.mandatory.map(7777777l),
			LongItem.max4.map(max4),
		});
	}

	/**
	 * Creates a new LongItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public LongItem(
				final long mandatory)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			LongItem.mandatory.map(mandatory),
		});
	}

	/**
	 * Creates a new LongItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	LongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getAny()
	{
		return LongItem.any.get(this);
	}

	/**
	 * Sets a new value for {@link #any}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setAny(@javax.annotation.Nullable final java.lang.Long any)
	{
		LongItem.any.set(this,any);
	}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public long getMandatory()
	{
		return LongItem.mandatory.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMandatory(final long mandatory)
	{
		LongItem.mandatory.set(this,mandatory);
	}

	/**
	 * Returns the value of {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getMin4()
	{
		return LongItem.min4.get(this);
	}

	/**
	 * Sets a new value for {@link #min4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4(@javax.annotation.Nullable final java.lang.Long min4)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		LongItem.min4.set(this,min4);
	}

	/**
	 * Returns the value of {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getMax4()
	{
		return LongItem.max4.get(this);
	}

	/**
	 * Sets a new value for {@link #max4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMax4(@javax.annotation.Nullable final java.lang.Long max4)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		LongItem.max4.set(this,max4);
	}

	/**
	 * Returns the value of {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getMin4Max8()
	{
		return LongItem.min4Max8.get(this);
	}

	/**
	 * Sets a new value for {@link #min4Max8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setMin4Max8(@javax.annotation.Nullable final java.lang.Long min4Max8)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		LongItem.min4Max8.set(this,min4Max8);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for longItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LongItem> TYPE = com.exedio.cope.TypesBound.newType(LongItem.class,LongItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
