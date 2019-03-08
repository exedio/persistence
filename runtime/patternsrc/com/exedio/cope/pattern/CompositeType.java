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
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
import com.exedio.cope.TypesBound;
import com.exedio.cope.misc.CopeNameUtil;
import com.exedio.cope.misc.LocalizationKeys;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CompositeType<T extends Composite> implements TemplatedType<T>
{
	private final Class<T> javaClass;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Constructor<T> constructor;
	private final LinkedHashMap<String, FunctionField<?>> templates = new LinkedHashMap<>();
	private final HashMap<FunctionField<?>, Integer> templatePositions = new HashMap<>();
	final List<? extends FunctionField<?>> templateList;
	final int componentSize;

	private static final HashMap<FunctionField<?>, String> templateNames = new HashMap<>();

	private CompositeType(final Class<T> javaClass)
	{
		//System.out.println("---------------new Composite.Type(" + vc + ')');
		this.javaClass = javaClass;
		this.constructor = getConstructor(javaClass, SetValue[].class);
		final String id = javaClass.getName();
		{
			int position = 0;
			for(final Map.Entry<Feature, java.lang.reflect.Field> entry : TypesBound.getFeatures(javaClass).entrySet())
			{
				final Feature feature = entry.getKey();
				final java.lang.reflect.Field field = entry.getValue();
				final String fieldID = id + '#' + field.getName();
				if(!(feature instanceof FunctionField<?>))
					throw new IllegalArgumentException(fieldID + " must be an instance of " + FunctionField.class);
				final FunctionField<?> template = (FunctionField<?>)feature;
				if(template.isFinal())
					throw new IllegalArgumentException("final fields not supported: " + fieldID);
				if(template.hasDefault() && template.getDefaultConstant()==null)
					throw new IllegalArgumentException("fields with non-constant defaults are not supported: " + fieldID);
				final String fieldName = CopeNameUtil.getAndFallbackToName(field);
				templates.put(fieldName, template);
				templatePositions.put(template, position++);
				//noinspection ThisEscapedInObjectConstruction
				template.mount(this, fieldName, fieldID, SerializedReflectionField.make(feature, field), field);
				templateNames.put(template, fieldName);
			}
		}
		this.templateList = Collections.unmodifiableList(new ArrayList<>(templates.values()));
		this.componentSize = templates.size();
	}

	@Override
	public Class<T> getJavaClass()
	{
		return javaClass;
	}

	Object[] values(final SetValue<?>... setValues)
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
				values[i] = templateList.get(i).getDefaultConstant();

		int i = 0;
		for(final FunctionField<?> ff : templateList)
			check(ff, values[i++]);

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

	@Override
	public CompositeType<? super T> getSupertype()
	{
		return null;
	}

	@Override
	public List<? extends CompositeType<? extends T>> getSubtypes()
	{
		return Collections.emptyList();
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // templateList is unmodifiable
	@Override
	public List<? extends Feature> getDeclaredFeatures()
	{
		return templateList;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // templateList is unmodifiable
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

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	private Object writeReplace()
	{
		return new Serialized(javaClass);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;

		private final Class<? extends Composite> javaClass;

		Serialized(final Class<? extends Composite> javaClass)
		{
			this.javaClass = javaClass;
		}

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
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

	public static <T extends Composite> CompositeType<T> get(final Class<T> javaClass)
	{
		assertFinalSubClass(CompositeField.class, Composite.class, javaClass);

		synchronized(types)
		{
			@SuppressWarnings("unchecked")
			CompositeType<T> result = (CompositeType)types.get(javaClass);
			if(result==null)
			{
				result = new CompositeType<>(javaClass);
				types.put(javaClass, result);
			}

			if(result.componentSize==0)
				throw new IllegalArgumentException("composite has no templates: " + javaClass.getName());

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
