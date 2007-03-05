/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;

public final class PartOf<C extends Item> extends Pattern
{
	private final ItemField<C> container;

	private PartOf(final ItemField<C> container)
	{
		this.container = container;
		
		if(container==null)
			throw new NullPointerException("container must not be null");
	}
	
	public static final <C extends Item> PartOf<C> newPartOf(final ItemField<C> container)
	{
		return new PartOf<C>(container);
	}
	
	public ItemField<C> getContainer()
	{
		return container;
	}
	
	public C getContainer(final Item part)
	{
		return container.get(part);
	}
	
	@SuppressWarnings("unchecked")
	public <P extends Item> List<? extends P> getParts(final C container)
	{
		return (List<P>)getType().search(this.container.equal(container));
	}
}
