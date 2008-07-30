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

package com.exedio.cope.instrument;

import java.util.Date;
import java.util.HashMap;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DayField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.NumberFunction;
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
		super(parent, javaAttribute, getPersistentType(typeClass));
		
		this.typeClass = normalizeTypeClass(typeClass);
		this.nativeType = toNativeTypeMapping.get(this.typeClass);
	}
	
	private static final Class normalizeTypeClass(final Class typeClass)
	{
		if(StringFunction.class.isAssignableFrom(typeClass))
			return StringFunction.class;
		else if(NumberFunction.class.isAssignableFrom(typeClass))
			return NumberFunction.class;
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
	
	private static final void fillNativeTypeMap(final Class typeClass, final Class persistentType, final Class nativeType)
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
	}
	
	private static final void fillNativeTypeMap(final Class typeClass, final Class persistentType)
	{
		fillNativeTypeMap(typeClass, persistentType, null);
	}

	static
	{
		fillNativeTypeMap(BooleanField.class,    Boolean.class, boolean.class);
		fillNativeTypeMap(LongField.class,       Long.class,    long.class);
		fillNativeTypeMap(NumberFunction.class, Integer.class, int.class);
		fillNativeTypeMap(DoubleField.class,     Double.class,  double.class);
		fillNativeTypeMap(StringFunction.class,  String.class);
		fillNativeTypeMap(DateField.class,       Date.class);
		fillNativeTypeMap(DayField.class,        Day.class);
	}
	
	@Override
	@Deprecated
	public final String getBoxedType()
	{
		return isBoxed() ? nativeType : persistentType;
	}
	
	@Override
	@Deprecated
	public final boolean isBoxed()
	{
		final Feature instance = getInstance();
		final boolean mandatory = instance instanceof Field && ((Field)instance).isMandatory();

		return mandatory && nativeType!=null;
	}
}
