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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CompositeFieldRenamedSchemaItem(
				@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite virgnComp,
				@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite wrongComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompositeFieldRenamedSchemaItem.virgnComp.map(virgnComp),
			CompositeFieldRenamedSchemaItem.wrongComp.map(wrongComp),
		});
	}

	/**
	 * Creates a new CompositeFieldRenamedSchemaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CompositeFieldRenamedSchemaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #virgnComp}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	CompositeFieldRenamedSchemaComposite getVirgnComp()
	{
		return CompositeFieldRenamedSchemaItem.virgnComp.get(this);
	}

	/**
	 * Sets a new value for {@link #virgnComp}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setVirgnComp(@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite virgnComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeFieldRenamedSchemaItem.virgnComp.set(this,virgnComp);
	}

	/**
	 * Returns the value of {@link #wrongComp}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	CompositeFieldRenamedSchemaComposite getWrongComp()
	{
		return CompositeFieldRenamedSchemaItem.wrongComp.get(this);
	}

	/**
	 * Sets a new value for {@link #wrongComp}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setWrongComp(@javax.annotation.Nonnull final CompositeFieldRenamedSchemaComposite wrongComp)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeFieldRenamedSchemaItem.wrongComp.set(this,wrongComp);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeFieldRenamedSchemaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CompositeFieldRenamedSchemaItem> TYPE = com.exedio.cope.TypesBound.newType(CompositeFieldRenamedSchemaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CompositeFieldRenamedSchemaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
