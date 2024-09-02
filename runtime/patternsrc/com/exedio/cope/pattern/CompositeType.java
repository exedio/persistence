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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
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

public final class CompositeType<T extends Composite> implements TemplatedType<T>
{
	private final Class<T> javaClass;
	private final Constructor<T> constructor;
	private final LinkedHashMap<String, FunctionField<?>> templates = new LinkedHashMap<>();
	private final HashMap<FunctionField<?>, Integer> templatePositions = new HashMap<>();
	final List<? extends FunctionField<?>> templateList;
	final int componentSize;

	private static final HashMap<FunctionField<?>, String> templateNames = new HashMap<>();

	private final LinkedHashMap<String, CheckConstraint> constraints = new LinkedHashMap<>();

	private final LinkedHashMap<String, Feature> templatesFeature;
	private final List<Feature> templateListFeature;

	private CompositeType(final Class<T> javaClass)
	{
		//System.out.println("---------------new Composite.Type(" + vc + ')');
		this.javaClass = javaClass;
		this.constructor = getConstructor(javaClass, SetValue[].class);
		final String id = javaClass.getName();
		final LinkedHashMap<String, Feature> templatesFeature = new LinkedHashMap<>();
		{
			int position = 0;
			for(final Map.Entry<Feature, java.lang.reflect.Field> entry : TypesBound.getFeatures(javaClass).entrySet())
			{
				final Feature feature = entry.getKey();
				final java.lang.reflect.Field field = entry.getValue();
				final String fieldID = id + '#' + field.getName();
				final String fieldName = CopeNameUtil.getAndFallbackToName(field);
				if(feature instanceof final FunctionField<?> template)
				{
					if(template.isFinal())
						throw new IllegalArgumentException("final fields not supported: " + fieldID);
					if(template.hasDefault() && template.getDefaultConstant()==null)
						throw new IllegalArgumentException("fields with non-constant defaults are not supported: " + fieldID);
					templates.put(fieldName, template);
					templatePositions.put(template, position++);
					templateNames.put(template, fieldName);
				}
				else if(feature instanceof CheckConstraint)
				{
					constraints.put(fieldName, (CheckConstraint)feature);
				}
				else
				{
					throw new IllegalArgumentException(
							fieldID + " must be an instance of " +
							FunctionField.class + " or " + CheckConstraint.class);
				}

				//noinspection ThisEscapedInObjectConstruction
				feature.mount(this, fieldName, fieldID, SerializedReflectionField.make(feature, field), field);
				templatesFeature.put(fieldName, feature);
			}
		}

		if(templates.isEmpty())
			throw new IllegalArgumentException(
					"composite has no templates: " + javaClass.getName());

		this.templateList = List.copyOf(templates.values());
		this.componentSize = templates.size();
		this.templatesFeature = templatesFeature;
		this.templateListFeature = List.copyOf(templatesFeature.values());
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
	public <X extends Composite> CompositeType<X> as(final Class<X> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		if(javaClass!=this.javaClass)
			throw new ClassCastException(
					"expected " + javaClass.getName() + ", " +
					"but was " + this.javaClass.getName());

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final CompositeType<X> result = (CompositeType<X>)this;
		return result;
	}

	Object[] values(final SetValue<?>[] setValues, final Object[] valuesBefore)
	{
		final Object[] values = new Object[componentSize];
		final boolean[] valueSet = new boolean[values.length];
		for(final SetValue<?> v : setValues)
		{
			final int position = position((FunctionField<?>)v.settable);
			values[position] = v.value;
			valueSet[position] = true;
		}
		for(int i = 0; i<valueSet.length; i++)
			if(!valueSet[i])
				values[i] =
						valuesBefore==null
						? templateList.get(i).getDefaultConstant() // create
						: valuesBefore[i]; // set

		int i = 0;
		for(final FunctionField<?> ff : templateList)
			check(ff, values[i++]);

		if(!constraints.isEmpty())
		{
			final HashMap<FunctionField<?>, Object> valueMap = new HashMap<>();
			i = 0;
			for(final FunctionField<?> ff : templateList)
				valueMap.put(ff, values[i++]);
			for(final CheckConstraint cc : constraints.values())
				cc.check(valueMap);
		}

		return values;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void check(final FunctionField field, final Object value)
	{
		field.check(value);
	}

	Map<String,FunctionField<?>> getTemplateMap()
	{
		return Collections.unmodifiableMap(templates);
	}

	Map<String, CheckConstraint> getConstraintMap()
	{
		return Collections.unmodifiableMap(constraints);
	}

	@Override
	public CompositeType<? super T> getSupertype()
	{
		return null;
	}

	@Override
	public List<? extends CompositeType<? extends T>> getSubtypes()
	{
		return List.of();
	}

	@Override
	public List<? extends Feature> getDeclaredFeatures()
	{
		return templateListFeature;
	}

	@Override
	public List<? extends Feature> getFeatures()
	{
		return templateListFeature;
	}

	@Override
	public Feature getDeclaredFeature(final String name)
	{
		return templatesFeature.get(name);
	}

	@Override
	public Feature getFeature(final String name)
	{
		return templatesFeature.get(name);
	}

	private List<String> localizationKeysIfInitialized = null;

	@Override
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // result of LocalizationKeys#get is unmodifiable
	public List<String> getLocalizationKeys()
	{
		if(localizationKeysIfInitialized!=null)
			return localizationKeysIfInitialized;

		localizationKeysIfInitialized = LocalizationKeys.get(javaClass);
		return localizationKeysIfInitialized;
	}

	int position(final FunctionField<?> member)
	{
		final Integer result = templatePositions.get(member);
		if(result==null)
			throw new IllegalArgumentException("not a member");
		return result;
	}

	public T newValue(final SetValue<?>... setValues)
	{
		try
		{
			return constructor.newInstance(new Object[]{setValues});
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

	private record Serialized(Class<? extends Composite> javaClass) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 1l;

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		@Serial
		private Object readResolve() throws InvalidObjectException
		{
			final CompositeType<?> result = types.get(javaClass);
			if(result==null)
				throw new InvalidObjectException("type does not exist: " + javaClass);
			return result;
		}
	}

	// static registry

	private static final HashMap<Class<?>, CompositeType<?>> types = new HashMap<>();

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see TypesBound#forClass(Class)
	 */
	public static <T extends Composite> CompositeType<T> forClass(final Class<T> javaClass)
	{
		return forClassUnchecked(javaClass).as(javaClass);
	}

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see TypesBound#forClassUnchecked(Class)
	 */
	public static CompositeType<?> forClassUnchecked(final Class<? extends Composite> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		final CompositeType<?> result = types.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}

	/**
	 * @deprecated
	 * This method should not be used anymore.
	 * You may use {@link #forClass(Class)} if suitable.
	 * Note, that in contrast to this method {@link #forClass(Class)}
	 * does not create a new {@code CompositeType} for the given {@code javaClass}
	 * if none exists already.
	 */
	@Deprecated
	public static <T extends Composite> CompositeType<T> get(final Class<T> javaClass)
	{
		return newTypeOrExisting(javaClass);
	}

	static <T extends Composite> CompositeType<T> newTypeOrExisting(final Class<T> javaClass)
	{
		assertFinalSubClass(CompositeField.class, Composite.class, javaClass);

		synchronized(types)
		{
			@SuppressWarnings({"unchecked","rawtypes"})
			CompositeType<T> result = (CompositeType)types.get(javaClass);
			if(result==null)
			{
				result = new CompositeType<>(javaClass);
				types.put(javaClass, result);
			}

			return result;
		}
	}

	static String getTemplateName(final FunctionField<?> template)
	{
		requireNonNull(template, "template");

		final String result = templateNames.get(template);
		if(result==null)
			throw new IllegalStateException("feature not mounted to a composite: " + template);

		return result;
	}
}
