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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Wrapper;

public final class ListField<E> extends AbstractListField<E>
{
	private static final long serialVersionUID = 1l;
	
	private ItemField<?> parent = null;
	private final IntegerField order;
	private UniqueConstraint uniqueConstraint = null;
	private final FunctionField<E> element;
	private Type<?> relationType = null;

	private ListField(final FunctionField<E> element)
	{
		this.order = new IntegerField().toFinal().min(0);
		this.element = element;
		if(element==null)
			throw new NullPointerException("element");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
	}
	
	public static final <E> ListField<E> newList(final FunctionField<E> element)
	{
		return new ListField<E>(element);
	}
	
	@Override
	protected void onMount()
	{
		final Type<?> type = getType();
		
		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		uniqueConstraint = new UniqueConstraint(parent, order);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("order", order);
		features.put("uniqueConstraint", uniqueConstraint);
		features.put("element", element);
		this.relationType = newSourceType(PatternItem.class, features);
	}
	
	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		return parent.as(parentClass);
	}
	
	public ItemField<?> getParent()
	{
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
	
	@Override
	public FunctionField<E> getElement()
	{
		return element;
	}

	public Type<? extends Item> getRelationType()
	{
		assert relationType!=null;
		return relationType;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("get").
			addComment("Returns the value of {0}.").
			setReturn(Wrapper.generic(List.class, Wrapper.TypeVariable0.class)));
		
		result.add(
			new Wrapper("getDistinctParents").
			addComment("Returns the items, for which field list {0} contains the given element.").
			setReturn(Wrapper.generic(List.class, Wrapper.ClassVariable.class)).
			setStatic().
			setMethodWrapperPattern("getDistinctParentsOf{0}").
			addParameter(Wrapper.TypeVariable0.class, "element"));
			
		final Set<Class<? extends Throwable>> exceptions = element.getInitialExceptions();
		exceptions.add(ClassCastException.class);
		
		result.add(
			new Wrapper("add").
			setMethodWrapperPattern("addTo{0}").
			addComment("Adds a new value for {0}.").
			addThrows(exceptions).
			addParameter(Wrapper.TypeVariable0.class));
				
		result.add(
			new Wrapper("set").
			addComment("Sets a new value for {0}.").
			addThrows(exceptions).
			addParameter(Wrapper.genericExtends(Collection.class, Wrapper.TypeVariable0.class)));
			
		result.add(
			new Wrapper("getParent").
			addComment("Returns the parent field of the type of {0}.").
			setReturn(Wrapper.generic(ItemField.class, Wrapper.ClassVariable.class)).
			setMethodWrapperPattern("{1}Parent").
			setStatic());
				
		return Collections.unmodifiableList(result);
	}
	
	@Override
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
	public <P extends Item> List<P> getDistinctParents(final Class<P> parentClass, final E element)
	{
		final Query<P> q = new Query<P>(this.parent.as(parentClass), Cope.equalAndCast(this.element, element));
		q.setDistinct(true);
		return q.search();
	}
	
	public void add(final Item item, final E value)
	{
		final Query<Integer> q = new Query<Integer>(this.order.max(), Cope.equalAndCast(this.parent, item));
		final Integer max = q.searchSingleton();
		final int newOrder = max!=null ? (max.intValue()+1) : 0;
		this.relationType.newItem(
				Cope.mapAndCast(this.parent, item),
				this.order.map(newOrder),
				this.element.map(value));
	}
	
	@Override
	public void set(final Item item, final Collection<? extends E> value)
	{
		final Iterator<? extends Item> actual = this.relationType.search(Cope.equalAndCast(this.parent, item), this.order, true).iterator();
		final Iterator<? extends E> expected = value.iterator();
		
		for(int order = 0; ; order++)
		{
			if(!actual.hasNext())
			{
				while(expected.hasNext())
				{
					this.relationType.newItem(
							Cope.mapAndCast(this.parent, item),
							this.element.map(expected.next()),
							this.order.map(order++)
					);
				}
				return;
			}
			else if(!expected.hasNext())
			{
				while(actual.hasNext())
					actual.next().deleteCopeItem();
				return;
			}
			else
			{
				final Item tupel = actual.next();
				final int currentOrder = this.order.get(tupel);
				assert order<=currentOrder : String.valueOf(order) + '/' + currentOrder;
				order = currentOrder;
				this.element.set(tupel, expected.next());
			}
		}
	}
}
