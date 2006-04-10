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

import com.exedio.cope.Attribute;
import com.exedio.cope.IntegerAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

public final class VectorRelation<S extends Item, T extends Item> extends Pattern
{
	final ItemAttribute<S> source;
	final ItemAttribute<T> target;
	final IntegerAttribute order;
	final UniqueConstraint uniqueConstraint;
	
	public VectorRelation(final ItemAttribute<S> source, final ItemAttribute<T> target)
	{
		this.source = source;
		this.target = target;
		this.order = new IntegerAttribute(Attribute.Option.FINAL);
		this.uniqueConstraint = new UniqueConstraint(source, target, order);
		
		registerSource(source);
		registerSource(target);
		registerSource(order);
	}
	
	public ItemAttribute<S> getSource()
	{
		return source;
	}
	
	public ItemAttribute<T> getTarget()
	{
		return target;
	}
	
	public IntegerAttribute getOrder()
	{
		return order;
	}
	
	public UniqueConstraint getUniqueConstraint()
	{
		return uniqueConstraint;
	}
	
	// second initialization phase ---------------------------------------------------

	public void initialize()
	{
		final String name = getName();
		
		if(!source.isInitialized())
			initialize(source, name + "Source");
		if(!target.isInitialized())
			initialize(target, name + "Target");
		
		initialize(order, name + "Order");
		initialize(uniqueConstraint, name + "UniqueConstraint");
	}
	
	public Collection<T> getTargets(final S source)
	{
		final Query q = new Query(target, this.source.equal(source));
		q.setOrderBy(order, true);
		return castTarget(q.search());
	}

	public Collection<S> getSources(final T target)
	{
		final Query q = new Query(source, this.target.equal(target));
		q.setOrderBy(order, true);
		return castSource(q.search());
	}

	public void setTargets(final S source, final Collection<T> targets)
	{
		final Type type = getType();
		final Query q = new Query(type, this.source.equal(source));
		q.setOrderBy(order, true);
		final Collection<Item> oldTupels = castRelation(q.search());

		// TODO: this implementation wastes resources !!
		for(final Item tupel : oldTupels)
			tupel.deleteCopeItem();

		int order = 0;
		for(final T target : targets)
		{
			type.newItem(new SetValue[]{
					this.source.map(source),
					this.target.map(target),
					this.order.map(order++),
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	private Collection<S> castSource(final Collection c)
	{
		return (Collection<S>)c;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<T> castTarget(final Collection c)
	{
		return (Collection<T>)c;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Item> castRelation(final Collection c)
	{
		return (Collection<Item>)c;
	}
	
}
