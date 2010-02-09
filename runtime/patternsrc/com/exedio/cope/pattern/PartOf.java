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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.Cast;

public final class PartOf<C extends Item> extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	private final ItemField<C> container;

	private PartOf(final ItemField<C> container)
	{
		this.container = container;
		addSource(container, "Container");
	}
	
	public static final <C extends Item> PartOf<C> newPartOf(final ItemField<C> container)
	{
		return new PartOf<C>(container);
	}
	
	public ItemField<C> getContainer()
	{
		return container;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("getContainer").
			addComment("Returns the container this item is part of by {0}.").
			setReturn(container.getValueClass()));
		
		return Collections.unmodifiableList(result);
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

	public List<? extends Item> getPartsAndCast(final Item container)
	{
		return getParts(Cast.<C>verboseCast(this.container.getValueClass(), container));
	}
	
	// static convenience methods ---------------------------------

	private static final HashMap<Type<?>, List<PartOf>> cacheForGetPartOfs = new HashMap<Type<?>, List<PartOf>>();
	private static final HashMap<Type<?>, List<PartOf>> cacheForGetDeclaredPartOfs = new HashMap<Type<?>, List<PartOf>>();
	
	/**
	 * Returns all part-of declarations where <tt>type</tt> or any of it's super types is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static final List<PartOf> getPartOfs(final Type<?> type)
	{
		return getPartOfs(false, cacheForGetPartOfs, type);
	}

	/**
	 * Returns all part-of declarations where <tt>type</tt> is
	 * the container type {@link #getContainer()}.{@link ItemField#getValueType() getValueType()}.
	 */
	public static final List<PartOf> getDeclaredPartOfs(final Type<?> type)
	{
		return getPartOfs(true, cacheForGetDeclaredPartOfs, type);
	}
	
	private static final List<PartOf> getPartOfs(final boolean declared, final HashMap<Type<?>, List<PartOf>> cache, final Type<?> type)
	{
		synchronized(cache)
		{
			{
				final List<PartOf> cachedResult = cache.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}
			
			final ArrayList<PartOf> resultModifiable = new ArrayList<PartOf>();
			
			for(final ItemField<?> field : declared ? type.getDeclaredReferences() : type.getReferences())
			{
				final Pattern pattern = field.getPattern();
				if(pattern instanceof PartOf)
					resultModifiable.add((PartOf)pattern);
			}
			resultModifiable.trimToSize();
			
			final List<PartOf> result =
				!resultModifiable.isEmpty()
				? Collections.unmodifiableList(resultModifiable)
				: Collections.<PartOf>emptyList();
			cache.put(type, result);
			return result;
		}
	}
	
	/**
	 * Returns all partofs of the <tt>pattern</tt>. Considers a one step recursion
	 * for {@link History}.
	 */
	public static final List<PartOf> getPartOfs(final Pattern pattern)
	{
		final ArrayList<PartOf> result = new ArrayList<PartOf>();
		for(PartOf partOf : PartOf.getPartOfs(pattern.getType()))
		{
			if (pattern.getSourceTypes().contains(partOf.getType()) ||
					( pattern.getType().getPattern()!=null &&
					  pattern.getType().getPattern().getSourceTypes().contains(partOf.getType()) )
				)
			{
				result.add(partOf);
			}
		}
		return result;
	}
}
