/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A <tt>view</tt> represents a value computed from the
 * attributes of a {@link Type}.
 * The computation is available both in Java and SQL,
 * so you can use views in search conditions.
 * <p>
 * For an overview here is a
 * <a href="functions.png">UML diagram</a>.
 * 
 * @author Ralf Wiebicke
 */
public abstract class View<E> extends Feature implements Function<E>
{
	private final Function[] sources;
	private final List<Function> sourceList;
	private final String functionName;
	final int jdbcType;
	final Type sourceType;

	public View(final Function[] sources, final String functionName, final int jdbcType)
	{
		this.sources = sources;
		this.sourceList = Collections.unmodifiableList(Arrays.asList(sources));
		this.functionName = functionName;
		this.jdbcType = jdbcType;
		
		Type sourceType;
		try
		{
			sourceType = sources[0].getType();
		}
		catch(FeatureNotInitializedException e)
		{
			sourceType = null;
		}
		this.sourceType = sourceType;
	}
	
	public final List<Function> getSources()
	{
		return sourceList;
	}

	protected abstract Object mapJava(Object[] sourceValues);
	
	abstract Object load(ResultSet resultSet, int columnIndex) throws SQLException;

	abstract String surface2Database(Object value);
	
	abstract void surface2DatabasePrepared(Statement bf, Object value);
	
	public final Object getObject(final Item item)
	{
		final List sources = getSources();
		final Object[] values = new Object[sources.size()];
		int pos = 0;
		for(Iterator i = sources.iterator(); i.hasNext(); )
			values[pos++] = ((Function)i.next()).get(item);
	
		return mapJava(values);
	}
	
	public final void appendParameter(final Statement bf, final E value)
	{
		if(bf.parameters==null)
			bf.append(surface2Database(value));
		else
			surface2DatabasePrepared(bf, value);
	}
	
	final String toStringNonInitialized()
	{
		final StringBuffer buf = new StringBuffer(functionName);
		buf.append('(');
		for(int i = 0; i<sources.length; i++)
		{
			if(i>0)
				buf.append(',');
			buf.append(sources[i].toString());
		}
		buf.append(')');
		
		return buf.toString();
	}
	
	public boolean equals(final Object other)
	{
		if(!(other instanceof View))
			return false;
		
		final View o = (View)other;
		
		if(!functionName.equals(o.functionName) || sources.length!=o.sources.length)
			return false;
		
		for(int i = 0; i<sources.length; i++)
		{
			if(!sources[i].equals(o.sources[i]))
				return false;
		}

		return true;
	}
	
	public int hashCode()
	{
		int result = functionName.hashCode();
		
		for(int i = 0; i<sources.length; i++)
			result = (31*result) + sources[i].hashCode(); // may not be commutative

		return result;
	}


	// second initialization phase ---------------------------------------------------

	@Override
	final void initialize(final Type type, final String name, final java.lang.reflect.Type genericType)
	{
		if(sourceType!=null && type!=sourceType)
			throw new RuntimeException();
			
		super.initialize(type, name, genericType);
	}
	
	public final Type getType()
	{
		return (sourceType!=null) ? sourceType : super.getType();
	}
	
}
