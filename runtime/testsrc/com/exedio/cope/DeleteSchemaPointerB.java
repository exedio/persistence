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


final class DeleteSchemaPointerB extends Item
{
	static final IntegerField code = new IntegerField();
	static final ItemField<DeleteSchemaPointerA> other = ItemField.create(DeleteSchemaPointerA.class).optional();


	/**
	 * Creates a new DeleteSchemaPointerB with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DeleteSchemaPointerB(
				final int code)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DeleteSchemaPointerB.code.map(code),
		});
	}

	/**
	 * Creates a new DeleteSchemaPointerB and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteSchemaPointerB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getCode()
	{
		return DeleteSchemaPointerB.code.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCode(final int code)
	{
		DeleteSchemaPointerB.code.set(this,code);
	}

	/**
	 * Returns the value of {@link #other}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteSchemaPointerA getOther()
	{
		return DeleteSchemaPointerB.other.get(this);
	}

	/**
	 * Sets a new value for {@link #other}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOther(@javax.annotation.Nullable final DeleteSchemaPointerA other)
	{
		DeleteSchemaPointerB.other.set(this,other);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteSchemaPointerB.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteSchemaPointerB> TYPE = com.exedio.cope.TypesBound.newType(DeleteSchemaPointerB.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DeleteSchemaPointerB(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
