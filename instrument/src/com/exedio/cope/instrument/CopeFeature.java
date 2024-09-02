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

package com.exedio.cope.instrument;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Settable;
import com.exedio.cope.misc.PrimitiveUtil;
import com.exedio.cope.misc.ReflectionTypes;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

abstract class CopeFeature
{
	final CopeType<?> parent;

	private Object value;
	private Type initialType;
	private SortedSet<Class<? extends Throwable>> initialExceptions;
	private boolean initialTypePrimitive;

	CopeFeature(final CopeType<?> parent)
	{
		this.parent = parent;
	}

	abstract String getName();

	abstract int getModifier();

	final InternalVisibility getVisibility()
	{
		return InternalVisibility.forModifier(getModifier());
	}

	abstract Boolean getInitialByConfiguration();

	abstract Object evaluate();

	final Object getInstance()
	{
		if(value==null)
			value = evaluate();

		return value;
	}

	final boolean isInitial()
	{
		if(getInitialByConfiguration()!=null)
			return getInitialByConfiguration();

		final Object instance = getInstance();
		return instance instanceof Settable<?> && ((Settable<?>)instance).isInitial();
	}

	final boolean isMandatory()
	{
		final Object instance = getInstance();
		return instance instanceof Settable<?> && ((Settable<?>)instance).isMandatory();
	}

	final Type getInitialType()
	{
		if(initialType==null)
			makeInitialTypeAndExceptions();

		return initialType;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	final SortedSet<Class<? extends Throwable>> getInitialExceptions()
	{
		if(initialExceptions==null)
			makeInitialTypeAndExceptions();

		return initialExceptions;
	}

	final boolean isInitialTypePrimitive()
	{
		if(initialType==null)
			makeInitialTypeAndExceptions();

		return initialTypePrimitive;
	}

	private void makeInitialTypeAndExceptions()
	{
		final Settable<?> instance = (Settable<?>)getInstance();

		final Type initialTypeX = instance.getInitialType();
		final Type initialType;
		final boolean primitive;
		if(initialTypeX instanceof final Class<?> initialClass)
		{
			final Class<?> initialClassPrimitive = PrimitiveUtil.toPrimitive(initialClass);
			if(initialClassPrimitive!=null && instance.isMandatory())
			{
				initialType = initialClassPrimitive;
				primitive = true;
			}
			else
			{
				initialType = wildcard(initialClass);
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
		this.initialTypePrimitive = primitive;
	}

	static final Type wildcard(final Class<?> type)
	{
		if (type.getTypeParameters().length==0)
		{
			return type;
		}
		else
		{
			final Type[] parameters = new Type[type.getTypeParameters().length];
			for (int i = 0; i < type.getTypeParameters().length; i++)
			{
				parameters[i] = ReflectionTypes.sub(Object.class);
			}
			return ReflectionTypes.parameterized(type, parameters);
		}
	}

	final boolean isDefault()
	{
		return "defaultFeature".equals(getName());
	}

	@Override
	public String toString()
	{
		return parent.toString() + '#' + getName();
	}

	static final String link(final String target)
	{
		return "{@link #" + target + '}';
	}

	abstract String getJavadocReference();

	abstract String applyTypeShortcuts(String type);
}
