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

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class WrapperX
{
	private final String name;
	private final Method method;
	private final Nullability nullability;

	String getName()
	{
		return name;
	}

	WrapperX(final Method method, final Nullability nullability)
	{
		this.name = method.getName();
		this.method = method;
		this.nullability = nullability;
	}

	Method getMethod()
	{
		return method;
	}


	private boolean isStatic = false;
	private TypeVariable<?> staticToken = null;

	void setStatic()
	{
		isStatic = true;
		staticToken = null;
	}

	boolean isStatic()
	{
		return isStatic;
	}

	boolean hasStaticClassToken()
	{
		return staticToken!=null;
	}

	void setStatic(final TypeVariable<?> token)
	{
		if(token==null)
			throw new NullPointerException();

		isStatic = true;
		staticToken = token;
	}

	boolean matchesStaticToken(final TypeVariable<?> token)
	{
		if(token==null)
			throw new NullPointerException();

		return token==staticToken;
	}


	private java.lang.reflect.Type returnType = null;
	private String[] returnComment = EMPTY_STRING_ARRAY;

	void setReturn(final java.lang.reflect.Type type, final String[] comment)
	{
		if(type==null)
			throw new NullPointerException("type");
		if(type==void.class)
			throw new IllegalArgumentException("type must not be void");
		if(this.returnType!=null)
			throw new IllegalStateException("type must not be set twice");
		for(final String c : comment)
			assertComment(c);

		this.returnType = type;
		this.returnComment = comment;
	}

	java.lang.reflect.Type getReturnType()
	{
		return returnType!=null ? returnType : void.class;
	}

	String[] getReturnComment()
	{
		return com.exedio.cope.misc.Arrays.copyOf(returnComment);
	}

	boolean isVarArgs()
	{
		return method.isVarArgs();
	}

	static final class Parameter
	{
		private final java.lang.reflect.Type type;
		private final String name;
		private final String[] comment;
		final List<?> varargs;
		private final Nullability nullability;

		Parameter(
				final java.lang.reflect.Type type,
				final String name,
				final String[] comment,
				final List<?> varargs,
				final Nullability nullability)
		{
			if(type==null)
				throw new NullPointerException("type");
			if(name==null)
				throw new NullPointerException("name");
			for(final String c : comment)
				assertComment(c);

			this.type = type;
			this.name = name;
			this.comment = comment;
			this.varargs = varargs;
			this.nullability = nullability;
		}

		java.lang.reflect.Type getType()
		{
			return type;
		}

		String getName()
		{
			return name;
		}

		String[] getComment()
		{
			return com.exedio.cope.misc.Arrays.copyOf(comment);
		}

		boolean isNullable()
		{
			return nullability==Nullability.NULLABLE;
		}

		boolean isNonnull()
		{
			return nullability==Nullability.NONNULL;
		}

		@Override
		public String toString()
		{
			return type.toString();
		}
	}

	private ArrayList<Parameter> parameters;

	void addParameter(final java.lang.reflect.Type type, final List<?> varargs, final Nullability nullability)
	{
		addParameter(type, "{1}", EMPTY_STRING_ARRAY, varargs, nullability);
	}

	void addParameter(final java.lang.reflect.Type type, final String name, final String[] comment, final List<?> varargs, final Nullability nullability)
	{
		final Parameter p = new Parameter(type, name, comment, varargs, nullability);
		if(parameters==null)
			parameters = new ArrayList<>();
		parameters.add(p);
	}

	List<Parameter> getParameters()
	{
		return
			parameters!=null
			? Collections.unmodifiableList(parameters)
			: Collections.emptyList();
	}


	private LinkedHashMap<Class<? extends Throwable>, String[]> throwsClause;

	void addThrows(final Class<? extends Throwable> throwable)
	{
		addThrows(throwable, EMPTY_STRING_ARRAY);
	}

	void addThrows(final Class<? extends Throwable> throwable, final String[] comment)
	{
		if(throwable==null)
			throw new NullPointerException("throwable");
		for(final String c : comment)
			assertComment(c);

		if(throwsClause==null)
			throwsClause = new LinkedHashMap<>();

		throwsClause.put(throwable, comment);
	}

	Map<Class<? extends Throwable>, String[]> getThrowsClause()
	{
		return
			throwsClause!=null
			? Collections.unmodifiableMap(throwsClause)
			: Collections.emptyMap();
	}


	private String methodWrapperPattern;

	void setMethodWrapperPattern(final String pattern)
	{
		this.methodWrapperPattern = pattern;
	}

	String getMethodWrapperPattern()
	{
		return methodWrapperPattern;
	}


	private String optionTagName;

	void setOptionTagName(final String optionTagName)
	{
		this.optionTagName = optionTagName;
	}

	String getOptionTagName()
	{
		return optionTagName;
	}


	private ArrayList<String> comments = null;

	void addComment(final String comment)
	{
		assertComment(comment);

		if(comments==null)
			comments = new ArrayList<>();
		comments.add(comment);
	}

	String[] getCommentArray()
	{
		// TODO use String[] from the beginning
		return
			comments!=null
			? comments.toArray(new String[comments.size()])
			: new String[0];
	}


	boolean isMethodDeprecated()
	{
		return method.isAnnotationPresent(Deprecated.class);
	}

	Nullability getMethodNullability()
	{
		return nullability;
	}

	static void assertComment(final String comment)
	{
		if(comment==null)
			throw new NullPointerException("comment");
		if(comment.startsWith(" "))
			throw new IllegalArgumentException("comment must not start with space, but was '" + comment + '\'');
		if(comment.startsWith("@"))
			throw new IllegalArgumentException("comment must not contain tag, but was " + comment);
	}


	@Override
	public String toString()
	{
		return name + parameters;
	}


	private static final String[] EMPTY_STRING_ARRAY = {};
}
