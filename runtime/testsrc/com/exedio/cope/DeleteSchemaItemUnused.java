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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE)
final class DeleteSchemaItemUnused extends Item
{
	static final IntegerField code = new IntegerField();

	/**
	 * Tests an unsued table referencing a used table.
	 * This requires truncate ... cascade on PostgreSQL.
	 */
	static final ItemField<DeleteSchemaItem> unusedPointer  = ItemField.create(DeleteSchemaItem.class).optional();


	/**
	 * Creates a new DeleteSchemaItemUnused and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteSchemaItemUnused(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getCode()
	{
		return DeleteSchemaItemUnused.code.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCode(final int code)
	{
		DeleteSchemaItemUnused.code.set(this,code);
	}

	/**
	 * Returns the value of {@link #unusedPointer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteSchemaItem getUnusedPointer()
	{
		return DeleteSchemaItemUnused.unusedPointer.get(this);
	}

	/**
	 * Sets a new value for {@link #unusedPointer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setUnusedPointer(@javax.annotation.Nullable final DeleteSchemaItem unusedPointer)
	{
		DeleteSchemaItemUnused.unusedPointer.set(this,unusedPointer);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteSchemaItemUnused.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteSchemaItemUnused> TYPE = com.exedio.cope.TypesBound.newType(DeleteSchemaItemUnused.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DeleteSchemaItemUnused(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
