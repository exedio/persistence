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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use {@link Wrap} annotations instead.
 */
@Deprecated
public final class Wrapper
{
	private final String name;

	public Wrapper(final String name)
	{
		this.name = name;
		if(name==null)
			throw new NullPointerException("name");
	}

	public String getName()
	{
		return name;
	}


	private boolean isStatic = false;
	private boolean hasStaticClassToken = false;

	public Wrapper setStatic()
	{
		return setStatic(true);
	}

	public Wrapper setStatic(final boolean classToken)
	{
		isStatic = true;
		hasStaticClassToken = classToken;

		return this;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public boolean hasStaticClassToken()
	{
		return hasStaticClassToken;
	}


	private java.lang.reflect.Type returnType = null;
	private String[] returnComment = EMPTY_STRING_ARRAY;

	public Wrapper setReturn(final java.lang.reflect.Type type)
	{
		return setReturn(type, EMPTY_STRING_ARRAY);
	}

	public Wrapper setReturn(final java.lang.reflect.Type type, final String comment)
	{
		return setReturn(type, new String[]{comment});
	}

	private Wrapper setReturn(final java.lang.reflect.Type type, final String[] comment)
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

		return this;
	}

	public java.lang.reflect.Type getReturnType()
	{
		return returnType!=null ? returnType : void.class;
	}

	public String[] getReturnComment()
	{
		return com.exedio.cope.misc.Arrays.copyOf(returnComment);
	}


	static final class Parameter
	{
		private final java.lang.reflect.Type type;
		private final String name;
		private final String[] comment;
		private final boolean vararg;

		Parameter(
				final java.lang.reflect.Type type,
				final String name,
				final String[] comment,
				final boolean vararg)
		{
			if(type==null)
				throw new NullPointerException("type");
			if(name==null)
				throw new NullPointerException("name");
			for(final String c : comment)
				assertComment(c);
			if(vararg && !((Class<?>)type).isArray())
				throw new IllegalArgumentException("vararg requires array type, but was " + ((Class<?>)type).getName());

			this.type = type;
			this.name = name;
			this.comment = comment;
			this.vararg = vararg;
		}

		public java.lang.reflect.Type getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}

		public String[] getComment()
		{
			return com.exedio.cope.misc.Arrays.copyOf(comment);
		}

		public boolean isVararg()
		{
			return vararg;
		}

