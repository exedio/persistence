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

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.Attribute;
import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.ComputedFunction;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.Hash;
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

	final Option getterOption;
	final Option setterOption;
	final boolean initial;
	
	CopeAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final String persistentType,
			final List initializerArguments,
			final String setterOption,
			final String getterOption,
			final boolean initial)
		throws InjectorParseException
	{
		this.javaAttribute = javaAttribute;
		this.accessModifier = javaAttribute.accessModifier;
		this.copeClass = CopeClass.getCopeClass(javaAttribute.parent);
		this.persistentType = persistentType;
		final boolean computed = ComputedFunction.class.isAssignableFrom(typeClass);
		
		if(!computed)
		{
			if(initializerArguments.size()<1)
				throw new InjectorParseException("attribute "+javaAttribute.name+" has no option.");
			final String optionString = (String)initializerArguments.get(0);
			//System.out.println(optionString);
			final Attribute.Option option = getOption(optionString); 
	
			if(option.unique)
				copeClass.makeUnique(new CopeUniqueConstraint(this));
		}
		
		this.getterOption = new Option(getterOption, true);
		this.setterOption = new Option(setterOption, true);
		this.initial = initial;

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
	
	private final void writeGeneratedModifier(final Writer o, final int modifier) throws IOException
	{
		final String modifierString = Modifier.toString(modifier);
		if(modifierString.length()>0)
		{
			o.write(modifierString);
			o.write(' ');
		}
	}

	final void writeGeneratedGetterModifier(final Writer o) throws IOException
	{
		writeGeneratedModifier(o, getterOption.getModifier(javaAttribute.modifier));
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
	
	// TODO: put into rtlib
	final boolean isInitial()
	{
		if(initial)
			return true;
		
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;
		final boolean readOnly = instance instanceof Attribute && ((Attribute)instance).isReadOnly();
		final boolean notNull = instance instanceof Attribute && ((Attribute)instance).isMandatory();
		final boolean computed = instance instanceof ComputedFunction;

		return (readOnly || notNull) && !computed;
	}

	// TODO: put into rtlib
	private final boolean isWriteable()
	{
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;
		final boolean readOnly = instance instanceof Attribute && ((Attribute)instance).isReadOnly();
		final boolean computed = instance instanceof ComputedFunction;

		return !readOnly && !computed;
	}
	
	final boolean isTouchable()
	{
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;

		return instance instanceof DateAttribute;
	}

	final boolean hasIsGetter()
	{
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;
		final boolean isBoolean = instance instanceof BooleanAttribute;

		return isBoolean && getterOption.booleanAsIs;
	}

	final boolean hasGeneratedSetter()
	{
		return isWriteable() && setterOption.exists;
	}
	
	final void writeGeneratedSetterModifier(final Writer o) throws IOException
	{
		writeGeneratedModifier(o, setterOption.getModifier(javaAttribute.modifier));
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
	
	// TODO put this into rtlib
	protected void fillSetterExceptions(final SortedSet result)
	{
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;
		final boolean readOnly = instance instanceof Attribute && ((Attribute)instance).isReadOnly();
		final boolean notNull = (instance instanceof Attribute && ((Attribute)instance).isMandatory()) ||
										(instance instanceof Hash && ((Hash)instance).getStorage().isMandatory());
		final boolean unique = instance instanceof ObjectAttribute && !((ObjectAttribute)instance).getUniqueConstraints().isEmpty();
		final boolean isLengthConstrained = instance instanceof StringAttribute && ((StringAttribute)instance).isLengthConstrained();

		if(unique)
			result.add(UniqueViolationException.class);
		if(readOnly)
			result.add(ReadOnlyViolationException.class);
		if(notNull && !isBoxed())
			result.add(MandatoryViolationException.class);
		if(isLengthConstrained)
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
		if(!isBoxed())
			result.add(MandatoryViolationException.class);
		result.add(ReadOnlyViolationException.class);
	}

	private SortedSet toucherExceptions = null;

	final SortedSet getToucherExceptions()
	{
		final JavaClass.Value value = javaAttribute.evaluate();
		final Object instance = value.instance;
		final boolean readOnly = instance instanceof Attribute && ((Attribute)instance).isReadOnly();
		final boolean unique = instance instanceof ObjectAttribute && !((ObjectAttribute)instance).getUniqueConstraints().isEmpty();

		if(toucherExceptions!=null)
			return toucherExceptions;
		
		final TreeSet modifyableToucherExceptions = new TreeSet(ClassComparator.getInstance());
		
		if(unique)
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
	
	void show() // TODO remove
	{
		System.out.println("------attribute:"+javaAttribute.name);
		final Feature rtvalue = (Feature)javaAttribute.evaluate().instance;
		final JavaAttribute ja = (JavaAttribute)javaAttribute.parent.file.repository.getByRtValue(rtvalue);
		final Attribute a = rtvalue instanceof Attribute ? (Attribute)rtvalue : null;
		System.out.println("------attribute:"+ja.name+'/'+(a!=null&&a.isMandatory()));
	
		System.out.println("------");
	}
}
