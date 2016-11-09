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

package com.exedio.cope.instrument;

enum Kind
{
	item(JavaRepository.DummyItem.class),
	composite(JavaRepository.DummyComposite.class),
	block(JavaRepository.DummyBlock.class);


	final Class<?> dummy;

	private Kind(final Class<?> dummy)
	{
		this.dummy = dummy;
	}


	static Kind valueOf(final boolean isItem, final boolean isBlock, final boolean isComposite)
	{
		if (isItem)
		{
			if (isBlock||isComposite) throw new RuntimeException();
			return item;
		}
		else if (isBlock)
		{
			if (isItem||isComposite) throw new RuntimeException();
			return block;
		}
		else if (isComposite)
		{
			if (isItem||isBlock) throw new RuntimeException();
			return composite;
		}
		else
		{
			return null;
		}
	}
}