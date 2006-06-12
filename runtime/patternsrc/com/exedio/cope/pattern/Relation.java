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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.ItemAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UniqueViolationException;

/**
 * Makes a set of instances of type <tt>T</tt> available
 * on any instance of type <tt>S</tt>.
 * This set cannot contain duplicates,
 * and the user has no control of the order of it's elements.
 *
 * @see VectorRelation
 * @author Ralf Wiebicke
 */
public final class Relation<S extends Item, T extends Item> extends Pattern
{
	final ItemAttribute<S> source;
	final ItemAttribute<T> target;
	final UniqueConstraint uniqueConstraint;
	
	public Relation(final ItemAttribute<S> source, final ItemAttribute<T> target)
	{
		this.source = source;
		this.target = target;
		this.uniqueConstraint = new UniqueConstraint(source, target);
		
		registerSource(source);
		registerSource(target);
	}
	
	public static final <S extends Item, T extends Item> Relation<S,T> newRelation(final ItemAttribute<S> source, final ItemAttribute<T> target)
	{
		return new Relation<S, T>(source, target);
	}
	
	public ItemAttribute<S> getSource()
	{
		return source;
	}
	
	public ItemAttribute<T> getTarget()
	{
		return target;
	}
	
	public UniqueConstraint getUniqueConstraint()
	{
		return uniqueConstraint;
	}
	
	// second initialization phase ---------------------------------------------------

	@Override
	public void initialize()
	{
		final String name = getName();
		
		if(!source.isInitialized())
			initialize(source, name + "Source");
		if(!target.isInitialized())
			initialize(target, name + "Target");
		
		initialize(uniqueConstraint, name + "UniqueConstraint");
	}
	
	public List<T> getTargets(final S source)
	{
		return new Query<T>(target, this.source.equal(source)).search();
	}

	public List<S> getSources(final T target)
	{
		return new Query<S>(source, this.target.equal(target)).search();
	}

	public List<T> getTargetsAndCast(final Item source)
	{
		return getTargets(this.source.cast(source));
	}

	public List<S> getSourcesAndCast(final Item target)
	{
		return getSources(this.target.cast(target));
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #getTargets} changed as a result of the call.
	 */
	public boolean addToTargets(final S source, final T target)
	{
		try
		{
			getType().newItem(new SetValue[]{
					this.source.map(source),
					this.target.map(target),
			});
			return true;
		}
		catch(UniqueViolationException e)
		{
			assert uniqueConstraint==e.getConstraint();
			return false;
		}
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #getSources} changed as a result of the call.
	 */
	public boolean addToSources(final T target, final S source)
	{
		return addToTargets(source, target);
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #getTargets} changed as a result of the call.
	 */
	public boolean removeFromTargets(final S source, final T target)
	{
		final Item item = uniqueConstraint.searchUnique(new Object[]{source, target});
		if(item==null)
			return false;
		else
		{
			item.deleteCopeItem();
			return true;
		}
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #getSources} changed as a result of the call.
	 */
	public boolean removeFromSources(final T target, final S source)
	{
		return removeFromTargets(source, target);
	}

	private <L extends Item, R extends Item> void set(
			final ItemAttribute<L> leftAttribute,
			final ItemAttribute<R> rightAttribute,
			final L leftItem,
			final Collection<? extends R> rightItems)
	{
		final Type<? extends Item> type = getType();
		final Collection<? extends Item> oldTupels = type.search(leftAttribute.equal(leftItem));

		//System.out.println("---------start");
		// TODO for better performance one could modify tuples, if rightAttribute is not FINAL
		final HashSet<R> keptRightItems = new HashSet<R>();
		for(final Item tupel : oldTupels)
		{
			final R rightItem = rightAttribute.get(tupel);
			if(rightItems.contains(rightItem))
			{
				if(!keptRightItems.add(rightItem))
					assert false;
			}
			else
			{
				//System.out.println("---------delete--"+leftItem+'-'+rightItem);
				tupel.deleteCopeItem();
			}
		}

		for(final R rightItem : rightItems)
		{
			if(!keptRightItems.contains(rightItem))
			{
				//System.out.println("---------create--"+leftItem+'-'+rightItem);
				type.newItem(new SetValue[]{
						leftAttribute.map(leftItem),
						rightAttribute.map(rightItem),
				});
			}
		}
		//System.out.println("---------end");
	}
	
	public void setTargets(final S source, final Collection<? extends T> targets)
	{
		set(this.source, this.target, source, targets);
	}
	
	public void setSources(final T target, final Collection<? extends S> sources)
	{
		set(this.target, this.source, target, sources);
	}
	
	public void setTargetsAndCast(final Item source, final Collection<?> targets)
	{
		setTargets(this.source.cast(source), this.target.castCollection(targets));
	}
	
	public void setSourcesAndCast(final Item target, final Collection<?> sources)
	{
		setSources(this.target.cast(target), this.source.castCollection(sources));
	}
	
	// static convenience methods ---------------------------------
	
	private static final HashMap<Type<?>, List<Relation>> cacheForGetRelationsBySource = new HashMap<Type<?>, List<Relation>>();
	
	/**
	 * Returns all relations where <tt>type</tt> is
	 * the source type {@link #getSource()}.{@link ItemAttribute#getValueType() getValueType()}.
	 *
	 * @see #getRelationsByTarget(Type)
	 * @see Qualifier#getQualifiers(Type)
	 */
	public static final List<Relation> getRelationsBySource(final Type<?> type)
	{
		return getRelations(cacheForGetRelationsBySource, type, true);
	}

	private static final HashMap<Type<?>, List<Relation>> cacheForGetRelationsByTarget = new HashMap<Type<?>, List<Relation>>();
	
	/**
	 * Returns all relations where <tt>type</tt> is
	 * the target type {@link #getTarget()}.{@link ItemAttribute#getValueType() getValueType()}.
	 *
	 * @see #getRelationsBySource(Type)
	 * @see Qualifier#getQualifiers(Type)
	 */
	public static final List<Relation> getRelationsByTarget(final Type<?> type)
	{
		return getRelations(cacheForGetRelationsByTarget, type, false);
	}

	private static final List<Relation> getRelations(final HashMap<Type<?>, List<Relation>> cache, final Type<?> type, final boolean source)
	{
		synchronized(cache)
		{
			{
				final List<Relation> cachedResult = cache.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}
			
			final ArrayList<Relation> resultModifiable = new ArrayList<Relation>();
			
			for(final ItemAttribute<?> ia : type.getReferences())
				for(final Pattern pattern : ia.getPatterns())
				{
					if(pattern instanceof Relation)
					{
						final Relation relation = (Relation)pattern;
						if(type.equals((source ? relation.source : relation.target).getValueType()))
							resultModifiable.add(relation);
					}
				}
			resultModifiable.trimToSize();
			
			final List<Relation> result =
				!resultModifiable.isEmpty()
				? Collections.unmodifiableList(resultModifiable)
				: Collections.<Relation>emptyList();
			cache.put(type, result);
			return result;
		}
	}
}
