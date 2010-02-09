/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Collection;
import java.util.List;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.util.Cast;

public abstract class AbstractListField<E> extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	public abstract FunctionField<E> getElement();
	
	public int getMaximumSize()
	{
		return Integer.MAX_VALUE;
	}

	public abstract List<E> get(Item item);
	public abstract void set(Item item, Collection<? extends E> value);

	public final void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, Cast.castElements(getElement().getValueClass(), value));
	}
}
