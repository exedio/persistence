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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.StringField;

public final class BlockFully extends com.exedio.cope.pattern.Block
{
	static final StringField field = new StringField();

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getField()
	{
		return field().of(BlockFully.field).get(item());
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
		field().of(BlockFully.field).set(item(),field);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for blockFully.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.pattern.BlockType<BlockFully> TYPE = com.exedio.cope.pattern.BlockType.newType(BlockFully.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private BlockFully(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}
