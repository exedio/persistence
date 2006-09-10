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

package com.exedio.cope.instrument;

import java.util.Date;
import java.util.HashMap;

import com.exedio.cope.Attribute;
import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayAttribute;
import com.exedio.cope.DoubleAttribute;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerFunction;
import com.exedio.cope.LongField;
import com.exedio.cope.StringFunction;
import com.exedio.cope.util.Day;

final class CopeNativeAttribute extends CopeAttribute
{
	final Class typeClass;
	final String nativeType;

	public CopeNativeAttribute(
			final CopeType parent,
			final JavaAttribute javaAttribute,
			final Class typeClass)
		throws InjectorParseException
	{
		super(parent, javaAttribute, typeClass, getPersistentType(typeClass));
		
		this.typeClass = normalizeTypeClass(typeClass);
		this.nativeType = toNativeTypeMapping.get(this.typeClass);
	}
	
	private static final Class normalizeTypeClass(final Class typeClass)
	{
		if(StringFunction.class.isAssignableFrom(typeClass))
			return StringFunction.class;
		else if(IntegerFunction.class.isAssignableFrom(typeClass))
			return IntegerFunction.class;
		else
			return typeClass;
	}

	private static final String getPersistentType(final Class typeClass)
	{
		final String result = toPersistentTypeMapping.get(normalizeTypeClass(typeClass));

		if(result==null)
			throw new RuntimeException(typeClass.toString());

		return result;
	}

	private static final HashMap<Class, String> toPersistentTypeMapping = new HashMap<Class, String>(3);
	private static final HashMap<Class, String> toNativeTypeMapping = new HashMap<Class, String>(3);
	private static final HashMap<Class, String> toBoxingPrefixMapping = new HashMap<Class, String>(3);
	private static final HashMap<Class, String> toBoxingPostfixMapping = new HashMap<Class, String>(3);
	private static final HashMap<Class, String> toUnboxingPrefixMapping = new HashMap<Class, String>(3);
	private static final HashMap<Class, String> toUnboxingPostfixMapping = new HashMap<Class, String>(3);
	
	private static final void fillNativeTypeMap(final Class typeClass, final Class persistentType, final Class nativeType,
															  final String boxingPrefix, final String boxingPostfix,
															  final String unboxingPrefix, final String unboxingPostfix)
	{
		if(persistentType.isPrimitive())
			throw new RuntimeException(nativeType.toString());

		toPersistentTypeMapping.put(typeClass, persistentType.getName());

		if(nativeType!=null)
		{
			if(!nativeType.isPrimitive())
				throw new RuntimeException(nativeType.toString());

			toNativeTypeMapping.put(typeClass, nativeType.getName());
		}
		
		toBoxingPrefixMapping.put(typeClass, boxingPrefix);
		toBoxingPostfixMapping.put(typeClass, boxingPostfix);
		toUnboxingPrefixMapping.put(typeClass, unboxingPrefix);
		toUnboxingPostfixMapping.put(typeClass, unboxingPostfix);
	}
	
	private static final void fillNativeTypeMap(final Class typeClass, final Class persistentType, final Class nativeType)
	{
		fillNativeTypeMap(typeClass, persistentType, nativeType,
				"new "+persistentType.getName()+"(", ")", "(", ")."+nativeType.getName()+"Value()");
	}

	private static final void fillNativeTypeMap(final Class typeClass, final Class persistentType)
	{
		fillNativeTypeMap(typeClass, persistentType, null, null, null, null, null);
	}

	static
	{
		fillNativeTypeMap(BooleanField.class, Boolean.class, boolean.class, "(", "?"+Boolean.class.getName()+".TRUE:"+Boolean.class.getName()+".FALSE)", "(", ").booleanValue()");
		fillNativeTypeMap(LongField.class,    Long.class,    long.class);
		fillNativeTypeMap(IntegerFunction.class,  Integer.class, int.class);
		fillNativeTypeMap(DoubleAttribute.class,  Double.class,  double.class);
		fillNativeTypeMap(StringFunction.class,   String.class);
		fillNativeTypeMap(DateField.class,    Date.class);
		fillNativeTypeMap(DayAttribute.class,     Day.class);
	}
	
	@Override
	public final String getBoxedType()
	{
		return isBoxed() ? nativeType : persistentType;
	}
	
	@Override
	public final boolean isBoxed()
	{
		final Feature instance = getInstance();
		final boolean notNull = instance instanceof Attribute && ((Attribute)instance).isMandatory();

		return (notNull && nativeType!=null);
	}
	
	@Override
	public final String getBoxingPrefix()
	{
		if(!isBoxed())
			throw new RuntimeException();

		return toBoxingPrefixMapping.get(typeClass);
	}
	
	@Override
	public final String getBoxingPostfix()
	{
		if(!isBoxed())
			throw new RuntimeException();

		return toBoxingPostfixMapping.get(typeClass);
	}
	
	@Override
	public final String getUnBoxingPrefix()
	{
		if(!isBoxed())
			throw new RuntimeException();

		return toUnboxingPrefixMapping.get(typeClass);
	}
	
	@Override
	public final String getUnBoxingPostfix()
	{
		if(!isBoxed())
			throw new RuntimeException();

		return toUnboxingPostfixMapping.get(typeClass);
	}
}
