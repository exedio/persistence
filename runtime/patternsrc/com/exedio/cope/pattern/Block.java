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
import com.exedio.cope.ItemWildcardCast;
import com.exedio.cope.instrument.WrapType;
import java.io.Serializable;

@WrapType(
		wildcardClassCaster=ItemWildcardCast.class,
		type=BlockType.class,
		hasGenericConstructor=false,
		activationConstructor=BlockActivationParameters.class,
		allowStaticClassToken=false,
		featurePrefix="field().of(", featurePostfix=")", featureThis="item()",
		top=Block.class
)
public abstract class Block implements Serializable, TemplatedValue
{
	private static final long serialVersionUID = 1l;

	private final BlockField<?> field;
	private final Item item;

	/**
	 * Activation constructor.
	 * Is used for internal purposes only.
	 */
	protected Block(final BlockActivationParameters ap)
	{
		if(ap==null)
			throw new RuntimeException(
					"activation constructor is for internal purposes only, " +
					"don't use it in your application!");

		this.field = ap.field;
		this.item  = ap.item ;
	}

	public final BlockField<?> field()
	{
		return field;
	}

	public final Item item()
	{
		return item;
	}

	@Override
	public final BlockType<?> getCopeType()
	{
		return field.getValueType();
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(this==other)
			return true;

		if(other==null || !getClass().equals(other.getClass()))
			return false;

		final Block o = (Block)other;
		return
			field.equals(o.field) &&
			item .equals(o.item );
	}

	@Override
	public final int hashCode()
	{
		return field.hashCode() ^ item.hashCode();
	}

	@Override
	public final String toString()
	{
		return field.toString() + '#' + item;
	}
}
