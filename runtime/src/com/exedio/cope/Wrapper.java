/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Wrapper
{
	private final Class methodReturnType;
	private final String methodName;
	private final Class[] parameterTypes;
	private final String comment;
	private final String modifier;
	private final String modifierComment;
	private final Class[] throwsClause;
	private final String methodWrapperPattern;
	
	public Wrapper(
			final Class methodReturnType,
			final String methodName,
			final Class[] parameterTypes,
			final String comment,
			final String modifier,
			final String modifierComment)
	{
		this.methodReturnType = methodReturnType;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.comment = comment;
		this.modifier = modifier;
		this.modifierComment = modifierComment;
		this.throwsClause = null;
		this.methodWrapperPattern = null;
	}

	public Wrapper(
			final Class methodReturnType,
			final String methodName,
			final Class[] parameterTypes,
			final String comment,
			final String modifier,
			final String modifierComment,
			final Class[] throwsClause)
	{
		this.methodReturnType = methodReturnType;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.comment = comment;
		this.modifier = modifier;
		this.modifierComment = modifierComment;
		this.throwsClause = throwsClause;
		this.methodWrapperPattern = null;
	}

	public Wrapper(
			final Class methodReturnType,
			final String methodName,
			final Class[] parameterTypes,
			final String comment,
			final String modifier,
			final String modifierComment,
			final String methodWrapperPattern)
	{
		this.methodReturnType = methodReturnType;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.comment = comment;
		this.modifier = modifier;
		this.modifierComment = modifierComment;
		this.throwsClause = null;
		this.methodWrapperPattern = methodWrapperPattern;
	}

	public Class getMethodReturnType()
	{
		return methodReturnType;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public List<Class> getParameterTypes()
	{
		return
			parameterTypes!=null
			? Collections.unmodifiableList(Arrays.asList(parameterTypes))
			: Collections.<Class>emptyList();
	}

	public String getComment()
	{
		return comment;
	}

	public String getModifier()
	{
		return modifier;
	}

	public String getModifierComment()
	{
		return modifierComment;
	}

	public Collection<Class> getThrowsClause()
	{
		return
			throwsClause!=null
			? Collections.unmodifiableCollection(Arrays.asList(throwsClause))
			: Collections.<Class>emptySet();
	}

	public String getMethodWrapperPattern()
	{
		return methodWrapperPattern;
	}
}
