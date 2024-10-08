/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.BlockCompositeHelper.assertFinalSubClass;
import static com.exedio.cope.pattern.BlockCompositeHelper.getConstructor;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Copyable;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.misc.CopeNameUtil;
import com.exedio.cope.misc.LocalizationKeys;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class BlockType<T extends Block> implements TemplatedType<T>
{
	final Class<T> javaClass;
	private final Function<BlockActivationParameters,T> constructor;
	private final LinkedHashMap<String, Feature> templates = new LinkedHashMap<>();
	final List<? extends Feature> templateList;

	private BlockType(
			final Class<T> javaClass,
			final Function<BlockActivationParameters,T> constructor)
	{
		this.javaClass = javaClass;
		this.constructor = constructor;
		final String id = javaClass.getName();
		for(final Map.Entry<Feature, java.lang.reflect.Field> entry : TypesBound.getFeatures(javaClass).entrySet())
		{
			final Feature feature = entry.getKey();
			final java.lang.reflect.Field field = entry.getValue();
			final String fieldID = id + '#' + field.getName();
			if(!(feature instanceof Copyable))
				throw new IllegalArgumentException(
						fieldID + " must be an instance of " + Copyable.class + ", but was " +
						feature.getClass().getName());
			final String fieldName = CopeNameUtil.getAndFallbackToName(field);
			templates.put(fieldName, feature);
			//noinspection ThisEscapedInObjectConstruction
			feature.mount(this, fieldName, fieldID, SerializedReflectionField.make(feature, field), field);
		}

		if(templates.isEmpty())
			throw new IllegalArgumentException(
					"block has no templates: " + javaClass.getName());

		this.templateList = List.copyOf(templates.values());
	}

	@Deprecated
	private static <T extends Block> Function<BlockActivationParameters,T> reflectionActivator(final Class<T> javaClass)
	{
		final Constructor<T> constructor = getConstructor(javaClass, BlockActivationParameters.class);
		return ap -> newValue(constructor, ap);
	}

	@Override
	public Class<T> getJavaClass()
	{
		return javaClass;
	}

	/**
	 * @see Type#as(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Block> BlockType<X> as(final Class<X> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		if(javaClass!=this.javaClass)
			throw new ClassCastException(
					"expected " + javaClass.getName() + ", " +
					"but was " + this.javaClass.getName());

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final BlockType<X> result = (BlockType<X>)this;
		return result;
	}

	Map<String,Feature> getTemplateMap()
	{
		return Collections.unmodifiableMap(templates);
	}

	@Override
	public BlockType<? super T> getSupertype()
	{
		return null;
	}

	@Override
	public List<? extends BlockType<? extends T>> getSubtypes()
	{
		return List.of();
	}

	@Override
	public List<? extends Feature> getDeclaredFeatures()
	{
		return templateList;
	}

	@Override
	public List<? extends Feature> getFeatures()
	{
		return templateList;
	}

	@Override
	public Feature getDeclaredFeature(final String name)
	{
		return templates.get(name);
	}

	@Override
	public Feature getFeature(final String name)
	{
		return templates.get(name);
	}

	private List<String> localizationKeysIfInitialized = null;

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // result of LocalizationKeys#get is unmodifiable
	@Override
	public List<String> getLocalizationKeys()
	{
		if(localizationKeysIfInitialized!=null)
			return localizationKeysIfInitialized;

		localizationKeysIfInitialized = LocalizationKeys.get(javaClass);
		return localizationKeysIfInitialized;
	}

	T newValue(final BlockField<?> field, final Item item)
	{
		return constructor.apply(new BlockActivationParameters(field, item));
	}

	private static <T extends Block> T newValue(
			final Constructor<T> constructor,
			final BlockActivationParameters ap)
	{
		try
		{
			return constructor.newInstance(ap);
		}
		catch(final InvocationTargetException e)
		{
			final Throwable cause = e.getCause();
			if(cause instanceof ConstraintViolationException)
				throw (ConstraintViolationException)cause;
			else if(cause instanceof IllegalArgumentException)
				throw (IllegalArgumentException)cause;
			else
				throw new RuntimeException(e);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString()
	{
		return javaClass.getName();
	}

	// serialization -------------

	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	@Serial
	private Object writeReplace()
	{
		return new Serialized(javaClass);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@Serial
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private record Serialized(Class<? extends Block> javaClass) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 1l;

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		@Serial
		private Object readResolve() throws InvalidObjectException
		{
			final BlockType<?> result = types.get(javaClass);
			if(result==null)
				throw new InvalidObjectException("type does not exist: " + javaClass);
			return result;
		}
	}

	// static registry

	private static final HashMap<Class<?>, BlockType<?>> types = new HashMap<>();

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see TypesBound#forClass(Class)
	 */
	public static <T extends Block> BlockType<T> forClass(final Class<T> javaClass)
	{
		return forClassUnchecked(javaClass).as(javaClass);
	}

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see TypesBound#forClassUnchecked(Class)
	 */
	public static BlockType<?> forClassUnchecked(final Class<? extends Block> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		final BlockType<?> result = types.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}

	/**
	 * @deprecated Use {@link #newType(Class, Function)} instead.
	 */
	@Deprecated
	public static <T extends Block> BlockType<T> newType(final Class<T> javaClass)
	{
		return newType(javaClass, reflectionActivator(javaClass));
	}

	public static <T extends Block> BlockType<T> newType(
			final Class<T> javaClass,
			final Function<BlockActivationParameters,T> activator)
	{
		assertFinalSubClass(BlockField.class, Block.class, javaClass);
		requireNonNull(activator, "activator");
		if(types.containsKey(javaClass))
			throw new IllegalArgumentException("class is already bound to a type: " + javaClass.getName());

		final BlockType<T> result = new BlockType<>(javaClass, activator);
		types.put(javaClass, result);

		return result;
	}
}
