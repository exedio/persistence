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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class FieldList<E> extends Pattern
{
	private ItemField<?> parent = null;
	private final IntegerField order;
	private UniqueConstraint uniqueConstraint = null;
	private final FunctionField<E> element;
	private Type<?> relationType = null;

	private FieldList(final FunctionField<E> element)
	{
		this.order = new IntegerField(Field.Option.FINAL);
		this.element = element;
		if(element==null)
			throw new NullPointerException("element must not be null");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
	}
	
	public static final <E> FieldList<E> newList(final FunctionField<E> element)
	{
		return new FieldList<E>(element);
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		parent = type.newItemField(Field.Option.FINAL, ItemField.DeletePolicy.CASCADE);
		uniqueConstraint = new UniqueConstraint(parent, order);
		final LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>();
		features.put("parent", parent);
		features.put("order", order);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("element", element);
		this.relationType = newType(features);
	}
	
	public ItemField<?> getParent()
	{
		assert parent!=null;
		return parent;
	}
	
	public IntegerField getOrder()
	{
		return order;
	}
	
	public UniqueConstraint getUniqueConstraint()
	{
		assert uniqueConstraint!=null;
		return uniqueConstraint;
	}
	
	public FunctionField<E> getElement()
	{
		return element;
	}

	public Type<? extends Item> getRelationType()
	{
		assert relationType!=null;
		return relationType;
	}
	
	public List<E> get(final Item item)
	{
		final Query<E> q = new Query<E>(element, Cope.equalAndCast(this.parent, item));
		q.setOrderBy(order, true);
		return q.search();
	}

	/**
	 * Returns the items, for which this field list contains the given element.
	 * The result does not contain any duplicates,
	 * even if the element is contained in this field list for an item more than once.
	 * The order of the result is unspecified.
	 */
	public List<? extends Item> getDistinctParents(final E element)
	{
		final Query<? extends Item> q = new Query<Item>(this.parent, Cope.equalAndCast(this.element, element));
		q.setDistinct(true);
		return q.search();
	}

	public void set(final Item item, final Collection<? extends E> value)
	{
		final Iterator<? extends Item> actual = this.relationType.newQuery(Cope.equalAndCast(this.parent, item)).search().iterator();
		final Iterator<? extends E> expected = value.iterator();
		
		for(int order = 0; ; order++)
		{
			if(!actual.hasNext())
			{
				for(; expected.hasNext(); order++)
				{
					this.relationType.newItem(new SetValue[]{
							Cope.mapAndCast(this.parent, item),
							this.element.map(expected.next()),
							this.order.map(order),
					});
				}
				return;
			}
			else if(!expected.hasNext())
			{
				for(; actual.hasNext(); )
					actual.next().deleteCopeItem();
				return;
			}
			else
			{
				this.element.set(actual.next(), expected.next());
			}
		}
	}
	
	public void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, element.castCollection(value));
	}
}
