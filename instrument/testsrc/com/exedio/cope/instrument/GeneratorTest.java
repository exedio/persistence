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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.exedio.cope.instrument.testmodel.Standard;


public class GeneratorTest extends InstrumentorTest
{
	public static final int PUBLIC = Modifier.PUBLIC;
	public static final int FINAL = Modifier.FINAL;
	
	public void testStandard()
	{
		final Class standard = Standard.class;
		assertMethod(standard, "getDefaultString", String.class, PUBLIC|FINAL);
	}
	
	void assertMethod(final Class javaClass, final String name, final Class returnType, final int modifiers)
	{
		final Method method;
		try
		{
			method = javaClass.getDeclaredMethod(name, null);
		}
		catch(NoSuchMethodException e)
		{
			throw new AssertionError(e);
		}
		assertEquals(returnType, method.getReturnType());
		assertEquals(modifiers, method.getModifiers());
	}

}
