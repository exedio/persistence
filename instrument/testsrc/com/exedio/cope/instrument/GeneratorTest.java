/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PROTECTED;
import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DoubleRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.testmodel.DefaultTextInput;
import com.exedio.cope.instrument.testmodel.DoubleUnique;
import com.exedio.cope.instrument.testmodel.Enum2;
import com.exedio.cope.instrument.testmodel.FullQualifyInput;
import com.exedio.cope.instrument.testmodel.Input;
import com.exedio.cope.instrument.testmodel.Standard;
import com.exedio.cope.instrument.testmodel.Sub;
import com.exedio.cope.instrument.testmodel.Super;
import com.exedio.cope.instrument.testmodel.TypeNone;
import com.exedio.cope.instrument.testmodel.TypePrivate;
import com.exedio.cope.instrument.testmodel.sub.SubTarget;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import junit.framework.TestCase;

public class GeneratorTest extends TestCase
{
	public static final int VARARGS = 0x00000080;

	static final Class<?> STRING = String.class;
	static final Class<?> DOUBLE = Double.class;
	static final Class<?> BYTE_ARRAY = byte[].class;
	static final Class<?> INPUT_STREAM = InputStream.class;
	static final Class<?> OUTPUT_STREAM = OutputStream.class;
	static final Class<?> IO_EXCEPTION = IOException.class;

	static final Class<?> SET_VALUE_ARRAY = SetValue[].class;
	static final Class<?> MANDATORY_VIOLATION = MandatoryViolationException.class;
	static final Class<?> UNIQUE_VIOLATION = UniqueViolationException.class;
	static final Class<?> LENGTH_VIOLATION = StringLengthViolationException.class;
	static final Class<?> DOUBLE_RANGE_VIOLATION = DoubleRangeViolationException.class;
	static final Class<?> ACTIVATION = ActivationParameters.class;

	static final Class<?> STANDARD = Standard.class;
	static final Class<?> TYPE_NONE = TypeNone.class;
	static final Class<?> TYPE_PRIVATE = TypePrivate.class;
	static final Class<?> DOUBLE_UNIQUE = DoubleUnique.class;
	static final Class<?> SUB_TARGET = SubTarget.class;
	static final Class<?> SUPER = Super.class;
	static final Class<?> SUB = Sub.class;
	static final Class<?> INPUT = Input.class;
	static final Class<?> INPUT_SUB = DefaultTextInput.class;
	static final Class<?> INPUT_SUB2 = FullQualifyInput.class;

