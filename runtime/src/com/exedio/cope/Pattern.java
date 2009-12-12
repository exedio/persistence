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

package com.exedio.cope;

import java.util.ArrayList;
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
	private Features sourceFieldsGather = new Features();
	private List<Field> sourceFieldList = null;
	
	private ArrayList<Type<? extends Item>> sourceTypesWhileGather = new ArrayList<Type<? extends Item>>();
	private List<Type<? extends Item>> sourceTypes = null;
	
	protected final void addSource(final Field field, final String postfix, final java.lang.reflect.Field annotationSource)
	{
		if(postfix==null)
			throw new NullPointerException("postfix");
		if(field==null)
			throw new NullPointerException("field");
		if(sourceFieldsGather==null)
			throw new IllegalStateException("addSource can be called only until pattern is mounted, not afterwards");
		assert sourceFieldList==null;
		field.registerPattern(this);
		sourceFieldsGather.put(postfix, field, annotationSource);
	}
	
	protected final void addSource(final Field field, final String postfix)
	{
		addSource(field, postfix, null);
	}
	
	/**
	 * Here you can do additional initialization not yet done in the constructor.
	 * In this method you can call methods {@link #getType()} and {@link #getName()}
	 * for the first time.
	 */
	protected void onMount()
	{
		// empty default implementation
	}
	
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Features features)
	{
		return newSourceType(javaClass, features, "");
	}
	
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Features features,
			final String postfix)
	{
		return newSourceType(javaClass, false, null, features, postfix);
	}

	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final boolean isAbstract,
			final Type<? super T> supertype,
			final Features features,
			final String postfix)
	{
		if(sourceTypesWhileGather==null)
			throw new IllegalStateException("newSourceType can be called only until pattern is mounted, not afterwards");
		assert sourceTypes==null;
		final String id = getType().getID() + '.' + getName() + postfix;
		final Type<T> result = new Type<T>(javaClass, false, id, this, isAbstract, supertype, features);
		sourceTypesWhileGather.add(result);
		return result;
	}
	
	@Override
	final void mount(final Type<? extends Item> type, final String name, final java.lang.reflect.Field annotationSource)
	{
		super.mount(type, name, annotationSource);
		initialize();
		onMount();

		this.sourceFieldList = sourceFieldsGather.mountPattern(type, name);
		this.sourceFieldsGather = null;
		
		this.sourceTypesWhileGather.trimToSize();
		this.sourceTypes = Collections.unmodifiableList(sourceTypesWhileGather);
		this.sourceTypesWhileGather = null;
	}

	/**
	 * @see Field#getPattern()
	 */
	public List<? extends Field> getSourceFields()
	{
		if(sourceFieldList==null)
			throw new IllegalStateException("getSourceFields can be called only after pattern is mounted, not before");
		assert sourceFieldsGather==null;
		return sourceFieldList;
	}

	/**
	 * @see Type#getPattern()
	 */
	public List<Type<? extends Item>> getSourceTypes()
	{
		if(sourceTypes==null)
			throw new IllegalStateException("getSourceTypes can be called only after pattern is mounted, not before");
		assert sourceTypesWhileGather==null;
		return sourceTypes;
	}
	
	// Make non-final method from super class final
	@Override
	public final Type<? extends Item> getType()
	{
		return super.getType();
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
	protected final <T extends Item> Type<T> newType(final Class<T> javaClass, final LinkedHashMap<String, Feature> features)
	{
		return newSourceType(javaClass, features);
	}
	
	/**
	 * @deprecated Use {@link #newSourceType(Class,LinkedHashMap,String)} instead
	 */
	@Deprecated
	protected final <T extends Item> Type<T> newType(final Class<T> javaClass, final LinkedHashMap<String, Feature> features, final String postfix)
	{
		return newSourceType(javaClass, features, postfix);
	}
	
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final LinkedHashMap<String, Feature> features)
	{
		return newSourceType(javaClass, new Features(features));
	}
	
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final LinkedHashMap<String, Feature> features,
			final String postfix)
	{
		return newSourceType(javaClass, new Features(features), postfix);
	}

	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final boolean isAbstract,
			final Type<? super T> supertype,
			final LinkedHashMap<String, Feature> features,
			final String postfix)
	{
		return newSourceType(javaClass, isAbstract, supertype, new Features(features), postfix);
	}
	
	/**
	 * @deprecated Override {@link #onMount()} instead
	 */
	@Deprecated
	protected void initialize()
	{
		// empty default implementation
	}
	
	/**
	 * @deprecated Do not this method anymore.
	 */
	@Deprecated
	protected final java.lang.reflect.Field annotationField(final String name)
	{
		try
		{
			return getClass().getDeclaredField(name);
		}
		catch(NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
}
