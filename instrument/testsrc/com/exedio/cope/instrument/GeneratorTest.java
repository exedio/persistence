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

import com.exedio.cope.instrument.testmodel.Standard;
import com.exedio.cope.lib.AttributeValue;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;


public class GeneratorTest extends InstrumentorTest
{
	public static final int PUBLIC = Modifier.PUBLIC;
	public static final int PRIVATE = Modifier.PRIVATE;
	public static final int STATIC = Modifier.STATIC;
	public static final int FINAL = Modifier.FINAL;
	
	public void testStandard() throws ClassNotFoundException
	{
		final Class attributeValueArrayClass = Class.forName("[L"+AttributeValue.class.getName()+';');
		
		final Class standard = Standard.class;
		assertConstructor(standard, new Class[]{int.class}, PUBLIC);
		assertConstructor(standard, new Class[]{attributeValueArrayClass}, PRIVATE);
		assertConstructor(standard, new Class[]{ReactivationConstructorDummy.class, int.class}, PRIVATE);

		assertMethod(standard, "getDefaultString", String.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultString", new Class[]{String.class}, PUBLIC|FINAL);

		assertMethod(standard, "getDefaultInteger", Integer.class, PUBLIC|FINAL);
		assertMethod(standard, "setDefaultInteger", new Class[]{Integer.class}, PUBLIC|FINAL);
		assertMethod(standard, "getNativeInteger", int.class, PUBLIC|FINAL);
		assertMethod(standard, "setNativeInteger", new Class[]{int.class}, PUBLIC|FINAL);

		assertField(standard, "TYPE", Type.class, PUBLIC|STATIC|FINAL);
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

	void assertMethod(final Class javaClass, final String name, final Class returnType, final int modifiers)
	{
		assertMethod(javaClass, name, null, returnType, modifiers);
	}
	
	void assertMethod(final Class javaClass, final String name, final Class[] parameterTypes, final int modifiers)
	{
		assertMethod(javaClass, name, parameterTypes, Void.TYPE, modifiers);
	}

	void assertMethod(
			final Class javaClass, final String name, final Class[] parameterTypes,
			final Class returnType, final int modifiers)
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
