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
import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;

final class DeleteSchemaItem extends DeleteSchemaItemSuper
{
	static final StringField field = new StringField();
	static final IntegerField next = new IntegerField().defaultToNext(1000);
	static final Sequence sequence = new Sequence(2000);

	@WrapperInitial
	@Wrapper(wrap="get", visibility=PACKAGE)
	@Wrapper(wrap="set", visibility=NONE)
	private static final IntegerField nextUnused = new IntegerField().defaultToNext(10000);

	@Wrapper(wrap="next", visibility=NONE)
	@SuppressWarnings("unused")
	private static final Sequence sequenceUnused = new Sequence(20000);


	DeleteSchemaItem(final String field)
	{
		this(field, -10);
	}


	/**
	 * Creates a new DeleteSchemaItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @param nextUnused the initial value for field {@link #nextUnused}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	private DeleteSchemaItem(
				@javax.annotation.Nonnull final java.lang.String field,
				final int nextUnused)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DeleteSchemaItem.field.map(field),
			DeleteSchemaItem.nextUnused.map(nextUnused),
		});
	}

	/**
	 * Creates a new DeleteSchemaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteSchemaItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getField()
	{
		return DeleteSchemaItem.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setField(@javax.annotation.Nonnull final java.lang.String field)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DeleteSchemaItem.field.set(this,field);
	}

	/**
	 * Returns the value of {@link #next}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getNext()
	{
		return DeleteSchemaItem.next.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #next}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNext(final int next)
	{
		DeleteSchemaItem.next.set(this,next);
	}

	/**
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="next")
	static int nextSequence()
	{
		return DeleteSchemaItem.sequence.next();
	}

	/**
	 * Returns the value of {@link #nextUnused}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getNextUnused()
	{
		return DeleteSchemaItem.nextUnused.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteSchemaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteSchemaItem> TYPE = com.exedio.cope.TypesBound.newType(DeleteSchemaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DeleteSchemaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
