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

public final class BlockActivationParameters
{
	final BlockField<?> field;
	final Item item;

	BlockActivationParameters(final BlockField<?> field, final Item item)
	{
		this.field = field;
		this.item  = item;
	}

	/**
	 * This method is equivalent to {@link Block#toString()}.
	 */
	@Override
	public String toString()
	{
		return field.toString() + '#' + item.toString();
	}
}
