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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.exedio.cope.instrument.testmodel.Standard;
import com.exedio.cope.instrument.testmodel.TypeNone;
import com.exedio.cope.lib.AttributeValue;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;


public class GeneratorTest extends InstrumentorTest
{
	public static final int PUBLIC = Modifier.PUBLIC;
	public static final int PROTECTED = Modifier.PROTECTED;
	public static final int PRIVATE = Modifier.PRIVATE;
	public static final int STATIC = Modifier.STATIC;
	public static final int FINAL = Modifier.FINAL;
	
	public void testStandard() throws ClassNotFoundException
	{
		final Class attributeValueArrayClass = Class.forName("[L"+AttributeValue.class.getName()+';');
		
		final Class standard = Standard.class;
		assertConstructor(standard, new Class[]{
				String.class, // notNullString
				String.class, // readOnlyString
				int.class, // nativeInteger
				long.class, // nativeLong
				double.class, // nativeDouble
				boolean.class, // nativeBoolean
			}, PUBLIC);
		assertConstructor(standard, new Class[]{attributeValueArrayClass}, PRIVATE);
		assertConstructor(standard, new Class[]{ReactivationConstructorDummy.class, int.class}, PRIVATE);

		assertMethod(standard, "getDefaultString", String.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultString", new Class[]{String.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNotNullString", String.class, PUBLIC|FINAL);
		assertMethod(standard, "setNotNullString", new Class[]{String.class}, PUBLIC|FINAL, new Class[]{NotNullViolationException.class});
		assertMethod(standard, "getReadOnlyString", String.class, PUBLIC|FINAL);
		assertNoMethod(standard, "setReadOnlyString", new Class[]{String.class});
		assertMethod(standard, "getUniqueString", String.class, PUBLIC|FINAL);
		assertMethod(standard, "setUniqueString", new Class[]{String.class}, PUBLIC|FINAL, new Class[]{UniqueViolationException.class});
		assertMethod(standard, "findByUniqueString", new Class[]{String.class}, Standard.class, PUBLIC|STATIC|FINAL);

		assertMethod(standard, "getDefaultInteger", Integer.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultInteger", new Class[]{Integer.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNativeInteger", int.class, PUBLIC|FINAL);
		assertMethod(standard, "setNativeInteger", new Class[]{int.class}, PUBLIC|FINAL);

		assertMethod(standard, "getDefaultLong", Long.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultLong", new Class[]{Long.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNativeLong", long.class, PUBLIC|FINAL);
		assertMethod(standard, "setNativeLong", new Class[]{long.class}, PUBLIC|FINAL);

		assertMethod(standard, "getDefaultDouble", Double.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultDouble", new Class[]{Double.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNativeDouble", double.class, PUBLIC|FINAL);
		assertMethod(standard, "setNativeDouble", new Class[]{double.class}, PUBLIC|FINAL);

		assertMethod(standard, "getDefaultBoolean", Boolean.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultBoolean", new Class[]{Boolean.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNativeBoolean", boolean.class, PUBLIC|FINAL);
		assertMethod(standard, "setNativeBoolean", new Class[]{boolean.class}, PUBLIC|FINAL);

		assertMethod(standard, "getNoneSetterString", String.class, PUBLIC|FINAL);
		assertNoMethod(standard, "setNoneSetterString", new Class[]{String.class});
		assertMethod(standard, "getPrivateSetterString", String.class, PUBLIC|FINAL);
		assertMethod(standard, "setPrivateSetterString", new Class[]{String.class}, PRIVATE|FINAL);

		assertMethod(standard, "getNonfinalGetterString", String.class, PUBLIC);
		assertMethod(standard, "setNonfinalGetterString", new Class[]{String.class}, PROTECTED|FINAL);
		assertMethod(standard, "getNonfinalSetterString", String.class, PROTECTED|FINAL);
		assertMethod(standard, "setNonfinalSetterString", new Class[]{String.class}, PUBLIC);

		assertMethod(standard, "isAsIsBoolean", Boolean.class, PUBLIC|FINAL);
		assertNoMethod(standard, "getAsIsBoolean");
		assertMethod(standard, "setAsIsBoolean", new Class[]{Boolean.class}, PUBLIC|FINAL);

		assertField(standard, "TYPE", Type.class, PUBLIC|STATIC|FINAL);

		final Class typeNone = TypeNone.class;
		assertConstructor(typeNone, new Class[]{}, PUBLIC);
		assertConstructor(typeNone, new Class[]{attributeValueArrayClass}, PRIVATE);
		assertConstructor(typeNone, new Class[]{ReactivationConstructorDummy.class, int.class}, PRIVATE);
		assertMethod(typeNone, "getDefaultString", String.class, PUBLIC|FINAL);
		assertMethod(typeNone, "setDefaultString", new Class[]{String.class}, PUBLIC|FINAL);
		assertNoField(typeNone, "TYPE");
	}
	
	void assertField(
			final Class javaClass, final String name,
			final Class returnType, final int modifiers)
	{
		final Field field;
		try
		{
			field = javaClass.getDeclaredField(name);
		}
		catch(NoSuchFieldException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(returnType, field.getType());
		assertEquals(modifiers, field.getModifiers());
	}

	void assertNoField(final Class javaClass, final String name)
	{
		try
		{
			javaClass.getDeclaredField(name);
			fail("field " + name + " exists.");
		}
		catch(NoSuchFieldException e)
		{
			// success
		}
	}

	void assertMethod(final Class javaClass, final String name, final Class returnType, final int modifiers)
	{
		assertMethod(javaClass, name, null, returnType, modifiers, new Class[]{});
	}
	
	void assertMethod(final Class javaClass, final String name, final Class[] parameterTypes, final int modifiers)
	{
		assertMethod(javaClass, name, parameterTypes, Void.TYPE, modifiers, new Class[]{});
	}

	void assertMethod(final Class javaClass, final String name, final Class[] parameterTypes, final int modifiers, final Class[] exceptionTypes)
	{
		assertMethod(javaClass, name, parameterTypes, Void.TYPE, modifiers, exceptionTypes);
	}

	void assertMethod(final Class javaClass, final String name, final Class[] parameterTypes, final Class returnType, final int modifiers)
	{
		assertMethod(javaClass, name, parameterTypes, returnType, modifiers, new Class[]{});
	}

	void assertMethod(
			final Class javaClass, final String name, final Class[] parameterTypes,
			final Class returnType, final int modifiers, final Class[] exceptionTypes)
	{
		final Method method;
		try
		{
			method = javaClass.getDeclaredMethod(name, parameterTypes);
		}
		catch(NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(returnType, method.getReturnType());
		assertEquals(modifiers, method.getModifiers());
		assertEquals(Arrays.asList(exceptionTypes), Arrays.asList(method.getExceptionTypes()));
	}

	void assertNoMethod(final Class javaClass, final String name)
	{
		assertNoMethod(javaClass, name, null);
	}

	void assertNoMethod(final Class javaClass, final String name, final Class[] parameterTypes)
	{
		try
		{
			javaClass.getDeclaredMethod(name, parameterTypes);
			fail("method " + name + " exists.");
		}
		catch(NoSuchMethodException e)
		{
			// success
		}
	}

	void assertConstructor(
			final Class javaClass, final Class[] parameterTypes, final int modifiers)
	{
		final Constructor constructor;
		try
		{
			constructor = javaClass.getDeclaredConstructor(parameterTypes);
		}
		catch(NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(modifiers, constructor.getModifiers());
	}

}
