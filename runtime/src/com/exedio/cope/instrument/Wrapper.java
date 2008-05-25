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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class Wrapper
{
	private final String name;
	
	public Wrapper(final String name)
	{
		this.name = name;
		
		if(name==null)
			throw new NullPointerException("name must not be null");
	}

	public String getName()
	{
		return name;
	}
	
	
	private boolean isStatic = false;
	
	public Wrapper setStatic()
	{
		isStatic = true;
		
		return this;
	}
	
	public boolean isStatic()
	{
		return isStatic;
	}
	
	
	private java.lang.reflect.Type returnType = null;
	private String returnComment = null;
	
	public Wrapper setReturn(final java.lang.reflect.Type type)
	{
		return setReturn(type, null);
	}
	
	public Wrapper setReturn(final java.lang.reflect.Type type, final String comment)
	{
		if(type==null)
			throw new NullPointerException("type must not be null");
		if(type==void.class)
			throw new NullPointerException("type must not be void");
		if(this.returnType!=null)
			throw new NullPointerException("type must not be set twice");
		
		this.returnType = type;
		this.returnComment = comment;
		
		return this;
	}

	public java.lang.reflect.Type getReturnType()
	{
		return returnType!=null ? returnType : void.class;
	}

	public String getReturnComment()
	{
		return returnComment;
	}
	
	
	final class Parameter
	{
		private final java.lang.reflect.Type type;
		private final String name;
		private final String comment;
		
		Parameter(final java.lang.reflect.Type type, final String name, String comment)
		{
			this.type = type;
			this.name = name;
			this.comment = comment;
			
			assert type!=null;
			assert name!=null;
		}

		public java.lang.reflect.Type getType()
		{
			return type;
		}

		public String getName()
		{
			return name;
		}

		public String getComment()
		{
			return comment;
		}
	}
	
	private ArrayList<Parameter> parameters;
	
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
		
		if(parameters==null)
			parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(type, name, comment));

		return this;
	}

	public List<Parameter> getParameters()
	{
		return
			parameters!=null
			? Collections.unmodifiableList(parameters)
			: Collections.<Parameter>emptyList();
	}
	
	
	private LinkedHashMap<Class<? extends Throwable>, String> throwsClause;
	
	public Wrapper addThrows(final Collection<Class<? extends Throwable>> throwables)
	{
		for(final Class<? extends Throwable> throwable : throwables)
			addThrows(throwable, null);
		
		return this;
	}
	
	public Wrapper addThrows(final Class<? extends Throwable> throwable)
	{
		return addThrows(throwable, null);
	}
	
	public Wrapper addThrows(final Class<? extends Throwable> throwable, final String comment)
	{
		if(throwable==null)
			throw new NullPointerException("throwable must not be null");
		
		if(throwsClause==null)
			throwsClause = new LinkedHashMap<Class<? extends Throwable>, String>();
		
		throwsClause.put(throwable, comment);
		
		return this;
	}

	public Map<Class<? extends Throwable>, String> getThrowsClause()
	{
		return
			throwsClause!=null
			? Collections.unmodifiableMap(throwsClause)
			: Collections.<Class<? extends Throwable>, String>emptyMap();
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
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		if(comment.startsWith("@"))
			throw new IllegalArgumentException("comment must not contain tag, but was " + comment);
		
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
	
	
	private String deprecationComment = null;
	
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
	
	public static final java.lang.reflect.Type generic(final Class rawType, final Class... actualTypeArguments)
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
	
	public static final java.lang.reflect.Type genericExtends(final Class rawType, final Class... actualTypeArguments)
	{
		return new ExtendsType(rawType, actualTypeArguments);
	}
}
