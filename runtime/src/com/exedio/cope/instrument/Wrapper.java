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

package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class Wrapper
{
	private boolean isStatic = false;
	private final java.lang.reflect.Type methodReturnType;
	private final String methodName;
	private ArrayList<java.lang.reflect.Type> parameterTypes;
	private ArrayList<String> parameterNames;
	private final String comment;
	private final String modifier;
	private final Class[] throwsClause;
	private String methodWrapperPattern;
	private ArrayList<String> comments = null;
	private String deprecationComment = null;
	
	public Wrapper(
			final java.lang.reflect.Type methodReturnType,
			final String methodName,
			final String comment,
			final String modifier,
			final Class... throwsClause)
	{
		this.methodReturnType = methodReturnType;
		this.methodName = methodName;
		this.comment = comment;
		this.modifier = modifier;
		this.throwsClause = throwsClause;
		
		if(methodReturnType==null)
			throw new NullPointerException("methodReturnType must not be null");
		if(methodName==null)
			throw new NullPointerException("methodName must not be null");
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		if(modifier==null)
			throw new NullPointerException("modifier must not be null");
	}
	
	public Wrapper setStatic()
	{
		isStatic = true;
		
		return this;
	}
	
	public boolean isStatic()
	{
		return isStatic;
	}

	public java.lang.reflect.Type getMethodReturnType()
	{
		return methodReturnType;
	}

	public String getMethodName()
	{
		return methodName;
	}

	public Wrapper addParameter(final java.lang.reflect.Type type)
	{
		return addParameter(type, "{1}", null);
	}
	
	public Wrapper addParameter(final java.lang.reflect.Type type, final String name)
	{
		return addParameter(type, name, null);
	}
	
	public Wrapper addParameter(final java.lang.reflect.Type type, final String name, final String comment)
	{
		if(type==null)
			throw new NullPointerException("type must not be null");
		if(name==null)
			throw new NullPointerException("name must not be null");
		
		if(parameterTypes==null)
		{
			parameterTypes = new ArrayList<java.lang.reflect.Type>();
			parameterNames = new ArrayList<String>();
		}
		parameterTypes.add(type);
		parameterNames.add(name);
		
		if(comment!=null)
			addCommentPrivate("@param " + name + ' ' + comment);

		return this;
	}

	public List<java.lang.reflect.Type> getParameterTypes()
	{
		return
			parameterTypes!=null
			? Collections.unmodifiableList(parameterTypes)
			: Collections.<java.lang.reflect.Type>emptyList();
	}

	public List<String> getParameterNames()
	{
		return
			parameterNames!=null
			? Collections.unmodifiableList(parameterNames)
			: Collections.<String>emptyList();
	}

	public String getComment()
	{
		return comment;
	}

	public String getModifier()
	{
		return modifier;
	}

	public Collection<Class> getThrowsClause()
	{
		return
			throwsClause!=null
			? Collections.unmodifiableCollection(Arrays.asList(throwsClause))
			: Collections.<Class>emptySet();
	}

	public Wrapper setMethodWrapperPattern(final String pattern)
	{
		this.methodWrapperPattern = pattern;
		
		return this;
	}
	
	public String getMethodWrapperPattern()
	{
		return methodWrapperPattern;
	}
	
	public Wrapper addComment(final String comment)
	{
		if(comment.startsWith("@param"))
			throw new RuntimeException(comment);
		
		return addCommentPrivate(comment);
	}
	
	private Wrapper addCommentPrivate(final String comment)
	{
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		
		if(comments==null)
			comments = new ArrayList<String>();
		comments.add(comment);

		return this;
	}
	
	public List<String> getComments()
	{
		return
			comments!=null
			? Collections.unmodifiableList(comments)
			: Collections.<String>emptyList();
	}
	
	public Wrapper deprecate(final String comment)
	{
		if(comment==null)
			throw new NullPointerException();
		deprecationComment = comment;

		return this;
	}
	
	public boolean isDeprecated()
	{
		return deprecationComment!=null;
	}
	
	public String getDeprecationComment()
	{
		return deprecationComment;
	}
	
	public class ClassVariable { /* OK, just a placeholder */ }
	public class TypeVariable0 { /* OK, just a placeholder */ }
	public class TypeVariable1 { /* OK, just a placeholder */ }
	public class DynamicModelType { /* OK, just a placeholder */ } // TODO remove, is a hack
	public class DynamicModelField { /* OK, just a placeholder */ } // TODO remove, is a hack
	
	public static final java.lang.reflect.Type makeType(final Class rawType, final Class... actualTypeArguments)
	{
		return ParameterizedTypeImpl.make(rawType, actualTypeArguments, null);
	}
	
	public static class ExtendsType implements java.lang.reflect.Type
	{
		private final Class rawType;
		private final Class[] actualTypeArguments;
		
		ExtendsType(
				final Class rawType,
				final Class[] actualTypeArguments)
		{
			this.rawType = rawType;
			this.actualTypeArguments = actualTypeArguments;
		}

		public Class getRawType()
		{
			return rawType;
		}

		public Class[] getActualTypeArguments()
		{
			return actualTypeArguments;
		}
	}
	
	public static final java.lang.reflect.Type makeTypeExtends(final Class rawType, final Class... actualTypeArguments)
	{
		return new ExtendsType(rawType, actualTypeArguments);
	}
}
