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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.DateAttribute;
import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.IntegerFunction;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.StringFunction;

final class CopeNativeAttribute extends CopeAttribute
{
	final boolean touchable;
	private final String boxedType;
	private final boolean boxed;
	private final String boxingPrefix, boxingPostfix, unboxingPrefix, unboxingPostfix;

	public CopeNativeAttribute(
			final JavaAttribute javaAttribute,
			Class typeClass,
			final List initializerArguments,
			final String setterOptionString)
		throws InjectorParseException
	{
		super(javaAttribute, typeClass, getPersistentType(typeClass), initializerArguments, setterOptionString);
		
		this.touchable = DateAttribute.class.isAssignableFrom(typeClass);

		typeClass = normalizeTypeClass(typeClass);
		final String nativeType = (String)toNativeTypeMapping.get(typeClass);
		if(notNull && nativeType!=null)
		{
			boxedType = nativeType;
			boxed = true;
			boxingPrefix = (String)toBoxingPrefixMapping.get(typeClass);
			boxingPostfix = (String)toBoxingPostfixMapping.get(typeClass);
			unboxingPrefix = (String)toUnboxingPrefixMapping.get(typeClass);
			unboxingPostfix = (String)toUnboxingPostfixMapping.get(typeClass);
		}
		else
		{
			boxedType = persistentType;
			boxed = false;
			boxingPrefix = boxingPostfix = unboxingPrefix = unboxingPostfix = null;
		}
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
		final String result = (String)toPersistentTypeMapping.get(normalizeTypeClass(typeClass));

		if(result==null)
			throw new RuntimeException(typeClass.toString());

		return result;
	}

	private static final HashMap toPersistentTypeMapping = new HashMap(3);
	private static final HashMap toNativeTypeMapping = new HashMap(3);
	private static final HashMap toBoxingPrefixMapping = new HashMap(3);
	private static final HashMap toBoxingPostfixMapping = new HashMap(3);
	private static final HashMap toUnboxingPrefixMapping = new HashMap(3);
	private static final HashMap toUnboxingPostfixMapping = new HashMap(3);
	
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
		fillNativeTypeMap(BooleanAttribute.class, Boolean.class, boolean.class, "(", "?"+Boolean.class.getName()+".TRUE:"+Boolean.class.getName()+".FALSE)", "(", ").booleanValue()");
		fillNativeTypeMap(LongAttribute.class,    Long.class,    long.class);
		fillNativeTypeMap(IntegerFunction.class,  Integer.class, int.class);
		fillNativeTypeMap(DoubleAttribute.class,  Double.class,  double.class);
		fillNativeTypeMap(StringFunction.class,   String.class);
		fillNativeTypeMap(DateAttribute.class,    Date.class);
	}

	public final String getBoxedType()
	{
		return boxedType;
	}
	
	public final boolean isBoxed()
	{
		return boxed;
	}
	
	public final String getBoxingPrefix()
	{
		if(!boxed)
			throw new RuntimeException();

		return boxingPrefix;
	}
	
	public final String getBoxingPostfix()
	{
		if(!boxed)
			throw new RuntimeException();

		return boxingPostfix;
	}
	
	public final String getUnBoxingPrefix()
	{
		if(!boxed)
			throw new RuntimeException();

		return unboxingPrefix;
	}
	
	public final String getUnBoxingPostfix()
	{
		if(!boxed)
			throw new RuntimeException();

		return unboxingPostfix;
	}
	
}
