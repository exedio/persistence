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

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(comments=false)
final class UniqueDoubleNullItem extends Item
{
	@WrapperInitial static final StringField string = new StringField().optional();

	@WrapperInitial static final IntegerField integer = new IntegerField().optional();

	static final UniqueConstraint constraint = new UniqueConstraint(string, integer);

	@javax.annotation.Generated("com.exedio.cope.instrument")
	UniqueDoubleNullItem(
				@javax.annotation.Nullable final java.lang.String string,
				@javax.annotation.Nullable final java.lang.Integer integer)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			UniqueDoubleNullItem.string.map(string),
			UniqueDoubleNullItem.integer.map(integer),
		});
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private UniqueDoubleNullItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable
	final java.lang.String getString()
	{
		return UniqueDoubleNullItem.string.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setString(@javax.annotation.Nullable final java.lang.String string)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueDoubleNullItem.string.set(this,string);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable
	final java.lang.Integer getInteger()
	{
		return UniqueDoubleNullItem.integer.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setInteger(@javax.annotation.Nullable final java.lang.Integer integer)
			throws
				com.exedio.cope.UniqueViolationException
	{
		UniqueDoubleNullItem.integer.set(this,integer);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable
	static final UniqueDoubleNullItem forConstraint(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final java.lang.Integer integer)
	{
		return UniqueDoubleNullItem.constraint.search(UniqueDoubleNullItem.class,string,integer);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<UniqueDoubleNullItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueDoubleNullItem.class);

	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private UniqueDoubleNullItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
