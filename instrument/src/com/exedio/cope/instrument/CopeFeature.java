/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import com.exedio.cope.Feature;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Settable;
import com.exedio.cope.misc.PrimitiveUtil;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

final class CopeFeature
{
	static final String TAG_PREFIX = "cope.";
	static final String TAG_INITIAL = TAG_PREFIX + "initial";

	final CopeType parent;
	final JavaField javaField;
	final String name;
	final int modifier;
	final Visibility visibility;
	final String docComment;
	final boolean initial;

	private Feature value;
	private Type initialType;
	private SortedSet<Class<? extends Throwable>> initialExceptions;

	CopeFeature(final CopeType parent, final JavaField javaField)
	{
		this.parent = parent;
		this.javaField = javaField;
		this.name = javaField.name;
		this.modifier = javaField.modifier;
		this.visibility = javaField.getVisibility();

		this.docComment = javaField.getDocComment();
		this.initial = Tags.has(docComment, TAG_INITIAL);

		parent.register(this);
	}

	final JavaClass getParent()
	{
		return javaField.parent;
	}

	final Feature getInstance()
	{
		if(value==null)
			value = (Feature)javaField.evaluate();

		return value;
	}

	final boolean isInitial()
	{
		if(initial)
			return true;

		final Feature instance = getInstance();
		return instance instanceof Settable<?> && ((Settable<?>)instance).isInitial();
	}

	final Type getInitialType()
	{
		if(initialType==null)
			makeInitialTypeAndExceptions();

		return initialType;
	}

	final SortedSet<Class<? extends Throwable>> getInitialExceptions()
	{
		if(initialExceptions==null)
			makeInitialTypeAndExceptions();

		return initialExceptions;
	}

	@SuppressWarnings("rawtypes")
	private static final GenericResolver<Settable> settableResolver = GenericResolver.neW(Settable.class);

	private void makeInitialTypeAndExceptions()
	{
		final Settable<?> instance = (Settable<?>)getInstance();

		final Type initialTypeX = settableResolver.get(instance.getClass(), Generics.getTypes(javaField.type))[0];
		final Type initialType;
		final boolean primitive;
		if(initialTypeX instanceof Class<?>)
		{
			final Class<?> initialClass = (Class<?>)initialTypeX;
			final Class<?> initialClassPrimitive = PrimitiveUtil.toPrimitive(initialClass);
			if(initialClassPrimitive!=null && instance.isMandatory())
			{
				initialType = initialClassPrimitive;
				primitive = true;
			}
			else
			{
				initialType = initialClass;
				primitive = false;
			}
		}
		else
		{
			initialType = initialTypeX;
			primitive = false;
		}

		final Set<Class<? extends Throwable>> resultList = instance.getInitialExceptions();
		final SortedSet<Class<? extends Throwable>> initialExceptions = new TreeSet<>(CopeType.CLASS_COMPARATOR);
		initialExceptions.addAll(resultList);
		if(primitive)
			initialExceptions.remove(MandatoryViolationException.class);

		this.initialType = initialType;
		this.initialExceptions = initialExceptions;
	}

	final boolean isDefault()
	{
		return "defaultFeature".equals(name);
	}

	@Override
	public String toString()
	{
		return parent.toString() + '#' + name;
	}
}
