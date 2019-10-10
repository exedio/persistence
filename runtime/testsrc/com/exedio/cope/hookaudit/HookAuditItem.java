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

package com.exedio.cope.hookaudit;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE)
class HookAuditItem extends HookAuditItemSuper
{
	@WrapperInitial
	@HookAuditWatched("1")
	static final StringField field = new StringField().optional();

	static HookAuditItem newWithField(final String field)
	{
		return new HookAuditItem(HookAuditItem.field.map(field));
	}


	@HookAuditWatched("2")
	static final StringField field2 = new StringField().optional();

	static final StringField notCovered = new StringField().defaultTo("notCoveredDefault");

	static final HookAuditItem newWithNotCovered(final String notCovered)
	{
		return new HookAuditItem(HookAuditItem.notCovered.map(notCovered));
	}


	/**
	 * Creates a new HookAuditItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HookAuditItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getField()
	{
		return HookAuditItem.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setField(@javax.annotation.Nullable final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HookAuditItem.field.set(this,field);
	}

	/**
	 * Returns the value of {@link #field2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getField2()
	{
		return HookAuditItem.field2.get(this);
	}

	/**
	 * Sets a new value for {@link #field2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setField2(@javax.annotation.Nullable final java.lang.String field2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HookAuditItem.field2.set(this,field2);
	}

	/**
	 * Returns the value of {@link #notCovered}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getNotCovered()
	{
		return HookAuditItem.notCovered.get(this);
	}

	/**
	 * Sets a new value for {@link #notCovered}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNotCovered(@javax.annotation.Nonnull final java.lang.String notCovered)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		HookAuditItem.notCovered.set(this,notCovered);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hookAuditItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HookAuditItem> TYPE = com.exedio.cope.TypesBound.newType(HookAuditItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HookAuditItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
