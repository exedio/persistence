/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
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

public class GeneratorTest extends InstrumentorTest
{
	public static final int VARARGS = 0x00000080;
	
	final static Class STRING = String.class;
	final static Class BYTE_ARRAY = byte[].class;
	final static Class INPUT_STREAM = InputStream.class;
	final static Class OUTPUT_STREAM = OutputStream.class;
	final static Class IO_EXCEPTION = IOException.class;
	
	final static Class SET_VALUE_ARRAY = SetValue[].class;
	final static Class MANDATORY_VIOLATION = MandatoryViolationException.class;
	final static Class UNIQUE_VIOLATION = UniqueViolationException.class;
	final static Class LENGTH_VIOLATION = StringLengthViolationException.class;
	final static Class ACTIVATION = ActivationParameters.class;
	
	final static Class STANDARD = Standard.class;
	final static Class TYPE_NONE = TypeNone.class;
	final static Class TYPE_PRIVATE = TypePrivate.class;
	final static Class DOUBLE_UNIQUE = DoubleUnique.class;
	final static Class SUB_TARGET = SubTarget.class;
	final static Class SUPER = Super.class;
	final static Class SUB = Sub.class;
	final static Class INPUT = Input.class;
	final static Class INPUT_SUB = DefaultTextInput.class;
	final static Class INPUT_SUB2 = FullQualifyInput.class;

