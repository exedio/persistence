/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
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
	final ItemField<S> source;
	final ItemField<T> target;
	final UniqueConstraint uniqueConstraint;
	
	/**
	 * @deprecated
	 * use {@link #newRelation(ItemField, ItemField)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new Relation&lt;Source, Target&gt;(source, target)</tt>
	 * one can write <tt>Relation.newRelation(source, target)</tt>
	 */
	@Deprecated
	public Relation(final ItemField<S> source, final ItemField<T> target)
	{
		this.source = source;
		this.target = target;
		this.uniqueConstraint = new UniqueConstraint(source, target);
		
		registerSource(source);
		registerSource(target);
	}

	/**
	 * @deprecated use {@link SetField} instead.
	 */
	@Deprecated
	public static final <S extends Item, T extends Item> Relation<S,T> newRelation(final ItemField<S> source, final ItemField<T> target)
	{
		return new Relation<S, T>(source, target);
	}
	
	public ItemField<S> getSource()
	{
		return source;
	}
	
	public ItemField<T> getTarget()
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
		return getTargets(Cope.verboseCast(this.source.getValueClass(), source));
	}

	public List<S> getSourcesAndCast(final Item target)
	{
		return getSources(Cope.verboseCast(this.target.getValueClass(), target));
	}

	/**
	 * @return <tt>true</tt> if the result of {@link #getTargets} changed as a result of the call.
	 */
	public boolean addToTargets(final S source, final T target)
	{
		try
		{
			getType().newItem(
					this.source.map(source),
					this.target.map(target)
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
		final Item item = uniqueConstraint.searchUnique(source, target);
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
			final ItemField<L> leftField,
			final ItemField<R> rightField,
			final L leftItem,
			final Collection<? extends R> rightItems)
	{
		final Type<? extends Item> type = getType();

		//System.out.println("---------start");
		// TODO for better performance one could modify tuples, if rightField is not FINAL
		final HashSet<R> keptRightItems = new HashSet<R>();
		for(final Item tupel : type.search(leftField.equal(leftItem)))
		{
			final R rightItem = rightField.get(tupel);
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
				type.newItem(
						leftField.map(leftItem),
						rightField.map(rightItem)
				);
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
		setTargets(Cope.verboseCast(this.source.getValueClass(), source), this.target.castCollection(targets));
	}
	
	public void setSourcesAndCast(final Item target, final Collection<?> sources)
	{
		setSources(Cope.verboseCast(this.target.getValueClass(), target), this.source.castCollection(sources));
	}
	
	// static convenience methods ---------------------------------
	
	private static final HashMap<Type<?>, Map<Relation, Integer>> cacheForGetRelations = new HashMap<Type<?>, Map<Relation, Integer>>();
	private static final HashMap<Type<?>, Map<Relation, Integer>> cacheForGetDeclaredRelations = new HashMap<Type<?>, Map<Relation, Integer>>();
	
	public static final int IS_SOURCE = 1;
	public static final int IS_TARGET = 2;
	
	/**
	 * Returns all relations where <tt>type</tt> or any of it's super types is either
	 * the source type {@link #getSource()}.{@link ItemField#getValueType() getValueType()} or
	 * the target type {@link #getTarget()}.{@link ItemField#getValueType() getValueType()}.
	 *
	 * @see #getDeclaredRelations(Type)
	 * @see VectorRelation#getRelations(Type)
	 * @see Qualifier#getQualifiers(Type)
	 */
	public static final Map<Relation, Integer> getRelations(final Type<?> type)
	{
		return getRelations(false, cacheForGetRelations, type);
	}
	
	/**
	 * Returns all relations where <tt>type</tt> is either
	 * the source type {@link #getSource()}.{@link ItemField#getValueType() getValueType()} or
	 * the target type {@link #getTarget()}.{@link ItemField#getValueType() getValueType()}.
	 *
	 * @see #getRelations(Type)
	 * @see VectorRelation#getDeclaredRelations(Type)
	 * @see Qualifier#getDeclaredQualifiers(Type)
	 */
	public static final Map<Relation, Integer> getDeclaredRelations(final Type<?> type)
	{
		return getRelations(true, cacheForGetDeclaredRelations, type);
	}
	
	private static final Map<Relation, Integer> getRelations(final boolean declared, final HashMap<Type<?>, Map<Relation, Integer>> cache, final Type<?> type)
	{
		synchronized(cache)
		{
			{
				final Map<Relation, Integer> cachedResult = cache.get(type);
				if(cachedResult!=null)
					return cachedResult;
			}
			
			final LinkedHashMap<Relation, Integer> resultModifiable = new LinkedHashMap<Relation, Integer>();
			
			for(final ItemField<?> ia : declared ? type.getDeclaredReferences() : type.getReferences())
				for(final Pattern pattern : ia.getPatterns())
				{
					if(pattern instanceof Relation)
					{
						final Relation relation = (Relation)pattern;
						final int value;
						if(ia==relation.source)
							value = IS_SOURCE;
						else if(ia==relation.target)
							value = IS_TARGET;
						else
							continue;
						
						if(value>0)
						{
							final Integer previous = resultModifiable.get(relation);
							resultModifiable.put(relation, (previous!=null) ? (previous.intValue() | value) : value);
						}
					}
				}
			
			final Map<Relation, Integer> result =
				!resultModifiable.isEmpty()
				? Collections.unmodifiableMap(resultModifiable)
				: Collections.<Relation, Integer>emptyMap();
			cache.put(type, result);
			return result;
		}
	}
}
