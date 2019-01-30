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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;

final class MultiItemFieldItem extends Item
{
	static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
			MultiItemFieldValuex.class,
			MultiItemFieldComponentxA.class, MultiItemFieldComponentxB.class);

	MultiItemFieldComponentxA getFieldA()
	{
		return field.of(MultiItemFieldComponentxA.class).get(this);
	}

	MultiItemFieldComponentxB getFieldB()
	{
		return field.of(MultiItemFieldComponentxB.class).get(this);
	}


	static final MultiItemField<MultiItemFieldValuex> optionalField = MultiItemField.create(
			MultiItemFieldValuex.class,
			MultiItemFieldComponentxA.class, MultiItemFieldComponentxB.class)
			.optional();

	MultiItemFieldComponentxA getOptionalFieldA()
	{
		return optionalField.of(MultiItemFieldComponentxA.class).get(this);
	}

	MultiItemFieldComponentxB getOptionalFieldB()
	{
		return optionalField.of(MultiItemFieldComponentxB.class).get(this);
	}


	static final MultiItemField<MultiItemFieldValuex> uniqueField = MultiItemField.create(
			MultiItemFieldValuex.class,
			MultiItemFieldComponentxA.class, MultiItemFieldComponentxB.class).optional().unique();

	@WrapperIgnore
	static final PartOf<MultiItemFieldComponentxA> partOfClassA = PartOf.create(field.of(MultiItemFieldComponentxA.class));

	MultiItemFieldItem(final MultiItemFieldValuex field)
			throws com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]
		{
				MultiItemFieldItem.field.map(field),
		});
	}

	// TODO generate by instrumentor
	MultiItemFieldValuex getField()
	{
		return field.get(this);
	}

	// TODO generate by instrumentor
	void setField(final MultiItemFieldValuex value)
	{
		field.set(this, value);
	}

	// TODO generate by instrumentor
	MultiItemFieldValuex getOptionalField()
	{
		return optionalField.get(this);
	}

	// TODO generate by instrumentor
	void setOptionalField(final MultiItemFieldValuex value)
	{
		optionalField.set(this, value);
	}

	// TODO generate by instrumentor
	MultiItemFieldValuex getUniqueField()
	{
		return uniqueField.get(this);
	}

	// TODO generate by instrumentor
	void setUniqueField(final MultiItemFieldValuex value)
	{
		uniqueField.set(this, value);
	}


	/**
	 * Creates a new MultiItemFieldItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MultiItemFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MultiItemFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MultiItemFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for multiItemFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MultiItemFieldItem> TYPE = com.exedio.cope.TypesBound.newType(MultiItemFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MultiItemFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
