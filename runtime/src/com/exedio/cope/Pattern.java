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
	private LinkedHashMap<Field, String> sourceFieldMapGather = new LinkedHashMap<Field, String>();
	private LinkedHashMap<Field, String> sourceFieldMap = null;
	private List<Field> sourceFieldList = null;
	
	private ArrayList<Type<? extends Item>> sourceTypesWhileGather = new ArrayList<Type<? extends Item>>();
	private List<Type<? extends Item>> sourceTypes = null;
	
	protected final void addSource(final Field field, final String postfix)
	{
		if(postfix==null)
			throw new NullPointerException("postfix must not be null");
		if(field==null)
			throw new NullPointerException("field must not be null for postfix '" + postfix + '\'');
		if(sourceFieldMapGather==null)
			throw new IllegalStateException("addSource can be called only until initialize() is called, not afterwards");
		assert sourceFieldMap== null;
		assert sourceFieldList==null;
		field.registerPattern(this);
		final String collision = sourceFieldMapGather.put(field, postfix);
		if(collision!=null)
			throw new IllegalStateException("duplicate addSource " + field + '/' + collision);
	}
	
	/**
	 * Here you can do additional initialization not yet done in the constructor.
	 * In this method you can call methods {@link #getType()} and {@link #getName()}
	 * for the first time.
	 */
	protected void initialize()
	{
		// empty default implementation
	}
	
	protected final <X extends Item> Type<X> newSourceType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features)
	{
		return newSourceType(javaClass, features, "");
	}
	
	protected final <X extends Item> Type<X> newSourceType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features, final String postfix)
	{
		if(sourceTypesWhileGather==null)
			throw new IllegalStateException("newSourceType can be called only until initialize() is called, not afterwards");
		assert sourceTypes==null;
		final String id = getType().getID() + '.' + getName() + postfix;
		final Type<X> result = new Type<X>(javaClass, false, id, this, features);
		sourceTypesWhileGather.add(result);
		return result;
	}
	
	@Override
	final void initialize(final Type<? extends Item> type, final String name)
	{
		super.initialize(type, name);
		initialize();

		for(final Field<?> source : sourceFieldMapGather.keySet())
		{
			if(!source.isInitialized())
				source.initialize(type, name + sourceFieldMapGather.get(source));
			final Type<? extends Item> sourceType = source.getType();
			//System.out.println("----------check"+source);
			if(!sourceType.equals(type))
				throw new RuntimeException("Source " + source + " of pattern " + this + " must be declared on the same type, expected " + type + ", but was " + sourceType + '.');
		}
		this.sourceFieldMap = sourceFieldMapGather;
		this.sourceFieldMapGather = null;
		this.sourceFieldList =
			sourceFieldMap.isEmpty()
			? Collections.<Field>emptyList()
			: Collections.unmodifiableList(Arrays.asList(sourceFieldMap.keySet().toArray(new Field[sourceFieldMap.size()])));
		
		this.sourceTypesWhileGather.trimToSize();
		this.sourceTypes = Collections.unmodifiableList(sourceTypesWhileGather);
		this.sourceTypesWhileGather = null;
	}

	/**
	 * @see Field#getPatterns()
	 */
	public List<? extends Field> getSourceFields()
	{
		if(sourceFieldMap==null)
			throw new IllegalStateException("getSourceFields can be called only after initialize() is called");
		assert sourceFieldList!=null;
		assert sourceFieldMapGather==null;
		return sourceFieldList;
	}

	/**
	 * @see Type#getPattern()
	 */
	public List<Type<? extends Item>> getSourceTypes()
	{
		if(sourceTypes==null)
			throw new IllegalStateException("getSourceTypes can be called only after initialize() is called");
		assert sourceTypesWhileGather==null;
		return sourceTypes;
	}
	
	// Make non-final method from super class final
	@Override
	public final Type<? extends Item> getType()
	{
		return super.getType();
	}
	
	/**
	 * Common super class for all wrapper classes backed by a real
	 * item of a type created by {@link #newSourceType(Class, LinkedHashMap, String)};
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
	
	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getSourceFields()} instead
	 */
	@Deprecated
	public List<? extends Field> getSources()
	{
		return getSourceFields();
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
	 * @deprecated Use {@link #addSource(Field,String)} instead
	 */
	@Deprecated
	protected final void registerSource(final Field field, final String postfix)
	{
		addSource(field, postfix);
	}

	/**
	 * @deprecated Use {@link #newSourceType(Class,LinkedHashMap)} instead
	 */
	@Deprecated
	protected final <X extends Item> Type<X> newType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features)
	{
		return newSourceType(javaClass, features);
	}
	
	/**
	 * @deprecated Use {@link #newSourceType(Class,LinkedHashMap,String)} instead
	 */
	@Deprecated
	protected final <X extends Item> Type<X> newType(final Class<X> javaClass, final LinkedHashMap<String, Feature> features, final String postfix)
	{
		return newSourceType(javaClass, features, postfix);
	}
}
