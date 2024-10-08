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

package com.exedio.cope;

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.CopeSchemaNameElement;
import java.io.Serial;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A common super class for all patterns.
 *
 * @author Ralf Wiebicke
 */
public abstract class Pattern extends Feature
{
	@Serial
	private static final long serialVersionUID = 1l;

	private Features sourceFeaturesGather = new Features();
	private List<Feature> sourceFeatureList = null;

	private ArrayList<Type<?>> sourceTypesWhileGather = new ArrayList<>();
	private List<Type<?>> sourceTypes = null;

	/**
	 * @return parameter {@code feature}
	 * @see #getSourceFeatures()
	 */
	protected final <F extends Feature> F addSourceFeature(
			final F feature,
			final String postfix)
	{
		return addSourceFeature(feature, postfix, null, getClass());
	}

	/**
	 * @return parameter {@code feature}
	 * @see #getSourceFeatures()
	 */
	protected final <F extends Feature> F addSourceFeature(
			final F feature,
			final String postfix,
			final AnnotatedElement annotationSource)
	{
		return addSourceFeature(feature, postfix, annotationSource, getClass());
	}

	/**
	 * @return parameter {@code feature}
	 * @see #getSourceFeatures()
	 */
	protected final <F extends Feature> F addSourceFeature(
			final F feature,
			final String postfix,
			final AnnotatedElement annotationSource,
			final Class<?> innerLocalizationKeysClass)
	{
		requireNonNull(feature, "feature");
		requireNonEmpty(postfix, "postfix");
		requireNonNull(innerLocalizationKeysClass, "innerLocalizationKeysClass");
		if(sourceFeaturesGather==null)
			throw new IllegalStateException("addSourceFeature can be called only until pattern is mounted, not afterwards");
		assert sourceFeatureList==null;
		feature.registerPattern(this, innerLocalizationKeysClass, postfix);
		sourceFeaturesGather.put(postfix, feature, new SourceFeatureAnnotationProxy(annotationSource, postfix));
		return feature;
	}

	private final class SourceFeatureAnnotationProxy implements AnnotatedElement
	{
		private final AnnotatedElement source;
		private final String postfix;

		SourceFeatureAnnotationProxy(final AnnotatedElement source, final String postfix)
		{
			this.source = source;
			this.postfix = postfix;
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass ||
				Computed.class==annotationClass)
			{
				return getAnnotation(annotationClass)!=null;
			}

			if(source==null)
				return false;

			return source.isAnnotationPresent(annotationClass);
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass)
			{
				final CopeSchemaName patternAnn = Pattern.this.getAnnotation(CopeSchemaName.class);
				final CopeSchemaName sourceAnn = source!=null ? source.getAnnotation(CopeSchemaName.class) : null;

				if(patternAnn==null && sourceAnn==null)
					return null;

				final String patternName = patternAnn!=null ? patternAnn.value() : Pattern.this.getName();
				final String sourceName  = sourceAnn !=null ? sourceAnn .value() : postfix;

				final String result;
				if(patternName.isEmpty())
					if(sourceName.isEmpty())
						result = "";
					else
						result = sourceName;
				else
					if(sourceName.isEmpty())
						result = patternName;
					else
						result = patternName + '-' + sourceName;

				return annotationClass.cast(CopeSchemaNameElement.get(result));
			}
			else if(Computed.class==annotationClass)
			{
				final T patternAnn = Pattern.this.getAnnotation(annotationClass);
				if(patternAnn!=null)
					return patternAnn;
				return source!=null ? source.getAnnotation(annotationClass) : null;
			}

			if(source==null)
				return null;

			return source.getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations()
		{
			throw new RuntimeException(Pattern.this.toString());
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException(Pattern.this.toString());
		}

