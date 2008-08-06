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

package com.exedio.cope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A common super class for all patterns.
 * <p>
 * Patterns should be constructable in three different ways:
 * <dl>
 * <dt>1) by an explicit external source</dt>
 * <dd>
 * This is the most verbose kind of defining a pattern.
 * First the source for the pattern is created, such as:
 * <pre>static final StringField source = new StringField(OPTIONAL)</pre>
 * Then the pattern ist created using the previously defined source:
 * <pre>static final Hash hash = new MD5Hash(source)</pre>
 * </dd>
 * <dt>2) by an implicit external source</dt>
 * <dd>
 * More concisely the pattern can be constructed by defining the source
 * implicitely when the defining the pattern itself:
 * <pre>static final Hash hash = new MD5Hash(new StringField(OPTIONAL))</pre>
 * </dd>
 * <dt>3) by an internal source</dt>
 * <dd>
 * Finally, the construction of the source can be done the the pattern itself:
 * <pre>static final Hash hash = new MD5Hash(OPTIONAL)</pre>
 * </dd>
 * </dl>
 *
 * @author Ralf Wiebicke
 */
public abstract class Pattern extends Feature
{
	private LinkedHashMap<Field, String> sourceMapWhileRegistration = new LinkedHashMap<Field, String>();
	private LinkedHashMap<Field, String> sourceMap = null;
	private List<Field> sourceList = null;
	private ArrayList<Type<? extends Item>> generatedTypesWhileRegistration = new ArrayList<Type<? extends Item>>();
	private List<Type<? extends Item>> generatedTypes = null;
	
	@Override
	final void initialize(final Type<? extends Item> type, final String name)
	{
		super.initialize(type, name);
		initialize();

		for(final Field<?> source : sourceMapWhileRegistration.keySet())
		{
			if(!source.isInitialized())
				source.initialize(type, name + sourceMapWhileRegistration.get(source));
			final Type<? extends Item> sourceType = source.getType();
			//System.out.println("----------check"+source);
			if(!sourceType.equals(type))
				throw new RuntimeException("Source " + source + " of pattern " + this + " must be declared on the same type, expected " + type + ", but was " + sourceType + '.');
		}
		this.sourceMap = sourceMapWhileRegistration;
		this.sourceMapWhileRegistration = null;
		this.sourceList =
			sourceMap.isEmpty()
			? Collections.<Field>emptyList()
			: Collections.unmodifiableList(Arrays.asList(sourceMap.keySet().toArray(new Field[sourceMap.size()])));
		this.generatedTypesWhileRegistration.trimToSize();
		this.generatedTypes = Collections.unmodifiableList(generatedTypesWhileRegistration);
		this.generatedTypesWhileRegistration = null;
	}
	
	protected final void registerSource(final Field field, final String postfix)
	{
		if(postfix==null)
			throw new NullPointerException("postfix must not be null");
		if(field==null)
			throw new NullPointerException("field must not be null for postfix '" + postfix + '\'');
		if(sourceMapWhileRegistration==null)
			throw new IllegalStateException("registerSource can be called only until initialize() is called, not afterwards");
		assert sourceMap== null;
		assert sourceList==null;
		field.registerPattern(this);
		final String collision = sourceMapWhileRegistration.put(field, postfix);
		if(collision!=null)
			throw new IllegalStateException("duplicate source registration " + field + '/' + collision);
	}
	
	/**
	 * Here you can do additional initialization not yet done in the constructor.
	 * In this method you can call methods {@link #getType()} and {@link #getName()}
	 * for the first time.
	 */
	public void initialize()
	{
		// empty default implementation
	}
	
	protected final void initialize(final UniqueConstraint uniqueConstraint, final String name)
	{
		uniqueConstraint.initialize(getType(), name);
	}
	
	protected final <X extends Item> Type<X> newType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features)
	{
		return newType(javaClass, features, "");
	}
	
	protected final <X extends Item> Type<X> newType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features, final String postfix)
	{
		if(generatedTypesWhileRegistration==null)
			throw new IllegalStateException("newType can be called only until initialize() is called, not afterwards");
		assert generatedTypes==null;
		final String id = getType().getID() + '.' + getName() + postfix;
		final Type<X> result = new Type<X>(javaClass, false, id, this, features);
		generatedTypesWhileRegistration.add(result);
		return result;
	}

	/**
	 * @deprecated Use {@link #getSourceFields()} instead
	 */
	@Deprecated
	public List<? extends Field> getSources()
	{
		return getSourceFields();
	}

	/**
	 * @see Field#getPatterns()
	 */
	public List<? extends Field> getSourceFields()
	{
		if(sourceMap==null)
			throw new IllegalStateException("getSources can be called only after initialize() is called");
		assert sourceList!=null;
		assert sourceMapWhileRegistration==null;
		return sourceList;
	}
	
	/**
	 * @deprecated Use {@link #getSourceTypes()} instead
	 */
	@Deprecated
	public List<Type<? extends Item>> getGeneratedTypes()
	{
		return getSourceTypes();
	}

	/**
	 * @see Type#getPattern()
	 */
	public List<Type<? extends Item>> getSourceTypes()
	{
		if(generatedTypes==null)
			throw new IllegalStateException("getGeneratedTypes can be called only after initialize() is called");
		assert generatedTypesWhileRegistration==null;
		return generatedTypes;
	}
	
	// Make non-final method from super class final
	@Override
	public final Type<? extends Item> getType()
	{
		return super.getType();
	}
	
	/**
	 * Common super class for all wrapper classes backed by a real
	 * item of a type created by {@link #newType(Class, LinkedHashMap, String)};
	 * TODO: remove, support custom classes for such types.
	 */
	public abstract class BackedItem
	{
		protected final Item backingItem;
		
		protected BackedItem(final Item backingItem)
		{
			this.backingItem = backingItem;
			assert backingItem!=null;
		}
		
		public final Item getBackingItem()
		{
			return backingItem;
		}
		
		@Override
		public final boolean equals(final Object other)
		{
			return other instanceof BackedItem && backingItem.equals(((BackedItem)other).backingItem);
		}
		
		@Override
		public final int hashCode()
		{
			return backingItem.hashCode() ^ 765744;
		}
		
		@Override
		public final String toString()
		{
			return backingItem.toString();
		}
	}
}
