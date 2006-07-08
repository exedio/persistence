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
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.Attribute;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class AttributeList<T> extends Pattern
{
	private ItemAttribute<?> parent = null;
	private final IntegerAttribute order;
	private UniqueConstraint uniqueConstraint = null;
	private final FunctionAttribute<T> element;
	private Type<?> relationType = null;

	private AttributeList(final FunctionAttribute<T> element)
	{
		this.order = new IntegerAttribute(Attribute.Option.FINAL);
		this.element = element;
		if(element==null)
			throw new NullPointerException("element must not be null");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new NullPointerException("element must not be unique");
	}
	
	public static final <T> AttributeList<T> newList(final FunctionAttribute<T> element)
	{
		return new AttributeList<T>(element);
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		parent = Item.newItemAttribute(Attribute.Option.FINAL, type.getJavaClass(), ItemAttribute.DeletePolicy.CASCADE);
		uniqueConstraint = new UniqueConstraint(parent, order);
		final LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>();
		features.put("parent", parent);
		features.put("order", order);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("element", element);
		this.relationType = newType(features);
	}
	
	public ItemAttribute<?> getParent()
	{
		assert parent!=null;
		return parent;
	}
	
	public IntegerAttribute getOrder()
	{
		return order;
	}
	
	public UniqueConstraint getUniqueConstraint()
	{
		assert uniqueConstraint!=null;
		return uniqueConstraint;
	}
	
	public FunctionAttribute<T> getElement()
	{
		return element;
	}

	public Type<?> getRelationType()
	{
		assert relationType!=null;
		return relationType;
	}
	
	public List<T> get(final Item item)
	{
		final Query<T> q = new Query<T>(element, Cope.equalAndCast(this.parent, item));
		q.setOrderBy(order, true);
		return q.search();
	}

	public void set(final Item item, final Collection<? extends T> value)
	{
		// TODO: this implementation wastes resources !!
		for(final Item tupel : this.relationType.newQuery(Cope.equalAndCast(this.parent, item)).search())
			tupel.deleteCopeItem();

		int order = 0;
		for(final T element : value)
		{
			this.relationType.newItem(new SetValue[]{
					Cope.mapAndCast(this.parent, item),
					this.element.map(element),
					this.order.map(order++),
			});
		}
	}
	
}
