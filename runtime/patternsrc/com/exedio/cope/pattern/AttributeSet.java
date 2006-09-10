/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.exedio.cope.Attribute;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class AttributeSet<E> extends Pattern
{
	private ItemField<?> parent = null;
	private final FunctionField<E> element;
	private UniqueConstraint uniqueConstraint = null;
	private Type<?> relationType = null;

	private AttributeSet(final FunctionField<E> element)
	{
		this.element = element;
		if(element==null)
			throw new NullPointerException("element must not be null");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new NullPointerException("element must not be unique");
	}
	
	public static final <E> AttributeSet<E> newSet(final FunctionField<E> element)
	{
		return new AttributeSet<E>(element);
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		parent = Item.newItemAttribute(Attribute.Option.FINAL, type.getJavaClass(), ItemField.DeletePolicy.CASCADE);
		uniqueConstraint = new UniqueConstraint(parent, element);
		final LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>();
		features.put("parent", parent);
		features.put("element", element);
		features.put("uniqueConstraint", uniqueConstraint);
		this.relationType = newType(features);
	}
	
	public ItemField<?> getParent()
	{
		assert parent!=null;
		return parent;
	}
	
	public FunctionField<E> getElement()
	{
		return element;
	}

	public UniqueConstraint getUniqueConstraint()
	{
		assert uniqueConstraint!=null;
		return uniqueConstraint;
	}
	
	public Type<?> getRelationType()
	{
		assert relationType!=null;
		return relationType;
	}
	
	public Set<E> get(final Item item)
	{
		return new HashSet<E>(new Query<E>(element, Cope.equalAndCast(this.parent, item)).search());
	}

	public void set(final Item item, final Collection<? extends E> value)
	{
		// TODO: this implementation wastes resources !!
		for(final Item tupel : this.relationType.newQuery(Cope.equalAndCast(this.parent, item)).search())
			tupel.deleteCopeItem();

		for(final E element : value)
		{
			this.relationType.newItem(new SetValue[]{
					Cope.mapAndCast(this.parent, item),
					this.element.map(element),
			});
		}
	}
	
	public void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, element.castCollection(value));
	}
}
