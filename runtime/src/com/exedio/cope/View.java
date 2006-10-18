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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.search.ExtremumAggregate;

/**
 * A <tt>view</tt> represents a value computed from the
 * fields of a {@link Type}.
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
	private final Function<?>[] sources;
	private final List<Function<?>> sourceList;
	private final String name;
	final Class<E> valueClass;
	final int typeForDefiningColumn;
	final Type<? extends Item> sourceType;

	public View(final Function<?>[] sources, final String name, final Class<E> valueClass, final int typeForDefiningColumn)
	{
		this.sources = sources;
		this.sourceList = Collections.unmodifiableList(Arrays.asList(sources));
		this.name = name;
		this.valueClass = valueClass;
		this.typeForDefiningColumn = typeForDefiningColumn;
		
		Type<? extends Item> sourceType;
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
	
	public final List<Function<?>> getSources()
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
	
	public final Class<E> getValueClass()
	{
		return valueClass;
	}
	
	public final int getTypeForDefiningColumn()
	{
		return typeForDefiningColumn;
	}
	
	public final void appendParameter(final Statement bf, final E value)
	{
		if(bf.parameters==null)
			bf.append(surface2Database(value));
		else
			surface2DatabasePrepared(bf, value);
	}
	
	@Override
	final String toStringNonInitialized()
	{
		final StringBuffer buf = new StringBuffer(name);
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
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof View))
			return false;
		
		final View o = (View)other;
		
		if(!name.equals(o.name) || sources.length!=o.sources.length)
			return false;
		
		for(int i = 0; i<sources.length; i++)
		{
			if(!sources[i].equals(o.sources[i]))
				return false;
		}

		return true;
	}
	
	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		
		for(int i = 0; i<sources.length; i++)
			result = (31*result) + sources[i].hashCode(); // may not be commutative

		return result;
	}


	// second initialization phase ---------------------------------------------------

	@Override
	final void initialize(final Type<? extends Item> type, final String name)
	{
		if(sourceType!=null && type!=sourceType)
			throw new RuntimeException();
			
		super.initialize(type, name);
	}
	
	@Override
	public final Type<? extends Item> getType()
	{
		return (sourceType!=null) ? sourceType : super.getType();
	}
	
	// convenience methods for conditions and views ---------------------------------

	public final EqualCondition<E> equal(final E value)
	{
		return new EqualCondition<E>(this, value);
	}
	
	public final EqualCondition<E> equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}
	
	public final CompositeCondition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public final NotEqualCondition<E> notEqual(final E value)
	{
		return new NotEqualCondition<E>(this, value);
	}
	
	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.Less, this, value);
	}
	
	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.LessEqual, this, value);
	}
	
	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.Greater, this, value);
	}
	
	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.GreaterEqual, this, value);
	}
	
	public final CompareFunctionCondition<E> equal(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Equal, this, right);
	}
	
	public final CompareFunctionCondition<E> less(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Less, this, right);
	}
	
	public final CompareFunctionCondition<E> lessOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.LessEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> greater(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Greater, this, right);
	}
	
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.GreaterEqual, this, right);
	}

	public final ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}
	
	public final ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	public BindFunction<E> bind(final Join join)
	{
		return new BindFunction<E>(this, join);
	}
}
