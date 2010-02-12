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

import static com.exedio.cope.Intern.intern;

import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.CharSet;

public abstract class Feature implements Serializable
{
	private static final CharSet NAME_CHAR_SET = new CharSet('0', '9', 'A', 'Z', 'a', 'z');
	private Mount mount = null;
	
	private static final class Mount
	{
		final Type<? extends Item> type;
		final String name;
		final String id;
		private final java.lang.reflect.Field annotationSource;
		
		Mount(final Type<? extends Item> type, final String name, final java.lang.reflect.Field annotationSource)
		{
			assert type!=null;
			assert name!=null;
			
			this.type = type;
			this.name = intern(name);
			this.id =   intern(type.id + '.' + name);
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
		
		void toString(final StringBuilder bf, final Type defaultType)
		{
			bf.append((defaultType==type) ? name : id);
		}
	}
	
	/**
	 * Is called in the constructor of the containing type.
	 */
	void mount(final Type<? extends Item> type, final String name, final java.lang.reflect.Field annotationSource)
	{
		{
			final int l = name.length();
			for(int i = 0; i<l; i++)
				if(!NAME_CHAR_SET.contains(name.charAt(i)))
					throw new IllegalArgumentException("name >" + name + "< of feature in type " + type + " contains illegal character >" + name.charAt(i) + "< at position " + i);
		}
		
		if(this.mount!=null)
			throw new IllegalStateException("feature already mounted: " + mount.id);
		this.mount = new Mount(type, name, annotationSource);
		
		type.registerMounted(this);
		
		this.pattern = this.patternWhileTypeInitialization;
		this.patternWhileTypeInitialization = null;
	}
	
	private final Mount mount()
	{
		final Mount mount = this.mount;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}
	
	final boolean isMounted()
	{
		return mount!=null;
	}
	
	public Type<? extends Item> getType()
	{
		return mount().type;
	}
	
	public final String getName()
	{
		return mount().name;
	}
	
	/**
	 * @see Model#getFeature(String)
	 */
	public final String getID()
	{
		return mount().id;
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
	
	void toStringNotMounted(final StringBuilder bf)
	{
		bf.append(super.toString());
	}
	
	@Override
	public final String toString()
	{
		final Mount mount = this.mount;
		if(mount!=null)
		{
			return mount.id;
		}
		else
		{
			final StringBuilder bf = new StringBuilder();
			toStringNotMounted(bf);
			return bf.toString();
		}
	}
	
	public final void toString(final StringBuilder bf, final Type defaultType)
	{
		final Mount mount = this.mount;
		if(mount!=null)
			mount.toString(bf, defaultType);
		else
			toStringNotMounted(bf);
	}
	
	// patterns ------------------
	
	private Pattern patternWhileTypeInitialization = null;
	private Pattern pattern = null;
	
	final void registerPattern(final Pattern pattern)
	{
		if(isMounted())
			throw new RuntimeException("registerPattern cannot be called after initialization of the field.");
		if(pattern==null)
			throw new NullPointerException();
		
		if(patternWhileTypeInitialization!=null)
			throw new IllegalStateException("field has already registered pattern " + this.patternWhileTypeInitialization + " and tried to register a new one: " + pattern);
		
		this.patternWhileTypeInitialization = pattern;
	}
	
	/**
	 * @see Pattern#getSourceFields()
	 */
	public final Pattern getPattern()
	{
		if(!isMounted())
			throw new RuntimeException("getPattern cannot be called before initialization of the field.");
		if(patternWhileTypeInitialization!=null)
			throw new RuntimeException();

		return pattern;
	}
	
	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	protected final Object writeReplace() throws ObjectStreamException
	{
		final Mount mount = this.mount;
		if(mount==null)
			throw new NotSerializableException(getClass().getName());

		return new Serialized(mount);
	}
	
	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;
		
		private final Type type;
		private final String name;
		
		Serialized(final Mount mount)
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
