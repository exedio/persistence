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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DeleteSchemaItemUnused(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getCode()
	{
		return DeleteSchemaItemUnused.code.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCode(final int code)
	{
		DeleteSchemaItemUnused.code.set(this,code);
	}

	/**
	 * Returns the value of {@link #unusedPointer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	DeleteSchemaItem getUnusedPointer()
	{
		return DeleteSchemaItemUnused.unusedPointer.get(this);
	}

	/**
	 * Sets a new value for {@link #unusedPointer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setUnusedPointer(@javax.annotation.Nullable final DeleteSchemaItem unusedPointer)
	{
		DeleteSchemaItemUnused.unusedPointer.set(this,unusedPointer);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteSchemaItemUnused.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteSchemaItemUnused> TYPE = com.exedio.cope.TypesBound.newType(DeleteSchemaItemUnused.class,DeleteSchemaItemUnused::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DeleteSchemaItemUnused(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
