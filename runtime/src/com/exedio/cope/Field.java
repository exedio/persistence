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

package com.exedio.cope;

import java.util.ArrayList;
import java.util.Arrays;
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
 * <p>
 * For an overview here is a
 * <a href="attributes.png">UML diagram</a>.
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
				throw new MandatoryViolationException(this, exceptionItem);
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
	
	private ArrayList<Pattern> patternsWhileTypeInitialization = null;
	private List<Pattern> patterns = null;
	
	void registerPattern(final Pattern pattern)
	{
		if(isInitialized())
			throw new RuntimeException("registerPattern cannot be called after initialization of the field.");
		if(pattern==null)
			throw new NullPointerException();
		
		if(patternsWhileTypeInitialization==null)
			patternsWhileTypeInitialization = new ArrayList<Pattern>();
		
		patternsWhileTypeInitialization.add(pattern);
	}
	
	/**
	 * @see Pattern#getSourceFields()
	 */
	public List<Pattern> getPatterns()
	{
		if(!isInitialized())
			throw new RuntimeException("getPatterns cannot be called before initialization of the field.");
		if(patterns==null)
			throw new RuntimeException();

		return patterns;
	}
	
	// second initialization phase ---------------------------------------------------

	private Column column;
	
	@Override
	void initialize(final Type<? extends Item> type, final String name)
	{
		super.initialize(type, name);
		
		final ArrayList<Pattern> patterns = patternsWhileTypeInitialization;
		patternsWhileTypeInitialization = null;
		this.patterns =
			patterns==null
			? Collections.<Pattern>emptyList()
			: Collections.unmodifiableList(Arrays.asList(patterns.toArray(new Pattern[patterns.size()])));
	}
	
	final void connect(final Table table)
	{
		if(table==null)
			throw new NullPointerException();
		if(this.column!=null)
			throw new RuntimeException();
		
		final CopeSchemaName annotation = getAnnotation(CopeSchemaName.class);
		final String columnName = annotation!=null ? annotation.value() : getName();
		this.column = createColumn(table, columnName, optional);
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
	
	public static enum Option
	{
		/**
		 * @deprecated Use default constructor instead.
		 */
		@Deprecated
		MANDATORY            (false, false, false),

		/**
		 * @deprecated Use {@link Field#optional()} instead.
		 */
		@Deprecated
		OPTIONAL             (false, false, true),

		/**
		 * @deprecated Use {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		UNIQUE               (false, true,  false),

		/**
		 * @deprecated Use {@link Field#optional()} and {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		UNIQUE_OPTIONAL      (false, true,  true),

		/**
		 * @deprecated Use {@link Field#toFinal()} instead.
		 */
		@Deprecated
		FINAL                (true,  false, false),

		/**
		 * @deprecated Use {@link FunctionField#toFinal()} and {@link Field#optional()} instead.
		 */
		@Deprecated
		FINAL_OPTIONAL       (true,  false, true),

		/**
		 * @deprecated Use {@link Field#toFinal()} and {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		FINAL_UNIQUE         (true,  true,  false),

		/**
		 * @deprecated Use {@link Field#toFinal()}, {@link Field#optional()} and {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		FINAL_UNIQUE_OPTIONAL(true,  true,  true);
		
		/**
		 * Works around a bug in javac 1.5.0_08 (and 1.5.0_11),
		 * which propagates the deprecated flag from
		 * FINAL_UNIQUE_OPTIONAL to its successor in source code.
		 */
		@Deprecated
		@SuppressWarnings("unused")
		private final boolean dummyNotToBeUsed = true;
		
		public final boolean isFinal;
		public final boolean unique;
		public final boolean optional;

		private Option(final boolean isFinal, final boolean unique, final boolean optional)
		{
			this.isFinal = isFinal;
			this.unique = unique;
			this.optional = optional;
		}
	}
	
	/**
	 * @deprecated Use {@link SchemaInfo#getColumnName(Field)} instead
	 */
	@Deprecated
	public final String getColumnName()
	{
		return SchemaInfo.getColumnName(this);
	}
}
