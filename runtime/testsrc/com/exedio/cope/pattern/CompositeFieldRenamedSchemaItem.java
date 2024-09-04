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

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Item;

final class CompositeFieldRenamedSchemaItem extends Item
{
	static final CompositeField<CompositeFieldRenamedSchemaComposite> virgnComp = CompositeField.create(CompositeFieldRenamedSchemaComposite.class);
	@CopeSchemaName("namedComp")
	static final CompositeField<CompositeFieldRenamedSchemaComposite> wrongComp = CompositeField.create(CompositeFieldRenamedSchemaComposite.class);


	/**
	 * Creates a new CompositeFieldRenamedSchemaItem with all the fields initially needed.
	 * @param virgnComp the initial value for field {@link #virgnComp}.
	 * @param wrongComp the initial value for field {@link #wrongComp}.
	 * @throws com.exedio.cope.MandatoryViolationException if virgnComp, wrongComp is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CompositeFieldRenamedSchemaItem(
				@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite virgnComp,
				@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite wrongComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CompositeFieldRenamedSchemaItem.virgnComp,virgnComp),
			com.exedio.cope.SetValue.map(CompositeFieldRenamedSchemaItem.wrongComp,wrongComp),
		});
	}

	/**
	 * Creates a new CompositeFieldRenamedSchemaItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompositeFieldRenamedSchemaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #virgnComp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CompositeFieldRenamedSchemaComposite getVirgnComp()
	{
		return CompositeFieldRenamedSchemaItem.virgnComp.get(this);
	}

	/**
	 * Sets a new value for {@link #virgnComp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setVirgnComp(@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite virgnComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeFieldRenamedSchemaItem.virgnComp.set(this,virgnComp);
	}

	/**
	 * Returns the value of {@link #wrongComp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CompositeFieldRenamedSchemaComposite getWrongComp()
	{
		return CompositeFieldRenamedSchemaItem.wrongComp.get(this);
	}

	/**
	 * Sets a new value for {@link #wrongComp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWrongComp(@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite wrongComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeFieldRenamedSchemaItem.wrongComp.set(this,wrongComp);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeFieldRenamedSchemaItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CompositeFieldRenamedSchemaItem> TYPE = com.exedio.cope.TypesBound.newType(CompositeFieldRenamedSchemaItem.class,CompositeFieldRenamedSchemaItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CompositeFieldRenamedSchemaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
