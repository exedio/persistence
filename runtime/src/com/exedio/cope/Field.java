/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An <tt>field</tt> represents a persistently
 * stored field of a {@link Type}.
 * Subclasses specify the type of information to be stored
 * in the field.
 *
 * @author Ralf Wiebicke
 */
public abstract class Field<E> extends Feature implements Settable<E>
{
	final boolean isfinal;
	final boolean optional;
	final Class<E> valueClass;

	Field(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = valueClass;
		assert valueClass!=null;
	}
	
	/**
	 * Returns a new Field,
	 * that differs from this Field
	 * by being final.
	 * If this Field is already final,
	 * the the result is equal to this Field.
	 * @see #isFinal()
	 */
	public abstract Field<E> toFinal();
	
	/**
	 * Returns a new Field,
	 * that differs from this Field
	 * by being optional.
	 * If this Field is already optional,
	 * the the result is equal to this Field.
	 * @see #isMandatory()
	 */
	public abstract Field<E> optional();
	
	/**
	 * @see #toFinal()
	 */
	public final boolean isFinal()
	{
		return isfinal;
	}
	
	public final boolean isMandatory()
	{
		return !optional;
	}
	
	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This default implementation returns
	 * <tt>{@link #isFinal()} || {@link #isMandatory()}</tt>.
	 */
	public boolean isInitial()
	{
		return isfinal || !optional;
	}
	
	public Class getInitialType()
	{
		return valueClass;
	}
	
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<Class<? extends Throwable>>();
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}
	
	public final Class<E> getValueClass()
	{
		return valueClass;
	}
	
	public final Collection<E> castCollection(final Collection<?> c)
	{
		if(c==null)
			return null;
		
		final ArrayList<E> result = new ArrayList<E>(c.size());
		for(final Object o : c)
			result.add(Cope.verboseCast(valueClass, o));
		return result;
	}

	public final SetValue<E> map(final E value)
	{
		return new SetValue<E>(this, value);
	}
	
	public final SetValue<E> mapNull()
	{
		return new SetValue<E>(this, null);
	}
	
	public final SetValue[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue[]{ map(value) };
	}
	
	public final void check(final E value) throws ConstraintViolationException
	{
		check(value, null);
	}
	
	/**
	 * Checks field values set by
	 * {@link Item#set(FunctionField,Object)}
	 * and {@link Item#Item(SetValue[])}
	 * and throws the exception specified there.
	 */
	final void check(final Object value, final Item exceptionItem)
		throws
			MandatoryViolationException,
			StringLengthViolationException
	{
		if(value == null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, this, exceptionItem);
		}
		else
		{
			if(!valueClass.isInstance(value))
			{
				throw new ClassCastException(
						"expected a " + valueClass.getName() +
						", but was a " + value.getClass().getName() +
						" for " + toString() + '.');
			}
			
			checkNotNull(valueClass.cast(value), exceptionItem);
		}
	}
	
	/**
	 * Further checks non-null field values already checked by
	 * {@link #check(Object, Item)}.
	 * To be overidden by subclasses,
	 * the default implementation does nothing.
	 * @param value used in subclasses
	 * @param exceptionItem used in subclasses
	 */
	void checkNotNull(final E value, final Item exceptionItem)
		throws
			StringLengthViolationException
	{
		// empty default implementation
	}
		
	// patterns ---------------------------------------------------------------------
	
	private Pattern patternWhileTypeInitialization = null;
	private Pattern pattern = null;
	
	final void registerPattern(final Pattern pattern)
	{
		if(isMounted())
			throw new RuntimeException("registerPattern cannot be called after initialization of the field.");
		if(pattern==null)
			throw new NullPointerException();
		
		if(patternWhileTypeInitialization!=null)
			throw new IllegalStateException("field has already registered pattern " + this.patternWhileTypeInitialization + " and tried to register a new one: " + pattern);
		
		this.patternWhileTypeInitialization = pattern;
	}
	
	/**
	 * @see Pattern#getSourceFields()
	 */
	public final Pattern getPattern()
	{
		if(!isMounted())
			throw new RuntimeException("getPattern cannot be called before initialization of the field.");
		if(patternWhileTypeInitialization!=null)
			throw new RuntimeException();

		return pattern;
	}
	
	// second initialization phase ---------------------------------------------------

	private Column column;
	
	@Override
	void mount(final Type<? extends Item> type, final String name)
	{
		super.mount(type, name);
		
		this.pattern = this.patternWhileTypeInitialization;
		this.patternWhileTypeInitialization = null;
	}
	
	final void connect(final Table table)
	{
		if(table==null)
			throw new NullPointerException();
		if(this.column!=null)
			throw new RuntimeException();
		
		this.column = createColumn(table, getSchemaName(), optional);
	}
	
	void disconnect()
	{
		this.column = null;
	}
	
	final Column getColumn()
	{
		if(this.column==null)
			throw new RuntimeException();

		return column;
	}
	
	abstract Column createColumn(Table table, String name, boolean optional);
	public abstract E get(Item item);
	public abstract void set(Item item, E value);
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link SchemaInfo#getColumnName(Field)} instead
	 */
	@Deprecated
	public final String getColumnName()
	{
		return SchemaInfo.getColumnName(this);
	}
	
	@Deprecated
	public final List<Pattern> getPatterns()
	{
		final Pattern pattern = getPattern();
		return
			pattern!=null
			? Collections.singletonList(pattern)
			: Collections.<Pattern>emptyList();
	}
}