	@Test public void testStandard()
	{
		assertConstructor(STANDARD, new Class<?>[]{
				STRING, // notNullString
				STRING, // finalString
				STRING, // initialString
				int.class, // nativeInteger
				long.class, // nativeLong
				double.class, // nativeDouble
				boolean.class, // nativeBoolean
				Date.class, // mandatoryDate
				String.class, // mandatoryHash
				String.class, // privateSetterHash
			}, PUBLIC,
			new Class<?>[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(STANDARD, new Class<?>[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(STANDARD, new Class<?>[]{ACTIVATION}, PRIVATE);

		assertMethod(STANDARD, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getNotNullString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNotNullString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getFinalString", STRING, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "setFinalString", new Class<?>[]{STRING});
		assertMethod(STANDARD, "getDefaultToString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultToString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getUniqueString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setUniqueString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{UNIQUE_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "forUniqueString", new Class<?>[]{STRING}, STANDARD, PUBLIC|STATIC|FINAL);
		assertMethod(STANDARD, "getInitialString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInitialString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});

		assertMethod(STANDARD, "getDefaultInteger", Integer.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultInteger", new Class<?>[]{Integer.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeInteger", int.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeInteger", new Class<?>[]{int.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultLong", Long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultLong", new Class<?>[]{Long.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeLong", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeLong", new Class<?>[]{long.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultDouble", Double.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultDouble", new Class<?>[]{Double.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeDouble", double.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeDouble", new Class<?>[]{double.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultBoolean", Boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultBoolean", new Class<?>[]{Boolean.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeBoolean", boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeBoolean", new Class<?>[]{boolean.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getMandatoryDate", Date.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setMandatoryDate", new Class<?>[]{Date.class}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "touchMandatoryDate", new Class<?>[]{}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getPrivateDate", Date.class, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateDate", new Class<?>[]{Date.class}, PRIVATE|FINAL);
		assertMethod(STANDARD, "touchPrivateDate", new Class<?>[]{}, PRIVATE|FINAL);

		assertMethod(STANDARD, "getNowDate", Date.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNowDate", new Class<?>[]{Date.class}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "touchNowDate", new Class<?>[]{}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getMandatoryEnum", Standard.Enum1.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setMandatoryEnum", new Class<?>[]{Standard.Enum1.class}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "getProtectedEnum", Enum2.class, PROTECTED|FINAL);
		assertMethod(STANDARD, "setProtectedEnum", new Class<?>[]{Enum2.class}, PROTECTED|FINAL);

		assertMethod(STANDARD, "getInlineA", int.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInlineA", new Class<?>[]{int.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getInlineB", int.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInlineB", new Class<?>[]{int.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getPrivateString", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateString", new Class<?>[]{STRING}, PRIVATE|FINAL, new Class<?>[]{LENGTH_VIOLATION});

		assertNoMethod(STANDARD, "getNoneGetterString");
		assertMethod(STANDARD, "setNoneGetterString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getPrivateGetterString", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateGetterString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getInternalGetterStringInternal", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setInternalGetterString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertNoMethod(STANDARD, "getInternalGetterString");

		assertMethod(STANDARD, "getNoneSetterString", STRING, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "setNoneSetterString", new Class<?>[]{STRING});
		assertMethod(STANDARD, "getPrivateSetterString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateSetterString", new Class<?>[]{STRING}, PRIVATE|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getInternalSetterString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInternalSetterStringInternal", new Class<?>[]{STRING}, PRIVATE|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertNoMethod(STANDARD, "setInternalSetterString", new Class<?>[]{STRING});

		assertMethod(STANDARD, "getNonfinalGetterString", STRING, PUBLIC);
		assertMethod(STANDARD, "setNonfinalGetterString", new Class<?>[]{STRING}, PROTECTED|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getNonfinalSetterString", STRING, PROTECTED|FINAL);
		assertMethod(STANDARD, "setNonfinalSetterString", new Class<?>[]{STRING}, PUBLIC, new Class<?>[]{LENGTH_VIOLATION});

		assertMethod(STANDARD, "isAsIsBoolean", Boolean.class, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "getAsIsBoolean");
		assertMethod(STANDARD, "setAsIsBoolean", new Class<?>[]{Boolean.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDoubleUnique1", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDoubleUnique1", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{UNIQUE_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getDoubleUnique2", Integer.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDoubleUnique2", new Class<?>[]{Integer.class}, PUBLIC|FINAL, new Class<?>[]{UNIQUE_VIOLATION});
		assertMethod(STANDARD, "forDoubleUnique", new Class<?>[]{STRING, Integer.class}, STANDARD, PUBLIC|STATIC|FINAL);

		assertNoMethod(STANDARD, "getIgnoreString");
		assertNoMethod(STANDARD, "setIgnoreString", new Class<?>[]{STRING});

		assertMethod(STANDARD, "get", DOUBLE, FINAL);
		assertMethod(STANDARD, "set", new Class<?>[]{DOUBLE}, FINAL, new Class<?>[]{UNIQUE_VIOLATION, DOUBLE_RANGE_VIOLATION});
		assertMethod(STANDARD, "forDefaultFeature", new Class<?>[]{DOUBLE}, STANDARD, STATIC|FINAL);

		assertNoMethod(STANDARD, "getDefaultFeature");
		assertNoMethod(STANDARD, "setDefaultFeature"   , new Class<?>[]{DOUBLE});
		assertNoMethod(STANDARD, "findByDefaultFeature", new Class<?>[]{DOUBLE});

		assertMethod(STANDARD, "checkPublicHash", new Class<?>[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "checkPrivateHash", new Class<?>[]{STRING}, Boolean.TYPE, PRIVATE|FINAL);
		assertMethod(STANDARD, "checkMandatoryHash", new Class<?>[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "checkPrivateSetterHash", new Class<?>[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPublicHash", new Class<?>[]{STRING}, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateHash", new Class<?>[]{STRING}, PRIVATE|FINAL);
		assertMethod(STANDARD, "setMandatoryHash", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "setPrivateSetterHash", new Class<?>[]{STRING}, PRIVATE|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "getPublicHashSHA512s8i5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateHashSHA512s8i5", String.class, PRIVATE|FINAL);
		assertMethod(STANDARD, "getMandatoryHashSHA512s8i5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterHashSHA512s8i5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPublicHashSHA512s8i5", new Class<?>[]{STRING}, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateHashSHA512s8i5", new Class<?>[]{STRING}, PRIVATE|FINAL);
		assertMethod(STANDARD, "setMandatoryHashSHA512s8i5", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "setPrivateSetterHashSHA512s8i5", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{MANDATORY_VIOLATION});
		assertNoMethod(STANDARD, "getPublicHash");
		assertNoMethod(STANDARD, "getPrivateHash");
		assertNoMethod(STANDARD, "getMandatoryHash");
		assertNoMethod(STANDARD, "getPrivateSetterHash");

		assertMethod(STANDARD, "setXMLReader", new Class<?>[]{STRING}, FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getXMLReader", STRING, FINAL);

		assertField(STANDARD, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertField(STANDARD, "TYPE", Type.class, PUBLIC|STATIC|FINAL);

		assertConstructor(TYPE_NONE, new Class<?>[]{}, PRIVATE);
		assertConstructor(TYPE_NONE, new Class<?>[]{SET_VALUE_ARRAY}, PUBLIC|VARARGS); // @cope.generic.constructor public
		assertConstructor(TYPE_NONE, new Class<?>[]{ACTIVATION}, 0); // @cope.activation.constructor package
		assertMethod(TYPE_NONE, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(TYPE_NONE, "setDefaultString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertField(TYPE_NONE, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertNoField(TYPE_NONE, "TYPE");

		assertConstructor(TYPE_PRIVATE, new Class<?>[]{}, PUBLIC);
		assertConstructor(TYPE_PRIVATE, new Class<?>[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(TYPE_PRIVATE, new Class<?>[]{ACTIVATION}, PRIVATE);
		assertMethod(TYPE_PRIVATE, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(TYPE_PRIVATE, "setDefaultString", new Class<?>[]{STRING}, PUBLIC|FINAL, new Class<?>[]{LENGTH_VIOLATION});
		assertField(TYPE_PRIVATE, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertField(TYPE_PRIVATE, "TYPE", Type.class, PRIVATE|STATIC|FINAL);
	}

	@Test public void testDoubleUnique()
	{
		assertConstructor(DOUBLE_UNIQUE, new Class<?>[]{STRING, SUB_TARGET}, PUBLIC, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertMethod(DOUBLE_UNIQUE, "getString", STRING, PUBLIC|FINAL);
		assertMethod(DOUBLE_UNIQUE, "getItem", SUB_TARGET, PUBLIC|FINAL);
		assertMethod(DOUBLE_UNIQUE, "forUnique", new Class<?>[]{STRING, SUB_TARGET}, DOUBLE_UNIQUE, PRIVATE|STATIC|FINAL);
	}

	@Test public void testHierarchy()
	{
		assertConstructor(SUPER, new Class<?>[]{
				STRING, // superMandatory
				Integer.class, // superInitial
			}, PUBLIC,
			new Class<?>[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(SUPER, new Class<?>[]{SET_VALUE_ARRAY}, PROTECTED|VARARGS);
		assertConstructor(SUPER, new Class<?>[]{ACTIVATION}, PROTECTED);

		assertConstructor(SUB, new Class<?>[]{
				STRING, // superMandatory
				Integer.class, // superInitial
				boolean.class, // subMandatory
				Long.class, // subInitial
			}, PUBLIC,
			new Class<?>[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(SUB, new Class<?>[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(SUB, new Class<?>[]{ACTIVATION}, PRIVATE);

		// test protected constructors on non-abstract types
		assertConstructor(INPUT, new Class<?>[]{String.class, int.class}, PUBLIC, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT, new Class<?>[]{SET_VALUE_ARRAY}, PROTECTED|VARARGS);
		assertConstructor(INPUT, new Class<?>[]{ACTIVATION}, PROTECTED);
		assertConstructor(INPUT_SUB, new Class<?>[]{String.class, int.class}, PUBLIC, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT_SUB, new Class<?>[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(INPUT_SUB, new Class<?>[]{ACTIVATION}, PRIVATE);
		assertConstructor(INPUT_SUB2, new Class<?>[]{String.class, int.class}, PUBLIC, new Class<?>[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT_SUB2, new Class<?>[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(INPUT_SUB2, new Class<?>[]{ACTIVATION}, PRIVATE);
	}

	void assertField(
			final Class<?> javaClass, final String name,
			final Class<?> returnType, final int modifiers)
	{
		final Field field;
		try
		{
			field = javaClass.getDeclaredField(name);
		}
		catch(final NoSuchFieldException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(returnType, field.getType());
		assertEquals(modifiers, field.getModifiers());
	}

	void assertNoField(final Class<?> javaClass, final String name)
	{
		try
		{
			javaClass.getDeclaredField(name);
			fail("field " + name + " exists.");
		}
		catch(final NoSuchFieldException e)
		{
			// success
		}
	}

	void assertMethod(final Class<?> javaClass, final String name, final Class<?> returnType, final int modifiers)
	{
		assertMethod(javaClass, name, null, returnType, modifiers, new Class<?>[]{});
	}

	void assertMethod(final Class<?> javaClass, final String name, final Class<?>[] parameterTypes, final int modifiers)
	{
		assertMethod(javaClass, name, parameterTypes, Void.TYPE, modifiers, new Class<?>[]{});
	}

	void assertMethod(final Class<?> javaClass, final String name, final Class<?>[] parameterTypes, final int modifiers, final Class<?>[] exceptionTypes)
	{
		assertMethod(javaClass, name, parameterTypes, Void.TYPE, modifiers, exceptionTypes);
	}

	void assertMethod(final Class<?> javaClass, final String name, final Class<?>[] parameterTypes, final Class<?> returnType, final int modifiers)
	{
		assertMethod(javaClass, name, parameterTypes, returnType, modifiers, new Class<?>[]{});
	}

	void assertMethod(
			final Class<?> javaClass, final String name, final Class<?>[] parameterTypes,
			final Class<?> returnType, final int modifiers, final Class<?>[] exceptionTypes)
	{
		final Method method;
		try
		{
			method = javaClass.getDeclaredMethod(name, parameterTypes);
		}
		catch(final NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(returnType, method.getReturnType());
		assertEquals('('+Modifier.toString(modifiers)+'|'+Modifier.toString(method.getModifiers())+')', modifiers, method.getModifiers());
		assertEquals(Arrays.asList(exceptionTypes), Arrays.asList(method.getExceptionTypes()));
	}

	void assertNoMethod(final Class<?> javaClass, final String name)
	{
		assertNoMethod(javaClass, name, null);
	}

	void assertNoMethod(final Class<?> javaClass, final String name, final Class<?>[] parameterTypes)
	{
		try
		{
			javaClass.getDeclaredMethod(name, parameterTypes);
			fail("method " + name + " exists.");
		}
		catch(final NoSuchMethodException e)
		{
			// success
		}
	}

	void assertConstructor(
			final Class<?> javaClass, final Class<?>[] parameterTypes, final int modifiers)
	{
		assertConstructor(javaClass, parameterTypes, modifiers, new Class<?>[]{});
	}

	void assertConstructor(
			final Class<?> javaClass, final Class<?>[] parameterTypes, final int modifiers, final Class<?>[] exceptionTypes)
	{
		final Constructor<?> constructor;
		try
		{
			constructor = javaClass.getDeclaredConstructor(parameterTypes);
		}
		catch(final NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(modifiers, constructor.getModifiers());
		assertEquals(Arrays.asList(exceptionTypes), Arrays.asList(constructor.getExceptionTypes()));
	}
}
