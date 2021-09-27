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

import static com.exedio.cope.Intern.intern;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ListUtil;
import com.exedio.cope.misc.LocalizationKeys;
import com.exedio.cope.util.CharSet;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public abstract class Feature implements Serializable
{
	private static final AtomicInteger instantiationOrderSource = new AtomicInteger(Integer.MIN_VALUE);
	final int instantiationOrder = instantiationOrderSource.getAndIncrement();

	static final CharSet NAME_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');
	private Mount mountIfMounted = null;

	private abstract static class Mount
	{
		final AbstractType<?> type;
		final String name;
		private final AnnotatedElement annotationSource;
		final Pattern pattern;

		Mount(
				final AbstractType<?> type,
				final String name,
				final AnnotatedElement annotationSource,
				final Pattern pattern)
		{
			this.type = requireNonNull(type);
			this.name = intern(requireNonNull(name));
			this.annotationSource = annotationSource;
			this.pattern = pattern;
		}

		boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			return
				annotationSource!=null &&
				annotationSource.isAnnotationPresent(annotationClass);
		}

		<A extends Annotation> A getAnnotation(final Class<A> annotationClass)
		{
			return
				annotationSource!=null
				? annotationSource.getAnnotation(annotationClass)
				: null;
		}

		private List<String> localizationKeysIfInitialized = null;

		@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // result of ListUtil#trimUnmodifiable is unmodifiable
		final List<String> getLocalizationKeys()
		{
			if(localizationKeysIfInitialized!=null)
				return localizationKeysIfInitialized;

			final ArrayList<String> result = new ArrayList<>();
			addLocalizationKeys(result);
			localizationKeysIfInitialized = ListUtil.trimUnmodifiable(result);
			return localizationKeysIfInitialized;
		}

		void addLocalizationKeys(final ArrayList<String> result)
		{
			final String suffix = '.' + name.replace('-', '.');
			for(final String prefix : type.getLocalizationKeys())
				result.add(prefix + suffix);

			final int dashIndex = name.lastIndexOf('-');
			result.add(dashIndex>=0 ? name.substring(dashIndex+1) : name);
		}

		abstract void toString(StringBuilder bf, Type<?> defaultType);
		abstract Serializable serializable();
	}

	private static final class MountType extends Mount
	{
		final Type<?> type;
		final String id;
		final Class<?> precedingLocalizationKeysClass;
		final String precedingLocalizationKeysPostfix;

		MountType(
				final Type<?> type,
				final String name,
				final AnnotatedElement annotationSource,
				final Pattern pattern,
				final Class<?> precedingLocalizationKeysClass,
				final String   precedingLocalizationKeysPostfix)
		{
			super(type, name, annotationSource, pattern);
			this.type = requireNonNull(type);
			this.id =   intern(type.id + '.' + name);
			this.precedingLocalizationKeysClass   = precedingLocalizationKeysClass;
			this.precedingLocalizationKeysPostfix = precedingLocalizationKeysPostfix;
		}

		@Override
		boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			if(Computed.class==annotationClass && getAnnotation(annotationClass)!=null)
				return true;

			return super.isAnnotationPresent(annotationClass);
		}

		@Override
		<A extends Annotation> A getAnnotation(final Class<A> annotationClass)
		{
			if(Computed.class==annotationClass)
			{
				final Pattern pattern = type.getPattern();
				if(pattern!=null)
				{
					final A typePatternAnn = pattern.getAnnotation(annotationClass);
					if(typePatternAnn!=null)
						return typePatternAnn;
				}
			}

			return super.getAnnotation(annotationClass);
		}

		@Override
		void addLocalizationKeys(final ArrayList<String> result)
		{
			if(precedingLocalizationKeysClass!=null)
			{
				final String suffix = '.' + precedingLocalizationKeysPostfix;
				for(final String prefix : LocalizationKeys.get(precedingLocalizationKeysClass))
					result.add(prefix + suffix);
			}

			super.addLocalizationKeys(result);
		}

		@Override
		void toString(final StringBuilder bf, final Type<?> defaultType)
		{
			bf.append((defaultType==type) ? name : id);
		}

		@Override
		public String toString()
		{
			return id;
		}

		@Override
		Serializable serializable()
		{
			return new Serialized(this);
		}
	}

	private static final class MountAbstractType extends Mount
	{
		private final String string;
		private final Serializable serializable;

		MountAbstractType(
				final AbstractType<?> type,
				final String name,
				final String string,
				final Serializable serializable,
				final AnnotatedElement annotationSource)
		{
			super(type, name, annotationSource, null);
			this.string = requireNonNull(string);
			this.serializable = requireNonNull(serializable);
		}

		@Override
		void toString(final StringBuilder bf, final Type<?> defaultType)
		{
			bf.append(string);
		}

		@Override
		public String toString()
		{
			return string;
		}

		@Override
		Serializable serializable()
		{
			return serializable;
		}
	}

	/**
	 * Is called in the constructor of the containing type.
	 */
	void mount(
			final Type<?> type,
			final String name,
			final AnnotatedElement annotationSource)
	{
		{
			final int i = NAME_CHAR_SET.indexOfNotContains(name);
			if(i>=0)
				throw new IllegalArgumentException("name >" + name + "< of feature in type " + type + " contains illegal character >" + name.charAt(i) + "< at position " + i);
		}

		if(mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted);
		mountIfMounted = new MountType(
				type, name, annotationSource, patternUntilMount,
				precedingLocalizationKeysClassUntilMount,
				precedingLocalizationKeysPostfixUntilMount);

		type.registerMounted(this);

		this.patternUntilMount = null;
		this.precedingLocalizationKeysClassUntilMount   = null;
		this.precedingLocalizationKeysPostfixUntilMount = null;
	}

	public final void mount(
			final AbstractType<?> type,
			final String name,
			final String string,
			final Serializable serializable,
			final AnnotatedElement annotationSource)
	{
		requireNonNull(type, "type");
		requireNonNull(name, "name");
		requireNonNull(string, "string");
		requireNonNull(serializable, "serializable");
		if(mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted);
		mountIfMounted = new MountAbstractType(type, name, string, serializable, annotationSource);
	}

	public static final <T> T requireMounted(final T result)
	{
		if(result==null)
			throw new IllegalStateException("feature not mounted");
		return result;
	}

	private Mount mountAny()
	{
		return requireMounted(mountIfMounted);
	}

	private MountType mountType()
	{
		final Mount result = mountAny();
		if(!(result instanceof MountType))
			throw new IllegalStateException(
					"feature not mounted to a type, but to " + result.type.getClass().getName() + ": " + result);
		return (MountType)result;
	}

	final void assertNotMounted()
	{
		if(mountIfMounted!=null)
			throw new RuntimeException("must be called before mounting the feature");
	}

	final boolean isMountedToType()
	{
		return mountIfMounted instanceof MountType;
	}

	/**
	 * @see #getType()
	 */
	public AbstractType<?> getAbstractType()
	{
		return mountAny().type;
	}

	/**
	 * @see #getAbstractType()
	 */
	public Type<?> getType()
	{
		return mountType().type;
	}

	public final String getName()
	{
		return mountAny().name;
	}

	/**
	 * @see Model#getFeature(String)
	 */
	public final String getID()
	{
		return mountType().id;
	}

	protected final <P extends Item> Type<P> requireParentClass(
			@Nonnull final Class<P> parentClass,
			@Nonnull final String message)
	{
		requireNonNull(parentClass, message);
		final Class<?> typeClass = getType().getJavaClass();
		if(!typeClass.equals(parentClass))
			throw new ClassCastException(
					message + " " +
					"requires " + typeClass.getName() + ", " +
					"but was " + parentClass.getName());
		return getType().as(parentClass);
	}

	/**
	 * @see Class#isAnnotationPresent(Class)
	 */
	public final boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
	{
		return mountAny().isAnnotationPresent(annotationClass);
	}

	/**
	 * @see Class#getAnnotation(Class)
	 */
	public final <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
	{
		return mountAny().getAnnotation(annotationClass);
	}

	/**
	 * @see com.exedio.cope.misc.LocalizationKeys
	 */
	public final List<String> getLocalizationKeys()
	{
		return mountAny().getLocalizationKeys();
	}

	final String getDeclaredSchemaName()
	{
		final CopeSchemaName annotation =
			getAnnotation(CopeSchemaName.class);
		if(annotation==null)
			return getName();

		final String result = annotation.value();
		if(result.isEmpty())
			throw new IllegalArgumentException("@CopeSchemaName(\"\") for " + getID());
		return result;
	}

	public Collection<String> getSuspicions()
	{
		return Collections.emptyList();
	}

	/**
	 * @param defaultType is used by subclasses
	 */
	void toStringNotMounted(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append(super.toString());
	}

	@Override
	public final String toString()
	{
		final Mount mount = mountIfMounted;
		if(mount!=null)
		{
			return mount.toString();
		}
		else
		{
			final StringBuilder bf = new StringBuilder();
			toStringNotMounted(bf, null);
			return bf.toString();
		}
	}

	public final void toString(final StringBuilder bf, final Type<?> defaultType)
	{
		final Mount mount = mountIfMounted;
		if(mount!=null)
			mount.toString(bf, defaultType);
		else
			toStringNotMounted(bf, defaultType);
	}

	// patterns ------------------

	private Pattern patternUntilMount = null;
	private Class<?> precedingLocalizationKeysClassUntilMount;
	private String   precedingLocalizationKeysPostfixUntilMount;

	final void registerPattern(
			final Pattern pattern,
			final Class<?> precedingLocalizationKeysClass,
			final String precedingLocalizationKeysPostfix)
	{
		assertNotMounted();
		requireNonNull(pattern);

		if(patternUntilMount!=null)
			throw new IllegalStateException(
					"feature has already registered pattern " + patternUntilMount +
					" and tried to register a new one: " + pattern);

		patternUntilMount = pattern;
		precedingLocalizationKeysClassUntilMount   = precedingLocalizationKeysClass;
		precedingLocalizationKeysPostfixUntilMount = precedingLocalizationKeysPostfix;
	}

	public final boolean isSourceAlready()
	{
		assertNotMounted();
		return patternUntilMount!=null;
	}

	/**
	 * Returns the pattern, this feature is a source feature of.
	 * NOTE:
	 * Does not return the pattern that created the type
	 * of this feature. For such cases use
	 * {@link #getType()}.{@link Type#getPattern() getPattern()}.
	 *
	 * @see Pattern#getSourceFeatures()
	 */
	public final Pattern getPattern()
	{
		return mountAny().pattern;
	}


	/**
	 * This method is called before the termination of any constructor of class
	 * {@link Model}.
	 * It allows any initialization of the feature, that cannot be done earlier.
	 * The default implementation is empty.
	 */
	protected void afterModelCreated()
	{
		// empty default implementation
	}

	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	protected final Object writeReplace() throws ObjectStreamException
	{
		final Mount mount = mountIfMounted;
		if(mount==null)
		{
			if(isSerializableNonMounted())
				return this;
			else
				throw new NotSerializableException(getClass().getName());
		}

		return mount.serializable();
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		if(isSerializableNonMounted())
			ois.defaultReadObject();
		else
			throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	protected final Object readResolve() throws InvalidObjectException
	{
		if(isSerializableNonMounted())
			return this;
		else
			throw new InvalidObjectException("required " + Serialized.class);
	}

	private boolean isSerializableNonMounted()
	{
		return this instanceof View; // TODO something more generic
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;

		private final Type<?> type;
		private final String name;

		Serialized(final MountType mount)
		{
			this.type = mount.type;
			this.name = mount.name;
		}

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve() throws InvalidObjectException
		{
			final Feature result = type.getDeclaredFeature(name);
			if(result==null)
				throw new InvalidObjectException("feature does not exist: " + type + '.' + name);
			return result;
		}
	}
}