		@Override
		public String toString()
		{
			return Pattern.this + "-sourceFeatureAnnotations";
		}
	}


	private boolean calledOnMount;

	/**
	 * Here you can do additional initialization not yet done in the constructor.
	 * In this method you can call methods {@link #getType()} and {@link #getName()}
	 * for the first time.
	 */
	protected void onMount()
	{
		calledOnMount = true;
	}

	/**
	 * @see #getSourceTypes()
	 * @deprecated Use {@link #newSourceType(Class, Function, Features)} instead
	 */
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Features features)
	{
		return newSourceType(javaClass, Type.reflectionActivator(javaClass), features);
	}

	/**
	 * @see #getSourceTypes()
	 */
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Function<ActivationParameters,T> activator,
			final Features features)
	{
		return newSourceType(javaClass, activator, features, null);
	}

	/**
	 * @see #getSourceTypes()
	 * @deprecated Use {@link #newSourceType(Class, Function, Features, String)} instead
	 */
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Features features,
			final String postfix)
	{
		return newSourceType(javaClass, Type.reflectionActivator(javaClass), features, postfix);
	}

	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Function<ActivationParameters,T> activator,
			final Features features,
			final String postfix)
	{
		return newSourceType(javaClass, activator, null, features, postfix);
	}

	/**
	 * @see #getSourceTypes()
	 * @deprecated Use {@link #newSourceType(Class, Function, Type, Features, String)} instead
	 */
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Type<? super T> supertype,
			final Features features,
			final String postfix)
	{
		return newSourceType(javaClass, Type.reflectionActivator(javaClass), supertype, features, postfix);
	}

	/**
	 * @see #getSourceTypes()
	 */
	protected final <T extends Item> Type<T> newSourceTypeAbstract(
			final Class<T> javaClass,
			final Type<? super T> supertype,
			final Features features,
			final String postfix)
	{
		return newSourceType(javaClass, null, supertype, features, postfix);
	}

	/**
	 * @see #getSourceTypes()
	 */
	protected <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Function<ActivationParameters,T> activator,
			final Type<? super T> supertype,
			final Features features,
			final String postfix)
	{
		if(postfix!=null)
			requireNonEmpty(postfix, "postfix");
		if(sourceTypesWhileGather==null)
			throw new IllegalStateException("newSourceType can be called only until pattern is mounted, not afterwards");
		assert sourceTypes==null;
		final String id = newSourceTypeId(getType().getID(), getName(), postfix);
		final Type<T> result = new Type<>(javaClass, activator, new SourceTypeAnnotationProxy(javaClass, postfix), false, id, this, postfix, supertype, features);
		sourceTypesWhileGather.add(result);
		return result;
	}

	private final class SourceTypeAnnotationProxy implements AnnotatedElement
	{
		private final AnnotatedElement source;
		private final String postfix;

		SourceTypeAnnotationProxy(final Class<? extends Item> source, final String postfix)
		{
			this.source = requireNonNull(source);
			this.postfix = postfix;
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass ||
				Computed.class==annotationClass ||
				CopeCreateLimit.class==annotationClass ||
				CopeExternal.class==annotationClass)
			{
				return getAnnotation(annotationClass)!=null;
			}

			return source.isAnnotationPresent(annotationClass);
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass)
			{
				if(source.getAnnotation(annotationClass)!=null)
					throw new RuntimeException("conflicting @CopeSchemaName on " + Pattern.this);

				final Type<?> type = getType();
				final CopeSchemaName typeAnn = type.getAnnotation(CopeSchemaName.class);
				final CopeSchemaName patternAnn = Pattern.this.getAnnotation(CopeSchemaName.class);
				if(typeAnn!=null || patternAnn!=null)
				{
					return annotationClass.cast(CopeSchemaNameElement.get(
						newSourceTypeId(
							(   typeAnn!=null ?    typeAnn.value() : type.getID()),
							(patternAnn!=null ? patternAnn.value() : Pattern.this.getName()),
							postfix)
					));
				}
			}
			else if(Computed.class==annotationClass || CopeCreateLimit.class==annotationClass || CopeExternal.class==annotationClass)
			{
				final T patternAnn = Pattern.this.getAnnotation(annotationClass);
				if(patternAnn!=null)
					return patternAnn;
				return source.getAnnotation(annotationClass);
			}

			return source.getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations()
		{
			throw new RuntimeException(Pattern.this.toString());
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException(Pattern.this.toString());
		}

		@Override
		public String toString()
		{
			return Pattern.this + "-sourceTypeAnnotations";
		}
	}

	static final String newSourceTypeId(final String type, final String name, final String postfix)
	{
		final StringBuilder bf = new StringBuilder(type);

		if(!name.isEmpty())
			bf.append('-').
				append(name);
		else if(postfix==null)
			bf.append("-default"); // avoids id collision with parent type

		if(postfix!=null)
			bf.append('-').
				append(postfix);

		return bf.toString();
	}

	@Override
	final void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		calledOnMount = false;
		onMount();
		if(!calledOnMount)
			throw new RuntimeException("Method onMount did not call super.onMount in " + getClass().getName() + " or one of it's superclasses.");

		this.sourceFeatureList = sourceFeaturesGather.mountPattern(this, type, name);
		this.sourceFeaturesGather = null;

		this.sourceTypes = List.copyOf(sourceTypesWhileGather);
		this.sourceTypesWhileGather = null;
	}

	/**
	 * @see #addSourceFeature(Feature, String)
	 * @see Feature#getPattern()
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // sourceFeatureList is unmodifiable
	public final List<? extends Feature> getSourceFeatures()
	{
		if(sourceFeatureList==null)
			throw new IllegalStateException("getSourceFeatures can be called only after pattern is mounted, not before");
		assert sourceFeaturesGather==null;
		return sourceFeatureList;
	}

	/**
	 * @see #newSourceType(Class, Features)
	 * @see Type#getPattern()
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // sourceTypes is unmodifiable
	public final List<Type<?>> getSourceTypes()
	{
		if(sourceTypes==null)
			throw new IllegalStateException("getSourceTypes can be called only after pattern is mounted, not before");
		assert sourceTypesWhileGather==null;
		return sourceTypes;
	}

	/**
	 * Forbid override by subclasses.
	 */
	@Override
	public final Type<?> getType()
	{
		return super.getType();
	}

	/**
	 * Forbid override by subclasses.
	 */
	@Override
	public final boolean equals(final Object other)
	{
		return super.equals(other);
	}

	/**
	 * Forbid override by subclasses.
	 */
	@Override
	public final int hashCode()
	{
		return super.hashCode();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @see #getSourceTypes()
	 * @deprecated
	 * Use {@link #newSourceType(Class, Type, Features, String)} instead,
	 * {@code isAbstract} is taken from {@code javaClass}.
	 */
	@Deprecated
	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final boolean isAbstract,
			final Type<? super T> supertype,
			final Features features,
			final String postfix)
	{
		if(javaClass!=null &&
			(isAbstract != Modifier.isAbstract(javaClass.getModifiers())))
			throw new IllegalArgumentException(javaClass + " must" + (isAbstract?"":" not") + " be abstract");

		return newSourceType(javaClass, Type.reflectionActivator(javaClass), supertype, features, postfix);
	}

	/**
	 * @deprecated Use {@link #addSourceFeature(Feature, String)} instead and benefit from result.
	 * @see #getSourceFeatures()
	 */
	@Deprecated
	protected final void addSource(
			final Feature feature,
			final String postfix)
	{
		addSourceFeature(feature, postfix);
	}

	/**
	 * @deprecated Use {@link #addSourceFeature(Feature, String, AnnotatedElement)} instead and benefit from result.
	 * @see #getSourceFeatures()
	 */
	@Deprecated
	protected final void addSource(
			final Feature feature,
			final String postfix,
			final AnnotatedElement annotationSource)
	{
		addSourceFeature(feature, postfix, annotationSource);
	}

	/**
	 * @deprecated Use {@link #addSourceFeature(Feature, String, AnnotatedElement, Class)} instead and benefit from result.
	 * @see #getSourceFeatures()
	 */
	@Deprecated
	protected final void addSource(
			final Feature feature,
			final String postfix,
			final AnnotatedElement annotationSource,
			final Class<?> innerLocalizationKeysClass)
	{
		addSourceFeature(feature, postfix, annotationSource, innerLocalizationKeysClass);
	}
}