		@Override
		public String toString()
		{
			return type.toString();
		}
	}

	private ArrayList<Parameter> parameters;

	public Wrapper addParameter(final java.lang.reflect.Type type)
	{
		return addParameter(type, "{1}", EMPTY_STRING_ARRAY);
	}

	public Wrapper addParameter(final java.lang.reflect.Type type, final String name)
	{
		return addParameter(type, name, EMPTY_STRING_ARRAY);
	}

	public Wrapper addParameter(final java.lang.reflect.Type type, final String name, final String comment)
	{
		return addParameter(type, name, new String[]{comment});
	}

	private Wrapper addParameter(final java.lang.reflect.Type type, final String name, final String[] comment)
	{
		return addParameter(type, name, comment, false);
	}

	public Wrapper addParameterVararg(final Class<?> type, final String name)
	{
		return addParameter(type, name, new String[]{}, true);
	}

	private Wrapper addParameter(final java.lang.reflect.Type type, final String name, final String[] comment, final boolean vararg)
	{
		final Parameter p = new Parameter(type, name, comment, vararg);
		if(parameters==null)
			parameters = new ArrayList<>();
		parameters.add(p);

		return this;
	}

	public List<Parameter> getParameters()
	{
		return
			parameters!=null
			? Collections.unmodifiableList(parameters)
			: Collections.<Parameter>emptyList();
	}


	private LinkedHashMap<Class<? extends Throwable>, String[]> throwsClause;

	public Wrapper addThrows(final Collection<Class<? extends Throwable>> throwables)
	{
		for(final Class<? extends Throwable> throwable : throwables)
			addThrows(throwable, EMPTY_STRING_ARRAY);

		return this;
	}

	public Wrapper addThrows(final Class<? extends Throwable> throwable)
	{
		return addThrows(throwable, EMPTY_STRING_ARRAY);
	}

	public Wrapper addThrows(final Class<? extends Throwable> throwable, final String comment)
	{
		return addThrows(throwable, new String[]{comment});
	}

	private Wrapper addThrows(final Class<? extends Throwable> throwable, final String[] comment)
	{
		if(throwable==null)
			throw new NullPointerException("throwable");
		for(final String c : comment)
			assertComment(c);

		if(throwsClause==null)
			throwsClause = new LinkedHashMap<>();

		throwsClause.put(throwable, comment);

		return this;
	}

	public Map<Class<? extends Throwable>, String[]> getThrowsClause()
	{
		return
			throwsClause!=null
			? Collections.unmodifiableMap(throwsClause)
			: Collections.<Class<? extends Throwable>, String[]>emptyMap();
	}


	private String methodWrapperPattern;

	public Wrapper setMethodWrapperPattern(final String pattern)
	{
		this.methodWrapperPattern = pattern;

		return this;
	}

	public String getMethodWrapperPattern()
	{
		return methodWrapperPattern;
	}


	private ArrayList<String> comments = null;

	public Wrapper addComment(final String comment)
	{
		assertComment(comment);

		if(comments==null)
			comments = new ArrayList<>();
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


	/**
	 * @deprecated not supported anymore, does nothing
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public Wrapper deprecate(final String comment)
	{
		return this;
	}

	/**
	 * @deprecated not supported anymore, always returns false
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public boolean isDeprecated()
	{
		return false;
	}

	/**
	 * @deprecated not supported anymore, always returns null
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public String getDeprecationComment()
	{
		return null;
	}



	static final void assertComment(final String comment)
	{
		if(comment==null)
			throw new NullPointerException("comment");
		if(comment.startsWith(" "))
			throw new IllegalArgumentException("comment must not start with space, but was '" + comment + '\'');
		if(comment.startsWith("@"))
			throw new IllegalArgumentException("comment must not contain tag, but was " + comment);
	}


	/**
	 * @deprecated Not needed anymore by framework.
	 */
	@Deprecated
	public boolean matchesMethod(final String name, @SuppressWarnings("unused") final Class<?>... parameterTypes)
	{
		if(this.name.equals(name))
			return true;

		throw new NullPointerException(); // member variable method was always null
	}


	@Override
	public String toString()
	{
		return name + parameters;
	}


	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
	public class ClassVariable { /* OK, just a placeholder */ }
	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
	public class TypeVariable0 { /* OK, just a placeholder */ }
	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
	public class TypeVariable1 { /* OK, just a placeholder */ }

	/**
	 * @deprecated Not supported anymore, throws {@link NoSuchMethodError}.
	 */
	@Deprecated
	public static final java.lang.reflect.Type generic(@SuppressWarnings("unused") final Class<?> rawType, @SuppressWarnings("unused") final Class<?>... actualTypeArguments)
	{
		throw new NoSuchMethodError("wrapper mechanism not supported anymore");
	}

	static final class ExtendsType implements java.lang.reflect.Type
	{
		private final Class<?> rawType;
		private final Class<?>[] actualTypeArguments;

		ExtendsType(
				final Class<?> rawType,
				final Class<?>[] actualTypeArguments)
		{
			this.rawType = rawType;
			this.actualTypeArguments = actualTypeArguments;
		}

		public Class<?> getRawType()
		{
			return rawType;
		}

		public Class<?>[] getActualTypeArguments()
		{
			return com.exedio.cope.misc.Arrays.copyOf(actualTypeArguments);
		}
	}

	public static final java.lang.reflect.Type genericExtends(final Class<?> rawType, final Class<?>... actualTypeArguments)
	{
		return new ExtendsType(rawType, actualTypeArguments);
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[]{};

	/**
	 * @deprecated Not supported anymore, throws {@link NoSuchMethodError}.
	 */
	@Deprecated
	public static <F extends Feature> List<Wrapper> getByAnnotations(
			@SuppressWarnings("unused") final Class<F> clazz,
			@SuppressWarnings("unused") final F feature,
			@SuppressWarnings("unused") final List<Wrapper> superResult)
	{
		throw new NoSuchMethodError("wrapper mechanism not supported anymore");
	}
}
