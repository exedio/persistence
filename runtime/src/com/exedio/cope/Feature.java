/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.CharSet;

public abstract class Feature implements Serializable
{
	static final CharSet NAME_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');
	private Mount mountIfMounted = null;

	private static abstract class Mount
	{
		private final AnnotatedElement annotationSource;

		Mount(final AnnotatedElement annotationSource)
		{
			this.annotationSource = annotationSource;
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

		abstract void toString(StringBuilder bf, Type defaultType);
	}

	private static final class MountType extends Mount
	{
		final Type<? extends Item> type;
		final String name;
		final String id;

		MountType(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
		{
			super(annotationSource);
			assert type!=null;
			assert name!=null;

			this.type = type;
			this.name = intern(name);
			this.id =   intern(type.id + '.' + name);
		}

		@Override
		void toString(final StringBuilder bf, final Type defaultType)
		{
			bf.append((defaultType==type) ? name : id);
		}

		@Override
		public String toString()
		{
			return id;
		}
	}

	private static final class MountString extends Mount
	{
		private final String string;

		MountString(final String string, final AnnotatedElement annotationSource)
		{
			super(annotationSource);
			assert string!=null;

			this.string = string;
		}

		@Override
		void toString(final StringBuilder bf, final Type defaultType)
		{
			bf.append(string);
		}

		@Override
		public String toString()
		{
			return string;
		}
	}

	/**
	 * Is called in the constructor of the containing type.
	 */
	void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		{
			final int i = NAME_CHAR_SET.indexOfNotContains(name);
			if(i>=0)
				throw new IllegalArgumentException("name >" + name + "< of feature in type " + type + " contains illegal character >" + name.charAt(i) + "< at position " + i);
		}

		if(this.mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted.toString());
		this.mountIfMounted = new MountType(type, name, annotationSource);

		type.registerMounted(this);

		this.pattern = this.patternUntilMount;
		this.patternUntilMount = null;
	}

	public final void mount(final String string, final AnnotatedElement annotationSource)
	{
		if(string==null)
			throw new NullPointerException("string");
		if(this.mountIfMounted!=null)
			throw new IllegalStateException("feature already mounted: " + mountIfMounted.toString());
		this.mountIfMounted = new MountString(string, annotationSource);
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

	final boolean isMounted()
	{
		return mountIfMounted!=null;
	}

	final boolean isMountedToType()
	{
		final Mount mount = mountIfMounted;
		return (mount!=null) && (mount instanceof MountType);
	}

	public Type<? extends Item> getType()
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

	final String getSchemaName()
	{
		final CopeSchemaName annotation =
			getAnnotation(CopeSchemaName.class);
		return
			annotation!=null
			? annotation.value()
			: getName();
	}

	public List<Wrapper> getWrappers()
	{
		return Collections.<Wrapper>emptyList();
	}

	/**
	 * @param defaultType is used by subclasses
	 */
	void toStringNotMounted(final StringBuilder bf, final Type defaultType)
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

	public final void toString(final StringBuilder bf, final Type defaultType)
	{
		final Mount mount = this.mountIfMounted;
		if(mount!=null)
			mount.toString(bf, defaultType);
		else
			toStringNotMounted(bf, defaultType);
	}

	// patterns ------------------

	private Pattern patternUntilMount = null;
	private Pattern pattern = null;

	final void registerPattern(final Pattern pattern)
	{
		if(isMounted())
			throw new RuntimeException("registerPattern must be called before mounting the feature.");
		if(pattern==null)
			throw new NullPointerException();

		if(patternUntilMount!=null)
			throw new IllegalStateException("feature has already registered pattern " + this.patternUntilMount + " and tried to register a new one: " + pattern);

		this.patternUntilMount = pattern;
	}

	/**
	 * @see Pattern#getSourceFields()
	 */
	public final Pattern getPattern()
	{
		if(!isMounted())
			throw new RuntimeException("getPattern must be called after mounting the feature.");
		if(patternUntilMount!=null)
			throw new RuntimeException();

		return pattern;
	}

	// instantiation order ----------------

	private static final AtomicInteger instantiationOrderSource = new AtomicInteger(Integer.MIN_VALUE);

	final int instantiationOrder = instantiationOrderSource.getAndIncrement();

	static final Comparator<Feature> INSTANTIATION_COMPARATOR = new Comparator<Feature>()
	{
		@Override
		public int compare(final Feature f1, final Feature f2)
		{
			if(f1==f2)
				return 0;

			final int o1 = f1.instantiationOrder;
			final int o2 = f2.instantiationOrder;

			if(o1<o2)
				return -1;
			else
			{
				assert o1>o2 : f1.toString() + '/' + f2;
				return 1;
			}
		}
	};

	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	protected final Object writeReplace() throws ObjectStreamException
	{
		final Mount mount = this.mountIfMounted;
		if(mount==null)
			throw new NotSerializableException(getClass().getName());
		if(!(mount instanceof MountType))
			throw new NotSerializableException(mount.toString());

		return new Serialized((MountType)mount);
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;

		private final Type type;
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
