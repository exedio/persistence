/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.Feature;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Settable;

class CopeFeature
{
	static final String TAG_PREFIX = "cope.";
	static final String TAG_GETTER  = TAG_PREFIX + "get";
	static final String TAG_SETTER  = TAG_PREFIX + "set";
	static final String TAG_INITIAL = TAG_PREFIX + "initial";

	final CopeType parent;
	final JavaAttribute javaAttribute;
	final String name;
	final int modifier;
	final Visibility visibility;
	final String docComment;
	final Option setterOption;
	final boolean initial;
	private Feature value;
	
	CopeFeature(final CopeType parent, final JavaAttribute javaAttribute)
	{
		this.parent = parent;
		this.javaAttribute = javaAttribute;
		this.name = javaAttribute.name;
		this.modifier = javaAttribute.modifier;
		this.visibility = javaAttribute.getVisibility();

		this.docComment = javaAttribute.getDocComment();
		this.setterOption = new Option(Injector.findDocTagLine(docComment, TAG_SETTER), true);
		this.initial = Injector.hasTag(docComment, TAG_INITIAL);
		
		parent.register(this);
	}
	
	final JavaClass getParent()
	{
		return javaAttribute.parent;
	}
	
	final Feature getInstance()
	{
		if(value==null)
			value = (Feature)javaAttribute.evaluate();
		
		return value;
	}
	
	final boolean isInitial()
	{
		if(initial)
			return true;
		
		final Feature instance = getInstance();
		return instance instanceof Settable && ((Settable)instance).isInitial();
	}

	private final boolean isWriteable()
	{
		final Feature instance = getInstance();
		return instance instanceof Settable && !((Settable)instance).isFinal();
	}
	
	final boolean hasGeneratedSetter()
	{
		return isWriteable() && setterOption.exists;
	}
	
	final int getGeneratedSetterModifier()
	{
		return setterOption.getModifier(modifier);
	}
	
	final SortedSet<Class<? extends Throwable>> getSetterExceptions()
	{
		final Feature instance = getInstance();
		final Set<Class<? extends Throwable>> resultList = ((Settable<?>)instance).getSetterExceptions();
		final SortedSet<Class<? extends Throwable>> result = new TreeSet<Class<? extends Throwable>>(CopeType.CLASS_COMPARATOR);
		result.addAll(resultList);
		if(((Settable<?>)instance).getWrapperSetterType().isPrimitive())
			result.remove(MandatoryViolationException.class);
		return result;
	}
}
