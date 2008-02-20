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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.Wrapper;

public final class SetField<E> extends Pattern
{
	private ItemField<?> parent = null;
	private final FunctionField<E> element;
	private UniqueConstraint uniqueConstraint = null;
	private Type<?> relationType = null;

	private SetField(final FunctionField<E> element)
	{
		this.element = element;
		if(element==null)
			throw new NullPointerException("element must not be null");
		if(element.isFinal())
			throw new IllegalArgumentException("element must not be final");
		if(element.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("element must not be unique");
	}
	
	public static final <E> SetField<E> newSet(final FunctionField<E> element)
	{
		return new SetField<E>(element);
	}
	
	@Override
	public void initialize()
	{
		final Type<?> type = getType();
		
		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		uniqueConstraint = new UniqueConstraint(parent, element);
		final LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>();
		features.put("parent", parent);
		features.put("element", element);
		features.put("uniqueConstraint", uniqueConstraint);
		this.relationType = newType(PatternItem.class, features);
	}
	
	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		return parent.as(parentClass);
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
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			Wrapper.makeType(Set.class, Wrapper.TypeVariable0.class),
			"get",
			"Returns the value of {0}.",
			"getter"));
		
		result.add(new Wrapper(
			Wrapper.makeType(List.class, Wrapper.ClassVariable.class),
			"getParents",
			"Returns the items, for which field set {0} contains the given element.",
			"getter").
			setStatic().
			setMethodWrapperPattern("getParentsOf{0}").
			addParameter(Wrapper.TypeVariable0.class, "element"));
		
		final String MODIFICATION_RESULT =
			"@return <tt>true</tt> if the field set changed as a result of the call.";
		final Set<Class> exceptions = element.getSetterExceptions();
		exceptions.add(ClassCastException.class);
		final Class[] exceptionArray = exceptions.toArray(new Class[exceptions.size()]);
		
		result.add(new Wrapper(
			void.class, "set",
			"Sets a new value for {0}.",
			"setter", exceptionArray).
			addParameter(Wrapper.makeTypeExtends(Collection.class, Wrapper.TypeVariable0.class)));
		
		result.add(new Wrapper(
			boolean.class, "add",
			"Adds a new element to {0}.",
			"setter",
			exceptionArray).
			setMethodWrapperPattern("addTo{0}").
			addParameter(Wrapper.TypeVariable0.class, "element").
			addComment(MODIFICATION_RESULT));
			
		result.add(new Wrapper(
			boolean.class, "remove",
			"Removes an element from {0}.",
			"setter",
			exceptionArray).
			setMethodWrapperPattern("removeFrom{0}").
			addParameter(Wrapper.TypeVariable0.class, "element").
			addComment(MODIFICATION_RESULT));
				
		result.add(new Wrapper(
			Wrapper.makeType(ItemField.class, Wrapper.ClassVariable.class), "getParent",
			"Returns the parent field of the type of {0}.",
			"parent").
			setMethodWrapperPattern("{1}Parent").
			setStatic());
		
		return Collections.unmodifiableList(result);
	}
	
	public Set<E> get(final Item item)
	{
		return Collections.unmodifiableSet(new HashSet<E>(new Query<E>(element, Cope.equalAndCast(this.parent, item)).search()));
	}

	/**
	 * Returns the items, for which this field set contains the given element.
	 * The order of the result is unspecified.
	 */
	public <P extends Item> List<P> getParents(final Class<P> parentClass, final E element)
	{
		return new Query<P>(this.parent.as(parentClass), this.element.equal(element)).search();
	}
	
	/**
	 * @return <tt>true</tt> if the result of {@link #get(Item)} changed as a result of the call.
	 */
	public boolean add(final Item item, final E element)
	{
		try
		{
			relationType.newItem(
					Cope.mapAndCast(this.parent, item),
					this.element.map(element)
			);
			return true;
		}
		catch(UniqueViolationException e)
		{
			assert uniqueConstraint==e.getFeature();
			return false;
		}
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #get(Item)} changed as a result of the call.
	 */
	public boolean remove(final Item item, final E element)
	{
		final Item row = uniqueConstraint.searchUnique(item, element);
		if(row==null)
			return false;
		else
		{
			row.deleteCopeItem();
			return true;
		}
	}

	public void set(final Item item, final Collection<? extends E> value)
	{
		final LinkedHashSet<? extends E> toCreateSet = new LinkedHashSet<E>(value);
		final ArrayList<Item> toDeleteList = new ArrayList<Item>();
		
		for(final Item tupel : this.relationType.search(Cope.equalAndCast(this.parent, item)))
		{
			final Object element = this.element.get(tupel);
			
			if(toCreateSet.contains(element))
				toCreateSet.remove(element);
			else
				toDeleteList.add(tupel);
		}

		final Iterator<? extends E> toCreate = toCreateSet.iterator();
		final Iterator<Item> toDelete = toDeleteList.iterator();
		while(true)
		{
			if(!toDelete.hasNext())
			{
				while(toCreate.hasNext())
				{
					this.relationType.newItem(
							Cope.mapAndCast(this.parent, item),
							this.element.map(toCreate.next())
					);
				}
				return;
			}
			else if(!toCreate.hasNext())
			{
				while(toDelete.hasNext())
					toDelete.next().deleteCopeItem();
				return;
			}
			else
			{
				this.element.set(toDelete.next(), toCreate.next());
			}
		}
	}
	
	public void setAndCast(final Item item, final Collection<?> value)
	{
		set(item, element.castCollection(value));
	}
}
