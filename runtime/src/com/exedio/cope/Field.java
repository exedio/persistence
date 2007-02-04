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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.util.ClassComparator;

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
	}
	
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
	
	public SortedSet<Class> getSetterExceptions()
	{
		final TreeSet<Class> result = new TreeSet<Class>(ClassComparator.getInstance());
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
	
	public final SetValue[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue[]{ map(value) };
	}
	
	abstract void checkValue(final Object value, final Item item);
		
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

		this.column = createColumn(table, getName(), optional);
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
	
	/**
	 * Returns the name of database column for this field
	 * - <b>use with care!</b>
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Any INSERT/UPDATE/DELETE on the database bypassing cope
	 * may lead to inconsistent caches.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 *
	 * @see Type#getTableName()
	 * @see ItemField#getTypeColumnName()
	 */
	public final String getColumnName()
	{
		return column.id;
	}
	
	abstract Column createColumn(Table table, String name, boolean optional);
	public abstract E get(Item item);
	public abstract void set(Item item, E value);
	
	public static enum Option
	{
		MANDATORY            (false, false, false),
		OPTIONAL             (false, false, true),

		/**
		 * @deprecated Use {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		UNIQUE               (false, true,  false),

		/**
		 * @deprecated Use {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		UNIQUE_OPTIONAL      (false, true,  true),
		FINAL                (true,  false, false),
		FINAL_OPTIONAL       (true,  false, true),

		/**
		 * @deprecated Use {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		FINAL_UNIQUE         (true,  true,  false),

		/**
		 * @deprecated Use {@link FunctionField#unique()} instead.
		 */
		@Deprecated
		FINAL_UNIQUE_OPTIONAL(true,  true,  true);
		
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
	
}


