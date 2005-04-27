/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ComputedFunction;
import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.LengthViolationException;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ClassComparator;

abstract class CopeAttribute
{
	/**
	 * Defines this attribute as a media attribute.
	 * The dash prevents this name to be used as a java identifier.
	 * @see #getPersistentType()
	 */
	static final String MEDIA_TYPE = "Media-";

	final JavaAttribute javaAttribute;
	final int accessModifier;
	
	final CopeClass copeClass;

	/**
	 * The persistent type of this attribute.
	 */
	final String persistentType;

	final boolean readOnly;
	final boolean notNull;
	final boolean lengthConstrained;
	final boolean computed;
	final int setterOption;

	CopeAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final String persistentType,
			final List initializerArguments,
			final String setterOptionString)
		throws InjectorParseException
	{
		this.javaAttribute = javaAttribute;
		this.accessModifier = javaAttribute.accessModifier;
		this.copeClass = CopeClass.getCopeClass(javaAttribute.parent);
		this.persistentType = persistentType;
		this.computed = ComputedFunction.class.isAssignableFrom(typeClass);
		
		if(!computed)
		{
			if(initializerArguments.size()<1)
				throw new InjectorParseException("attribute "+javaAttribute.name+" has no option.");
			final String optionString = (String)initializerArguments.get(0);
			//System.out.println(optionString);
			final Attribute.Option option = getOption(optionString); 
	
			this.readOnly = option.readOnly;
			this.notNull = option.notNull;
			
			if(initializerArguments.size()>1)
			{
				final String secondArgument = (String)initializerArguments.get(1);
				boolean lengthConstrained = true;
				try
				{
					Integer.parseInt(secondArgument);
				}
				catch(NumberFormatException e)
				{
					lengthConstrained = false;
				}
				this.lengthConstrained = lengthConstrained;
			}
			else
				this.lengthConstrained = false;

			if(option.unique)
				copeClass.makeUnique(new CopeUniqueConstraint(this));
		}
		else
		{
			this.readOnly = false;
			this.notNull = false;
			this.lengthConstrained = false;
		}
		
		setterOption = Option.getOption(setterOptionString);

		copeClass.addCopeAttribute(this);
	}
	
	private ArrayList hashes;

	final void addHash(final CopeHash hash)
	{
		if(hashes==null)
			hashes = new ArrayList();
		hashes.add(hash);
	}
	
	final List getHashes()
	{
		if(hashes==null)
			return Collections.EMPTY_LIST;
		else
			return hashes;
	}
	
	final String getName()
	{
		return javaAttribute.name;
	}
	
	final int getGeneratedGetterModifier()
	{
		return javaAttribute.modifier
			& (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)
			| Modifier.FINAL;
	}

	final JavaClass getParent()
	{
		return javaAttribute.parent;
	}
	
	/**
	 * Returns the type of this attribute to be used in accessor (setter/getter) methods.
	 * Differs from {@link #getPersistentType() the persistent type},
	 * if and only if the attribute is {@link #isBoxed() boxed}.
	 */
	String getBoxedType()
	{
		return persistentType;
	}
	
	/**
	 * Returns, whether the persistent type is &quot;boxed&quot; into a native type.
	 * This happens if the attribute has a not-null constraint 
	 * and the persistent type is convertable to a native types (int, double, boolean).
	 * @see #getBoxedType()
	 */
	boolean isBoxed()
	{
		return false;
	}
	
	String getBoxingPrefix()
	{
		throw new RuntimeException();
	}
	
	String getBoxingPostfix()
	{
		throw new RuntimeException();
	}
	
	String getUnBoxingPrefix()
	{
		throw new RuntimeException();
	}
	
	String getUnBoxingPostfix()
	{
		throw new RuntimeException();
	}
	
	final boolean isPartOfUniqueConstraint()
	{
		for( final Iterator i = copeClass.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final CopeAttribute[] uniqueConstraint = ((CopeUniqueConstraint)i.next()).copeAttributes;
			for(int j=0; j<uniqueConstraint.length; j++)
			{
				if(this == uniqueConstraint[j])
					return true;
			}
		}
		return false;
	}
	
	final boolean isInitial()
	{
		return (readOnly || notNull) && !computed;
	}
	
	private final boolean isWriteable()
	{
		return !readOnly && !computed;
	}
	
	final boolean hasGeneratedSetter()
	{
		return isWriteable() && (setterOption!=Option.NONE);
	}
	
	final int getGeneratedSetterModifier()
	{
		final int result;
		switch(setterOption)
		{
			case Option.NONE:
				throw new RuntimeException();
			case Option.AUTO:
				result = javaAttribute.modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
				break;
			case Option.PRIVATE:
				result = Modifier.PRIVATE;
				break;
			case Option.PROTECTED:
				result = Modifier.PROTECTED;
				break;
			case Option.PACKAGE:
				result = 0;
				break;
			case Option.PUBLIC:
				result = Modifier.PUBLIC;
				break;
			default:
				throw new RuntimeException(String.valueOf(setterOption));
		}
		return result | Modifier.FINAL;
	}
	
	private SortedSet setterExceptions = null;

	final SortedSet getSetterExceptions()
	{
		if(setterExceptions!=null)
			return setterExceptions;
		
		final TreeSet modifyableSetterExceptions = new TreeSet(ClassComparator.getInstance());
		
		if(isPartOfUniqueConstraint())
			modifyableSetterExceptions.add(UniqueViolationException.class);
		if(readOnly)
			modifyableSetterExceptions.add(ReadOnlyViolationException.class);
		if(notNull && !isBoxed())
			modifyableSetterExceptions.add(NotNullViolationException.class);
		if(lengthConstrained)
			modifyableSetterExceptions.add(LengthViolationException.class);
		

		this.setterExceptions = Collections.unmodifiableSortedSet(modifyableSetterExceptions);
		return this.setterExceptions;
	}


	private SortedSet exceptionsToCatchInSetter = null;

	/**
	 * Compute exceptions to be caught in the setter.
	 * These are just those thrown by {@link com.exedio.cope.lib.Item#setAttribute(ObjectAttribute,Object)}
	 * which are not in the setters throws clause.
	 * (see {@link #getSetterExceptions()})
	 */
	final SortedSet getExceptionsToCatchInSetter()
	{
		if(exceptionsToCatchInSetter!=null)
			return exceptionsToCatchInSetter;

		final TreeSet result = new TreeSet(ClassComparator.getInstance());
		result.add(UniqueViolationException.class);
		result.add(NotNullViolationException.class);
		result.add(ReadOnlyViolationException.class);
		result.add(LengthViolationException.class);
		result.removeAll(getSetterExceptions());
		
		this.exceptionsToCatchInSetter = Collections.unmodifiableSortedSet(result);
		return this.exceptionsToCatchInSetter;
	}

	private SortedSet toucherExceptions = null;

	final SortedSet getToucherExceptions()
	{
		if(toucherExceptions!=null)
			return toucherExceptions;
		
		final TreeSet modifyableToucherExceptions = new TreeSet(ClassComparator.getInstance());
		
		if(isPartOfUniqueConstraint())
			modifyableToucherExceptions.add(UniqueViolationException.class);
		if(readOnly)
			modifyableToucherExceptions.add(ReadOnlyViolationException.class);

		this.toucherExceptions = Collections.unmodifiableSortedSet(modifyableToucherExceptions);
		return this.toucherExceptions;
	}

	private SortedSet exceptionsToCatchInToucher = null;

	/**
	 * Compute exceptions to be caught in the toucher.
	 * These are just those thrown by {@link com.exedio.cope.lib.Item#touchAttribute(DateAttribute)}
	 * which are not in the touchers throws clause.
	 * (see {@link #getToucherExceptions()})
	 */
	final SortedSet getExceptionsToCatchInToucher()
	{
		if(exceptionsToCatchInToucher!=null)
			return exceptionsToCatchInToucher;

		final TreeSet result = new TreeSet(ClassComparator.getInstance());
		result.add(UniqueViolationException.class);
		result.add(ReadOnlyViolationException.class);
		result.removeAll(getSetterExceptions());
		
		this.exceptionsToCatchInToucher = Collections.unmodifiableSortedSet(result);
		return this.exceptionsToCatchInToucher;
	}

	private final static Attribute.Option getOption(final String optionString)	
	{
		try
		{
			//System.out.println(optionString);
			final Attribute.Option result = 
				(Attribute.Option)Item.class.getDeclaredField(optionString).get(null);
			if(result==null)
				throw new NullPointerException(optionString);
			return result;
		}
		catch(NoSuchFieldException e)
		{
			throw new NestingRuntimeException(e, optionString);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e, optionString);
		}
	}
	
}
