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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.util.ClassComparator;

/**
 * An <tt>attribute</tt> represents a persistently
 * stored attribute of a {@link Type}.
 * Subclasses specify the type of information to be stored
 * in an attribute.
 * <p>
 * For an overview here is a
 * <a href="attributes.png">UML diagram</a>.
 * 
 * @author Ralf Wiebicke
 */
public abstract class Attribute<E> extends Feature implements Settable<E>
{
	final boolean isfinal;
	final boolean optional;

	Attribute(final boolean isfinal, final boolean optional)
	{
		this.isfinal = isfinal;
		this.optional = optional;
	}
	
	public final boolean isFinal()
	{
		return isfinal;
	}
	
	public final boolean isMandatory()
	{
		return !optional;
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
	
	/**
	 * Returns true, if a value for the attribute should be specified
	 * on the creation of an item.
	 * This default implementation returns
	 * <tt>{@link #isFinal()} || {@link #isMandatory()}</tt>.
	 */
	public boolean isInitial()
	{
		return isfinal || !optional;
	}
	
	abstract void checkValue(final Object value, final Item item);
		
	// patterns ---------------------------------------------------------------------
	
	private ArrayList<Pattern> patternsWhileTypeInitialization = null;
	private List<Pattern> patterns = null;
	
	void registerPattern(final Pattern pattern)
	{
		if(isInitialized())
			throw new RuntimeException("registerPattern cannot be called after initialization of the attribute.");
		if(pattern==null)
			throw new NullPointerException();
		
		if(patternsWhileTypeInitialization==null)
			patternsWhileTypeInitialization = new ArrayList<Pattern>();
		
		patternsWhileTypeInitialization.add(pattern);
	}
	
	public List<Pattern> getPatterns()
	{
		if(!isInitialized())
			throw new RuntimeException("getPatterns cannot be called before initialization of the attribute.");
		if(patterns==null)
			throw new RuntimeException();

		return patterns;
	}
	
	// second initialization phase ---------------------------------------------------

	private Column column;
	
	void initialize(final Type<? extends Item> type, final String name, final java.lang.reflect.Type genericType)
	{
		super.initialize(type, name, genericType);
		
		final ArrayList<Pattern> patterns = patternsWhileTypeInitialization;
		patternsWhileTypeInitialization = null;
		this.patterns =
			patterns==null
			? Collections.<Pattern>emptyList()
			: Collections.unmodifiableList(Arrays.asList(patterns.toArray(new Pattern[patterns.size()])));
	}
	
	final void materialize(final Table table)
	{
		if(table==null)
			throw new NullPointerException();
		if(this.column!=null)
			throw new RuntimeException();

		this.column = createColumn(table, getName(), optional);
		materialize();
	}
	
	void materialize()
	{
		// empty default implementation
	}
	
	final Column getColumn()
	{
		if(this.column==null)
			throw new RuntimeException();

		return column;
	}
	
	/**
	 * Returns the name of database column for this attribute - use with care!
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 * 
	 * @see Type#getTableName()
	 */
	public final String getColumnName()
	{
		return column.id;
	}
	
	abstract Column createColumn(Table table, String name, boolean optional);
	
	public static enum Option
	{
		MANDATORY            (false, false, false),
		OPTIONAL             (false, false, true),
		UNIQUE               (false, true,  false),
		UNIQUE_OPTIONAL      (false, true,  true),
		FINAL                (true,  false, false),
		FINAL_OPTIONAL       (true,  false, true),
		FINAL_UNIQUE         (true,  true,  false),
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


