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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.CharSet;

public abstract class Feature
{
	private static final CharSet NAME_CHAR_SET = new CharSet('0', '9', 'A', 'Z', 'a', 'z');
	private Mount mount = null;
	private java.lang.reflect.Field annotationField = null;
	
	private static final class Mount
	{
		final Type<? extends Item> type;
		final String name;
		final String id;
		
		Mount(final Type<? extends Item> type, final String name)
		{
			assert type!=null;
			assert name!=null;
			
			this.type = type;
			this.name = intern(name);
			this.id =   intern(type.id + '.' + name);
		}
		
		void toString(final StringBuilder bf, final Type defaultType)
		{
			bf.append((defaultType==type) ? name : id);
		}
	}
	
	/**
	 * Is called in the constructor of the containing type.
	 */
	void mount(final Type<? extends Item> type, final String name)
	{
		{
			final int l = name.length();
			for(int i = 0; i<l; i++)
				if(!NAME_CHAR_SET.contains(name.charAt(i)))
					throw new IllegalArgumentException("name >" + name + "< of feature in type " + type + " contains illegal character >" + name.charAt(i) + "< at position " + i);
		}
		
		if(this.mount!=null)
			throw new IllegalStateException("feature already mounted: " + mount.id);
		this.mount = new Mount(type, name);
		
		type.registerMounted(this);
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
	
	final void setAnnotationField(final java.lang.reflect.Field annotationField)
	{
		assert this.annotationField==null;
		this.annotationField = annotationField;
	}
	
	/**
	 * @see Class#getAnnotation(Class)
	 */
	public final <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
	{
		return
			annotationField!=null
			? annotationField.getAnnotation(annotationClass)
			: null;
	}
	
	String getSchemaName()
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
}