	public void testStandard()
	{
		assertConstructor(STANDARD, new Class[]{
				STRING, // notNullString
				STRING, // finalString
				STRING, // initialString
				int.class, // nativeInteger
				long.class, // nativeLong
				double.class, // nativeDouble
				boolean.class, // nativeBoolean
				Date.class, // mandatoryDate
				Standard.Enum1.class, // mandatoryEnum
				String.class, // mandatoryHash
				String.class, // privateSetterHash
			}, PUBLIC,
			new Class[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(STANDARD, new Class[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(STANDARD, new Class[]{ACTIVATION}, PRIVATE);

		assertMethod(STANDARD, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getNotNullString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNotNullString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getFinalString", STRING, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "setFinalString", new Class[]{STRING});
		assertMethod(STANDARD, "getDefaultToString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultToString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getUniqueString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setUniqueString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{UNIQUE_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "forUniqueString", new Class[]{STRING}, STANDARD, PUBLIC|STATIC|FINAL);
		assertMethod(STANDARD, "getInitialString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInitialString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});

		assertMethod(STANDARD, "getDefaultInteger", Integer.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultInteger", new Class[]{Integer.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeInteger", int.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeInteger", new Class[]{int.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultLong", Long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultLong", new Class[]{Long.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeLong", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeLong", new Class[]{long.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultDouble", Double.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultDouble", new Class[]{Double.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeDouble", double.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeDouble", new Class[]{double.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getDefaultBoolean", Boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDefaultBoolean", new Class[]{Boolean.class}, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNativeBoolean", boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNativeBoolean", new Class[]{boolean.class}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getMandatoryDate", Date.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setMandatoryDate", new Class[]{Date.class}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "touchMandatoryDate", new Class[]{}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getPrivateDate", Date.class, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateDate", new Class[]{Date.class}, PRIVATE|FINAL);
		assertMethod(STANDARD, "touchPrivateDate", new Class[]{}, PRIVATE|FINAL);

		assertMethod(STANDARD, "getNowDate", Date.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setNowDate", new Class[]{Date.class}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "touchNowDate", new Class[]{}, PUBLIC|FINAL);

		assertMethod(STANDARD, "getMandatoryEnum", Standard.Enum1.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setMandatoryEnum", new Class[]{Standard.Enum1.class}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "getProtectedEnum", Enum2.class, PROTECTED|FINAL);
		assertMethod(STANDARD, "setProtectedEnum", new Class[]{Enum2.class}, PROTECTED|FINAL);

		assertMethod(STANDARD, "getPrivateString", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateString", new Class[]{STRING}, PRIVATE|FINAL, new Class[]{LENGTH_VIOLATION});

		assertNoMethod(STANDARD, "getNoneGetterString");
		assertMethod(STANDARD, "setNoneGetterString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getPrivateGetterString", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateGetterString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getInternalGetterStringInternal", STRING, PRIVATE|FINAL);
		assertMethod(STANDARD, "setInternalGetterString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertNoMethod(STANDARD, "getInternalGetterString");

		assertMethod(STANDARD, "getNoneSetterString", STRING, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "setNoneSetterString", new Class[]{STRING});
		assertMethod(STANDARD, "getPrivateSetterString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateSetterString", new Class[]{STRING}, PRIVATE|FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getInternalSetterString", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setInternalSetterStringInternal", new Class[]{STRING}, PRIVATE|FINAL, new Class[]{LENGTH_VIOLATION});
		assertNoMethod(STANDARD, "setInternalSetterString", new Class[]{STRING});

		assertMethod(STANDARD, "getNonfinalGetterString", STRING, PUBLIC);
		assertMethod(STANDARD, "setNonfinalGetterString", new Class[]{STRING}, PROTECTED|FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getNonfinalSetterString", STRING, PROTECTED|FINAL);
		assertMethod(STANDARD, "setNonfinalSetterString", new Class[]{STRING}, PUBLIC, new Class[]{LENGTH_VIOLATION});

		assertMethod(STANDARD, "isAsIsBoolean", Boolean.class, PUBLIC|FINAL);
		assertNoMethod(STANDARD, "getAsIsBoolean");
		assertMethod(STANDARD, "setAsIsBoolean", new Class[]{Boolean.class}, PUBLIC|FINAL);
		
		assertMethod(STANDARD, "getDoubleUnique1", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDoubleUnique1", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{UNIQUE_VIOLATION, LENGTH_VIOLATION});
		assertMethod(STANDARD, "getDoubleUnique2", Integer.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setDoubleUnique2", new Class[]{Integer.class}, PUBLIC|FINAL, new Class[]{UNIQUE_VIOLATION});
		assertMethod(STANDARD, "forDoubleUnique", new Class[]{STRING, Integer.class}, STANDARD, PUBLIC|STATIC|FINAL);

		assertNoMethod(STANDARD, "getIgnoreString");
		assertNoMethod(STANDARD, "setIgnoreString", new Class[]{STRING});

		assertMethod(STANDARD, "isAnyMediaNull", boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaURL", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaContentType", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaLength", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaLastModified", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaBody", BYTE_ARRAY, PUBLIC|FINAL);
		assertMethod(STANDARD, "getAnyMediaBody", new Class[]{OUTPUT_STREAM}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "getAnyMediaBody", new Class[]{File.class}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setAnyMedia", new Class[]{INPUT_STREAM, STRING}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setAnyMedia", new Class[]{File.class, STRING}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});

		assertMethod(STANDARD, "isMajorMediaNull", boolean.class, FINAL);
		assertMethod(STANDARD, "getMajorMediaURL", STRING, FINAL);
		assertMethod(STANDARD, "getMajorMediaContentType", STRING, FINAL);
		assertMethod(STANDARD, "getMajorMediaLength", long.class, FINAL);
		assertMethod(STANDARD, "getMajorMediaLastModified", long.class, FINAL);
		assertMethod(STANDARD, "getMajorMediaBody", BYTE_ARRAY, FINAL);
		assertMethod(STANDARD, "getMajorMediaBody", new Class[]{OUTPUT_STREAM}, FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "getMajorMediaBody", new Class[]{File.class}, FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setMajorMedia", new Class[]{BYTE_ARRAY, STRING}, FINAL);
		assertMethod(STANDARD, "setMajorMedia", new Class[]{INPUT_STREAM, STRING}, FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setMajorMedia", new Class[]{File.class, STRING}, FINAL, new Class[]{IO_EXCEPTION});

		assertMethod(STANDARD, "isMinorMediaNull", boolean.class, PROTECTED|FINAL);
		assertMethod(STANDARD, "getMinorMediaURL", STRING, PROTECTED|FINAL);
		assertNoMethod(STANDARD, "getMinorMediaContentType");
		assertMethod(STANDARD, "getMinorMediaLength", long.class, PROTECTED|FINAL);
		assertMethod(STANDARD, "getMinorMediaLastModified", long.class, PROTECTED|FINAL);
		assertMethod(STANDARD, "getMinorMediaBody", BYTE_ARRAY, PROTECTED|FINAL);
		assertMethod(STANDARD, "getMinorMediaBody", new Class[]{OUTPUT_STREAM}, PROTECTED|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "getMinorMediaBody", new Class[]{File.class}, PROTECTED|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setMinorMedia", new Class[]{BYTE_ARRAY, STRING}, PROTECTED|FINAL);
		assertMethod(STANDARD, "setMinorMedia", new Class[]{INPUT_STREAM, STRING}, PROTECTED|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setMinorMedia", new Class[]{File.class, STRING}, PROTECTED|FINAL, new Class[]{IO_EXCEPTION});

		assertMethod(STANDARD, "isNoSetterMediaNull", boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaURL", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaContentType", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaLength", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaLastModified", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaBody", BYTE_ARRAY, PUBLIC|FINAL);
		assertMethod(STANDARD, "getNoSetterMediaBody", new Class[]{OUTPUT_STREAM}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "getNoSetterMediaBody", new Class[]{File.class}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertNoMethod(STANDARD, "setNoSetterMedia", new Class[]{BYTE_ARRAY,STRING});
		assertNoMethod(STANDARD, "setNoSetterMedia", new Class[]{INPUT_STREAM,STRING});
		assertNoMethod(STANDARD, "setNoSetterMedia", new Class[]{File.class,STRING});

		assertMethod(STANDARD, "isPrivateSetterMediaNull", boolean.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaURL", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaContentType", STRING, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaLength", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaLastModified", long.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaBody", BYTE_ARRAY, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterMediaBody", new Class[]{OUTPUT_STREAM}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "getPrivateSetterMediaBody", new Class[]{File.class}, PUBLIC|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setPrivateSetterMedia", new Class[]{BYTE_ARRAY,STRING}, PRIVATE|FINAL);
		assertMethod(STANDARD, "setPrivateSetterMedia", new Class[]{INPUT_STREAM,STRING}, PRIVATE|FINAL, new Class[]{IO_EXCEPTION});
		assertMethod(STANDARD, "setPrivateSetterMedia", new Class[]{File.class,STRING}, PRIVATE|FINAL, new Class[]{IO_EXCEPTION});

		assertMethod(STANDARD, "checkPublicHash", new Class[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "checkPrivateHash", new Class[]{STRING}, Boolean.TYPE, PRIVATE|FINAL);
		assertMethod(STANDARD, "checkMandatoryHash", new Class[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "checkPrivateSetterHash", new Class[]{STRING}, Boolean.TYPE, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPublicHash", new Class[]{STRING}, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateHash", new Class[]{STRING}, PRIVATE|FINAL);
		assertMethod(STANDARD, "setMandatoryHash", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "setPrivateSetterHash", new Class[]{STRING}, PRIVATE|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "getPublicHashMD5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateHashMD5", String.class, PRIVATE|FINAL);
		assertMethod(STANDARD, "getMandatoryHashMD5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "getPrivateSetterHashMD5", String.class, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPublicHashMD5", new Class[]{STRING}, PUBLIC|FINAL);
		assertMethod(STANDARD, "setPrivateHashMD5", new Class[]{STRING}, PRIVATE|FINAL);
		assertMethod(STANDARD, "setMandatoryHashMD5", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertMethod(STANDARD, "setPrivateSetterHashMD5", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{MANDATORY_VIOLATION});
		assertNoMethod(STANDARD, "getPublicHash");
		assertNoMethod(STANDARD, "getPrivateHash");
		assertNoMethod(STANDARD, "getMandatoryHash");
		assertNoMethod(STANDARD, "getPrivateSetterHash");

		assertMethod(STANDARD, "setXMLReader", new Class[]{STRING}, FINAL, new Class[]{LENGTH_VIOLATION});
		assertMethod(STANDARD, "getXMLReader", STRING, FINAL);
		
		assertField(STANDARD, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertField(STANDARD, "TYPE", Type.class, PUBLIC|STATIC|FINAL);

		assertConstructor(TYPE_NONE, new Class[]{}, PRIVATE);
		assertConstructor(TYPE_NONE, new Class[]{SET_VALUE_ARRAY}, PUBLIC|VARARGS); // @cope.generic.constructor public
		assertConstructor(TYPE_NONE, new Class[]{ACTIVATION}, 0); // @cope.activation.constructor package
		assertMethod(TYPE_NONE, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(TYPE_NONE, "setDefaultString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertField(TYPE_NONE, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertNoField(TYPE_NONE, "TYPE");

		assertConstructor(TYPE_PRIVATE, new Class[]{}, PUBLIC);
		assertConstructor(TYPE_PRIVATE, new Class[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(TYPE_PRIVATE, new Class[]{ACTIVATION}, PRIVATE);
		assertMethod(TYPE_PRIVATE, "getDefaultString", STRING, PUBLIC|FINAL);
		assertMethod(TYPE_PRIVATE, "setDefaultString", new Class[]{STRING}, PUBLIC|FINAL, new Class[]{LENGTH_VIOLATION});
		assertField(TYPE_PRIVATE, "serialVersionUID", long.class, PRIVATE|STATIC|FINAL);
		assertField(TYPE_PRIVATE, "TYPE", Type.class, PRIVATE|STATIC|FINAL);
	}

	public void testDoubleUnique()
	{
		assertConstructor(DOUBLE_UNIQUE, new Class[]{STRING, SUB_TARGET}, PUBLIC, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertMethod(DOUBLE_UNIQUE, "getString", STRING, PUBLIC|FINAL);
		assertMethod(DOUBLE_UNIQUE, "getItem", SUB_TARGET, PUBLIC|FINAL);
		assertMethod(DOUBLE_UNIQUE, "forUnique", new Class[]{STRING, SUB_TARGET}, DOUBLE_UNIQUE, PUBLIC|STATIC|FINAL);
	}
	
	public void testHierarchy()
	{
		assertConstructor(SUPER, new Class[]{
				STRING, // superMandatory
				Integer.class, // superInitial
			}, PUBLIC,
			new Class[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(SUPER, new Class[]{SET_VALUE_ARRAY}, PROTECTED|VARARGS);
		assertConstructor(SUPER, new Class[]{ACTIVATION}, PROTECTED);

		assertConstructor(SUB, new Class[]{
				STRING, // superMandatory
				Integer.class, // superInitial
				boolean.class, // subMandatory
				Long.class, // subInitial
			}, PUBLIC,
			new Class[]{
				MANDATORY_VIOLATION,
				LENGTH_VIOLATION,
			});
		assertConstructor(SUB, new Class[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(SUB, new Class[]{ACTIVATION}, PRIVATE);

		// test protected constructors on non-abstract types
		assertConstructor(INPUT, new Class[]{String.class, int.class}, PUBLIC, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT, new Class[]{SET_VALUE_ARRAY}, PROTECTED|VARARGS);
		assertConstructor(INPUT, new Class[]{ACTIVATION}, PROTECTED);
		assertConstructor(INPUT_SUB, new Class[]{String.class, int.class}, PUBLIC, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT_SUB, new Class[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(INPUT_SUB, new Class[]{ACTIVATION}, PRIVATE);
		assertConstructor(INPUT_SUB2, new Class[]{String.class, int.class}, PUBLIC, new Class[]{MANDATORY_VIOLATION, LENGTH_VIOLATION, UNIQUE_VIOLATION});
		assertConstructor(INPUT_SUB2, new Class[]{SET_VALUE_ARRAY}, PRIVATE|VARARGS);
		assertConstructor(INPUT_SUB2, new Class[]{ACTIVATION}, PRIVATE);
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
			final Class<?> javaClass, final String name, final Class[] parameterTypes,
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
		assertEquals('('+Modifier.toString(modifiers)+'|'+Modifier.toString(method.getModifiers())+')', modifiers, method.getModifiers());
		assertEquals(Arrays.asList(exceptionTypes), Arrays.asList(method.getExceptionTypes()));
	}

	void assertNoMethod(final Class javaClass, final String name)
	{
		assertNoMethod(javaClass, name, null);
	}

	void assertNoMethod(final Class<?> javaClass, final String name, final Class[] parameterTypes)
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
		assertConstructor(javaClass, parameterTypes, modifiers, new Class[]{});
	}
	
	void assertConstructor(
			final Class<?> javaClass, final Class[] parameterTypes, final int modifiers, final Class[] exceptionTypes)
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
		assertEquals(Arrays.asList(exceptionTypes), Arrays.asList(constructor.getExceptionTypes()));
	}
}
