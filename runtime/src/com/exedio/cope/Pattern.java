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

import static com.exedio.cope.misc.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.CopeSchemaNameElement;
import com.exedio.cope.misc.ListUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
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
	private static final long serialVersionUID = 1l;

	private Features sourceFeaturesGather = new Features();
	private List<Feature> sourceFeatureList = null;

	private ArrayList<Type<?>> sourceTypesWhileGather = new ArrayList<>();
	private List<Type<?>> sourceTypes = null;

	protected final void addSource(final Feature feature, final String postfix)
	{
		addSource(feature, postfix, null);
	}

	protected final void addSource(final Feature feature, final String postfix, final AnnotatedElement annotationSource)
	{
		requireNonEmpty(postfix, "postfix");
		requireNonNull(feature, "feature");
		if(sourceFeaturesGather==null)
			throw new IllegalStateException("addSource can be called only until pattern is mounted, not afterwards");
		assert sourceFeatureList==null;
		feature.registerPattern(this);
		sourceFeaturesGather.put(postfix, feature, new SourceFeatureAnnotationProxy(annotationSource, postfix));
	}

	private final class SourceFeatureAnnotationProxy implements AnnotatedElement
	{
		private final AnnotatedElement source;
		final String postfix;

		SourceFeatureAnnotationProxy(final AnnotatedElement source, final String postfix)
		{
			this.source = source;
			this.postfix = postfix;
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass)
			{
				return getAnnotation(annotationClass)!=null;
			}
			else if(Computed.class==annotationClass)
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
				final CopeSchemaName patternName = Pattern.this.getAnnotation(CopeSchemaName.class);
				final CopeSchemaName sourceName = source!=null ? source.getAnnotation(CopeSchemaName.class) : null;

				if(patternName==null && sourceName==null)
					return null;

				final StringBuilder bf = new StringBuilder();

				bf.append(
					patternName!=null
					? patternName.value()
					: Pattern.this.getName());

				if(sourceName!=null)
				{
					final String v = sourceName.value();
					if(!v.isEmpty())
						bf.append('-').append(v);
				}
				else
					bf.append('-').append(postfix);

				return annotationClass.cast(CopeSchemaNameElement.get(bf.toString()));
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
			return Pattern.this.toString() + "-sourceFeatureAnnotations";
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

	protected final <T extends Item> Type<T> newSourceType(
			final Class<T> javaClass,
			final Features features)
	{
		return newSourceType(javaClass, features, null);
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
		if(postfix!=null && postfix.isEmpty())
			throw new IllegalArgumentException("postfix must not be empty");
		if(sourceTypesWhileGather==null)
			throw new IllegalStateException("newSourceType can be called only until pattern is mounted, not afterwards");
		assert sourceTypes==null;
		final String id = newSourceTypeId(getType().getID(), getName(), postfix);
		final Type<T> result = new Type<>(javaClass, new SourceTypeAnnotationProxy(javaClass, postfix), false, id, this, isAbstract, supertype, features);
		sourceTypesWhileGather.add(result);
		return result;
	}

	private final class SourceTypeAnnotationProxy implements AnnotatedElement
	{
		private final AnnotatedElement source;
		final String postfix;

		SourceTypeAnnotationProxy(final AnnotatedElement source, final String postfix)
		{
			this.source = source;
			this.postfix = postfix;
		}

		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass)
				return getAnnotation(annotationClass)!=null;
			else if(Computed.class==annotationClass)
				return getAnnotation(annotationClass)!=null;
			else if(CopeCacheWeight.class==annotationClass)
				return getAnnotation(annotationClass)!=null;

			if(source==null)
				return false;

			return source.isAnnotationPresent(annotationClass);
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			if(CopeSchemaName.class==annotationClass)
			{
				if(source!=null && source.getAnnotation(annotationClass)!=null)
					throw new RuntimeException("conflicting @CopeSchemaName on " + Pattern.this.toString());

				final Type<?> type = getType();
				final CopeSchemaName typeName = type.getAnnotation(CopeSchemaName.class);
				final CopeSchemaName patternName = Pattern.this.getAnnotation(CopeSchemaName.class);
				if(typeName!=null || patternName!=null)
				{
					return annotationClass.cast(CopeSchemaNameElement.get(
						newSourceTypeId(
							(   typeName!=null ?    typeName.value() : type.getID()),
							(patternName!=null ? patternName.value() : Pattern.this.getName()),
							postfix)
					));
				}
			}
			else if(Computed.class==annotationClass)
			{
				final T patternAnn = Pattern.this.getAnnotation(annotationClass);
				if(patternAnn!=null)
					return patternAnn;
				return source.getAnnotation(annotationClass);
			}
			else if(CopeCacheWeight.class==annotationClass)
			{
				final T patternAnn = Pattern.this.getAnnotation(annotationClass);
				if(patternAnn!=null)
					return patternAnn;
				return source.getAnnotation(annotationClass);
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
			return Pattern.this.toString() + "-sourceTypeAnnotations";
		}
	}

	static final String newSourceTypeId(final String type, final String name, final String postfix)
	{
		final StringBuilder bf = new StringBuilder(type);

		bf.append('-').
			append(name);

		if(postfix!=null)
			bf.append('-').
				append(postfix);

		return bf.toString();
	}

	@Override
	final void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);
		initialize();

		calledOnMount = false;
		onMount();
		if(!calledOnMount)
			throw new RuntimeException("Method onMount did not call super.onMount in " + getClass().getName() + " or one of it's superclasses.");

		this.sourceFeatureList = sourceFeaturesGather.mountPattern(type, name);
		this.sourceFeaturesGather = null;

		this.sourceTypes = ListUtil.trimUnmodifiable(sourceTypesWhileGather);
		this.sourceTypesWhileGather = null;
	}

	/**
	 * @see Feature#getPattern()
	 */
	public final List<? extends Feature> getSourceFeatures()
	{
		if(sourceFeatureList==null)
			throw new IllegalStateException("getSourceFeatures can be called only after pattern is mounted, not before");
		assert sourceFeaturesGather==null;
		return sourceFeatureList;
	}

	/**
	 * @see Type#getPattern()
	 */
	public final List<Type<?>> getSourceTypes()
	{
		if(sourceTypes==null)
			throw new IllegalStateException("getSourceTypes can be called only after pattern is mounted, not before");
		assert sourceTypesWhileGather==null;
		return sourceTypes;
	}

	// Make non-final method from super class final
	@Override
	public final Type<?> getType()
	{
		return super.getType();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getSourceFeatures()} instead
	 */
	@Deprecated
	public List<? extends Field<?>> getSourceFields()
	{
		final ArrayList<Field<?>> result = new ArrayList<>();
		for(final Feature f : getSourceFeatures())
			if(f instanceof Field<?>)
				result.add((Field<?>)f);
		return ListUtil.trimUnmodifiable(result);
	}

	/**
	 * @deprecated Use {@link #getSourceFields()} instead
	 */
	@Deprecated
	public final List<? extends Field<?>> getSources()
	{
		return getSourceFields();
	}

	/**
	 * @deprecated Use {@link #getSourceTypes()} instead
	 */
	@Deprecated
	public final List<Type<?>> getGeneratedTypes()
	{
		return getSourceTypes();
	}

	/**
	 * @deprecated Use {@link #addSource(Feature,String)} instead
	 */
	@Deprecated
	protected final void registerSource(final Field<?> field, final String postfix)
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
	 * @deprecated Do not use this method anymore.
	 */
	@Deprecated
	protected final java.lang.reflect.Field annotationField(final String name)
	{
		try
		{
			return getClass().getDeclaredField(name);
		}
		catch(final NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @deprecated For binary compatibility only, use {@link #addSource(Feature,String,AnnotatedElement)} instead.
	 */
	@Deprecated
	protected final void addSource(final Feature feature, final String postfix, final java.lang.reflect.Field annotationSource)
	{
		addSource(feature, postfix, (AnnotatedElement)annotationSource);
	}
}
