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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.Attribute;
import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.ComputedFunction;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.util.ClassComparator;

abstract class CopeAttribute
{
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
	final Option getterOption;
	final Option setterOption;
	final boolean isBoolean;

	CopeAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final String persistentType,
			final List initializerArguments,
			final String setterOption,
			final String getterOption)
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
			this.notNull = option.mandatory;
			
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
		
		this.getterOption = new Option(getterOption, true);
		this.setterOption = new Option(setterOption, true);
		this.isBoolean = BooleanAttribute.class.equals(typeClass);

		copeClass.add(this);
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
		return getterOption.getModifier(javaAttribute.modifier);
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
	 * This happens if the attribute is mandatory
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
	
	final boolean hasIsGetter()
	{
		return isBoolean && getterOption.booleanAsIs;
	}

	final boolean hasGeneratedSetter()
	{
		return isWriteable() && setterOption.exists;
	}
	
	final int getGeneratedSetterModifier()
	{
		return setterOption.getModifier(javaAttribute.modifier);
	}
	
	private SortedSet setterExceptions = null;

	final SortedSet getSetterExceptions()
	{
		if(setterExceptions!=null)
			return setterExceptions;
		
		final TreeSet result = new TreeSet(ClassComparator.getInstance());
		fillSetterExceptions(result);
		this.setterExceptions = Collections.unmodifiableSortedSet(result);
		return this.setterExceptions;
	}
	
	protected void fillSetterExceptions(final SortedSet result)
	{
		if(isPartOfUniqueConstraint())
			result.add(UniqueViolationException.class);
		if(readOnly)
			result.add(ReadOnlyViolationException.class);
		if(notNull && !isBoxed())
			result.add(MandatoryViolationException.class);
		if(lengthConstrained)
			result.add(LengthViolationException.class);
	}


	private SortedSet exceptionsToCatchInSetter = null;

	/**
	 * Compute exceptions to be caught in the setter.
	 * These are just those thrown by {@link com.exedio.cope.Item#setAttribute(ObjectAttribute,Object)}
	 * which are not in the setters throws clause.
	 * (see {@link #getSetterExceptions()})
	 */
	final SortedSet getExceptionsToCatchInSetter()
	{
		if(exceptionsToCatchInSetter!=null)
			return exceptionsToCatchInSetter;

		final TreeSet result = new TreeSet(ClassComparator.getInstance());
		fillExceptionsThrownByGenericSetter(result);
		result.removeAll(getSetterExceptions());
		
		this.exceptionsToCatchInSetter = Collections.unmodifiableSortedSet(result);
		return this.exceptionsToCatchInSetter;
	}

	protected void fillExceptionsThrownByGenericSetter(final SortedSet result)
	{
		result.add(UniqueViolationException.class);
		result.add(MandatoryViolationException.class);
		result.add(ReadOnlyViolationException.class);
		result.add(LengthViolationException.class);
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
	 * These are just those thrown by {@link com.exedio.cope.Item#touchAttribute(DateAttribute)}
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
	
	final static Attribute.Option getOption(final String optionString)	
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
