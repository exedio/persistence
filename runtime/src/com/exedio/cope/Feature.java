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
import com.exedio.cope.util.CharSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Feature implements Serializable
{
	private static final AtomicInteger instantiationOrderSource = new AtomicInteger(Integer.MIN_VALUE);
	final int instantiationOrder = instantiationOrderSource.getAndIncrement();

	static final CharSet NAME_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');
	private Mount mountIfMounted = null;

	private static abstract class Mount
	{
		private final AnnotatedElement annotationSource;
		final Pattern pattern;

		Mount(final AnnotatedElement annotationSource, final Pattern pattern)
		{
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

		abstract void toString(StringBuilder bf, Type<?> defaultType);
		abstract Serializable serializable();
	}

	private static final class MountType extends Mount
	{
		final Type<?> type;
		final String name;
		final String id;

		MountType(
				final Type<?> type,
				final String name,
				final AnnotatedElement annotationSource,
				final Pattern pattern)
		{
			super(annotationSource, pattern);
			assert type!=null;
			assert name!=null;

			this.type = type;
			this.name = intern(name);
			this.id =   intern(type.id + '.' + name);
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

	private static final class MountString extends Mount
	{
		private final String string;
		private final Serializable serializable;

		MountString(final String string, final Serializable serializable, final AnnotatedElement annotationSource)
		{
			super(annotationSource, null);
			assert string!=null;
			assert serializable!=null;

			this.string = string;
			this.serializable = serializable;
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
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		{
			final int i = NAME_CHAR_SET.indexOfNotContains(name);
			if(i>=0)
				throw new IllegalArgumentException("name >" + name + "< of feature in type " + type + " contains illegal character >" + name.charAt(i) + "< at position " + i);
		}

		if(this.mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted.toString());
		this.mountIfMounted = new MountType(type, name, annotationSource, patternUntilMount);

		type.registerMounted(this);

		this.patternUntilMount = null;
	}

	public final void mount(final String string, final Serializable serializable, final AnnotatedElement annotationSource)
	{
		requireNonNull(string, "string");
		requireNonNull(serializable, "serializable");
		if(this.mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted.toString());
		this.mountIfMounted = new MountString(string, serializable, annotationSource);
	}

	private final Mount mount()
	{
		final Mount result = this.mountIfMounted;
		if(result==null)
			throw new IllegalStateException("feature not mounted");
		return result;
	}

	private final MountType mountType()
	{
		final Mount result = mount();
		if(!(result instanceof MountType))
			throw new IllegalStateException("feature not mounted to a type: " + result.toString());
		return (MountType)result;
	}

	final void assertNotMounted()
	{
		if(mountIfMounted!=null)
			throw new RuntimeException("must be called before mounting the feature");
	}

	final boolean isMountedToType()
	{
		final Mount mount = mountIfMounted;
		return (mount!=null) && (mount instanceof MountType);
	}

	public Type<?> getType()
	{
		return mountType().type;
	}

	public final String getName()
	{
		return mountType().name;
	}

	/**
	 * @see Model#getFeature(String)
	 */
	@SuppressFBWarnings("NM_CONFUSING") // Confusing method names, the referenced methods have names that differ only by capitalization.
	public final String getID()
	{
		return mountType().id;
	}

	/**
	 * @see Class#isAnnotationPresent(Class)
	 */
	public final boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
	{
		return mount().isAnnotationPresent(annotationClass);
	}

	/**
	 * @see Class#getAnnotation(Class)
	 */
	public final <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
	{
		return mount().getAnnotation(annotationClass);
	}

	final String getDeclaredSchemaName()
	{
		final CopeSchemaName annotation =
			getAnnotation(CopeSchemaName.class);
		return
			annotation!=null
			? annotation.value()
			: getName();
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
		final Mount mount = this.mountIfMounted;
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
		final Mount mount = this.mountIfMounted;
		if(mount!=null)
			mount.toString(bf, defaultType);
		else
			toStringNotMounted(bf, defaultType);
	}

	// patterns ------------------

	private Pattern patternUntilMount = null;

	final void registerPattern(final Pattern pattern)
	{
		assertNotMounted();
		assert pattern!=null;

		if(patternUntilMount!=null)
			throw new IllegalStateException(
					"feature has already registered pattern " + this.patternUntilMount +
					" and tried to register a new one: " + pattern);

		this.patternUntilMount = pattern;
	}

	public final boolean isSourceAlready()
	{
		assertNotMounted();
		return patternUntilMount!=null;
	}

	/**
	 * @see Pattern#getSourceFeatures()
	 */
	public final Pattern getPattern()
	{
		return mount().pattern;
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
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	protected final Object writeReplace() throws ObjectStreamException
	{
		final Mount mount = this.mountIfMounted;
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
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws IOException, ClassNotFoundException
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
		 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
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
